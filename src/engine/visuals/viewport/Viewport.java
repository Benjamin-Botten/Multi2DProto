package engine.visuals.viewport;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;

import engine.visuals.Color;
import engine.visuals.Sprite;
import engine.visuals.SpriteSheet;
import engine.visuals.Text;
import engine.visuals.gui.GUIInventory;
import engine.world.World;
import engine.world.entity.AttackEntity;
import engine.world.entity.Entity;
import engine.world.entity.FrostbiteEntity;
import engine.world.entity.Inventory;
import engine.world.entity.ItemEntity;
import engine.world.entity.Player;
import engine.world.entity.PlayerOnline;
import engine.world.entity.SteelArrowProjectile;
import engine.world.entity.action.Action;
import engine.world.item.ConsumableItem;
import engine.world.item.Item;
import engine.world.tile.Tile;
import game.Game;

/**
 * The frame & graphics context used to rasterize graphics
 * @author robot
 *
 */
public class Viewport extends Canvas {
	public static final int BUFFER_DEPTH = 2;
	private static final long serialVersionUID = 1L;
	private static final int COLOR_HEALTH_BAR_FILL = 0xff00ff00;
	
	private String charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
							"1234567890";

	
	
	public static final int MAX_RADIUS_LIGHT = 4; //Measured in tiles
	
	private int[] pixels;
	private float[] tilelight;
	private final int w, h;
	private int lightmapWidth, lightmapHeight;
	private int scale;
	private JFrame frame;
	private String title = Game.TITLE;
	private BufferedImage raster;
	private BufferStrategy bs;
	private Camera camera;
	
	private float dayPhase = 0.79f;
	
	/**
	 * Creates a new viewport for rendering
	 * @param w
	 * @param h
	 * @param scale
	 * @param title
	 * @param camera
	 * @param jframe
	 */
	public Viewport(int w, int h, int scale, String title, Camera camera, JFrame jframe) {
		this.w = w;
		this.h = h;
		this.scale = scale;
		this.title = title;
		this.camera = camera;
		
		raster = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		pixels = ((DataBufferInt) raster.getRaster().getDataBuffer()).getData();
		
		setFrame(jframe);
		configureFrame(false, true);
	}
	
	/**
	 * Set the viewport's frame to a specific JFrame
	 * @param frame
	 */
	private void setFrame(JFrame frame) {
		if(frame == null) {
			throw new IllegalArgumentException("Attempted setting viewport JFrame to null");
		}
		this.frame = frame;
	}
	
