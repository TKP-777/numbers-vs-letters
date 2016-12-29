package za.co.yahoo.timothyandroberta.numbersvsletters.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import za.co.yahoo.timothyandroberta.numbersvsletters.NumbersVsLetters;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(480, 320);
        }

        @Override
        public ApplicationListener getApplicationListener () {
                return new NumbersVsLetters();
        }
}