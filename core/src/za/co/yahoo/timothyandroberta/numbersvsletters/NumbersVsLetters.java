package za.co.yahoo.timothyandroberta;

import za.co.yahoo.timothyandroberta.screens.MainMenuScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class NumbersVsLetters extends Game {
	public static final String LOG = "NUMBERS.VS.LETTERS";
	public static final String ERROR_LOG = "NVL.ERROR";
	public int progress;

	@Override
	public void create() {
		Gdx.app.log(NumbersVsLetters.LOG, "Starting game...");
		progress = 1;
		setScreen(new MainMenuScreen(this));
	}

	@Override
	public void dispose() {
		Gdx.app.log(NumbersVsLetters.LOG, "Shutting down game...");
		super.dispose();
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		Gdx.app.log(NumbersVsLetters.LOG, "Resizing window...");
		super.resize(width, height);
	}

	@Override
	public void pause() {
		Gdx.app.log(NumbersVsLetters.LOG, "Game paused...");
		super.pause();
	}

	@Override
	public void resume() {
		Gdx.app.log(NumbersVsLetters.LOG, "Game resumed...");
		super.resume();
	}
}