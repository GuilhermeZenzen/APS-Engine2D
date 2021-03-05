package com.aps.game;

import java.util.ArrayList;

import com.aps.engine.gfx.Image;
import com.aps.engine.util.Vector2;
import com.aps.game.components.Sprite;
import com.aps.game.components.RectCollider;
import com.aps.game.components.ShapeCollider;

public class Scene {
	private String name;
	private ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();
	private Vector2 playerSpawnPoint = null;
	
	private Vector2 gravity = new Vector2(0, 9.81f);
	
	public Scene(String name) {
		this.name = name;
	}

	// SCENE BUILDING
	public void buildTiles(Vector2 startPos, int amount, boolean up, String imagePath) {
		Vector2 tilesStartPos = startPos.add(Vector2.fill(0.5f));
		
		if (amount > 1) {
			GameObject tilePivot = GameManager.createGameObject("TilePivot");
			tilePivot.setPosition(startPos);
			
			for (int i = 0; i < amount; i++) {
				GameObject tile = GameManager.createGameObject("Tile");
				tile.createComponent(Sprite.class).setImage(new Image(imagePath));
				Vector2 pos = tilesStartPos.add(up ? new Vector2(0, i) : new Vector2(i, 0));
				tile.setPosition(pos);
				tile.setParent(tilePivot);
			}
			
			tilePivot.createComponent(RectCollider.class);
			tilePivot.getComponent(RectCollider.class).setProperties(up ? new Vector2(0.5f, amount / 2f) : new Vector2(amount / 2f, 0.5f),
																	 up ? new Vector2(1f, amount) : new Vector2(amount, 1f));
		} else {
			GameObject tile = GameManager.createGameObject("Tile");
			tile.createComponent(Sprite.class).setImage(new Image(imagePath));
			tile.setPosition(tilesStartPos);
			tile.createComponent(RectCollider.class);
		}
	}
	
	public void buildRamp(Vector2 startPos, Vector2 endPos) {
		GameObject ramp = GameManager.createGameObject("Ramp");
		ramp.setPosition(Vector2.center(startPos, endPos));
		
		float deltaX = Math.abs(endPos.getX() - startPos.getX()) / 2;
		float deltaY = Math.abs(endPos.getY() - startPos.getY()) / 2;
		
		ShapeCollider collider = ramp.createComponent(ShapeCollider.class);
		collider.setPoints(new Vector2(-deltaX, deltaY), new Vector2(deltaX, -deltaY), new Vector2(deltaX, -deltaY + 1f), new Vector2(-deltaX, deltaY + 1f));
	}
	
	public GameObject getGameObject(String tag) {
		for(GameObject go : gameObjects) {
			if(go.getTag().equals(tag)) {
				return go;
			}
		}
		
		return null;
	}
	
	public GameObject createGameObject(String tag) {
		GameObject go = new GameObject(tag);
		addGameObject(go);
		return go;
	}
	public void addGameObject(GameObject go) {
		gameObjects.add(go);
	}
	
	public void removeGameObject(GameObject go) {
		gameObjects.remove(go);
	}
	
	public ArrayList<GameObject> getGameObjects() {
		return gameObjects;
	}

	public void setGameObjects(ArrayList<GameObject> gameObjects) {
		this.gameObjects = gameObjects;
	}
	
	public Vector2 getGravity() {
		return gravity;
	}
	
	public void setGravity(Vector2 gravity) {
		this.gravity = gravity;
	}

	public Vector2 getPlayerSpawnPoint() {
		return playerSpawnPoint;
	}

	public void setPlayerSpawnPoint(Vector2 playerSpawnPoint) {
		this.playerSpawnPoint = playerSpawnPoint;
	}
}
