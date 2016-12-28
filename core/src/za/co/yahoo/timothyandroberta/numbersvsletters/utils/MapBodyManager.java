package za.co.yahoo.timothyandroberta.utils;

import java.util.Iterator;

import za.co.yahoo.timothyandroberta.screens.GamePlayScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * @author David Saltares Márquez david.saltares at gmail.com
 * @brief Populates box2D world with static bodies using data from a map object
 * 
 *        It uses an XML formatted materials file to assign properties to the
 *        static bodies it creates. To assign a material to a shape add a
 *        "material" custom property to the shape in question using your editor
 *        of choice (Tiled, Gleed, Tide...). Such file uses the following
 *        structure:
 * @code <materials> <material name="ice" density="1.0" restitution="0.0"
 *       friction="0.1" /> <material name="elastic" density="1.0"
 *       restitution="0.8" friction="0.8" /> </materials>
 * @endcode
 * 
 *          In case no material property is found, it'll get a default one.
 * 
 */
public class MapBodyManager {
	private Logger m_logger;
	private World m_world;
	private GamePlayScreen m_screen;
	private float m_units;
	private Array<Body> m_bodies = new Array<Body>();
	private ObjectMap<String, FixtureDef> m_materials = new ObjectMap<String, FixtureDef>();

	/**
	 * @param world
	 *            box2D world to work with.
	 * @param unitsPerPixel
	 *            conversion ratio from pixel units to box2D metres.
	 * @param materialsFile
	 *            xml file with specific physics properties to be assigned to
	 *            newly created bodies.
	 * @param loggingLevel
	 *            verbosity of the embedded logger.
	 */
	public MapBodyManager(World world, GamePlayScreen screen,
			float unitsPerPixel, String materialsFile, int loggingLevel) {
		m_logger = new Logger("MapBodyManager", loggingLevel);
		m_logger.info("initialising");

		m_world = world;
		m_screen = screen;
		m_units = unitsPerPixel;

		FixtureDef defaultFixture = new FixtureDef();
		defaultFixture.density = 1.0f;
		defaultFixture.friction = 0.8f;
		defaultFixture.restitution = 0.0f;

		m_materials.put("default", defaultFixture);

		if (materialsFile != null) {
			loadMaterialsFile(materialsFile);
		}
	}

	/**
	 * @param map
	 *            will use the "physics" layer of this map to look for shapes in
	 *            order to create the static bodies.
	 */
	public void createPhysics(Map map) {
		createPhysics(map, "physics");
	}

	/**
	 * @param map
	 *            map to be used to create the static bodies.
	 * @param layerName
	 *            name of the layer that contains the shapes.
	 */
	public void createPhysics(Map map, String layerName) {
		MapLayer layer = map.getLayers().get(layerName);

		if (layer == null) {
			m_logger.error("layer " + layerName + " does not exist");
			return;
		}

		MapObjects objects = layer.getObjects();
		Iterator<MapObject> objectIt = objects.iterator();

		while (objectIt.hasNext()) {
			MapObject object = objectIt.next();

			if (object instanceof TextureMapObject) {
				continue;
			}

			Shape shape;

			if (object instanceof RectangleMapObject) {
				String name = object.getName();
				getRectangle((RectangleMapObject) object, name);
			}
			if (object instanceof PolygonMapObject) {
				shape = getPolygon((PolygonMapObject) object);
			} else if (object instanceof PolylineMapObject) {
				shape = getPolyline((PolylineMapObject) object);
			} else if (object instanceof CircleMapObject) {
				shape = getCircle((CircleMapObject) object);
			} else {
				m_logger.error("non suported shape " + object);
				continue;
			}

			MapProperties properties = object.getProperties();
			String material = properties.get("material", String.class);
			FixtureDef fixtureDef = null; // m_materials.get(material);

			if (fixtureDef == null) {
				m_logger.error("material does not exist " + material
						+ " using default");
				fixtureDef = m_materials.get("default");
			}

			fixtureDef.shape = shape;

			BodyDef bodyDef = new BodyDef();
			bodyDef.type = BodyDef.BodyType.StaticBody;

			Body body = m_world.createBody(bodyDef);
			body.createFixture(fixtureDef);

			m_bodies.add(body);

			fixtureDef.shape = null;
			shape.dispose();
		}
	}

