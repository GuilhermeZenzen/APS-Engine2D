package com.aps.game;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import com.aps.engine.util.Vector2;
import com.aps.game.components.Collider;
import com.aps.game.components.RectCollider;
import com.aps.game.components.ShapeCollider;
import com.aps.game.components.Sprite;
import com.aps.game.components.TileInfo;

public class GameData {
	private static int unlockedLevel = 1;
	
	public static void loadData() {
		int lineCount = 1;
		try {
			InputStream file = GameData.class.getResourceAsStream("/data/game-data.txt");
			if (file == null) return;
			BufferedReader reader = new BufferedReader(new InputStreamReader(file));

			String line = reader.readLine();
			String statsName = null;
			int stats[] = new int[7];
			Arrays.fill(stats, 1);
			int statsIndex = 0;
			CharacterStats characterStats = null;

			while (line != null) {
				String objects[] = line.split(";", 0);
				switch (objects[0]) {
				case "unlocked-level":
					unlockedLevel = Integer.parseInt(objects[1]);
					break;
				case "attribute-points":
					CharacterStats.setAttributePoints(Integer.parseInt(objects[1]));
					break;
				case "skill-points":
					CharacterStats.setSkillPoints(Integer.parseInt(objects[1]));
					break;
				case "character":
					statsName = objects[1];
					characterStats = new CharacterStats(statsName);
					Arrays.fill(stats, 1);
					statsIndex = 0;
					break;
				case "stats":
					if (characterStats != null) {
						stats[statsIndex] = Integer.parseInt(objects[1]);
						statsIndex++;
						
						if (statsIndex == 6) {
							characterStats.setStats(stats);
							characterStats = null;
							statsIndex = 0;
						}
					}
				}
				
				line = reader.readLine();
				lineCount++;
			}
			
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Erro na linha " + lineCount + ".");
		}
	}
	
	public static void saveData() {
		try {
			URL url = GameData.class.getResource("/data/game-data.txt");
			if (url == null) return;
			
			File file = new File(url.getPath());
			PrintWriter writer = new PrintWriter(file);			
			
			writer.println("unlocked-level;" + String.valueOf(unlockedLevel));
			writer.println("attribute-points;" + String.valueOf(CharacterStats.getAttributePoints()));
			writer.println("skill-points;" + String.valueOf(CharacterStats.getSkillPoints()));
			for (CharacterStats stats : CharacterStats.getCharactersStats()) {
				writer.println("character;" + stats.getName());
				writer.println("stats;" + String.valueOf(stats.getHealth()));
				writer.println("stats;" + String.valueOf(stats.getDefense()));
				writer.println("stats;" + String.valueOf(stats.getDamage()));
				writer.println("stats;" + String.valueOf(stats.getSkill1Level()));
				writer.println("stats;" + String.valueOf(stats.getSkill2Level()));
				writer.println("stats;" + String.valueOf(stats.getSkill3Level()));
				writer.println("stats;" + String.valueOf(stats.getUltimateSkillLevel()));
			}
			
			writer.close();
		} catch (IOException e) {
			System.out.println("Erro ao salvar a fase.");
		}
	}

	public static int getUnlockedLevel() {
		return unlockedLevel;
	}
}
