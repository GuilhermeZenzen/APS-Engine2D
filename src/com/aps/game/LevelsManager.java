package com.aps.game;

import java.util.Map;
import java.util.LinkedHashMap;

public class LevelsManager {
	private static Map<String, String> levels = new LinkedHashMap<String, String>();
	
	public static void loadLevels() {
		levels.put("Wasteland", "/levels/level-01.txt");
		levels.put("Iceland", "/levels/level-02.txt");
		levels.put("Sea", "/levels/level-03.txt");
		levels.put("Desert", "/levels/level-04.txt");
		levels.put("Smokeland", "/levels/level-05.txt");
		levels.put("Florest", "/levels/level-06.txt");
		levels.put("Mountain", "/levels/level-07.txt");
		levels.put("Polutionland", "/levels/level-08.txt");
		levels.put("Hotland", "/levels/level-09.txt");
		levels.put("Final Boss", "/levels/level-10.txt");
	}
	
	public static String levelPath(int index) {
		return (String)levels.values().toArray()[index];
	}
	public static String levelName(int index) {
		return (String)levels.keySet().toArray()[index];
	}
}