	/**
	 * Destroys every static body that has been created using the manager.
	 */
	public void destroyPhysics() {
		for (Body body : m_bodies) {
			m_world.destroyBody(body);
		}

		m_bodies.clear();
	}

	private void loadMaterialsFile(String materialsFile) {
		m_logger.info("adding default material");

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = 1.0f;
		fixtureDef.friction = 1.0f;
		fixtureDef.restitution = 0.0f;
		m_materials.put("default", fixtureDef);

		m_logger.info("loading materials file");

		try {
			XmlReader reader = new XmlReader();
			Element root = reader.parse(Gdx.files.internal(materialsFile));

			Array<Element> materials = root.getChildrenByName("materials");

			for (Element material : materials) {
				String name = material.getAttribute("name");

				if (name == null) {
					m_logger.error("material without name");
					continue;
				}

				fixtureDef = new FixtureDef();
				fixtureDef.density = Float.parseFloat(material.getAttribute(
						"density", "1.0"));
				fixtureDef.friction = Float.parseFloat(material.getAttribute(
						"friction", "1.0"));
				fixtureDef.restitution = Float.parseFloat(material
						.getAttribute("restitution", "1.0"));
				m_logger.info("adding material " + name);
				m_materials.put(name, fixtureDef);
			}

		} catch (Exception e) {
			m_logger.error("error loading " + materialsFile + " "
					+ e.getMessage());
		}
	}

	private void getRectangle(RectangleMapObject rectangleObject, String name) {
		Rectangle rectangle = rectangleObject.getRectangle();

		Vector2 midpoint1 = new Vector2();
		rectangle.getCenter(midpoint1);
		midpoint1 = new Vector2(midpoint1.x * m_units, midpoint1.y * m_units);

		//level.addEntity(name);
		
		// put in class
		if (name.equals("player"))
			m_screen.setStartingPoint(midpoint1);
		if (name.equals("a"))
			m_screen.setLetterStartingPoint(midpoint1, 0);
		if (name.equals("b"))
			m_screen.setLetterStartingPoint(midpoint1, 1);
		if (name.equals("c"))
			m_screen.setLetterStartingPoint(midpoint1, 2);
		if (name.startsWith("life"))
			m_screen.setLifeStartingPoint(midpoint1);
		if (name.equals("door"))
			m_screen.setDoorStartingPoint(midpoint1);
		if (name.equals("spikes"))
			m_screen.setSpikeStartingPoint(midpoint1);
		if (name.equals("button"))
			m_screen.setButtonStartingPoint(midpoint1);
		if (name.equals("camera"))
			m_screen.setCameraStartingPoint(midpoint1);
	}

	private Shape getCircle(CircleMapObject circleObject) {
		Circle circle = circleObject.getCircle();
		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(circle.radius * m_units);
		circleShape.setPosition(new Vector2(circle.x * m_units, circle.y
				* m_units));
		return circleShape;
	}

	private Shape getPolygon(PolygonMapObject polygonObject) {
		PolygonShape polygon = new PolygonShape();
		float[] vertices = polygonObject.getPolygon().getVertices();
		float[] worldVertices = new float[vertices.length];

		for (int i = 0; i < vertices.length; ++i) {
			worldVertices[i] = vertices[i] * m_units;
		}

		polygon.set(worldVertices);
		return polygon;
	}

	private Shape getPolyline(PolylineMapObject polylineObject) {
		float[] vertices = polylineObject.getPolyline()
				.getTransformedVertices();
		Vector2[] worldVertices = new Vector2[vertices.length / 2];

		for (int i = 0; i < vertices.length / 2; ++i) {
			worldVertices[i] = new Vector2();
			worldVertices[i].x = vertices[i * 2] * m_units;
			worldVertices[i].y = vertices[i * 2 + 1] * m_units;
		}

		ChainShape chain = new ChainShape();
		chain.createChain(worldVertices);
		return chain;
	}
}
