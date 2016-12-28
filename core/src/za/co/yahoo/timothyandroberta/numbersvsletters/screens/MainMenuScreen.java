package za.co.yahoo.timothyandroberta.screens;

import za.co.yahoo.timothyandroberta.NumbersVsLetters;
import za.co.yahoo.timothyandroberta.NumbersVsLetters;
import za.co.yahoo.timothyandroberta.logic.GenericButton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;

public class MainMenuScreen extends AbstractScreen {

	private Texture texture;
	private BitmapFont black;
	private TextureAtlas atlas;
	private Skin skin;
	private TextButtonStyle style;

	public MainMenuScreen(NumbersVsLetters game) {
		super(game);

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
	public void resize(int width, int height) {
		super.resize(width, height);

		stage.clear();
		Gdx.input.setInputProcessor(stage);

		addImage(width, height);

		float x = width / 2;
		float y = height / 2;

		GenericButton btnNewGame = new GenericButton("New Game", style, x, y,
				width, height, new InputListener() {
					@Override
					public boolean touchDown(InputEvent event, float x,
							float y, int pointer, int button) {
						return true;
					}

					@Override
					public void touchUp(InputEvent event, float x, float y,
							int pointer, int button) {
						game.setScreen(new LevelSelectScreen(game));
					}
				});
		stage.addActor(btnNewGame);

		GenericButton btnOptions = new GenericButton("Options", style, x, y - 300,
				width, height, new InputListener() {
					@Override
					public boolean touchDown(InputEvent event, float x,
							float y, int pointer, int button) {
						return true;
					}

					@Override
					public void touchUp(InputEvent event, float x, float y,
							int pointer, int button) {
						game.setScreen(new OptionsScreen(game));
					}
				});
		stage.addActor(btnOptions);
	}

	private void addImage(int width, int height) {
		texture = new Texture(
				Gdx.files.internal("data/background-with-numbers.jpg"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		Image image = new Image(texture);
		image.setBounds(0, 0, width, height);

		stage.addActor(image);
	}

	@Override
	public void dispose() {
		super.dispose();
		texture.dispose();
		skin.dispose();
		atlas.dispose();
		black.dispose();
	}
}
