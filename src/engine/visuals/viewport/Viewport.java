package engine.visuals.viewport;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

import engine.visuals.Sprite;
import engine.visuals.SpriteSheet;
import engine.world.World;
import engine.world.entity.Entity;
import engine.world.entity.Inventory;
import engine.world.entity.ItemEntity;
import engine.world.entity.Player;
import engine.world.entity.PlayerOnline;
import engine.world.tile.Tile;
import game.Game;

public class Viewport extends Canvas {
	public static final int BUFFER_DEPTH = 2;
	private static final long serialVersionUID = 1L;
	
	private String charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
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
		setSize(w, h);
		frame = new JFrame(title);
        frame.add(this);
        frame.setResizable(resizable);
        frame.pack();
        frame.setVisible(visible);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
	}
	
	public void render(World world) { 
		for(int y = 0; y < Game.HEIGHT + 16; y += 16) {
			if(y >= world.tilesHeight * 8) continue;
			if(y + camY < 0) continue;
			for(int x = 0; x < Game.WIDTH; x += 16) {
				if(x >= world.tilesWidth * 8) continue;
				if(x + camX < 0) continue;
				render(world.getTile(x + camX >> 0, y + camY >> 0), x + ((camX >> 4) << 4), y + ((camY >> 4) << 4));
			}
		}
	}
	
	public void render(Tile tile, int xp, int yp) {
		int textureX = (tile.id % (256 >> 3)) << 3; //bitshift 'ere
		int textureY = (tile.id / (256 >> 3)) << 3; //bitshift 'ere
		int sheetWidth = SpriteSheet.tiles.w;
		
		xp -= camX;
		yp -= camY;
		
		for(int y = 0; y < 8 * scale; ++y) {
			if(y + yp < 0 || y + yp >= h) continue;
			for(int x = 0; x < 8 * scale; ++x) {
				if(x + xp < 0 || x + xp >= w) continue;
				setPixel(x + xp, y + yp, SpriteSheet.tiles.pixels[((x >> 1) + textureX) + ((y >> 1) + textureY) * 256]);
			}
		}
	}
	
	public void render(int tileId, int xp, int yp) {
		int textureX = (tileId % (256 >> 3)) << 3; //bitshift 'ere
		int textureY = (tileId / (256 >> 3)) << 3; //bitshift 'ere
		int sheetWidth = SpriteSheet.tiles.w;
		
		for(int y = 0; y < 8 * scale; ++y) {
			if(y + yp < 0 || y + yp >= h) continue;
			for(int x = 0; x < 8 * scale; ++x) {
				if(x + xp < 0 || x + xp >= w) continue;
				setPixel(x + xp, y + yp, SpriteSheet.tiles.pixels[((x >> 1) + textureX) + ((y >> 1) + textureY) * 256]);
			}
		}
	}
	
	public void render(Sprite sprite, int xp, int yp) {
		int textureX = sprite.getCurrentColumnIndex() * (256 >> 4); //bitshift 'ere
		int textureY = sprite.getCurrentRowIndex() * (256 >> 4); //bitshift 'ere
		int sheetWidth = sprite.getSpriteSheet().w;
		
		xp -= camX;
		yp -= camY;
		
		for(int y = 0; y < 16 * scale; ++y) {
			if(y + yp < 0 || y + yp >= h) continue;
			for(int x = 0; x < 16 * scale; ++x) {
				if(x + xp < 0 || x + xp >= w) continue;
				int spriteColor = sprite.getSpriteSheet().pixels[((x >> 1) + textureX) + ((y >> 1) + textureY) * sheetWidth];
				if(!(spriteColor == SpriteSheet.COLORKEY))
					setPixel(x + xp, y + yp, spriteColor);
			}
		}
	}
	
	public void renderGuiElement(int id, int sx, int sy) {
		int textureX = (id % (256 >> 3)) << 3;
		int textureY = (id / (256 >> 3)) << 3;
		
		sx -= camX;
		sy -= camY;
		
		for(int y = 0; y < 8 * scale; ++y) {
			if(y + sy < 0 || y + sy >= h) continue;
			for(int x = 0; x < 8 * scale; ++x) {
				if(x + sx < 0 || x + sx >= w) continue;
				int spriteColor = SpriteSheet.gui.pixels[((x >> 1) + textureX) + ((y >> 1) + textureY) * SpriteSheet.items.w];
				if(spriteColor != SpriteSheet.COLORKEY) {
					setPixel(x + sx, y + sy, spriteColor);
				}
			}
		}
	}
	
	public void render(Entity entity) {
		//Render any entity in here
		//Entities contain sprite object
		if(entity instanceof PlayerOnline) {
			PlayerOnline player = (PlayerOnline) entity;
			player.getSprite().render(this, (int) player.x, (int) player.y);
			render(player.getUsername(), (int) player.x + 8, (int) player.y - 8);
			
			//Display healthbar
			int healthBarId = 4;
			if(player.getLife() >= (int) (player.getMaxLife() * 0.76)) {
				healthBarId = 0;
			}
			else if(player.getLife() >= (int) (player.getMaxLife() * 0.51)) {
				healthBarId = 1;
			}
			else if(player.getLife() >= (int) (player.getMaxLife() * 0.26)) {
				healthBarId = 2;
			}
			else if(player.getLife() >= (int) (player.getMaxLife() * 0.01)) {
				healthBarId = 3;
			}
			renderGuiElement(healthBarId, (int) player.x + 4, (int) player.y - 2);
		}
		else if(entity instanceof Player) {
			Player player = (Player) entity;
			player.getSprite().render(this, (int) player.x, (int) player.y);
			
			//Display healthbar
			int healthBarId = 4;
			if(player.getLife() >= (int) (player.getMaxLife() * 0.76)) {
				healthBarId = 0;
			}
			else if(player.getLife() >= (int) (player.getMaxLife() * 0.51)) {
				healthBarId = 1;
			}
			else if(player.getLife() >= (int) (player.getMaxLife() * 0.26)) {
				healthBarId = 2;
			}
			else if(player.getLife() >= (int) (player.getMaxLife() * 0.01)) {
				healthBarId = 3;
			}
			renderGuiElement(healthBarId, (int) player.x + 4, (int) player.y - 2);
		}
//		else if(entity instanceof ItemEntity) {
//			ItemEntity item = (ItemEntity) entity;
//			int itemId = item.getItemId();
//			int textureX = (itemId % (256 / 8)) * 8;
//			int textureY = (itemId / (256 / 8)) * 8;
//			int xp = (int) item.x;
//			int yp = (int) item.y;
//			for(int y = 0; y < 8 * scale; ++y) {
//				if(y + yp < 0 || y + yp >= h) continue;
//				for(int x = 0; x < 8 * scale; ++x) {
//					if(x + xp < 0 || x + xp >= w) continue;
//					int spriteColor = SpriteSheet.items.pixels[((x / scale) + textureX) + ((y / scale) + textureY) * SpriteSheet.items.w];
//					if(spriteColor != SpriteSheet.COLORKEY) {
//						setPixel(x + xp, y + yp, spriteColor);
//					}
//				}
//			}
//		}
		
//		else {
//			int xp = (int) entity.x;
//			int yp = (int) entity.y;
//			if(xp < 0 || yp < 0 || xp >= w || yp >= h) return;
//			for(int y = 0; y < 8; ++y) {
//				for(int x = 0; x < 8; ++x) {
//					setPixel(x + xp, y + yp, 0xff00ff00);
//				}
//			}
//		}
	}
	
	public void renderItemEntity(ItemEntity item, int tick, float phaseSize) {
		int itemId = item.getItemId();
		int textureX = (itemId % (256 / 8)) * 8;
		int textureY = (itemId / (256 / 8)) * 8;
		int xp = (int) item.x;
		int yp = (int) (item.y + ((tick % 32) - 16) * phaseSize * 4);
		
		xp -= camX;
		yp -= camY;
		
		for(int y = 0; y < 8 * scale; ++y) {
			if(y + yp < 0 || y + yp >= h) continue;
			for(int x = 0; x < 8 * scale; ++x) {
				if(x + xp < 0 || x + xp >= w) continue;
				int spriteColor = SpriteSheet.items.pixels[((x >> 1) + textureX) + ((y >> 1) + textureY) * SpriteSheet.items.w];
				if(spriteColor != SpriteSheet.COLORKEY) {
					setPixel(x + xp, y + yp, spriteColor);
				}
			}
		}
		
		renderGroundShadow((int) item.x, (int) item.y + 18, 16, 2);
	}
	
	public void renderGroundShadow(int xp, int yp, int shadowWidth, int shadowHeight) {
		float shadowAlpha = 75f / 255.f;
		
		xp -= camX;
		yp -= camY;
		
		for(int y = 0; y < shadowHeight; ++y) {
			if(y + yp < 0 || y + yp >= h) continue;
			for(int x = 0; x < shadowWidth; ++x) {
				if(x + xp < 0 || x + xp >= w) continue;
				int oldColor = getPixel(x + xp, y + yp);
				int r0 = (oldColor >> 16) & 0xff;
				int g0 = (oldColor >> 8) & 0xff;
				int b0 = (oldColor >> 0) & 0xff;
				
				int r1 = (int) ((1.f - shadowAlpha) * r0);
				int g1 = (int) ((1.f - shadowAlpha) * g0);
				int b1 = (int) ((1.f - shadowAlpha) * b0);
				int shadowColor = (0xff << 24 | r1 << 16 | g1 << 8 | b1);
				setPixel(x + xp, y + yp, shadowColor);
			}
		}
	}
	
	public void render(String text, int xp, int yp) {
		xp -= camX;
		yp -= camY;
		
		String tmpText = text.toUpperCase();
		int xpCenter = (text.length() * 8) / 2;
		for(int i = 0; i < text.length(); ++i) {
			int index = charset.indexOf(tmpText.charAt(i));
			
			if(index < 0) continue;
			
			int xpChar = (index % 32) * 8;
			int ypChar = (index / 32) * 8;
			
			for(int y = 0; y < 8; ++y) {
				if(y + yp < 0 || y + yp >= h) continue;
				for(int x = 0; x < 8; ++x) {
					if(((x + xp) + i * 8) - xpCenter < 0 || ((x + xp) + i * 8) - xpCenter >= w) continue;
					int textColor = SpriteSheet.font.getPixel(x + xpChar, ypChar + y);
					if(textColor != SpriteSheet.COLORKEY) {
						setPixel(((x + xp) + i * 8) - xpCenter, y + yp, textColor);
					}
				}
			}
		}
	}
	
	public void renderInventory(Inventory inventory) {
		for(int i = 0; i < inventory.numItems(); ++i) {
			for(int y = 0; y < 8; ++y) {
				for(int x = 0; x < 8; ++x) {
					
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
		if(camX < 0) {
			camX = 0;
		}
		if(camY < 0) {
			camY = 0;
		}
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
		g.fillRect(0, 0, w, h);
		g.drawImage(raster, 0, 0, w, h, null);
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
	
	public void setTitle(String title) {
		frame.setTitle(title);
	}
}