	/**
	 * Wrapper-method to configure our JFrame
	 * @param resizable
	 * @param visible
	 */
	private void configureFrame(boolean resizable, boolean visible) {
		if(frame == null) {
			throw new RuntimeException("frame is null, must be set to a valid JFrame");
		}
		
		setSize(w, h);
        frame.add(this);
        frame.setResizable(resizable);
        frame.pack();
        frame.setVisible(visible);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        
        try {
			frame.setIconImage(ImageIO.read(Viewport.class.getResourceAsStream("/resrc/art/lightmap4x4.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Render the world (tiles)
	 * @param world
	 */
	public void render(World world) {
		
		tilelight = world.getTileLight();
		lightmapWidth = world.tilesWidth;
		lightmapHeight = world.tilesHeight;
		dayPhase = world.getDayPhase();
		
		int camX = camera.getX();
		int camY = camera.getY();
		for(int y = 0; y < world.w; y += 16) {
			if(y >= world.tilesHeight * 16) continue;
			if(y + camY < 0) continue;
			for(int x = 0; x < world.h; x += 16) {
				if(x >= world.tilesWidth * 16) continue;
				if(x + camX < 0) continue;
				
				render(world.getTile((x + camX) >> 0, (y + camY) >> 0), x + ((camX >> 4) << 4), y + ((camY >> 4) << 4), 
						world.getTileLight((x + camX) >> 0, (y + camY) >> 0) + world.getDayPhase());
			}
		}
	}
	
	/**
	 * Render a tile given a tile and a position to render in world-space
	 * @param tile
	 * @param xp
	 * @param yp
	 */
	public void render(Tile tile, int xp, int yp) {
		int textureX = (tile.id % (256 >> 3)) << 3; //bitshift 'ere
		int textureY = (tile.id / (256 >> 3)) << 3; //bitshift 'ere
//		int textureY = (tile.id >> 3) << 3; //bitshift 'ere
		int sheetWidth = SpriteSheet.tiles.w;
		
		xp -= camera.getX();
		yp -= camera.getY();
		
		for(int y = 0; y < 8 * scale; ++y) {
			if(y + yp < 0 || y + yp >= h) continue;
			for(int x = 0; x < 8 * scale; ++x) {
				if(x + xp < 0 || x + xp >= w) continue;
				pixels[(x + xp) + (y + yp) * w] = SpriteSheet.tiles.pixels[((x >> 1) + textureX) + ((y >> 1) + textureY) * sheetWidth];
			}
		}
	}
	
	/**
	 * Render tile with a multiplicative luminosity on it
	 * @param tile
	 * @param xp
	 * @param yp
	 * @param luminosity
	 */
	public void render(Tile tile, int xp, int yp, float luminosity) {
		int textureX = (tile.id % (256 >> 3)) << 3; //bitshift 'ere
		int textureY = (tile.id / (256 >> 3)) << 3; //bitshift 'ere
		int sheetWidth = SpriteSheet.tiles.w;
		
		int camX = camera.getX();
		int camY = camera.getY();
		
		xp -= camX;
		yp -= camY;
		
		for(int y = 0; y < 8 * scale; ++y) {
			if(y + yp < 0 || y + yp >= h) continue;
			for(int x = 0; x < 8 * scale; ++x) {
				if(x + xp < 0 || x + xp >= w) continue;
				setPixel((x + xp), (y + yp), Color.getColorMultipliedRGB(SpriteSheet.tiles.pixels[((x >> 1) + textureX) + ((y >> 1) + textureY) * 256], luminosity));
			}
		}
	}
	
	/**
	 * Render a tile given its id and a position in world-space
	 * @param tileId
	 * @param xp
	 * @param yp
	 */
	public void render(int tileId, int xp, int yp) {
		int textureX = (tileId % (256 >> 3)) << 3; //bitshift 'ere
		int textureY = (tileId / (256 >> 3)) << 3; //bitshift 'ere
		int sheetWidth = SpriteSheet.tiles.w;
		
		for(int y = 0; y < 8 * scale; ++y) {
			if(y + yp < 0 || y + yp >= h) continue;
			for(int x = 0; x < 8 * scale; ++x) {
				if(x + xp < 0 || x + xp >= w) continue;
				setPixel(x + xp, y + yp, Color.getColorMultipliedRGB(SpriteSheet.tiles.pixels[((x >> 1) + textureX) + ((y >> 1) + textureY) * 256], dayPhase));
			}
		}
	}
	
	/**
	 * Renders a sprite in world-space
	 * @param sprite
	 * @param xp
	 * @param yp
	 */
	public void render(Sprite sprite, int xp, int yp) {
		int textureX = sprite.getCurrentColumnIndex() * (256 >> 4); //bitshift 'ere
		int textureY = sprite.getCurrentRowIndex() * (256 >> 4); //bitshift 'ere
		int sheetWidth = sprite.getSpriteSheet().w;
		
		int camX = camera.getX();
		int camY = camera.getY();
		
		xp -= camX;
		yp -= camY;
		
		for(int y = 0; y < 16 * scale; ++y) {
			if(y + yp < 0 || y + yp >= h) continue;
			for(int x = 0; x < 16 * scale; ++x) {
				if(x + xp < 0 || x + xp >= w) continue;
				int spriteColor = sprite.getSpriteSheet().pixels[((x >> 1) + textureX) + ((y >> 1) + textureY) * sheetWidth];
//				int spriteColor = Color.getColorMultipliedRGB(sprite.getSpriteSheet().pixels[((x >> 1) + textureX) + ((y >> 1) + textureY) * sheetWidth], tilelight[(xp / 16) + (yp / 16) * World.tilesWidth]);
				if(!(spriteColor == SpriteSheet.COLORKEY))
					setPixel(x + xp, y + yp, Color.getColorMultipliedRGB(sprite.getSpriteSheet().pixels[((x >> 1) + textureX) + ((y >> 1) + textureY) * sheetWidth], dayPhase + getTileLight((xp + camX), (yp + camY)))); //)[((xp + camX) / 16) + ((yp + camY) / 16) * World.tilesWidth]));
			}
		}
	}
	
	/**
	 * Renders id-specific GUI element in screen-space with given dimensions
	 * @param id
	 * @param sx
	 * @param sy
	 * @param sw
	 * @param sh
	 */
	public void renderGuiElement(int id, int sx, int sy, int sw, int sh) {
		int textureX = (id % (256 >> 3)) << 3;
		int textureY = (id / (256 >> 3)) << 3;
		
		int camX = camera.getX();
		int camY = camera.getY();
		
		sx -= camX;
		sy -= camY;
		
		int scaleWidth = ((sw >> 1) >> 3) - 1;
		int scaleHeight = ((sh >> 1) >> 3) - 1;
		
		for(int y = 0; y < 8 * (scale + scaleHeight); ++y) {
			if(y + sy < 0 || y + sy >= h) continue;
			for(int x = 0; x < 8 * (scale + scaleWidth); ++x) {
				if(x + sx < 0 || x + sx >= w) continue;
				int spriteColor = SpriteSheet.gui.pixels[((x / (scale + scaleWidth)) + textureX) + ((y / (scale + scaleHeight)) + textureY) * SpriteSheet.items.w];
				if(spriteColor != SpriteSheet.COLORKEY) {
					setPixel(x + sx, y + sy, spriteColor);
				}
			}
		}
	}
	
	/**
	 * Renders id-specific GUI element in screen-space with given dimensions, and animation parameters for bobbing in-game
	 * @param id
	 * @param sx
	 * @param sy
	 * @param sw
	 * @param sh
	 * @param tick
	 * @param phaseSize
	 * @param freqScalar
	 */
	public void renderGuiElement(int id, int sx, int sy, int sw, int sh, int tick, float phaseSize, float freqScalar) {
		int textureX = (id % (256 >> 3)) << 3;
		int textureY = (id / (256 >> 3)) << 3;
		
		int camX = camera.getX();
		int camY = camera.getY();
		
		sy = (int) (sy + ((tick % (Game.DEFAULT_CLIENT_TICKS * freqScalar)) - 16) * phaseSize * 4);
		
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
	
	/**
	 * Renders any entity
	 * @param entity
	 */
	public void render(Entity entity) {
		//Render any entity in here
		//Entities contain sprite object
		if(entity instanceof Player) {
			Player player = (Player) entity;
			player.getSprite().render(this, (int) player.x, (int) player.y);
			render(player.getUsername(), (int) player.x + 8 * scale, (int) player.y - 8 * scale);
			
			if(player.getCurrentAction().getId() != Action.noAction.getId()) {
				this.renderActionProgressBar(player, player.getActionProgress(), Game.SCALE);
			}
		}
		else if(entity instanceof SteelArrowProjectile) {
			int id = ((SteelArrowProjectile) entity).id;
			int textureX = (id % (256 >> 3)) << 3;
			int textureY = (id / (256 >> 3)) << 3;
			int xp = (int) entity.x;
			int yp = (int) entity.y;
			
			int camX = camera.getX();
			int camY = camera.getY();
			
			xp -= camX;
			yp -= camY;
			
			for(int y = 0; y < 8 * scale; ++y) {
				if(y + yp < 0 || y + yp >= h) continue;
				for(int x = 0; x < 8 * scale; ++x) {
					if(x + xp < 0 || x + xp >= w) continue;
					int spriteColor = SpriteSheet.itementities.pixels[((x >> 1) + textureX) + ((y >> 1) + textureY) * SpriteSheet.items.w];
					if(spriteColor != SpriteSheet.COLORKEY) {
						setPixel(x + xp, y + yp, spriteColor);
					}
				}
			}
			
			renderGroundShadow((int) entity.x, (int) entity.y + 18, 16, 2);
		}
		else if(entity instanceof FrostbiteEntity) {
			
			FrostbiteEntity attackEntity = (FrostbiteEntity) entity;
			
			int xp = (int) entity.x;
			int yp = (int) entity.y;
			
			xp -= camera.getX();
			yp -= camera.getY();
			
			//Get angle to target
			int x0 = 1;
			int y0 = 0;
			
			double dx = attackEntity.getTarget().x - entity.x;
			double dy = attackEntity.getTarget().y - entity.y;
			
			double mag = Math.sqrt(dx * dx + dy * dy);
			
			dx /= mag;
			dy /= mag;
			
			double atan2Angle = Math.toDegrees(Math.atan2(dy, dx));
			if(atan2Angle < 0) {
				atan2Angle = -atan2Angle + 180;
			}
			int angle = (int) atan2Angle;
//			if(atan2Angle > 360) angle = 360;
//			if(atan2Angle < 0) angle = 0;
//			System.out.println("Angle between attack entity and target > " + angle);
			
			int tx = (angle % 32) * 8;
			int ty = (angle / 32) * 8;
			
//			System.out.println("Rendering FrostbiteEntity X " + xp + ", Y " + yp);
			
			for(int y = 0; y < 8 * scale; ++y) {
				if(y + yp < 0 || y + yp >= h) continue;
				for(int x = 0; x < 8 * scale; ++x) {
					if(x + xp < 0 || x + xp >= w) continue;
					int spriteColor = SpriteSheet.frostbite.pixels[((x >> 1) + tx) + ((y >> 1) + ty) * SpriteSheet.frostbite.w];
					if(spriteColor != SpriteSheet.COLORKEY) {
						setPixel(x + xp, y + yp, spriteColor);
					}
//					setPixel(x + xp, y + yp, 0xff0000ff);
				}
			}
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
	
	/**
	 * Renders a @ItemEntity with given bobbing params
	 * @param item
	 * @param tick
	 * @param phaseSize
	 */
	public void renderItemEntity(ItemEntity item, int tick, float phaseSize) {
		int itemId = item.getItemId();
		int textureX = (itemId % (256 / 8)) * 8;
		int textureY = (itemId / (256 / 8)) * 8;
		int xp = (int) item.x;
		int yp = (int) (item.y + ((tick % 32) - 16) * phaseSize * 4);
		
		int camX = camera.getX();
		int camY = camera.getY();
		
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
	
	/**
	 * Renders a shadow (transparent slice on the ground)
	 * TODO: Add other methods to render different shape ground-shadows?
	 * @param xp
	 * @param yp
	 * @param shadowWidth
	 * @param shadowHeight
	 */
	public void renderGroundShadow(int xp, int yp, int shadowWidth, int shadowHeight) {
		float shadowAlpha = 75f / 255.f;
		
		int camX = camera.getX();
		int camY = camera.getY();
		
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
	
	/**
	 * Renders a string as a 2D-text with the font's default color
	 * @param text
	 * @param xp
	 * @param yp
	 */
	public void render(String text, int xp, int yp) {
		int camX = camera.getX();
		int camY = camera.getY();
		
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
	
	/**
	 * Renders a string as a 2D-text with a specific color
	 * @param text
	 * @param xp
	 * @param yp
	 * @param color
	 */
	public void render(String text, int xp, int yp, int color) {
		int camX = camera.getX();
		int camY = camera.getY();
		
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
					int pixel = SpriteSheet.font.getPixel(x + xpChar, ypChar + y);
					if(pixel != SpriteSheet.COLORKEY) {
						setPixel(((x + xp) + i * 8) - xpCenter, y + yp, color);
					}
				}
			}
		}
	}
	
	/**
	 * Renders a string as a 2D-text with a specific color and size
	 * @param text
	 * @param xp
	 * @param yp
	 * @param color
	 */
	public void render(String text, int xp, int yp, int color, int scale) {
		int camX = camera.getX();
		int camY = camera.getY();
		
		xp -= camX;
		yp -= camY;
		
		String tmpText = text.toUpperCase();
		int xpCenter = (text.length() * 8 * scale) / 2;
		for(int i = 0; i < text.length(); ++i) {
			int index = charset.indexOf(tmpText.charAt(i));
			
			if(index < 0) continue;
			
			int xpChar = (index % 32) * 8;
			int ypChar = (index / 32) * 8;
			
			for(int y = 0; y < 8 * scale; ++y) {
				if(y + yp < 0 || y + yp >= h) continue;
				for(int x = 0; x < 8 * scale; ++x) {
					if(((x + xp) + i * 8 * scale) - xpCenter < 0 || ((x + xp) + i * 8 * scale) - xpCenter >= w) continue;
					int pixel = SpriteSheet.font.getPixel((x / scale) + xpChar, (y / scale) + ypChar);
					if(pixel != SpriteSheet.COLORKEY) {
						setPixel(((x + xp) + i * 8 * scale) - xpCenter, y + yp, color);
					}
				}
			}
		}
	}
	
	/**
	 * Renders a string as a 2D-text with a specific color in screen-space instead of view-space/world-space
	 * @param text
	 * @param xp
	 * @param yp
	 * @param color
	 */
	public void renderTextScreenSpace(String text, int xp, int yp, int color) {
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
					int pixel = SpriteSheet.font.getPixel(x + xpChar, ypChar + y);
					if(pixel != SpriteSheet.COLORKEY) {
						setPixel(((x + xp) + i * 8) - xpCenter, y + yp, color);
					}
				}
			}
		}
	}
	
	/**
	 * Renders a specific text object
	 * @param text
	 */
	public void render(Text text) {
		String tmpText = text.getText().toUpperCase();

		int xp = text.getY();
		int yp = text.getX();
		
		xp -= camera.getX();
		yp -= camera.getY();
		
		int xpCenter = 0;
		if(text.getAlignment() == Text.ALIGNMENT_CENTRE) {
			xpCenter = (text.length() * 8 * scale) / 2;
		}
		
		for(int i = 0; i < text.length(); ++i) {
			int index = charset.indexOf(tmpText.charAt(i));
			
			if(index < 0) continue;
			
			int xpChar = (index % 32) * 8;
			int ypChar = (index / 32) * 8;
			
			for(int y = 0; y < 8 * scale; ++y) {
				if(y + yp < 0 || y + yp >= h) continue;
				for(int x = 0; x < 8 * scale; ++x) {
					if(((x + xp) + i * 8 * scale) - xpCenter < 0 || ((x + xp) + i * 8 * scale) - xpCenter >= w) continue;
					int pixel = SpriteSheet.font.getPixel((x / scale) + xpChar, (y / scale) + ypChar);
					if(pixel != SpriteSheet.COLORKEY) {
						setPixel(((x + xp) + i * 8 * scale) - xpCenter, y + yp, text.getColor());
					}
				}
			}
		}
	}
	
	
	public void renderTextScreenSpace(Text text) {
		
		//Render background if text has background
		if(text.hasBackground()) {
			renderRect(text.getX(), text.getY(), text.getWidth(), text.getHeight(), 0x2200ff00);
		}
		
		
		String tmpText = text.getText().toUpperCase();

		int scale = text.getSize();
		int xp = text.getX();
		int yp = text.getY();
		int xpCenter = 0;
		if(text.getAlignment() == Text.ALIGNMENT_CENTRE) {
			xpCenter = (text.length() * 8 * scale) / 2;
		}
		
		for(int i = 0; i < text.length(); ++i) {
			int index = charset.indexOf(tmpText.charAt(i));
			
			if(index < 0) continue;
			
			int xpChar = (index % 32) * 8;
			int ypChar = (index / 32) * 8;
			
			for(int y = 0; y < 8 * scale; ++y) {
				if(y + yp < 0 || y + yp >= h) continue;
				for(int x = 0; x < 8 * scale; ++x) {
					if(((x + xp) + i * 8 * scale) - xpCenter < 0 || ((x + xp) + i * 8 * scale) - xpCenter >= w) continue;
					int pixel = SpriteSheet.font.getPixel((x / scale) + xpChar, (y / scale) + ypChar);
					if(pixel != SpriteSheet.COLORKEY) {
						setPixel(((x + xp) + i * 8 * scale) - xpCenter, y + yp, text.getColor());
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
	
	/**
	 * Renders an action progress bar on an entity performing an action
	 * TODO: Get the action progress from the entity itself instead of via a param
	 * @param entity
	 * @param actionProgress
	 * @param scale
	 */
	public void renderActionProgressBar(Entity entity, int actionProgress, int scale) {
		int x0 = (int) entity.x;
		int y0 = (int) entity.y + entity.h;
		
		x0 -= camera.getX();
		y0 -= camera.getY();
		
		final int w = entity.w;
		final int h = 2;
		final int COLOR_ACTION_PROGRESS_BAR_FILL = 0xffff222f;
		int fillWidth = (int) ((actionProgress / 100.f) * (float) w);
		
		for(int y = y0; y < y0 + (h * scale); ++y) {
			for(int x = x0; x < x0 + fillWidth; ++x) {
				setPixel(x, y, COLOR_ACTION_PROGRESS_BAR_FILL);
			}
			for(int x = x0 + fillWidth; x < x0 + entity.w; ++x) {
				setPixel(x, y, 0);
			}
		}
	}
	
	/**
	 * Render the health left on an entity
	 * @param entity
	 * @param scale
	 */
	public void renderHealthBar(Entity entity, int scale) {
		int x0 = (int) entity.x;
		int y0 = (int) entity.y - (entity.h >> 3);
		
		x0 -= camera.getX();
		y0 -= camera.getY();
		
		final int w = entity.w;
		final int h = 2;
		int fillWidth = (int) ((entity.getLife() / (float)entity.getMaxLife()) * (float) w);
		
		for(int y = y0; y < y0 + (h * scale); ++y) {
			for(int x = x0; x < x0 + fillWidth; ++x) {
				setPixel(x, y, COLOR_HEALTH_BAR_FILL);
			}
			for(int x = x0 + fillWidth; x < x0 + entity.w; ++x) {
				setPixel(x, y, 0);
			}
		}
	}
	
	/**
	 * Render a specific item given inventory params
	 * @param item
	 * @param posInventory
	 * @param xInventory
	 * @param yInventory
	 */
	public void render(Item item, int posInventory, int xInventory, int yInventory) {
		
		//Inventory-space coordinates (coordinates in reference to inventory)
		int xInvSlot = (posInventory % GUIInventory.WIDTH_ITEMS) * SpriteSheet.items.ew;
		int yInvSlot = (posInventory / GUIInventory.HEIGHT_ITEMS) * SpriteSheet.items.eh;
		xInvSlot *= Game.SCALE;
		yInvSlot *= Game.SCALE;
		
		//Get texture coordinates for item
		int itemId = item.getId();
		int xTile = (itemId % (SpriteSheet.items.w / SpriteSheet.items.ew)) * 8;
		int yTile = (itemId / (SpriteSheet.items.h / SpriteSheet.items.eh)) * 8;
		
		//Set the screen-space offset
		int xOffset = xInvSlot + xInventory;
		int yOffset = yInvSlot + yInventory;
		
		//Render item pixels
		for(int y = 0; y < 8 * Game.SCALE; ++y) {
			for(int x = 0; x < 8 * Game.SCALE; ++x) {
				int color = SpriteSheet.items.getPixel((x >> 1) + xTile, (y >> 1) + yTile);
				if(color == SpriteSheet.COLORKEY) continue;
				setPixel(x + xOffset, y + yOffset, color);
			}
		}
		
		//If it's a consumable item (stacked), render amount
		if(item instanceof ConsumableItem) {
			renderTextScreenSpace(Integer.toString(((ConsumableItem) item).getAmount()), xOffset, yOffset, 0xffffffff);
		}
	}
	
	/**
	 * Render item at a specific position
	 * @param item
	 * @param xp
	 * @param yp
	 */
	public void render(Item item, int xp, int yp) {
		
		//Get texture coordinates for item
		int itemId = item.getId();
		int xTile = (itemId % (SpriteSheet.items.w / SpriteSheet.items.ew)) * 8;
		int yTile = (itemId / (SpriteSheet.items.h / SpriteSheet.items.eh)) * 8;
		
		//Render item pixels
		for(int y = 0; y < 8 * Game.SCALE; ++y) {
			for(int x = 0; x < 8 * Game.SCALE; ++x) {
				int color = SpriteSheet.items.getPixel((x >> 1) + xTile, (y >> 1) + yTile);
				if(color == SpriteSheet.COLORKEY) continue;
				setPixel(x + xp, y + yp, color);
			}
		}
		
		if(item instanceof ConsumableItem) {
			renderTextScreenSpace(Integer.toString(((ConsumableItem) item).getAmount()), xp, yp, 0xffffffff);
		}
	}
	
	/**
	 * Render the minimap for a given world, centered to a given entity
	 * @param world
	 * @param centerEntity
	 * @param xp
	 * @param yp
	 * @param w
	 * @param h
	 * @param scale
	 */
	public void renderMinimap(World world, Entity centerEntity, int xp, int yp, int w, int h, int scale) {
		
		int xCam = world.getPlayer().getCamera().getX();
		int yCam = world.getPlayer().getCamera().getY();
		
		int xTranslated, yTranslated;
		
		int xt, yt;
		for(int y = 0; y < h * scale; ++y) {
			yt = (y << 2) + yCam;
			for(int x = 0; x < w * scale; ++x) {
				xt = (x << 2) + xCam;
				int tile = world.getTile(xt, yt).id;
				int color = 0;
				
				if(tile == Tile.grass.id) {
					color = 0xff007f00;
				}
				else if(tile == Tile.sand.id) {
					color = 0xffffff00;
				}
				else if(tile == Tile.water.id) {
					color = 0xff0000ff;
				}
				else if(tile == Tile.stone.id) {
					color = 0xff777777;
				}
				else if(tile == Tile.torch.id) {
					color = 0xffffffff;
				}
				else {
					color = 0xff000000;
				}
				
				setPixel((x) + xp, (y) + yp, color);
			}
		}
		setPixel((((int) centerEntity.getCenterX() - xCam) >> 2) + xp, (((int) centerEntity.getCenterY() - yCam) >> 2) + yp, 0xffff0000, 3);
	}
	
	public void renderRect(int xp, int yp) {
		for(int y = 0; y < 8; ++y) {
			for(int x = 0; x < 8; ++x) {
				setPixel(x + xp, y + yp, 0xff00ff00);
			}
		}
	}
	
	public void renderRect(int xp, int yp, int w, int h, int color) {
		for(int y = 0; y < h; ++y) {
			for(int x = 0; x < w; ++x) {
				setPixel(x + xp, y + yp, Color.getColorBlendAlpha(getPixel(x + xp, y + yp), color));
			}
		}
	}
	
	public void renderTextBackground(String text, int xp, int yp, int w, int h, int color) {
		int textCenterX = (text.length() * 8) >> 1;
		for(int y = 0; y < h; ++y) {
			if(y + yp < 0 || y + yp >= this.h) continue;
			for(int x = 0; x < w; ++x) {
				if(x + xp - textCenterX < 0 || x + xp - textCenterX >= this.w) continue;
				setPixel(x + xp - textCenterX, y + yp, Color.getColorBlendAlpha(getPixel(x + xp - textCenterX, y + yp), color));
			}
		}
	}
	
	public void renderBounds(int xp, int yp, int ew, int eh) {
		int camX = camera.getX();
		int camY = camera.getY();
		
		xp -= camX;
		yp -= camY;
		for(int y = 0; y < eh; ++y) {
			if(y + yp < 0 || y + yp >= h) continue;
			for(int x = 0; x < ew; ++x) {
				if(x + xp < 0 || x + xp >= w) continue;
				if(x == 0 || x == ew - 1 || y == 0 || y == eh - 1) {
					setPixel(x + xp, y + yp, 0xffff0000);
				}
			}
		}
	}
	
	public void renderBoundsScreenSpace(int xp, int yp, int ew, int eh) {
		for(int y = 0; y < eh; ++y) {
			if(y + yp < 0 || y + yp >= h) continue;
			for(int x = 0; x < ew; ++x) {
				if(x + xp < 0 || x + xp >= w) continue;
				if(x == 0 || x == ew - 1 || y == 0 || y == eh - 1) {
					setPixel(x + xp, y + yp, 0xffff0000);
				}
			}
		}
	}
	
	public void renderBounds(Entity entity) {
		int xp = (int) entity.x;
		int yp = (int) entity.y;
		
		renderBounds(xp, yp, entity.w, entity.h);
	}
	
	
	public void setCamera(int camX, int camY) {
		if(camX < 0) {
			camX = 0;
		}
		if(camY < 0) {
			camY = 0;
		}
		camera.set(camX, camY);
	}
	
	/**
	 * Center camera to player
	 * @param player
	 */
	public void setCamera(Player player) {
		setCamera((int) player.x - Game.HALF_WIDTH, (int) player.y - Game.HALF_HEIGHT);
	}
	
	public void setPixel(int x, int y, int color) {
		if(x < 0 || y < 0 || x >= w || y >= h) return;//throw new IllegalArgumentException("Attempted to set pixel in negative space");
		pixels[x + y * w] = color;
	}
	
	public void setPixel(int x, int y, int color, int size) {
		if(x < 0 || y < 0 || (x + size) < 0 || (y + size) < 0 || x >= w || y >= h || (x + size) >= w || (y + size) >= h) return;//throw new IllegalArgumentException("Attempted to set pixel in negative space");
		for(int y0 = 0; y0 <= size; ++y0) {
			for(int x0 = 0; x0 <= size; ++x0) {
				pixels[(x + x0) + (y + y0) * w] = color;
			}
		}
	}
	
	public int getPixel(int x, int y) {
		if(x < 0 || y < 0 || x >= w || y >= h) throw new IllegalArgumentException("Attempted to get pixel in negative space");
		return pixels[x + y * w];
	}
	
	public float getTileLight(int x, int y) {
		x /= 16;
		y /= 16;
		if(x < 0 || y < 0 || x >= lightmapWidth || y >= lightmapHeight) return 0; //throw new IllegalArgumentException("Attempted to get tile-light in negative space");
		return tilelight[x + y * lightmapWidth];
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
	
	public void addWindowListener(WindowAdapter windowAdapter) {
		frame.addWindowListener(windowAdapter);
	}
	
	public void setCamera(Camera camera) {
		this.camera = camera;
	}
	
	public void setDayPhase(float dayPhase) {
		this.dayPhase = dayPhase;
	}

	public Camera getCamera() {
		return camera;
	}
	
	public int getScale() {
		return scale;
	}
}
