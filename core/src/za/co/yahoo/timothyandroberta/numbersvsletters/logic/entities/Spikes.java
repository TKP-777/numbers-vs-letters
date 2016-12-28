package za.co.yahoo.timothyandroberta.logic.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Spikes {
	
	public World world;
	public Vector2 position;
	// box2D
	public Fixture fixture;
	public Body body;
	// sprite
	public Sprite spikeSprite;
	
	public Spikes(World world, Vector2 position) {
		this.world = world;
		this.position = position;
		this.spikeSprite = new Sprite(new Texture(Gdx.files.internal("sprites/spikes.png")));
		
		setUpBox2D();
		
		body.setUserData(spikeSprite);
	}
	
	public void setUpBox2D() {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set(position.x, position.y);
		bodyDef.fixedRotation = true;
		
		PolygonShape spikeShape = new PolygonShape();
		spikeShape.setAsBox(0.5f, 0.25f);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = spikeShape;
		fixtureDef.density = 1f;
		fixtureDef.friction = 0f;
		fixtureDef.restitution = 0f;
		
		body = world.createBody(bodyDef);
		fixture = body.createFixture(fixtureDef);

		spikeShape.dispose();
	}
}
