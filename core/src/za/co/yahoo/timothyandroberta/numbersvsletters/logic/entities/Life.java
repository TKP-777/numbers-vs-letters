package za.co.yahoo.timothyandroberta.logic.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Life {
	
	public World world;
	public boolean collected;
	// animation
	public int frameRows, frameCols;
	public float stateTime, frameDuration;
	public Animation animation;
	public Vector2 position;
	// box2D
	public Fixture fixture;
	public Body body;
	
	public Life(World world, Vector2 position) {
		this.world = world;
		this.position = position;
		this.collected = false;
		
		setUpAnimation();
		
		setUpBox2D();
	}
	
	public void setUpAnimation() {
		frameRows = frameCols = 2;
		frameDuration = 0.2f;
		animation = getAnimation("animations/life.png");
		stateTime = 0;	
	}
	
	public void setUpBox2D() {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set(position.x, position.y);
		bodyDef.fixedRotation = true;
		
		PolygonShape lifeShape = new PolygonShape();
		lifeShape.setAsBox(.25f, .25f);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = lifeShape;
		fixtureDef.density = 1f;
		fixtureDef.friction = 0f;
		fixtureDef.restitution = 0f;
		fixtureDef.isSensor = true;
		
		body = world.createBody(bodyDef);
		fixture = body.createFixture(fixtureDef);

		lifeShape.dispose();
	}
	
	public Animation getAnimation(String path) {
		Animation animation;

		Texture sheet = new Texture(Gdx.files.internal(path));
		TextureRegion[][] tmp = TextureRegion.split(sheet, sheet.getWidth()
				/ frameCols, sheet.getHeight() / frameRows);
		TextureRegion[] frames = new TextureRegion[frameCols * frameRows];
		int index = 0;
		for (int i = 0; i < frameRows; i++) {
			for (int j = 0; j < frameCols; j++) {
				frames[index++] = tmp[i][j];
			}
		}
		animation = new Animation(frameDuration, frames);

		return animation;
	}
	
	public void collectLife() {
		body.setUserData(null);
		collected = true;
	}
	
	public void update(float deltaTime) {
		stateTime += deltaTime;
		TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
		Sprite lifeSprite = new Sprite(currentFrame);
		body.setUserData(lifeSprite);
	}

}
