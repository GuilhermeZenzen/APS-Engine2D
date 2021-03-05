package com.aps.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.aps.engine.gfx.Image;

public class Tileset {
	public static Map<String, Tileset> tilesets = new LinkedHashMap<>();
	
	private String name;
	private String type;
	private Image icon;
	private Map<String, Image> tiles;
	
	public Tileset(String name, String type, String icon, Map<String, Image> tiles) {
		this.name = name;
		this.type = type;
		this.icon = tiles.get(icon);
		this.tiles = tiles;
	}
	
	public Image get(String key) {
		return tiles.get(key);
	}
	public Map<String, Image> getTiles() {
		return tiles;
	}
	
	public static String[] getTileInfo(Image image) {
		String info[] = new String[2];
		boolean found = false;
		for (Tileset tileset : tilesets.values()) {
			for (Entry<String, Image> tile : tileset.getTiles().entrySet()) {
				if (tile.getValue() == image) {
					info[0] = tileset.getName();
					info[1] = tile.getKey();
					found = true;
					break;
				}
			}
			
			if (found) {
				break;
			}
		}
		
		return info;
	}
	
	public static void createTileset(String tilesetName, String tilesetPattern, String tilesImageName, int tileWidth, int tileHeight) {
		createTileset(tilesetName, tilesetPattern, new Image("/tiles/" + tilesImageName + ".png"), tileWidth, tileHeight);
	}
	public static void createTileset(String tilesetName, String tilesetPattern, Image tilesImage, int tileWidth, int tileHeight) {
		ArrayList<Image> tiles = getTiles(tilesImage, tileWidth, tileHeight);
		
		if (tiles != null) {
			Tileset tileset = storeTiles(tilesetName, tiles, readTilesetPattern(tilesetPattern));
			tilesets.put(tilesetName, tileset);
		}
	}
	
	public static ArrayList<Image> getTiles(Image tiles, int tileWidth, int tileHeight) {
		if (tiles.getWidth() % tileWidth != 0 || tiles.getHeight() % tileHeight != 0) return null;
		
		int xTilesAmount = tiles.getWidth() / tileWidth;
		int yTilesAmount = tiles.getHeight() / tileHeight;
		
		ArrayList<Image> tileset = new ArrayList<Image>();
		
		int pixelsPerTile = tileWidth * tileHeight;

		for (int i = 0; i < yTilesAmount; i ++) {
			for (int j = 0; j < xTilesAmount; j++) {
				int pixels[] = new int[pixelsPerTile];
				boolean isEmpty = true;
				for (int k = 0; k < tileHeight; k++) {
					for (int l = 0; l < tileWidth; l++) {
						int pixelIndex = k * tileWidth + l;
						pixels[pixelIndex] = tiles.getPixels()[(tiles.getWidth() * i * tileHeight) + (j * tileWidth) + (tiles.getWidth() * k) + l];

						if (isEmpty) {
							int alpha = (pixels[pixelIndex] >> 24) & 0xff; 
							if (alpha != 0) {
								isEmpty = false;
							}
						}
					}
				}
				
				if (!isEmpty) {
					tileset.add(new Image(pixels, tileWidth, tileHeight));					
				}
			}
		}
		
		return tileset;
	}
	
	public static ArrayList<String> readTilesetPattern(String path) {
		ArrayList<String> tileNames = new ArrayList<String>();

		try {
			InputStreamReader file = new InputStreamReader(Tileset.class.getResourceAsStream("/tilesetpatterns/" + path + ".txt"));
			BufferedReader reader = new BufferedReader(file);
			String line = reader.readLine();

			while (line != null) {
				tileNames.add(line);
				line = reader.readLine();
			}
			
			reader.close();
			
			return tileNames;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Tileset storeTiles(String tilesetName, ArrayList<Image> tiles, ArrayList<String> tilesetPattern) {
		Map<String, Image> tileset = new LinkedHashMap<>();
		int unnamedTileIndex = 0;
		
		for (int i = 0; i < tiles.size(); i++) {
			if (i + 2 < tilesetPattern.size()) {
				tileset.put(tilesetPattern.get(i + 2), tiles.get(i));
			} else {
				tileset.put("tile_" + unnamedTileIndex, tiles.get(i));
				unnamedTileIndex++;
			}
		}
		
		return new Tileset(tilesetName, tilesetPattern.get(0), tilesetPattern.get(1), tileset);
	}
	
	public static Tileset getTileset(String tilesetName) {
		return tilesets.get(tilesetName);
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public Image getIcon() {
		return icon;
	}
}
