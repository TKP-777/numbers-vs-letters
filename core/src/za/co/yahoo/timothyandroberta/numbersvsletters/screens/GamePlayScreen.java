package za.co.yahoo.timothyandroberta.numbersvsletters.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.Array;
import za.co.yahoo.timothyandroberta.numbersvsletters.NumbersVsLetters;
import za.co.yahoo.timothyandroberta.numbersvsletters.logic.GenericButton;
import za.co.yahoo.timothyandroberta.numbersvsletters.logic.Level;
import za.co.yahoo.timothyandroberta.numbersvsletters.logic.entities.*;
import za.co.yahoo.timothyandroberta.numbersvsletters.utils.MapBodyManager;

public class GamePlayScreen extends AbstractScreen implements ContactListener {

	// font and button
	private BitmapFont black;
	private TextureAtlas atlas;
	private Skin skin;
	private TextButtonStyle style;

	// camera
	private OrthographicCamera camera;
	private float aspectRatio;

	// map
	private OrthogonalTiledMapRenderer tileMapRenderer;
	private TiledMap map;
	private MapBodyManager mapCollisionLayer;

	// box2d
	private World world;
	private Box2DDebugRenderer debugRenderer;
	private Array<Body> tmpBodies = new Array<Body>();

	// game entities (these aspects must go into the Level class)
	// level
	public Level currentLevel;
	// players
	private Player player;
	// letters
	private String[] letterNames;
	public Vector2[] letterPositions;
	private Letter[] letters;
	// spikes
	private Spikes spike;
	// life
	private Life life;
	// door
	private Door door;
	// buttons
	private Button button;

	public GamePlayScreen(NumbersVsLetters game, String level) {
		super(game);

		this.currentLevel = new Level(level);

		setStyle();
	}

	private void setStyle() {
		this.atlas = new TextureAtlas("data/button.pack");
		this.skin = new Skin();
		this.skin.addRegions(atlas);
		this.black = new BitmapFont(Gdx.files.internal("data/font.fnt"), false);

		this.style = new TextButtonStyle();
		this.style.up = skin.getDrawable("buttonnormal");
		this.style.down = skin.getDrawable("buttonpressed");
		this.style.font = black;
	}

	@Override
	public void show() {
		super.show();

		TmxMapLoader mapLoader = new TmxMapLoader();
		map = mapLoader.load("map/" + currentLevel.level + ".tmx");

		camera = new OrthographicCamera();

		float unitScale = 1 / 512f;
		tileMapRenderer = new OrthogonalTiledMapRenderer(map, unitScale);

		world = new World(new Vector2(0, -9.81f), true);
		world.setContactListener(this);

		debugRenderer = new Box2DDebugRenderer();

		letterNames = new String[] { "a", "b", "c" };
		letters = new Letter[letterNames.length];
		letterPositions = new Vector2[letterNames.length];

		mapCollisionLayer = new MapBodyManager(world, this, unitScale, null, 0);
		mapCollisionLayer.createPhysics(map);

		player = new Player(world, "1", playerStartingPoint);

		for (int i = 0; i < letterNames.length; i++) {
			Letter l = new Letter(world, letterNames[i], letterPositions[i]);
			letters[i] = l;
		}

		life = new Life(world, lifeStartingPoint);

		spike = new Spikes(world, spikeStartingPoint);

		door = new Door(world, doorStartingPoint);
		//button = new Button(world, "a", buttonStartingPoint);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);

		float halfWidth = width / 2, halfHeight = height / 2;

		aspectRatio = (float) width / (float) height;
		camera.setToOrtho(false, (18 * aspectRatio), 15);
		camera.position.set(cameraStartingPoint.x, cameraStartingPoint.y, 0);

		stage.clear();
		Gdx.input.setInputProcessor(stage);

		GenericButton btnLeft = new GenericButton("L", style, 100, 0,
				halfWidth, halfHeight, new InputListener() {

					@Override
					public boolean touchDown(InputEvent event, float x,
							float y, int pointer, int button) {
						player.isFacingRight = false;
						player.movement.x = -player.speed;
						player.walkStateTime = 0;
						return true;
					}

					@Override
					public void touchUp(InputEvent event, float x, float y,
							int pointer, int button) {
						player.movement.x = 0;
					}

				});
		stage.addActor(btnLeft);

		GenericButton btnRight = new GenericButton("R", style, 350, 0,
				halfWidth, halfHeight, new InputListener() {

					@Override
					public boolean touchDown(InputEvent event, float x,
							float y, int pointer, int button) {
						player.isFacingRight = true;
						player.movement.x = player.speed;
						player.walkStateTime = 0;
						return true;
					}

					@Override
					public void touchUp(InputEvent event, float x, float y,
							int pointer, int button) {
						player.movement.x = 0;
					}

				});
		stage.addActor(btnRight);

		GenericButton btnJump = new GenericButton("J", style, width - 200,
				height - 200, halfWidth, halfHeight, new InputListener() {

					@Override
					public boolean touchDown(InputEvent event, float x,
							float y, int pointer, int button) {

						player.isJumpPrepping = true;

						return true;
					}

					@Override
					public void touchUp(InputEvent event, float x, float y,
							int pointer, int button) {

						player.isJumpPrepping = false;

						if (player.isAllowedToJump) {
							player.jump.y = player.jumpHeight;
						}
					}
				});
		stage.addActor(btnJump);

		GenericButton btnRestart = new GenericButton("Re", style, 100,
				height - 200, width / 2, height / 2, new InputListener() {

					@Override
					public boolean touchDown(InputEvent event, float x,
							float y, int pointer, int button) {
						return true;
					}

					@Override
					public void touchUp(InputEvent event, float x, float y,
							int pointer, int button) {
						restart();
					}
				});
		stage.addActor(btnRestart);
	}

