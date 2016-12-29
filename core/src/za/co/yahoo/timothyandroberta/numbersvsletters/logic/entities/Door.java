package za.co.yahoo.timothyandroberta.numbersvsletters.logic.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Door {
	
	public World world;
	public Vector2 position;
	// box2D
	public Fixture fixture;
	public Body body;
	// sprite
	public Sprite doorSprite;
	
	public Door(World world, Vector2 position) {
		this.world = world;
		this.position = position;
		this.doorSprite = new Sprite(new Texture(Gdx.files.internal("sprites/door.png")));
		
		setUpBox2D();
		
		body.setUserData(doorSprite);
	}
	
	public void setUpBox2D() {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set(position.x, position.y);
		bodyDef.fixedRotation = true;
		
		PolygonShape doorShape = new PolygonShape();
		doorShape.setAsBox(.25f, .5f);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = doorShape;
		fixtureDef.density = 1f;
		fixtureDef.friction = 0f;
		fixtureDef.restitution = 0f;
		
		body = world.createBody(bodyDef);
		fixture = body.createFixture(fixtureDef);

		doorShape.dispose();
	}

}
