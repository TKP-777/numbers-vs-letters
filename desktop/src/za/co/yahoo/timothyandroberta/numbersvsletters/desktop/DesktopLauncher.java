package za.co.yahoo.timothyandroberta.numbersvsletters.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import za.co.yahoo.timothyandroberta.numbersvsletters.NumbersVsLetters;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new NumbersVsLetters(), config);
	}
}
