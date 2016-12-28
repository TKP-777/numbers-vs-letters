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

public class Button {

	public World world;
	public String name;
	public boolean type, pressed; // true = stay-in, false = pop-back
	// animation
	public int frameRows, frameCols;
	public float stateTime, frameDuration;
	public Animation animation;
	public Vector2 position;
	// box2D
	public Fixture fixture;
	public Body body;
	// sprite
	public Sprite buttonSprite, buttonDepressedSprite;

	public Button(World world, String name, Vector2 position) {
		this.world = world;
		this.name = name;
		this.position = new Vector2(position.x, position.y - .5f + .1f);
		this.buttonSprite = new Sprite(new Texture(
				Gdx.files.internal("sprites/button_a.png")));
		this.buttonDepressedSprite = new Sprite(new Texture(
				Gdx.files.internal("sprites/button_a_depressed.png")));
		setUpAnimation();

		setUpBox2D();

		body.setUserData(buttonSprite);
	}

	public void setUpAnimation() {
		frameRows = 2;
		frameCols = 3;
		frameDuration = 0.1f;
		animation = getAnimation("animations/button_" + name + ".png");
		stateTime = 0;
	}

	public void setUpBox2D() {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set(position.x, position.y);
		bodyDef.fixedRotation = true;

		PolygonShape letterShape = new PolygonShape();
		letterShape.setAsBox(.125f, .1f);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = letterShape;
		fixtureDef.density = 1f;
		fixtureDef.friction = 0f;
		fixtureDef.restitution = 0f;
		fixtureDef.isSensor = true;

		body = world.createBody(bodyDef);
		fixture = body.createFixture(fixtureDef);

		letterShape.dispose();
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

	public void update(float deltaTime) {
		if (pressed) {
			PolygonShape ps = (PolygonShape) body.getFixtureList()
					.first().getShape();
			ps.setAsBox(.125f, .025f);
			body.setTransform(new Vector2(position.x, position.y - .1f + .025f), body.getAngle());
			body.setUserData(buttonDepressedSprite);
		}
		// stateTime += deltaTime;
		// TextureRegion currentFrame = animation.getKeyFrame(stateTime, false);
		// Sprite letterSprite = new Sprite(currentFrame);
		// body.setUserData(letterSprite);
	}

}
