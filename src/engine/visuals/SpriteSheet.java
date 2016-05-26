package engine.visuals;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class SpriteSheet {
	public static final int COLORKEY = 0xffff00ff;
	
	public static final SpriteSheet entities = loadSpriteSheet("/resrc/art/entitysprites.png");
	public static final SpriteSheet items = loadSpriteSheet("/resrc/art/itemsprites.png");
	public static final SpriteSheet tiles = loadSpriteSheet("/resrc/art/tilesprites.png");
	
	public final int w, h;
	public final int[] pixels;
	
	public SpriteSheet(int[] pixels, int w, int h) {
		this.pixels = pixels;
		this.w = w;
		this.h = h;
	}
	
	
	public static SpriteSheet loadSpriteSheet(String filename) {
		SpriteSheet result = null;
		try {
			BufferedImage img = ImageIO.read(SpriteSheet.class.getResourceAsStream(filename));
			int w = img.getWidth();
			int h = img.getHeight();
			result = new SpriteSheet(img.getRGB(0, 0, w, h, null, 0, w), w, h);
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
