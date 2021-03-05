package com.aps.game;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.aps.engine.AbstractGame;
import com.aps.engine.GameController;
import com.aps.engine.Input;
import com.aps.engine.Renderer;
import com.aps.engine.gfx.Image;
import com.aps.engine.util.Vector2;
import com.aps.game.animation.AnimationController;
import com.aps.game.animation.AnimationList;
import com.aps.game.animation.AnimatorController;
import com.aps.game.components.Animator;
import com.aps.game.components.Camera;
import com.aps.game.components.Collider;
import com.aps.game.components.MainMenu;
import com.aps.game.components.PauseMenu;
import com.aps.game.components.Player;
import com.aps.game.components.PlayerSkillsManager;
import com.aps.game.components.PlayerUI;
import com.aps.game.components.RadioactiveWasteEnemy;
import com.aps.game.components.RectCollider;
import com.aps.game.components.Rigidbody;
import com.aps.game.components.ShapeCollider;
import com.aps.game.components.Sprite;
import com.aps.game.components.UIElement;

public class GameManager extends AbstractGame {
	private static ArrayList<Scene> scenes = new ArrayList<Scene>();
	private static Scene currentScene;
	private static Scene sceneToLoad;
	
	private static ArrayList<GameObject> objectsToCreate = new ArrayList<GameObject>();
	private static ArrayList<GameObject> objectsToDestroy = new ArrayList<GameObject>();

	public static final int PIXELS_PER_METER = 32;
	
	private static LevelCreator levelCreator;
	private static boolean loadLevelCreator;
	public static final float LEVEL_BOTTOM_LIMIT = 10f;
	
	private static GameManager instance;
	private static boolean loadPlayScene = false;
	
	public GameManager() {
		GameData.loadData();
		scenes.add(new Scene("Main Menu"));
		currentScene = scenes.get(0);
		LevelsManager.loadLevels();
		Physics.addLayer("Default", true);
		Physics.addLayer("Entity", true, false);
		Physics.addLayer("Player", true, false, false);
		
		Tileset.createTileset("grass", "environment", "dirt-grass-tile", 16, 16);
		Tileset.createTileset("radioactive-waste-enemy", "radioactive-waste-enemy", "radioactive-waste-enemy", 32, 32);
		Tileset.createTileset("radioactive-explosion", "radioactive-explosion", "radioactive-explosion", 192, 192);
		Tileset.createTileset("petase", "petase", "petase", 32, 32);
		AnimationList.loadAnimations("radioactive-waste-enemy", "radioactive-waste-enemy");
		AnimationList.loadAnimations("petase", "petase");
		AnimatorController.newController("radioactive-waste-enemy", "radioactive-waste-enemy");
		
		Skill skill1 = new Skill("Reborn 1", "petase-clone", false, 1, 10, 4);
		Skill skill2 = new Skill("Reborn 2", "petase-clone", false, 1, 10, 2);
		Skill skill3 = new Skill("Reborn 3", "petase-clone", false, 1, 10, 1);
		Skill skill4 = new Skill("Reborn 4", "petase-clone", true, 1, 500, 200);
		Character.getCharacters().add(new Character(CharacterStats.getCharactersStats().get(0), 100, 10, 50, 0.25f, 0.2f, 0.4f));
		Character.getCharacters().get(0).setSkills(skill1, skill2, skill3, skill4);
		
		createGameObject("Main Menu").createComponent(MainMenu.class);
		GameObject cameraGO = createGameObject("Camera"); 
		Camera camera = cameraGO.createComponent(Camera.class);
		cameraGO.setPosition(Vector2.zero());
	}
	
	public void update(GameController gameController, float deltaTime) {
		if (sceneToLoad != null) {
			if (levelCreator != null) {
				levelCreator.remove();
			}
			loadScene(sceneToLoad);
			Camera.getMainCamera().setShowColliders(false);
			if (loadLevelCreator) {
				levelCreator = new LevelCreator();
				levelCreator.loadLevel();
				loadLevelCreator = false;
			}
			
			sceneToLoad = null;
		}
		
		if (Input.isKeyDown(KeyEvent.VK_H)) {
			GameObject enemy = createGameObject("Enemy");
			enemy.createComponent(RadioactiveWasteEnemy.class);
			enemy.setPosition(new Vector2(-0.5f, -3));
		}
		
		for (GameObject obj : objectsToCreate) {
			currentScene.addGameObject(obj);
		}
		objectsToCreate = new ArrayList<GameObject>();
		
		for (int i = 0; i < objectsToDestroy.size(); i++) {
			GameObject go = objectsToDestroy.get(i);
			go.onDestroy();
			currentScene.removeGameObject(go);
			go.setParent(null);
			
			objectsToDestroy.addAll(go.getChildren());
		}
		objectsToDestroy = new ArrayList<GameObject>();
		
		for (GameObject gameObject : currentScene.getGameObjects()) {
			if(!gameObject.isDisabled()) {
				gameObject.physicsUpdate();				
			}
		}
		
		Physics.update();

		if (levelCreator != null) {
			levelCreator.update();
		}
		
		for (GameObject gameObject : currentScene.getGameObjects()) {
			if(!gameObject.isDisabled()) {
				gameObject.update(gameController, deltaTime);				
			}
		}
		
		AnimationController.update();
		
		for (GameObject gameObject : currentScene.getGameObjects()) {
			if(!gameObject.isDisabled()) {
				gameObject.lateUpdate();				
			}
		}
		
		Renderer.gameToScreenPixels();
		UI.update();
	}
	
	public void render(GameController gameController, Renderer renderer) {
		for(GameObject gameObject : currentScene.getGameObjects()) {
			if(!gameObject.isDisabled()) {
				gameObject.render(gameController, renderer);				
			}
		}
		
		UI.render();
	}

