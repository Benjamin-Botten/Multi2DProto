package engine.world;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

import engine.world.entity.Entity;

/**
 * 
 * @author robot
 *
 */
public class Level {
	public static final String PATH_LEVELS = "src/resrc/data/level/";
	public static final String FILENAME_DATA = "/level.dat";
	public static final String FILENAME_MAP = "/level.map";
	
	
	private final String name;
	private final String filenameData;
	private final String filenameMap;
	
	public static final Level test1 = new Level("test1");
	public static final Level dungeontest1 = new Level("dungeontest1");
	
	public Level(String name) {
		this.name = name;

		filenameData = name + FILENAME_DATA;
		filenameMap = name + FILENAME_MAP;
	}
	
	public String getName() {
		return name;
	}
	
	public String getFullPath() {
		return (PATH_LEVELS + name);
	}
	
	public String getFilenameData() {
		return (getFullPath() + FILENAME_DATA);
	}
	
	public String getFilenameMap() {
		return (getFullPath() + FILENAME_MAP);
	}
}
