package engine.visuals.gui;

import engine.io.Input;
import engine.util.Rect2D;
import engine.visuals.Color;
import engine.visuals.SpriteSheet;
import engine.visuals.Text;
import engine.visuals.viewport.Viewport;

public class GUITextField extends GUIElement {

	private static final int DEFAULT_LIMIT_CHARS = 50;
	
	private int limit = DEFAULT_LIMIT_CHARS;
	private int x, y;
	
	private Text text;
	
	public GUITextField(String placeholder, int x, int y) {
		this.x = x;
		this.y = y;

		text = new Text(placeholder, x, y);
		text.setHasBackground(true);
		text.setColorBackground(0xffffffff);
		bounds = new Rect2D(x, y, text.getSize() * SpriteSheet.font.ew * limit, text.getHeight());
	}
	
	public GUITextField(String placeholder, int x, int y, int size) {
		this.x = x;
		this.y = y;

		text = new Text(placeholder, x, y, size);
		text.setHasBackground(true);
		text.setColorBackground(0xffffffff);
		bounds = new Rect2D(x, y, text.getSize() * SpriteSheet.font.ew * limit, text.getHeight());
	}
	
	public GUITextField(String placeholder, int x, int y, int size, int limit) {
		this.x = x;
		this.y = y;
		this.limit = limit;

		text = new Text(placeholder, x, y);
	}
	
	public GUITextField(Text text, int limit) {
		this.x = text.getX();
		this.y = text.getY();
		this.text = text;
		this.limit = limit;
	}
	
	public void tick(Input input) {
		if(input.mouseLeft()) {
			int mx = input.getMouseX();
			int my = input.getMouseY();
			
			if(bounds.isColliding(mx, my, 1, 1)) {
				focused = true;
			} else {
				focused = false;
			}
		}
		
		if(focused) {
			String tmp = input.pollTypedChar();
			text.add(tmp);
		}
	}
	
	public void render(Viewport viewport) {
		viewport.renderTextScreenSpace(text);
		viewport.renderBoundsScreenSpace((int)bounds.x, (int)bounds.y, bounds.getWidth(), bounds.getHeight());
	}
	
	public String getText() {
		return text.getText();
	}
	
	public String toString() {
		return text.getText();
	}

	public void setText(String string) {
		text.setText(string);
	}
}
