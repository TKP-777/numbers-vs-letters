package za.co.yahoo.timothyandroberta.logic;

import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class GenericButton extends TextButton {
	
	private float x, y, w, h;
	private InputListener listener;
	
	
	public GenericButton(String text, TextButtonStyle style, float x, float y, float w, float h, InputListener listener) {
		super(text, style);
		
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.listener = listener;		
		
		setupButton();
	}

	private void setupButton() {
		setWidth(w / 4);
		setHeight(h / 4);
		setX(w / 2 - (getWidth() / 2));
		setX(x - (getWidth() / 2));
		setY(y);
		
		addListener(listener);
	}
	
	

}
