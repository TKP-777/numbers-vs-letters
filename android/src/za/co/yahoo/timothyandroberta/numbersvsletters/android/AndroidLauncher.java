package za.co.yahoo.timothyandroberta.numbersvsletters.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import za.co.yahoo.timothyandroberta.numbersvsletters.NumbersVsLetters;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new NumbersVsLetters(), config);
	}
}
