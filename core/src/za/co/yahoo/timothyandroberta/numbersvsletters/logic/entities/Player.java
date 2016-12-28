package za.co.yahoo.timothyandroberta.logic.entities;

import za.co.yahoo.timothyandroberta.utils.StopWatch;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Player {

	public World world;
	public String name;
	public Vector2 position;
	public Vector2 movement;
	public Vector2 jump;
	public float speed;
	public float jumpHeight;
	public float maxVelocityY;
	public float playerWidth = .15f, playerHeight = .35f;
	// animation
	public int walkFrameRows, walkFrameCols;
	public float walkStateTime, frameDuration;
	public Animation walkRightAnimation, walkLeftAnimation;
	private TextureRegion currentFrame;
	// box2D
	public Fixture fixture;
	public Body body;

	public Sprite playerSprite;
	private Sprite[] playerLeftSprites, playerRightSprites, playerSprites;

	public boolean isFacingRight, isJumpPrepping, isAllowedToJump, isDead;

	private StopWatch landingTime, bumpingTime;
	
	public Letter[] letters;
	public int letterCount = 0;

	public enum States {
		IDLE, WALKING, JUMPPREPING, DOUBLEJUMPPREPPING, JUMPING, DOUBLEJUMPING, FALLING, LANDING, BUMPING
	};

	public States state = States.FALLING;

	public Player(World world, String name, Vector2 position) {
		this.world = world;
		this.name = name;
		this.position = position;
		this.letters = new Letter[3];

		isFacingRight = true;
		isJumpPrepping = false;
		isAllowedToJump = false;

		landingTime = new StopWatch(0.1f);
		bumpingTime = new StopWatch(0.2f);

		movement = new Vector2();
		speed = 2.5f;
		jump = new Vector2();
		jumpHeight = 50;
		maxVelocityY = 4.7f;

		setUpBox2D();
		setUpSprites();
		setUpAnimations();
	}

	public void setUpBox2D() {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(position.x, position.y);
		bodyDef.fixedRotation = true;

		PolygonShape playerShape = new PolygonShape();
		playerShape.setAsBox(playerWidth, playerHeight);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = playerShape;
		fixtureDef.density = 5f;
		fixtureDef.friction = 0f;
		fixtureDef.restitution = 0f;

		body = world.createBody(bodyDef);
		fixture = body.createFixture(fixtureDef);

		playerShape.dispose();
	}

	public void setUpSprites() {
		int noOfSprites = 6;
		playerLeftSprites = new Sprite[noOfSprites];
		playerRightSprites = new Sprite[noOfSprites];
		playerSprites = new Sprite[noOfSprites];

		String[] spriteNames = new String[] { "looking_", "jump_prep_",
				"jumping_", "falling_", "landing_", "bumping_top_" };

		for (int i = 0; i < noOfSprites; i++) {
			Texture t = new Texture(Gdx.files.internal("sprites/"
					+ spriteNames[i] + "right.png"));
			Sprite s = new Sprite(t);
			playerRightSprites[i] = s;

			t = new Texture(Gdx.files.internal("sprites/" + spriteNames[i]
					+ "left.png"));
			s = new Sprite(t);
			playerLeftSprites[i] = s;
		}
	}

	public void setUpAnimations() {
		walkFrameCols = 4;
		walkFrameRows = 2;
		frameDuration = 0.1f;

		// walk animation
		// right
		walkRightAnimation = getAnimation("animations/walk_right.png",
				walkFrameCols, walkFrameRows, frameDuration);

		// left
		walkLeftAnimation = getAnimation("animations/walk_left.png",
				walkFrameCols, walkFrameRows, frameDuration);
	}

	public Animation getAnimation(String path, int frameCols, int frameRows,
			float frameDuration) {
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
		body.setLinearVelocity(new Vector2(movement.x,
				body.getLinearVelocity().y));
		body.applyLinearImpulse(jump, body.getPosition(), true);

		if (jump.y > 0) {
			jump.y = 0;
		}

		if (body.getLinearVelocity().y > maxVelocityY) {
			body.setLinearVelocity(new Vector2(body.getLinearVelocity().x, Math
					.signum(body.getLinearVelocity().y) * maxVelocityY));
		}

		if (isFacingRight)
			playerSprites = playerRightSprites;
		else
			playerSprites = playerLeftSprites;

		int playerSpriteNumber = -1;

		switch (state) {
		case IDLE:
			// graphics
			playerSpriteNumber = 0;

			// logic
			if (body.getLinearVelocity().x != 0) {
				state = States.WALKING;
			}

			if (isJumpPrepping) {
				state = States.JUMPPREPING;
			}
			break;
		case WALKING:
			// graphics
			if (isFacingRight) {
				walkStateTime += Gdx.graphics.getDeltaTime();
				currentFrame = walkRightAnimation.getKeyFrame(walkStateTime,
						true);
				playerSprite = new Sprite(currentFrame);
			} else {
				walkStateTime += Gdx.graphics.getDeltaTime();
				currentFrame = walkLeftAnimation.getKeyFrame(walkStateTime,
						true);
				playerSprite = new Sprite(currentFrame);
			}

			// logic
			if (body.getLinearVelocity().x == 0) {
				state = States.IDLE;
			} else if (body.getLinearVelocity().y > 0) {
				state = States.JUMPING;
			} else if (body.getLinearVelocity().y < 0) {
				isAllowedToJump = false;

				state = States.FALLING;
			}
			break;
		case JUMPPREPING:
			// graphics
			playerSpriteNumber = 1;

			// logic
			if (body.getLinearVelocity().x != 0) {
				state = States.WALKING;
			} else if (!isJumpPrepping) {
				state = States.JUMPING;
			}
			break;
		case DOUBLEJUMPPREPPING:
			// graphics
			playerSpriteNumber = 1;

			// logic
			if (body.getLinearVelocity().y > 0) {
				if (!isJumpPrepping) {
					state = States.DOUBLEJUMPING;
				}
			} else {
				isAllowedToJump = false;

				state = States.FALLING;
			}
			break;
		case JUMPING:
			// graphics
			playerSpriteNumber = 2;

			// logic
			if (body.getLinearVelocity().y > -0.001 && body.getLinearVelocity().y < 0.001) {
				isAllowedToJump = false;
				
				state = States.BUMPING;
			} else if (body.getLinearVelocity().y < 0) {
				isAllowedToJump = false;

				state = States.FALLING;
			} else {
				if (isJumpPrepping) {
					state = States.DOUBLEJUMPPREPPING;
				}
			}
			break;
		case DOUBLEJUMPING:
			// graphics
			playerSpriteNumber = 2;

			// logic
			isAllowedToJump = false;

			if (body.getLinearVelocity().y > -0.001 && body.getLinearVelocity().y < 0.001) {
				state = States.BUMPING;
			} else if (body.getLinearVelocity().y < 0) {
				state = States.FALLING;
			}
			break;
		case FALLING:
			// graphics
			playerSpriteNumber = 3;

			// logic
			isAllowedToJump = false;

			if (body.getLinearVelocity().y == 0) {
				state = States.LANDING;
			}
			break;
		case LANDING:
			// graphics
			playerSpriteNumber = 4;

			// logic
			isAllowedToJump = true;

			landingTime.update(deltaTime);
			if (landingTime.isFinished()) {
				landingTime.reset();
				state = States.IDLE;
			}
			break;
		case BUMPING:
			// graphics
			playerSpriteNumber = 5;

			// logic
			bumpingTime.update(deltaTime);
			if (bumpingTime.isFinished()) {
				bumpingTime.reset();
				state = States.FALLING;
			}
			break;
		}

		if (playerSpriteNumber != -1) {
			playerSprite = playerSprites[playerSpriteNumber];
		}

		body.setUserData(playerSprite);
	}
	
	public void collectLetter(Letter l) {
		letters[letterCount] = l;
		letterCount++;
		l.collected = true;
		l.body.setUserData(null);
	}
}
