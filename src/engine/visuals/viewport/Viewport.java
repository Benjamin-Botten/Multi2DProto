package engine.visuals.viewport;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

import engine.visuals.Sprite;
import engine.visuals.SpriteSheet;
import engine.world.entity.Entity;
import engine.world.entity.Player;
import engine.world.tile.Tile;
import game.Game;

public class Viewport extends Canvas {
	public static final int BUFFER_DEPTH = 2;
	private static final long serialVersionUID = 1L;
	
	private int[] pixels;
	private final int w, h;
	private int scale;
	private JFrame frame;
	private String title = Game.TITLE;
	private BufferedImage raster;
	private BufferStrategy bs;
	private int camX, camY;
	
	public Viewport(int w, int h, int scale, String title) {
		this.w = w;
		this.h = h;
		this.scale = scale;
		this.title = title;
		
		raster = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		pixels = ((DataBufferInt) raster.getRaster().getDataBuffer()).getData();
		System.out.println(pixels.length);
		configureFrame(false, true);
	}
	
	private void configureFrame(boolean resizable, boolean visible) {
		setSize(w * scale, h * scale);
		frame = new JFrame(title);
        frame.add(this);
        frame.setResizable(resizable);
        frame.pack();
        frame.setVisible(visible);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
	}
	
	public void render(Tile tile, int xp, int yp) {
		int textureX = (tile.id % (256 / 8)) * 8; //bitshift 'ere
		int textureY = (tile.id / (256 / 8)) * 8; //bitshift 'ere
		int sheetWidth = SpriteSheet.tiles.w;
		
		if(xp < 0 || yp < 0 || xp >= w || yp >= h) return;
		for(int y = 0; y < 8; ++y) {
			for(int x = 0; x < 8; ++x) {
				pixels[(x + xp) + (y + yp) * w] = SpriteSheet.tiles.pixels[(x + textureX) + (y + textureY) * sheetWidth];
			}
		}
	}
	
	public void render(Sprite sprite, int xp, int yp) {
		int textureX = sprite.getCurrentColumnIndex() * (256 / 16); //bitshift 'ere
		int textureY = sprite.getCurrentRowIndex() * (256 / 16); //bitshift 'ere
		int sheetWidth = sprite.getSpriteSheet().w;
		
		for(int y = 0; y < 16; ++y) {
			if(y < 0 || y + yp >= h) continue;
			for(int x = 0; x < 16; ++x) {
				if(x < 0 || x + xp >= w) continue;
				int spriteColor = sprite.getSpriteSheet().pixels[(x + textureX) + (y + textureY) * sheetWidth];
				if(!(spriteColor == SpriteSheet.COLORKEY))
					pixels[(x + xp) + (y + yp) * w] = spriteColor;
			}
		}
	}
	
	public void render(Entity entity) {
		//Render any entity in here
		//Entities contain sprite object
		if(entity instanceof Player) {
			Player player = (Player) entity;
			player.getSprite().render(this, (int)player.x, (int)player.y);
		} else {
			int xp = (int) entity.x;
			int yp = (int) entity.y;
			if(xp < 0 || yp < 0 || xp >= w || yp >= h) return;
			for(int y = 0; y < 8; ++y) {
				for(int x = 0; x < 8; ++x) {
					setPixel(x + xp, y + yp, 0xff00ff00);
				}
			}
		}
	}
	
	public void renderRect(int xp, int yp) {
		for(int y = 0; y < 8; ++y) {
			for(int x = 0; x < 8; ++x) {
				setPixel(x + xp, y + yp, 0xff00ff00);
			}
		}
	}
	
	public void setCamera(int camX, int camY) {
		this.camX = camX;
		this.camY = camY;
	}
	
	public void setPixel(int x, int y, int color) {
		if(x < 0 || y < 0 || x >= w || y >= h) throw new IllegalArgumentException("Attempted to set pixel in negative space");
		pixels[x + y * w] = color;
	}
	
	public int getPixel(int x, int y) {
		if(x < 0 || y < 0 || x >= w || y >= h) throw new IllegalArgumentException("Attempted to get pixel in negative space");
		return pixels[x + y * w];
	}
	
	public void swap() {
		BufferStrategy bs = getBufferStrategy();
		if(bs == null) {
			createBufferStrategy(BUFFER_DEPTH);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		g.drawImage(raster, 0, 0, w * scale, h * scale, null);
		bs.show();
		g.dispose();
	}
	
	public void clear() {
		for(int i = 0; i < pixels.length; ++i) {
			pixels[i] = 0xff000000;
		}
	}
	
	public void clear(int color) {
		for(int i = 0; i < pixels.length; ++i) {
			pixels[i] = color;
		}
	}
}
