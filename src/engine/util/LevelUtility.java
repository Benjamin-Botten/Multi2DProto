package engine.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import engine.editor.EditorWorld;
import engine.world.Level;
import engine.world.World;
import engine.world.entity.Entity;
import engine.world.entity.EntityFactory;
import engine.world.entity.Player;
import engine.world.tile.Tile;
import game.Game;

public class LevelUtility {
	
	
	public static Level get(String name) {
		//Check if the level-path already exists
		File file = new File(Level.PATH_LEVELS + name);
		if(file.exists()) {
			return new Level(name);
		}
		return null;
	}
	
	/**
	 * Fetches all the level names and stores them in the active world object
	 * @param world
	 */
	public static void fetchAll(World world) {
	}
	
	/**
	 * Load the world's current level
	 * @param world
	 */
	public static void loadLevel(World world) {
		
		long w = 0, h = 0;
		List<Entity> entities = new ArrayList<>();
		//Load level data from level.dat
		try {
			FileReader reader = new FileReader(world.getCurrentLevel().getFilenameData());
			JSONParser parser = new JSONParser();
			
			JSONObject jsonObj = (JSONObject) parser.parse(reader);
			
			w = (long) jsonObj.get("w");
			h = (long) jsonObj.get("h");
			double x = 0, y = 0;
			long id = 0;
			
			long numEntities = (long) ((JSONObject) jsonObj.get("entities")).get("size");
			
			for(int i = 0; i < numEntities; ++i) {
				JSONObject currentEntity = (JSONObject) ((JSONObject) jsonObj.get("entities")).get(new Integer(i).toString());
				x = (double) currentEntity.get("x");
				y = (double) currentEntity.get("y");
				id = (long) currentEntity.get("id");
				
				Entity newEntity = EntityFactory.create((int) id, (int) x, (int) y);
				if(newEntity != null) {
					entities.add(newEntity);
				}
			}
			
			System.out.println("entities > " + ((JSONObject) jsonObj.get("entities")).get("size"));
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		
		
		int[] tiles = null;
		StringTokenizer tokenizer;
		Scanner scanner;
		String line = "";
		//Load tiles from level.map
		try {
			scanner = new Scanner(new File(world.getCurrentLevel().getFilenameMap()));
			
			tiles = new int[(int) (w * h)];
			int tilecount = 0;
			for(; scanner.hasNextLine() ;) {
				line = scanner.nextLine();
				tokenizer = new StringTokenizer(line, " ");
				while(tokenizer.hasMoreTokens()) {
					tiles[tilecount++] = Integer.parseInt(tokenizer.nextToken());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		world.setTiles(tiles);
		world.setWidth((int) w);
		world.setHeight((int) h);
		for(int i = 0; i < entities.size(); ++i) {
			world.addEntity(entities.get(i));
		}
	}
	
	/**
	 * Load the world's current level
	 * @param world
	 */
	public static void loadLevel(EditorWorld world) {
		
		long w = 0, h = 0;
		List<Entity> entities = new ArrayList<>();
		//Load level data from level.dat
		try {
			FileReader reader = new FileReader(world.getLevel().getFilenameData());
			JSONParser parser = new JSONParser();
			
			JSONObject jsonObj = (JSONObject) parser.parse(reader);
			
			w = (long) jsonObj.get("w");
			h = (long) jsonObj.get("h");
			double x = 0, y = 0;
			long id = 0;
			
			long numEntities = (long) ((JSONObject) jsonObj.get("entities")).get("size");
			
			for(int i = 0; i < numEntities; ++i) {
				JSONObject currentEntity = (JSONObject) ((JSONObject) jsonObj.get("entities")).get(new Integer(i).toString());
				x = (double) currentEntity.get("x");
				y = (double) currentEntity.get("y");
				id = (long) currentEntity.get("id");
				
				Entity newEntity = EntityFactory.create((int) id, (int) x, (int) y);
				if(newEntity != null) {
					entities.add(newEntity);
				}
			}
			
			System.out.println("entities > " + ((JSONObject) jsonObj.get("entities")).get("size"));
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		
		
		int[] tiles = null;
		StringTokenizer tokenizer;
		Scanner scanner;
		String line = "";
		//Load tiles from level.map
		try {
			scanner = new Scanner(new File(world.getLevel().getFilenameMap()));
			
			tiles = new int[(int) (w * h)];
			int tilecount = 0;
			for(; scanner.hasNextLine() ;) {
				line = scanner.nextLine();
				tokenizer = new StringTokenizer(line, " ");
				while(tokenizer.hasMoreTokens()) {
					tiles[tilecount++] = Integer.parseInt(tokenizer.nextToken());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		world.setTiles(tiles);
		world.setWidth((int) w);
		world.setHeight((int) h);
		world.setEntities(entities);
	}
	
	/**
	 * Save the world's current level
	 * @param world
	 */
	@SuppressWarnings("unchecked")
	public static void saveLevel(World world) {
		//Save World Data
		FileWriter outputStream = null;
		try {
			outputStream = new FileWriter(new File(world.getCurrentLevel().getFilenameData()));
			
			JSONObject jsonObj = new JSONObject();
			JSONObject jsonObjEntities = new JSONObject();
			
			List<Entity> entities = world.getEntities();

			jsonObjEntities.put("size", world.getSizeEntities());
			
			for(int i = 0; i < entities.size(); ++i) {
				Entity tmp = entities.get(i);
				
				JSONObject jsonEntity = new JSONObject();
				jsonEntity.put("id", tmp.getType());
				jsonEntity.put("x", tmp.x);
				jsonEntity.put("y", tmp.y);
				
				jsonObjEntities.put(Integer.toString(i), jsonEntity);
			}
			
			jsonObj.put("w", world.tilesWidth);
			jsonObj.put("h", world.tilesHeight);
			jsonObj.put("entities", jsonObjEntities);
			
			
			
			outputStream.write(jsonObj.toJSONString());
			outputStream.flush();
			outputStream.close();
			
			outputStream = new FileWriter(new File(world.getCurrentLevel().getFilenameMap()));
		
			//Save Map Data
			for(int y = 0; y < world.tilesHeight; ++y) {
				for(int x = 0; x < world.tilesWidth; ++x) {
					outputStream.write(world.getTile(x * Tile.w * Game.SCALE, y * Tile.h * Game.SCALE).id + " ");
				}
				outputStream.write("\r\n");
			}
			
			outputStream.flush();
			outputStream.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Save a level
	 * @param world
	 */
	@SuppressWarnings("unchecked")
	public static void saveLevel(Level level, List<Entity> entities, int[] tiles, int w, int h) {
		//Save World Data
		FileWriter outputStream = null;
		try {
			outputStream = new FileWriter(new File(level.getFilenameData()));
			
			JSONObject jsonObj = new JSONObject();
			JSONObject jsonObjEntities = new JSONObject();
			

			jsonObjEntities.put("size", entities.size());
			
			for(int i = 0; i < entities.size(); ++i) {
				Entity tmp = entities.get(i);
				
				JSONObject jsonEntity = new JSONObject();
				jsonEntity.put("id", tmp.getType());
				jsonEntity.put("x", tmp.x);
				jsonEntity.put("y", tmp.y);
				
				jsonObjEntities.put(Integer.toString(i), jsonEntity);
			}
			
			jsonObj.put("w", w);
			jsonObj.put("h", h);
			jsonObj.put("entities", jsonObjEntities);
			
			outputStream.write(jsonObj.toJSONString());
			outputStream.flush();
			outputStream.close();
			
			outputStream = new FileWriter(new File(level.getFilenameMap()));
			
			//Save Map Data
			for(int y = 0; y < h; ++y) {
				for(int x = 0; x < w; ++x) {
					outputStream.write(tiles[x + y * w] + " ");
				}
				outputStream.write("\r\n");
			}
			
			outputStream.flush();
			outputStream.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Save Map Data
		
	}
	
	public static boolean createLevel(String name, int w, int h) {
		if(name == null) {
			throw new IllegalArgumentException("Attempted creating level with null name");
		}
		if(name.length() == 0) {
			throw new IllegalArgumentException("Attempted creating level with zero-length name");
		}
		if(w < 0 || h < 0) {
			throw new IllegalArgumentException("Attempted creating level with a negative axis-size");
		}
		
		//Check if the level-path already exists
		File file = new File(Level.PATH_LEVELS + name);
		if(file.exists()) {
			return false;
		}
		
		//If it doesn't exist, create it
		file.mkdirs();
		
		Level newLevel = new Level(name);
		
		File dataFile = new File(newLevel.getFilenameData());
		File mapFile = new File(newLevel.getFilenameMap());
		try {
			dataFile.createNewFile();
			mapFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Initialize the tiles of the level
		int[] tiles = new int[w * h];
		for(int i = 0; i < tiles.length; ++i) {
			tiles[i] = Tile.air.id;
		}
		
		//Initialize the entities of the level
		List<Entity> entities = new ArrayList<Entity>();
		
		LevelUtility.saveLevel(newLevel, entities, tiles, w, h);
		
		return true;
	}
}
