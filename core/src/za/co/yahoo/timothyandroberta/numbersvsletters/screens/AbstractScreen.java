package za.co.yahoo.timothyandroberta.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import za.co.yahoo.timothyandroberta.NumbersVsLetters;

public abstract class AbstractScreen implements Screen {
	protected final NumbersVsLetters game;
	protected final BitmapFont font;
	protected final SpriteBatch batch;
	protected final Stage stage;
	protected Viewport viewport = new ScreenViewport();

	public AbstractScreen(NumbersVsLetters game) {
		this.game = game;
		this.font = new BitmapFont();
		this.batch = new SpriteBatch();
		this.stage = new Stage(viewport);
	}

	protected String getName() {
		return getClass().getSimpleName();
	}

	// Screen implementation
	@Override
	public void show() {
		Gdx.app.log(NumbersVsLetters.LOG, "Showing screen: " + getName());
	}

	@Override
	public void resize(int width, int height) {
		Gdx.app.log(NumbersVsLetters.LOG, "Resizing screen: " + getName()
				+ " to: " + width + " x " + height);

		// resize the stage
		viewport.update(width, height, true);
		stage.setViewport(viewport);
	}

	@Override
	public void render(float delta) {
		// the following code clears the screen with the given RGB color (black)
		float color = .5f;
		Gdx.gl.glClearColor(color, color, color, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// update and draw the stage actors
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void hide() {
		Gdx.app.log(NumbersVsLetters.LOG, "Hiding screen: " + getName());
	}

	@Override
	public void pause() {
		Gdx.app.log(NumbersVsLetters.LOG, "Pausing screen: " + getName());
	}

	@Override
	public void resume() {
		Gdx.app.log(NumbersVsLetters.LOG, "Resuming screen: " + getName());
	}

	@Override
	public void dispose() {
		Gdx.app.log(NumbersVsLetters.LOG, "Disposing screen: " + getName());

		// dispose the collaborators
		stage.dispose();
		batch.dispose();
		font.dispose();
	}
}