	@Override
	public void render(float delta) {
		super.render(delta);

		world.step(1 / 60f, 8, 3);

		tileMapRenderer.render();
		camera.update();
		tileMapRenderer.setView(camera);

		if (camera.zoom > (1 / 7.5f)) {
			camera.zoom -= 0.01f;
			Vector2 v = new Vector2(player.position.x - camera.position.x,
					player.position.y - camera.position.y);
			float length = (float) Math.sqrt(v.x * v.x + v.y * v.y);
			Vector2 unit = new Vector2(v.x / length, v.y / length);
			camera.position.x += unit.x * 0.08f;
			camera.position.y += unit.y * 0.08f;
		} else {

			camera.position.set(player.body.getPosition().x,
					player.body.getPosition().y, 0);

			player.update(Gdx.graphics.getDeltaTime());

			if (player.isDead) {
				player.isDead = false;
				restart();
			}

			for (Letter l : letters) {
				if (!l.collected) {
					l.update(Gdx.graphics.getDeltaTime());
				}
			}

			if (!life.collected)
				life.update(Gdx.graphics.getDeltaTime());

			if (player.letterCount == letterNames.length && button == null)
				button = new Button(world, "a", buttonStartingPoint);

			if (button != null) {
				button.update(Gdx.graphics.getDeltaTime());
				if (button.pressed && door == null)
					door = new Door(world, doorStartingPoint);
			}

			batch.setProjectionMatrix(camera.combined);
			batch.begin();
			world.getBodies(tmpBodies);
			for (Body body : tmpBodies) {
				Object userData = body.getUserData();
				if (userData != null && userData instanceof Sprite) {
					Sprite s = (Sprite) body.getUserData();

					PolygonShape ps = (PolygonShape) body.getFixtureList()
							.first().getShape();

					Vector2 dimensions = getShapeDimensions(ps);

					s.setSize(dimensions.x, dimensions.y);
					s.setOrigin(s.getWidth() / 2, s.getHeight() / 2);
					s.setPosition(body.getPosition().x - (s.getWidth() / 2),
							body.getPosition().y - (s.getHeight() / 2));
					// s.setRotation(body.getAngle() *
					// MathUtils.radiansToDegrees);
					s.draw(batch);
				} else {
					for (Letter l : letters) {
						if (body == l.body) {
							world.destroyBody(body);
							body = null;
						}
					}

					if (body == life.body) {
						world.destroyBody(body);
						body = null;
					}
				}
			}
			batch.end();

			// debugRenderer.render(world, camera.combined);
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		tileMapRenderer.dispose();
	}

	private Vector2 getShapeDimensions(PolygonShape ps) {
		Vector2 v1 = new Vector2(), v2 = new Vector2(), v3 = new Vector2(), v4 = new Vector2();
		ps.getVertex(0, v1);
		ps.getVertex(1, v2);
		ps.getVertex(2, v3);
		ps.getVertex(3, v4);

		float w = v2.x - v1.x;
		float h = v3.y - v1.y;

		Vector2 dimensions = new Vector2(w, h);

		return dimensions;
	}

	Vector2 cameraStartingPoint, playerStartingPoint, buttonStartingPoint,
			lifeStartingPoint, doorStartingPoint, spikeStartingPoint;

	public void setCameraStartingPoint(Vector2 v) {
		cameraStartingPoint = v;
	}

	public void setStartingPoint(Vector2 v) {
		playerStartingPoint = v;
	}

	public void setLetterStartingPoint(Vector2 v, int index) {
		letterPositions[index] = v;
	}

	public void setButtonStartingPoint(Vector2 v) {
		buttonStartingPoint = v;
	}

	public void setLifeStartingPoint(Vector2 v) {
		lifeStartingPoint = v;
	}

	public void setDoorStartingPoint(Vector2 v) {
		doorStartingPoint = v;
	}

	public void setSpikeStartingPoint(Vector2 v) {
		spikeStartingPoint = v;
	}

	public void restart() {
		player.body.setTransform(playerStartingPoint, player.body.getAngle());
	}

	@Override
	public void beginContact(Contact contact) {
		Fixture a = contact.getFixtureA();
		Fixture b = contact.getFixtureB();

		for (Letter l : letters) {
			if (a == player.fixture && b == l.fixture) {
				player.collectLetter(l);
			}
		}

		if (button != null) {
			if (a == player.fixture && b == button.fixture) {
				// button.states = pressed;
				if (player.body.getPosition().y - button.position.y > .3f)
					button.pressed = true;
			}
		}

		if (a == player.fixture && b == spike.fixture) {
			player.isDead = true;
		}

		if (a == player.fixture && b == life.fixture) {
			life.collectLife();
		}

		if (door != null) {
			if (a == player.fixture && b == door.fixture) {
				game.progress++;
				game.setScreen(new LevelSelectScreen(game));
				//game.setScreen(new WinScreen(game));
			}
		}
	}

	@Override
	public void endContact(Contact contact) {
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
	}
}
