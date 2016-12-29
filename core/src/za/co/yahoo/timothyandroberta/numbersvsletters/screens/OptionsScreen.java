package za.co.yahoo.timothyandroberta.numbersvsletters.screens;

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
import za.co.yahoo.timothyandroberta.numbersvsletters.NumbersVsLetters;

public class OptionsScreen extends AbstractScreen {

	NumbersVsLetters game;
	Texture texture;
	BitmapFont black;
	TextureAtlas atlas;
	Skin skin;
	TextButton button;

	public OptionsScreen(NumbersVsLetters game) {
		super(game);
		
		this.atlas = new TextureAtlas("data/button.pack");
		this.skin = new Skin();
		this.skin.addRegions(atlas);
		this.black = new BitmapFont(Gdx.files.internal("data/font.fnt"), false);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);

		stage.clear();
		Gdx.input.setInputProcessor(stage);

		addImage(width, height);
	}

	private void addImage(int width, int height) {
		texture = new Texture(Gdx.files.internal("data/background-with-numbers.jpg"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		Image image = new Image(texture);
		image.setBounds(0, 0, width, height);

		stage.addActor(image);
	}

	private void addButton(String text, float y, int width, int height) {
		TextButtonStyle style = new TextButtonStyle();
		style.up = skin.getDrawable("buttonnormal");
		style.down = skin.getDrawable("buttonpressed");
		style.font = black;

		button = new TextButton(text, style);
		button.setWidth(width / 4);
		button.setHeight(height / 4);
		button.setX(width / 2 - (button.getWidth() / 2));
		button.setY(y);

		button.addListener(new InputListener() {

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
			}
		});
		
		stage.addActor(button);
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

