package com.aps.game.animation;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

import com.aps.engine.gfx.Image;
import com.aps.engine.util.Vector2;
import com.aps.game.GameManager;
import com.aps.game.Tileset;
import com.aps.game.components.RectCollider;
import com.aps.game.components.ShapeCollider;
import com.aps.game.components.Sprite;

public class AnimationList {
	public static final Map<String, Map<String, Animation>> ANIMATIONS = new LinkedHashMap<>();
	
	public static void loadAnimations(String owner, String animationsPattern) {
		Map<String, Animation> animations = new LinkedHashMap<>();
		
		int lineCount = 1;
		
		try {
			InputStream file = GameManager.class.getResourceAsStream("/animations/" + animationsPattern + ".txt");
			if (file == null) return;
			BufferedReader reader = new BufferedReader(new InputStreamReader(file));
			
			String line = reader.readLine();
			Map<String, Image> currentTileset = null;
			Animation currentAnimation = null;
			int currentFrameIndex = 0;
			Image currentFrameImage = null;
			
			while (line != null) {
				String objects[] = line.split("/", 0);
				switch (objects[0]) {
				case "Tileset":
					currentTileset = Tileset.tilesets.get(objects[1]).getTiles();
					break;
				case "Animation":
					String name = objects[1];
					currentAnimation = new Animation(name, objects[2].equalsIgnoreCase("loop"));
					animations.put(name, currentAnimation);
					currentFrameIndex = 0;
					currentFrameImage = null;
					break;
				case "Frame":
					int frame = Integer.parseInt(objects[1]);
					if (frame - currentFrameIndex > 1) {
						for (int i = currentFrameIndex + 1; i < frame; i++) {
							currentAnimation.addFrame(currentFrameImage);
						}
					}
					
					Image image = currentTileset.get(objects[2]);
					currentAnimation.addFrame(image);
					currentFrameIndex = frame;
					currentFrameImage = image;
				}
				line = reader.readLine();
				lineCount++;
			}
			
			ANIMATIONS.put(owner, animations);

			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Erro na linha " + lineCount + ".");
		}
	}
}
