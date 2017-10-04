package engine.visuals.gui;

import engine.io.Input;
import engine.visuals.Text;
import engine.visuals.viewport.Viewport;

public class GUIButton extends GUIElement {
	
	private final Text text;
	private final int textColor;
	private final int buttonColor;
	private final int w, h;
	private final int x, y;
	
	private boolean pressed = false;
	
	public GUIButton(String text, int x, int y, int w, int h, int textColor, int buttonColor) {
		this.x = x;
		this.y = y;

		this.w = w;
		this.h = h;
		
		this.textColor = textColor;
		this.buttonColor = buttonColor;
		
		this.text = new Text(text, x, y);
		this.text.setColor(textColor);
		this.text.setHasBackground(true);
		this.text.setColorBackground(buttonColor);
		
	}
	
	public GUIButton(String text, int x, int y, int w, int h, int size, int textColor, int buttonColor) {
		this.x = x;
		this.y = y;
		
		this.w = w * size;
		this.h = h * size;
		
		this.textColor = textColor;
		this.buttonColor = buttonColor;
		
		this.text = new Text(text, x, y);
		this.text.setSize(size);
		this.text.setColor(textColor);
		this.text.setHasBackground(true);
		this.text.setColorBackground(buttonColor);
		
	}
	
	public void render(Viewport viewport) {
		viewport.renderTextScreenSpace(text);
		viewport.renderBounds(x, y, w, h);
	}
	
	public void tick(Input input) {
		if(input.mouseLeft()) {
			
			int mx = input.getMouseX();
			int my = input.getMouseY();
			int textCenterX = ((text.length() * 8) >> 1);
			if(mx > (x - textCenterX) && mx < (x + w - textCenterX) && my > y && my < (y + h)) {
				pressed = true;
			} else {
				pressed = false;
			}
		}
	}
	
	public boolean pressed() {
		return pressed;
	}
}
