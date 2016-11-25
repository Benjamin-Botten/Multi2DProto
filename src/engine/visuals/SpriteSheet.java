package engine.visuals;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class SpriteSheet {
	public static final int COLORKEY = 0xffff00ff;
	public static final int DEFAULT_ELEMENT_WIDTH = 8;
	public static final int DEFAULT_ELEMENT_HEIGHT = 8;
	
	
	public static final SpriteSheet entities = loadSpriteSheet("/resrc/art/entitysprites.png", 16, 16);
	public static final SpriteSheet items = loadSpriteSheet("/resrc/art/itemsprites.png", 8, 8);
	public static final SpriteSheet tiles = loadSpriteSheet("/resrc/art/tilesprites.png", 8, 8);
	public static final SpriteSheet font = loadSpriteSheet("/resrc/art/font_general.png", 8, 8);
	public static final SpriteSheet gui = loadSpriteSheet("/resrc/art/guisprites.png", 8, 8);
	public static final SpriteSheet itementities = loadSpriteSheet("/resrc/art/itementitysprites.png", 8, 8);
	
	public final int w, h;
	public final int[] pixels;
	public final int ew, eh; //width and height per element in the spritesheet
	
	public SpriteSheet(int[] pixels, int w, int h) {
		this.pixels = pixels;
		this.w = w;
		this.h = h;
		ew = DEFAULT_ELEMENT_WIDTH;
		eh = DEFAULT_ELEMENT_HEIGHT;
	}
	
	public SpriteSheet(int[] pixels, int w, int h, int ew, int eh) {
		this.pixels = pixels;
		this.w = w;
		this.h = h;
		this.ew = ew;
		this.eh = eh;
	}
	
	
	public static SpriteSheet loadSpriteSheet(String filename, int ew, int eh) {
		SpriteSheet result = null;
		try {
			BufferedImage img = ImageIO.read(SpriteSheet.class.getResourceAsStream(filename));
			int w = img.getWidth();
			int h = img.getHeight();
			result = new SpriteSheet(img.getRGB(0, 0, w, h, null, 0, w), w, h, ew, eh);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public int getPixel(int x, int y) {
		if(x < 0 || y < 0 || x >= w || y >= h) throw new IllegalArgumentException("Attempted to get pixel from spritesheet out of bounds");
		return pixels[x + y * w];
	}
}