	public static void prepareSceneLoading(Scene scene) {
		sceneToLoad = scene;
	}
	
	public static void cancelSceneLoading() {
		sceneToLoad = null;
		loadLevelCreator = false;
	}
	
	private static void loadScene(Scene scene) {
		objectsToCreate.clear();
		objectsToDestroy.clear();
		Camera.clearSprites();
		UI.clearUIElements();
		Physics.clearColliders();
		
		for (GameObject go : scene.getGameObjects()) {
			Sprite sprite = go.getComponent(Sprite.class);
			UIElement uiElement = go.getComponent(UIElement.class);
			Collider collider = go.getComponent(Collider.class);

			if (sprite != null) {
				Camera.addSprite(sprite);
			}
			if (uiElement != null) {
				UI.addUIElement(uiElement);
			}
			if (collider != null) {
				Physics.addCollider(collider);
			}
		}
		
		currentScene = scene;
		currentScene.addGameObject(Camera.getMainCamera().getGameObject());
		Camera.getMainCamera().setShowColliders(loadLevelCreator);

		if (scene.getPlayerSpawnPoint() != null || loadLevelCreator) {
			createGameObject("Pause Menu").createComponent(PauseMenu.class);
			
			if (scene.getPlayerSpawnPoint() != null && !loadLevelCreator) {
				GameObject player = createGameObject("Player");
				player.createComponent(Player.class);
				player.setPosition(scene.getPlayerSpawnPoint());
			}
		}
	}
	
	public static boolean loadLevel(String level, boolean loadLevelCreator) {
		int lineCount = 1;
		
		try {
			InputStream file = GameManager.class.getResourceAsStream(level);
			if (file == null) return false;
			BufferedReader reader = new BufferedReader(new InputStreamReader(file));
			Scene scene = new Scene(level);
			
			String line = reader.readLine();
			
			GameObject currentAnchor = null;
			
			while (line != null) {
				String objects[] = line.split("/", 0);
				switch (objects[0]) {
				case "Player Spawn":
					String playerPos[] = objects[1].split(",", 0);
					scene.setPlayerSpawnPoint(new Vector2(Float.parseFloat(playerPos[0]), Float.parseFloat(playerPos[1])));
					break;
				case "Anchor":
					GameObject anchor = new GameObject("Tile Anchor");
					scene.addGameObject(anchor);
					String position[] = objects[1].split(",", 0);
					anchor.setPosition(new Vector2(Float.parseFloat(position[0]), Float.parseFloat(position[1])));
					currentAnchor = anchor;
					break;
				case "Rect Collider":
					RectCollider rect = currentAnchor.createComponent(RectCollider.class);
					String rectCenter[] = objects[1].split(",", 0);
					rect.setCenter(new Vector2(Float.parseFloat(rectCenter[0]), Float.parseFloat(rectCenter[1])));
					String size[] = objects[2].split(",", 0);
					rect.setSize(new Vector2(Float.parseFloat(size[0]), Float.parseFloat(size[1])));
					break;
				case "Shape Collider":
					ShapeCollider shape = currentAnchor.createComponent(ShapeCollider.class);
					
					for (int i = 1; i < objects.length; i++) {
						String point[] = objects[i].split(",", 0);
						shape.addPoint(new Vector2(Float.parseFloat(point[0]), Float.parseFloat(point[1])));
					}
					break;
				case "Tile":
					GameObject tile = new GameObject("Tile");
					scene.addGameObject(tile);
					tile.setParent(currentAnchor);
					String tilePosition[] = objects[1].split(",", 0);
					tile.setLocalPosition(new Vector2(Float.parseFloat(tilePosition[0]), Float.parseFloat(tilePosition[1])));
					Sprite sprite = tile.createComponent(Sprite.class);
					String image[] = objects[2].split(",", -1);
					sprite.setImage(Tileset.tilesets.get(image[0]).get(image[1]));
					String color[] = objects[3].split(",", -1);
					sprite.setColor(Integer.parseInt(color[0]) << 24 | Integer.parseInt(color[1]) << 16 | Integer.parseInt(color[2]) << 8 | Integer.parseInt(color[3]));
				}
				
				line = reader.readLine();
				lineCount++;
			}
			loadPlayScene = true;
			GameManager.loadLevelCreator = loadLevelCreator;
			scenes.add(scene);
			prepareSceneLoading(scene);
			
			reader.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Erro na linha " + lineCount + ".");
			return false;
		}
	}

	
	public static void instantiateGameObject(GameObject go) {
		objectsToCreate.add(go);
	}
	
	public static GameObject createGameObject(String tag) {
		GameObject go = new GameObject(tag);
		instantiateGameObject(go);
		return go;
	}
	
	public static void destroyGameObject(GameObject gameObject) {
		objectsToDestroy.add(gameObject);
	}
	
	public static int pixelsPerMeter() {
		int pixelsPM = (int)(PIXELS_PER_METER / Renderer.getScale());
		return pixelsPM % 2 == 0 ? pixelsPM : pixelsPM + 1;
	}
	
	public static void main(String args[]) {
		GameController.setWidth(320);
		GameController.setHeight(180);
		GameController.setScale(3f);
		instance = new GameManager();
		GameController gameController = new GameController(instance);
		gameController.start();
	}
	
	public static Scene getCurrentScene() {
		return currentScene;
	}

	public static ArrayList<Scene> getScenes() {
		return scenes;
	}

	public static void setLoadPlayScene(boolean loadPlayScene) {
		GameManager.loadPlayScene = loadPlayScene;
	}

	public static LevelCreator getLevelCreator() {
		return levelCreator;
	}

	public static void setLevelCreator(LevelCreator levelCreator) {
		GameManager.levelCreator = levelCreator;
	}
}
