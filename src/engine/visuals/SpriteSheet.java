package engine.visuals;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class SpriteSheet {
	public static final int COLORKEY = 0xffff00ff;
	public static final int DEFAULT_ELEMENT_WIDTH = 8;
	public static final int DEFAULT_ELEMENT_HEIGHT = 8;
	public static final int DEFAULT_NUM_SPRITES_WIDTH = 16;
	public static final int DEFAULT_NUM_SPRITES_HEIGHT = 16;
	
	
	public static final SpriteSheet entities = loadSpriteSheet("/resrc/art/entitysprites.png", 16, 16);
	public static final SpriteSheet items = loadSpriteSheet("/resrc/art/itemsprites.png", 8, 8);
	public static final SpriteSheet tiles = loadSpriteSheet("/resrc/art/tilesprites.png", 8, 8);
	public static final SpriteSheet font = loadSpriteSheet("/resrc/art/font_general.png", 8, 8);
	public static final SpriteSheet gui = loadSpriteSheet("/resrc/art/guisprites.png", 8, 8);
	public static final SpriteSheet itementities = loadSpriteSheet("/resrc/art/itementitysprites.png", 8, 8);
//	public static final SpriteSheet arrow = renderSpriteRotationsToFile(SpriteSheet.itementities, 6, "C:/Users/robot/experiments/Multi2DProto/src/resrc/art/arrow.png");
	public static final SpriteSheet frostbite = renderSpriteRotationsToFile(SpriteSheet.itementities, 8, "C:/Users/robot/experiments/Multi2DProto/src/resrc/art/frostbite.png");
	public static final SpriteSheet smoothLightMap3x3 = renderLightMap("C:/Users/robot/experiments/Multi2DProto/src/resrc/art/lightmap4x4.png", 3, 16);
	
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
	
	public static SpriteSheet renderLightMap(String filename, int r, int tileSize) {

		BufferedImage bufferedImage = new BufferedImage(r * tileSize * 2, r * tileSize * 2, BufferedImage.TYPE_INT_ARGB);
		
		int imageWidth = r * tileSize * 2;
		int xCenter = imageWidth >> 1;
		int yCenter = xCenter;
		
		for(int y = 0; y < r * tileSize * 2; ++y) {
			for(int x = 0; x < r * tileSize * 2; ++x) {
				
				int dx = x - xCenter;
				int dy = y - yCenter;
				
				dx >>= 1;
				dy >>= 1;
				
				double mag = Math.sqrt(dx * dx + dy * dy);
//				double k0 = 1;
//				double k1 = 0.30;
//				double k2 = 0.05;
				
				double k0 = 0.89;
				double k1 = 0.25;
				double k2 = 0.035;
				
//				double intensityLight = 1.d / (mag * mag);
				
				double intensityLight = 3.d / (k0 + (mag * k1) + (mag * mag * k2));
				int color = (int) (255.d * intensityLight);
				bufferedImage.setRGB(x, y, Color.getColor(255, color, color, color));
				
			}
		}
		
		File f = new File(filename);
		try {
			f.createNewFile();
			ImageIO.write(bufferedImage, "PNG", f);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new SpriteSheet(bufferedImage.getRGB(0, 0, imageWidth, imageWidth, null, 0, imageWidth), imageWidth, imageWidth);
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
	
	public static SpriteSheet renderSpriteRotationsToFile(SpriteSheet spritesheet, int tileId, String filename) {
		//Dimensions
		int spriteWidth = spritesheet.ew;
		int spriteHeight = spritesheet.eh;
		
		int imageWidth = spritesheet.w;
		int imageHeight = spritesheet.h;
		
		int spriteCenterX = spriteWidth / 2;
		int spriteCenterY = spriteHeight / 2;
		
		int tileX = (tileId % imageWidth) * spriteWidth;
		
		double angle = 0;
		double radians = Math.toRadians(angle);
		
		//Open new image
		BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
		BufferedImage spriteImg = new BufferedImage(spriteWidth, spriteHeight, BufferedImage.TYPE_INT_ARGB);
		for(int y = 0; y < imageHeight; y += spriteHeight) {
			if(angle > 360) break;
			for(int x = 0; x < imageWidth; x += spriteWidth) {
				
				if(angle > 360) break;
				
				radians = Math.toRadians(angle);
				
				for(int sy = 0; sy < spriteHeight; ++sy) {
					for(int sx = 0; sx < spriteWidth; ++sx) {
						
						double cx = sx - 4.0;
						double cy = sy - 4.0;
						double nx = (cx * Math.cos(radians) - cy * Math.sin(radians));
						double ny = (cy * Math.cos(radians) + cx * Math.sin(radians));
						nx += 4.0;
						ny += 4.0;
						int currentPixel = spritesheet.getPixel(sx + tileX, sy);
						//Calculate rotations
						//img.setRGB(x + sx, y + sy, currentPixel);
						spriteImg.setRGB(sx, sy, 0xffff00ff);
						int xp = (int) Math.floor((0 + nx) + 0.5d);
						int yp = (int) Math.floor((0 + ny) - 0.5d);
						if(xp >= 0 && xp < spriteWidth && yp >= 0 && yp < spriteHeight) {
							spriteImg.setRGB((int) (xp), (int) (yp), currentPixel);
						}
					}
				}
				
				for(int sy = 0; sy < spriteHeight; ++sy) {
					for(int sx = 0; sx < spriteWidth; ++sx) {
						//Calculate rotations
						img.setRGB(x + sx, y + sy, spriteImg.getRGB(sx, sy));
					}
				}
				
				angle++;
				
			}
		}
		
		File imageFile = new File(filename);
		try {
			ImageIO.write(img, "PNG", imageFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new SpriteSheet(img.getRGB(0, 0, imageWidth, imageHeight, null, 0, imageWidth), imageWidth, imageHeight, spriteWidth, spriteHeight);
	}
	
	public int getPixel(int x, int y) {
		if(x < 0 || y < 0 || x >= w || y >= h) throw new IllegalArgumentException("Attempted to get pixel from spritesheet out of bounds");
		return pixels[x + y * w];
	}
}
