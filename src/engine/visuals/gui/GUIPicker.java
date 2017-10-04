package engine.visuals.gui;

import engine.io.Input;
import engine.util.EntityRegistry;
import engine.util.IDRegistry;
import engine.util.Rect2D;
import engine.util.TileRegistry;
import engine.visuals.SpriteSheet;
import engine.visuals.viewport.Viewport;

public class GUIPicker<T extends IDRegistry> extends GUIElement {

	protected int selection; //Id of selection
	protected boolean selected;
	protected GUITextField idInput;
	protected GUITextField nameInput;
	protected GUIButton buttonSelect;
	protected GUIButton buttonReset;
	protected SpriteSheet sheet;
	protected T type;
	
	public GUIPicker(SpriteSheet sheet, T t, int x, int y, int w, int h) {
		if(sheet == null) {
			throw new IllegalArgumentException("Attempted creating GUIPicker with null spritesheet");
		}
		
		this.sheet = sheet;
		
		buttonSelect = new GUIButton("Select", x, y + (int) (h * 0.75), "Select".length() * 8, 8, 2, 0xff000000, 0xffffffff);
		buttonReset = new GUIButton("Reset", x + "Select".length() * 8 * 2 + 4, y + (int) (h * 0.75), "Reset".length() * 8, 8, 2, 0xff000000, 0xffffffff);
		idInput = new GUITextField("Input ID", x, y + (int) (h * 0.25f), 2);
		nameInput = new GUITextField("Input Name", x, y + (int) (h * 0.5f), 2);
		
		bounds = new Rect2D(x, y, w, h);
		
		type = t;
	}
	
	public void tick(Input input) {
		idInput.tick(input);
		nameInput.tick(input);
		buttonSelect.tick(input);
		buttonReset.tick(input);
		
		if(buttonSelect.pressed()) {
			System.out.println("Pressed button select");
			selected = true;
			
			if(type instanceof EntityRegistry) {
				System.out.println("Setting name shit");
				int id = Integer.parseInt(idInput.getText());
				nameInput.setText(type.getName(id));
			}
			if(type instanceof TileRegistry) {
				System.out.println("Setting name shit");
				int id = Integer.parseInt(idInput.getText());
				nameInput.setText(type.getName(id));
			}
		}
		if(buttonReset.pressed()) {
			selected = false;
		}
	}
	
	public void render(Viewport viewport) {
		idInput.render(viewport);
		nameInput.render(viewport);
		buttonSelect.render(viewport);
		buttonReset.render(viewport);
		viewport.renderBoundsScreenSpace((int) bounds.x, (int) bounds.y, bounds.getWidth(), bounds.getHeight());
	}
	
	public IDRegistry getType() {
		return type;
	}

}
