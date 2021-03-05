package com.aps.game;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map.Entry;

import com.aps.engine.GameController;
import com.aps.engine.Input;
import com.aps.engine.Renderer;
import com.aps.engine.gfx.Image;
import com.aps.engine.util.Mathf;
import com.aps.engine.util.Vector2;
import com.aps.game.GameStateMachine.GameStates;
import com.aps.game.components.Book;
import com.aps.game.components.Button;
import com.aps.game.components.Camera;
import com.aps.game.components.Collider;
import com.aps.game.components.InputText;
import com.aps.game.components.RectCollider;
import com.aps.game.components.ShapeCollider;
import com.aps.game.components.Sprite;
import com.aps.game.components.Text;
import com.aps.game.components.Text.TextHorizontalAlignment;
import com.aps.game.components.Text.TextVerticalAlignment;
import com.aps.game.components.TileInfo;
import com.aps.game.components.TilesetInfo;
import com.aps.game.components.UIElement;
import com.aps.game.components.UIElement.Types;

public class LevelCreator {
	private Scene scene;
	private Camera camera;
	private GameObject ui;

	public enum Tools {
		TILE, ANCHOR, COLLISION, SPAWN;
	}
	private Tools currentTool = Tools.ANCHOR;
	
	private UIElement anchorTool;
	private final Image ANCHOR_TOOL_IMAGE = new Image("/anchor-tool.png");
	private final Image ANCHOR_TOOL_SELECTED_IMAGE = new Image("/anchor-tool-selected.png");
	
	private UIElement tileTool;
	private final Image TILE_TOOL_IMAGE = new Image("/tile-tool.png");
	private final Image TILE_TOOL_SELECTED_IMAGE = new Image("/tile-tool-selected.png");
	
	private UIElement collisionTool;
	private final Image COLLISION_TOOL_IMAGE = new Image("/collision-tool.png");
	private final Image COLLISION_TOOL_SELECTED_IMAGE = new Image("/collision-tool-selected.png");
	
	private UIElement spawnTool;
	
	private float actionCooldown = 0.05f;
	private float elapsedActionTime = 0;
	private boolean canAct = true;

	// ANCHOR
	private static final Image ANCHOR_IMAGE = new Image("/anchor.png");
	private static final Image SELECTED_ANCHOR_IMAGE = new Image("/anchor-selected.png");
	
	private ArrayList<Anchor> anchors = new ArrayList<Anchor>();
	private Anchor selectedAnchor;
	private AnchorSelector anchorSelector;
	private boolean movingAnchor = false;
	private float previousAnchorX;
	private float previousAnchorY;
	
	// TILE
	public enum TileTools {
		WRITE, ERASE, PICK;
	}
	private TileTools currentTileTool = TileTools.WRITE;
	
	private UIElement writeTool;
	private final Image WRITE_TOOL_IMAGE = new Image("/write-tool.png");
	private final Image WRITE_TOOL_SELECTED_IMAGE = new Image("/write-tool-selected.png");
	
	private UIElement eraseTool;
	private final Image ERASE_TOOL_IMAGE = new Image("/erase-tool.png");
	private final Image ERASE_TOOL_SELECTED_IMAGE = new Image("/erase-tool-selected.png");
	
	private UIElement pickTool;
	
	private GameObject tilesetsPanel;
	private Book tilesetsBook;
	
	private TileSelector tileSelector;
	private TileInfo selectedTile;
	private UIElement selectedTileIcon;
	private GameObject tilesetItemsPanel;
	private Button backToTilesetsButton;
	private Book tilesBook;
	private UIElement previousPageButton;
	private UIElement nextPageButton;
	private InputText tileAlphaInput;
	private InputText tileRedInput;
	private InputText tileGreenInput;
	private InputText tileBlueInput;
	private UIElement tileColorPreview;
	
	// COLLISION
	private final Image COLLISION_POINT_IMAGE = new Image("/collider-point.png");
	private final Image SELECTED_COLLISION_POINT_IMAGE = new Image("/collider-point-selected.png");
	private final Image RECT_COLLISION_IMAGE = new Image("/rect-collision.png");
	private final Image RECT_COLLISION_SELECTED_IMAGE = new Image("/rect-collision-selected.png");
	private final Image SHAPE_COLLISION_IMAGE = new Image("/shape-collision.png");
	private final Image SHAPE_COLLISION_SELECTED_IMAGE = new Image("/shape-collision-selected.png");
	
	public enum CollisionTypes {
		RECT, CIRCLE, SHAPE;
	}
	private CollisionTypes collisionType = CollisionTypes.RECT;
	
	private UIElement rectCollision;
	private UIElement shapeCollision;
	
	private Vector2 rectColliderStartPoint = null;
	private Vector2 rectColliderEndPoint = null;
	private Collision.CollisionPoint selectedCollisionPoint = null;
	private CollisionPointSelector collisionPointSelector;
	private boolean movingCollisionPoint = false;
	private Vector2 previousCollisionPointPos;
	
	// SPAWN
	public enum SpawnType {
		PLAYER, ENEMY;
	}
	private SpawnType spawnType = SpawnType.PLAYER;
	
	private UIElement playerSpawn;
	private UIElement enemySpawn;
	
	private Vector2 playerSpawnPoint = Vector2.zero();
	private UIElement playerSpawnPointIcon;
	
	// SAVE
	private Button saveButton;
	private UIElement savePanel;
	private InputText levelName;
	private Button cancelSaveButton;
	private Button confirmSaveButton;
	private boolean saving = false;
	private boolean sucessfulSaving = false;
	
	private GameStateEvent pauseListener;
	private GameStateEvent resumeListener;
	
	public LevelCreator() {
		this.scene = scene;
		GameManager.setLevelCreator(this);
		
		ui = GameManager.createGameObject("UI");
		anchorSelector = new AnchorSelector();
		collisionPointSelector = new CollisionPointSelector();
		ToolSelector toolSelector = new ToolSelector();
		anchorTool = UI.createUIElement("Anchor Tool", GameController.getScaledWidth() / 2 - 60, 0, 64, 64, 1, 0, 1, ANCHOR_TOOL_SELECTED_IMAGE, 0xffffffff, Types.IMAGE);
		anchorTool.addClickListener(toolSelector);
		anchorTool.getGameObject().setParent(ui);
		tileTool = UI.createUIElement("Tile Tool", GameController.getScaledWidth() / 2 + 2, 0, 64, 64, 1, 0, 1, TILE_TOOL_IMAGE, 0xffffffff, Types.IMAGE);
		tileTool.addClickListener(toolSelector);
		tileTool.getGameObject().setParent(ui);
		collisionTool = UI.createUIElement("Collision Tool", GameController.getScaledWidth() / 2, 0, 64, 64, 0, 0, 1, COLLISION_TOOL_IMAGE, 0xffffffff, Types.IMAGE);
		collisionTool.addClickListener(toolSelector);
		collisionTool.getGameObject().setParent(ui);
		spawnTool = UI.createUIElement("Spawn Tool", GameController.getScaledWidth() / 2 + 60, 0, 64, 64, 0, 0, 1, COLLISION_TOOL_IMAGE, 0xffffffff, Types.IMAGE);
		spawnTool.addClickListener(toolSelector);
		spawnTool.getGameObject().setParent(ui);
		
		TileToolSelector tileToolSelector = new TileToolSelector();
		writeTool = UI.createUIElement("Write Tool", GameController.getScaledWidth() / 2 - 30, 64, 64, 64, 1, 0, 1, WRITE_TOOL_IMAGE, 0xffffffff, Types.IMAGE);
		writeTool.addClickListener(tileToolSelector);
		writeTool.getGameObject().setParent(ui);
		eraseTool = UI.createUIElement("Erase Tool", GameController.getScaledWidth() / 2, 64, 64, 64, 0.5f, 0, 1, ERASE_TOOL_IMAGE, 0xffffffff, Types.IMAGE);
		eraseTool.addClickListener(tileToolSelector);
		eraseTool.getGameObject().setParent(ui);
		pickTool = UI.createUIElement("Picker Tool", GameController.getScaledWidth() / 2 + 30, 64, 64, 64, 0, 0, 1, ERASE_TOOL_IMAGE, 0xffffffff, Types.IMAGE);
		pickTool.addClickListener(tileToolSelector);
		pickTool.getGameObject().setParent(ui);
		tilesetsPanel = GameManager.createGameObject("Tilesets Panel");
		tilesetsPanel.setParent(ui);
		tilesetItemsPanel = GameManager.createGameObject("Tiles Panel");
		tilesetItemsPanel.setParent(tilesetsPanel);
		tilesetItemsPanel.setDisabled(true);
		backToTilesetsButton = UI.createButton("Back To Tilesets Button", 5, 39, 163, 34, 0, 1, 0, null, 0xff00ff9b, "<<<", 2, 0xffffffff);
		backToTilesetsButton.getGameObject().setParent(tilesetItemsPanel);
		backToTilesetsButton.addClickListener(new BackToTilesets());
		tilesBook = UI.createBook("TilesBook", 5, 40, 163, 409, 0, 0, 0, new Image("/tiles-inventory.png"), 0xffffffff, 4, 10, 1, 1);
		tilesBook.getGameObject().setParent(tilesetItemsPanel);
		PageNavigation pageNavigation = new PageNavigation();
		previousPageButton = UI.createUIElement("Previous Page Button", 86, 450, 81, 30, 1, 0, 1, new Image("/previous-page.png"), 0xffffffff, Types.IMAGE);
		previousPageButton.addClickListener(pageNavigation);
		previousPageButton.getGameObject().setParent(tilesetItemsPanel);
		nextPageButton = UI.createUIElement("Next Page Button", 86, 450, 81, 30, 0, 0, 1, new Image("/next-page.png"), 0xffffffff, Types.IMAGE);
		nextPageButton.addClickListener(pageNavigation);
		nextPageButton.getGameObject().setParent(tilesetItemsPanel);
		tileAlphaInput = UI.createInputText("Tile Alpha Input", 5, 481, 39, 20, 0, 0, 1, null, 0xff111111, 0xffffffff, 2);
		tileAlphaInput.setTypingRules(true, true, true);
		tileAlphaInput.setText("255");
		tileAlphaInput.getGameObject().setParent(tilesetItemsPanel);
		tileRedInput = UI.createInputText("Tile Red Input", 46, 481, 39, 20, 0, 0, 1, null, 0xff111111, 0xffffffff, 2);
		tileRedInput.setTypingRules(true, true, true);
		tileRedInput.setText("255");
		tileRedInput.getGameObject().setParent(tilesetItemsPanel);
		tileGreenInput = UI.createInputText("Tile Green Input", 87, 481, 39, 20, 0, 0, 1, null, 0xff111111, 0xffffffff, 2);
		tileGreenInput.setTypingRules(true, true, true);
		tileGreenInput.setText("255");
		tileGreenInput.getGameObject().setParent(tilesetItemsPanel);
		tileBlueInput = UI.createInputText("Tile Blue Input", 128, 481, 39, 20, 0, 0, 1, null, 0xff111111, 0xffffffff, 2);
		tileBlueInput.setTypingRules(true, true, true);
		tileBlueInput.setText("255");
		tileBlueInput.getGameObject().setParent(tilesetItemsPanel);
		tileColorPreview = UI.createUIElement("Tile Color Preview", 5, 502, 163, 20, 0, 0, 1, null, 0xffffffff, Types.IMAGE);
		tileColorPreview.getGameObject().setParent(tilesetItemsPanel);
		selectedTileIcon = UI.createUIElement("Current Tile", 0, 0, 32, 32, -0.2f, -0.2f, 0, null, 0xffffffff, Types.IMAGE);
		
		tileSelector = new TileSelector();
		fillTilesetsBook();
		
		CollisionTypeSelector collisionTypeSelector = new CollisionTypeSelector();
		rectCollision = UI.createUIElement("Rect Collision", GameController.getScaledWidth() / 2 + 2, 64, 64, 64, 1, 0, 1, RECT_COLLISION_SELECTED_IMAGE, 0xffffffff, Types.IMAGE);
		rectCollision.addClickListener(collisionTypeSelector);
		rectCollision.getGameObject().setParent(ui);
		shapeCollision = UI.createUIElement("Shape Collision", GameController.getScaledWidth() / 2, 64, 64, 64, 0, 0, 1, SHAPE_COLLISION_IMAGE, 0xffffffff, Types.IMAGE);
		shapeCollision.addClickListener(collisionTypeSelector);
		shapeCollision.getGameObject().setParent(ui);
		
		SpawnTypeSelector spawnTypeSelector = new SpawnTypeSelector();
		playerSpawn = UI.createUIElement("Player Spawn", GameController.getScaledWidth() / 2 + 2, 64, 64, 64, 1, 0, 1, RECT_COLLISION_SELECTED_IMAGE, 0xffffffff, Types.IMAGE);
		playerSpawn.addClickListener(spawnTypeSelector);
		playerSpawn.getGameObject().setParent(ui);
		Vector2 playerSpawnPointIconPos = Camera.worldToScreen(playerSpawnPoint);
		playerSpawnPointIcon = UI.createUIElement("Player Spawn Point Icon", (int)playerSpawnPointIconPos.getX(), (int)playerSpawnPointIconPos.getY(), 32, 32, 0.5f, 0.5f, 1, new Image("/player-spawn-point.png"), 0xffffffff, Types.IMAGE);
		playerSpawnPointIcon.setInteractable(false);
		playerSpawnPointIcon.getGameObject().setParent(ui);
		enemySpawn = UI.createUIElement("Enemy Collision", GameController.getScaledWidth() / 2, 64, 64, 64, 0, 0, 1, SHAPE_COLLISION_IMAGE, 0xffffffff, Types.IMAGE);
		enemySpawn.addClickListener(spawnTypeSelector);
		enemySpawn.getGameObject().setParent(ui);
		
		camera = Camera.getMainCamera();
		camera.setShowColliders(true);
		
		saveButton = UI.createButton("Save Button", GameController.getScaledWidth(), GameController.getScaledHeight(), 200, 50, 1, 1, 1, null, 0xff222222, "Save", 5, 0xffffffff);
		saveButton.addClickListener(new Save());
		saveButton.getGameObject().setParent(ui);
		savePanel = UI.createUIElement("Save Panel", GameController.getScaledWidth() / 2, GameController.getScaledHeight() / 2, 518, 178, 0.5f, 0.5f, 10, null, 0xcc333333, Types.IMAGE);
		savePanel.getGameObject().setParent(ui);
		levelName = UI.createInputText("Level Name", 0, 0, 503, 80, 0.5f, 1, 11, null, 0xcc888888, 0xffffffff, 1);
		levelName.getGameObject().setParent(savePanel.getGameObject());
		levelName.getGameObject().setLocalPosition(new Vector2(0, -3f));
		cancelSaveButton = UI.createButton("Cancel Save Button", 0, 0, 250, 80, 1, 0, 11, null, 0xcc888888, "Cancel", 4, 0xffffffff);
		cancelSaveButton.getGameObject().setParent(savePanel.getGameObject());
		cancelSaveButton.getGameObject().setLocalPosition(new Vector2(-3f, 3f));
		cancelSaveButton.addClickListener(new CancelSave());
		cancelSaveButton.setIgnorable(false);
		confirmSaveButton = UI.createButton("Cancel Save Button", 0, 0, 250, 80, 0, 0, 11, null, 0xcc888888, "Confirm", 4, 0xffffffff);
		confirmSaveButton.getGameObject().setParent(savePanel.getGameObject());
		confirmSaveButton.getGameObject().setLocalPosition(new Vector2(3f, 3f));
		confirmSaveButton.addClickListener(new ConfirmSave());
		confirmSaveButton.setIgnorable(false);
		
		pauseListener = new GameStateEvent() {
			public void onStateChange(GameStates gameState) {
				ui.setDisabled(true);
				camera.setShowColliders(false);
			}
		};
		resumeListener = new GameStateEvent() {
			public void onStateChange(GameStates gameState) {
				ui.setDisabled(false);
				camera.setShowColliders(true);
			}
		};
		GameStateMachine.addPauseListener(pauseListener);
		GameStateMachine.addResumeListener(resumeListener);
		
		openSavePanel(false);
		
		selectTool("Anchor Tool");
	}

	public void update() {
		if (saving) return;
		
		if (!canAct) {
			elapsedActionTime += GameController.getDeltaTime();
			
			if (elapsedActionTime > actionCooldown) {
				elapsedActionTime = 0;
				canAct = true;
			}
		}
		
		Renderer.setScale(Mathf.clamp(Renderer.getScale() + (float)(0.05 * Input.getScroll()), 0.05f, 1));
		
		float camVelocity = 16f / GameManager.pixelsPerMeter();
		if (Input.isKey(KeyEvent.VK_W)) {
			camera.getGameObject().move(0, -camVelocity);
		}
		if (Input.isKey(KeyEvent.VK_S)) {
			camera.getGameObject().move(0, camVelocity);
		}
		if (Input.isKey(KeyEvent.VK_D)) {
			camera.getGameObject().move(camVelocity, 0);
		}
		if (Input.isKey(KeyEvent.VK_A)) {
			camera.getGameObject().move(-camVelocity, 0);
		}
		
		camera.getGameObject().setPosition(new Vector2(Mathf.roundTo(camera.getGameObject().getPosition().getX(), GameManager.PIXELS_PER_METER), Mathf.roundTo(camera.getGameObject().getPosition().getY(), GameManager.PIXELS_PER_METER)));

		selectedTileIcon.getGameObject().setPosition(new Vector2(Input.getMouseX(), Input.getMouseY()));
		Vector2 mousePos = Camera.screenToWorld(Input.getMouseX(), Input.getMouseY());
		Vector2 mousePixelPos = new Vector2(Mathf.roundTo(mousePos.getX(), GameManager.PIXELS_PER_METER), Mathf.roundTo(mousePos.getY(), GameManager.PIXELS_PER_METER));
		float posX = Mathf.roundToHalf(mousePos.getX());
		float posY = Mathf.roundToHalf(mousePos.getY());
		int tileX = (int)(posX * 2);
		int tileY = (int)(posY * 2);
		
		if (movingAnchor) {
			previousAnchorX = selectedAnchor.getPosX();
			previousAnchorY = selectedAnchor.getPosY();
		}
		
		boolean canPlaceAnchor = true;
		
		tileColorPreview.setColor(getTileColor());
		
		for (Anchor anchor : anchors) {
			if (anchor.getTileX() == tileX && anchor.getTileY() == tileY) {
				canPlaceAnchor = false;
			}
		}
		
		if (movingAnchor) {
			if (canPlaceAnchor) {
				selectedAnchor.setPosition(posX, posY);
			}

			if (Input.isKeyDown(KeyEvent.VK_ESCAPE)) {
				selectedAnchor.setPosition(previousAnchorX, previousAnchorY);
				movingAnchor = false;
			}
		}
		
		if (Input.isMouseButton(3) && canAct && !movingAnchor && !movingCollisionPoint) {
			canAct = false;

			switch (currentTool) {
			case ANCHOR:
				if (canPlaceAnchor) {
					addAnchor(posX, posY);
				}
				
				break;
			case TILE:
				if (selectedAnchor != null) {
					if (currentTileTool == TileTools.WRITE) {
						if (selectedTile != null) {
							addTile(tileX, tileY);
						}
					} else if (currentTileTool == TileTools.ERASE) {
						for (int i = 0; i < selectedAnchor.getTiles().size(); i++) {
							Tile tile = selectedAnchor.getTiles().get(i);
							if (tile.getTileX() == tileX && tile.getTileY() == tileY) {
								tile.removeTile();
								selectedAnchor.getTiles().remove(tile);
								i--;
							}
						}
					} else {
						Sprite pickedTile = null;
						for (Anchor anchor : anchors) {
							for (Tile tile : anchor.getTiles()) {
								if (tile.getTileX() == tileX && tile.getTileY() == tileY) {
									Sprite tileSprite = tile.getGameObject().getComponent(Sprite.class);
									if (pickedTile == null) {
										pickedTile = tileSprite;
									} else if (pickedTile.getDepth() < tileSprite.getDepth()) {
										pickedTile = tileSprite;
									}
								}
							}
						}
						
						if (pickedTile != null) {
							TileInfo tileInfo = pickedTile.getGameObject().getComponent(TileInfo.class);
							selectTile(tileInfo);
							
							if (Input.isKey(KeyEvent.VK_CONTROL)) {
								setTileColor(tileInfo.getColor());
							}
						}
					}
				}

				break;
			case COLLISION:
				if (selectedAnchor != null) {
					elapsedActionTime -= 0.3f;
					GameObject go = selectedAnchor.getGameObject();
					Collider collider = go.getComponent(Collider.class);
					
					if (collisionType == CollisionTypes.RECT) {
						if (collider == null || !(collider instanceof RectCollider)) {
							if (collider != null && !(collider instanceof RectCollider)) {
								go.removeComponent(Collider.class);
								selectedAnchor.getCollision().clearPoints();
							}
							RectCollider rect = go.createComponent(RectCollider.class);
							rectColliderStartPoint = mousePixelPos;
							rect.setProperties(rectColliderStartPoint.subtract(go.getPosition()), Vector2.zero());
							selectedAnchor.setCollision(new Collision(rect));
						} else {
							if (rectColliderStartPoint != null) {
								float width = Math.abs(mousePixelPos.getX() - rectColliderStartPoint.getX());
								float height = Math.abs(mousePixelPos.getY() - rectColliderStartPoint.getY());
								((RectCollider)collider).setProperties(Vector2.center(rectColliderStartPoint, mousePixelPos).subtract(go.getPosition()), 
										                               new Vector2(width, height));
								rectColliderStartPoint = null;
								selectedAnchor.getCollision().updatePoints();
								selectCollisionPoint(null);
							} else {
								rectColliderStartPoint = mousePixelPos;
								((RectCollider)collider).setProperties(rectColliderStartPoint.subtract(go.getPosition()), Vector2.zero());
								selectedAnchor.getCollision().updatePoints();
							}
						}
					}
					else if (collisionType == CollisionTypes.SHAPE) {
						if (collider == null || !(collider instanceof ShapeCollider)) {
							if (collider != null && !(collider instanceof ShapeCollider)) {
								go.removeComponent(Collider.class);
								selectedAnchor.getCollision().clearPoints();
							}
							ShapeCollider shape = go.createComponent(ShapeCollider.class);
							shape.addPoint(mousePixelPos.subtract(go.getPosition()));
							selectedAnchor.setCollision(new Collision(shape));
						} else {
							((ShapeCollider)collider).addPoint(mousePixelPos.subtract(go.getPosition()));
							selectedAnchor.getCollision().updatePoints();
						}
					}
				}
				break;
			case SPAWN:
				if (spawnType == SpawnType.PLAYER) {
					playerSpawnPoint = mousePos;
				}
			}
		}
		
		if (Input.isKeyDown(KeyEvent.VK_T) && selectedAnchor != null && !movingCollisionPoint) {
			moveAnchor(selectedAnchor);
		}
		
		if (selectedCollisionPoint != null && !movingAnchor) {
			Collider collider = selectedAnchor.getCollision().getCollider();
			
			if (Input.isKeyDown(KeyEvent.VK_F)) {
				movingCollisionPoint = !movingCollisionPoint;
				
				if (movingCollisionPoint) {
					if (collider instanceof ShapeCollider) {
						previousCollisionPointPos = ((ShapeCollider)collider).getPoints().get(selectedAnchor.getCollision().getPoints().indexOf(selectedCollisionPoint));
					} else {
						int selectedPointIndex = selectedAnchor.getCollision().getPoints().indexOf(selectedCollisionPoint);
						previousCollisionPointPos = ((RectCollider)collider).getPoints()[selectedPointIndex];
						rectColliderStartPoint = ((RectCollider)collider).getPoints()[(selectedPointIndex + 2) % ((RectCollider)collider).getPoints().length];
					}
				} else {
					rectColliderStartPoint = null;
				}
			}
			
			if (Input.isKeyDown(KeyEvent.VK_ESCAPE) && movingCollisionPoint) {
				if (collider instanceof ShapeCollider) {
					((ShapeCollider)collider).getPoints().set(selectedAnchor.getCollision().getPoints().indexOf(selectedCollisionPoint), previousCollisionPointPos);
				} else {
					((RectCollider)collider).setBounds(rectColliderStartPoint, previousCollisionPointPos);
					rectColliderStartPoint = null;
				}
				
				movingCollisionPoint = false;
			}
		}
		
		if (movingCollisionPoint) {
			if (selectedAnchor.getCollision().getCollider() instanceof ShapeCollider) {
				ShapeCollider shape = (ShapeCollider)selectedAnchor.getCollision().getCollider();
				shape.getPoints().set(selectedAnchor.getCollision().getPoints().indexOf(selectedCollisionPoint), mousePixelPos);
			} else {
				RectCollider rect = (RectCollider)selectedAnchor.getCollision().getCollider();
				rect.setBounds(rectColliderStartPoint, mousePixelPos);
			}
		}
		
		if (Input.isKeyDown(KeyEvent.VK_Q)) {
			tilesBook.turnPageBack();
		}
		if (Input.isKeyDown(KeyEvent.VK_E)) {
			tilesBook.turnPage();
		}
		
		if (Input.isKeyDown(KeyEvent.VK_V)) {
			UIElement tile = tilesBook.getItem(getTileIndex(selectedTile.getGameObject().getComponent(UIElement.class)) - 1);
			if (tile != null) {
				selectTile(tile.getGameObject().getComponent(TileInfo.class));
			}
		}
		if (Input.isKeyDown(KeyEvent.VK_B)) {
			UIElement tile = tilesBook.getItem(getTileIndex(selectedTile.getGameObject().getComponent(UIElement.class)) + 1);
			if (tile != null) {
				selectTile(tile.getGameObject().getComponent(TileInfo.class));
			}
		}
		
		if (Input.isKeyDown(KeyEvent.VK_R)) {
			if (currentTool == Tools.TILE) {
				currentTileTool = currentTileTool == TileTools.WRITE ? TileTools.ERASE : currentTileTool == TileTools.ERASE ? TileTools.PICK : TileTools.WRITE;
				updateSelectedTileTool();
			} else if (currentTool == Tools.COLLISION) {
				selectCollisionType(collisionType == CollisionTypes.RECT ? "Shape Collision" : "Rect Collision");
			}
		}
	
		if (selectedAnchor != null) {
			if (Input.isKeyDown(KeyEvent.VK_X)) {
				removeAnchor(selectedAnchor);
				movingAnchor = false;
				movingCollisionPoint = false;
			} else {
				if (Input.isKeyDown(KeyEvent.VK_C)) {
					if (currentTool == Tools.TILE) {
						selectedAnchor.clearTiles();
					} else if (currentTool == Tools.COLLISION) {
						if (selectedAnchor.getCollision() != null) {
							if (movingCollisionPoint) {
								movingCollisionPoint = false;
								rectColliderStartPoint = null;
							}
							selectCollisionPoint(null);
							selectedAnchor.getGameObject().removeComponent(Collider.class);
							selectedAnchor.getCollision().clearPoints();
							selectedAnchor.setCollision(null);
							rectColliderStartPoint = null;
						}
					}
				}
				if (Input.isKey(KeyEvent.VK_CONTROL) && Input.isKeyDown(KeyEvent.VK_QUOTE) && !movingCollisionPoint) {
					copyAnchor(selectedAnchor);
				}
			}
		}
		
		if (selectedCollisionPoint != null) {
			if (collisionType == CollisionTypes.SHAPE) {
				if (Input.isKeyDown(KeyEvent.VK_G)) {
					removeCollisionPoint(selectedCollisionPoint);
					movingCollisionPoint = false;
				}
			}
		}

		if (Input.isKeyDown(KeyEvent.VK_1)) {
			selectTool("Anchor Tool");
		}
		if (Input.isKeyDown(KeyEvent.VK_2)) {
			selectTool("Tile Tool");
		}
		if (Input.isKeyDown(KeyEvent.VK_3)) {
			selectTool("Collision Tool");
		}
		if (Input.isKeyDown(KeyEvent.VK_4)) {
			selectTool("Spawn Tool");
		}
		
		if (selectedTile == null) {
			selectedTileIcon.setColor(0x00000000);
		} else {
			int tileColor = getTileColor();
			selectedTileIcon.setColor(getTileColor());
			selectedTile.setColor(tileColor);
		}
		
		if (selectedAnchor != null) {
			if (selectedAnchor.getCollision() != null) {
				selectedAnchor.getCollision().getCollider().updateGlobalCenter();
				selectedAnchor.getCollision().updatePoints();
			}
		}
		
		for (Anchor anchor : anchors) {
			anchor.updateIconPosition();
		}
		
		playerSpawnPointIcon.getGameObject().setPosition(Camera.worldToScreen(playerSpawnPoint));
	}
	
	private void openSavePanel(boolean open) {
		savePanel.getGameObject().setDisabled(!open);
		saving = open;
		
		if (open) {
			levelName.lock();
			levelName.focus();
		} else {
			levelName.setText("");
			levelName.unlock();
			levelName.defocus();
		}
	}
	
	public void loadLevel() {
		playerSpawnPoint = GameManager.getCurrentScene().getPlayerSpawnPoint() != null ? GameManager.getCurrentScene().getPlayerSpawnPoint() : Vector2.zero();
		
		for (GameObject go : GameManager.getCurrentScene().getGameObjects()) {
			if (go.getTag().contentEquals("Tile Anchor")) {
				Anchor anchor = addAnchor(go.getPosition().getX(), go.getPosition().getY(), go);
				
				Collider collider = go.getComponent(Collider.class);
				
				if (collider != null) {
					anchor.setCollision(new Collision(collider));
				}
				
				for (GameObject child : go.getChildren()) {
					addTile(anchor, child.getComponent(Sprite.class));
				}
			}
		}
	}
	
	private void saveLevel(String levelName) {
		try {
			URL url = this.getClass().getResource("/levels/" + levelName + ".txt");
			if (url == null) return;
			
			File file = new File(url.getPath());
			PrintWriter writer = new PrintWriter(file);			
			ArrayList<GameObject> savedGameObjects = new ArrayList<GameObject>();
			
			writer.println("Player Spawn/" + String.valueOf(playerSpawnPoint.getX() + "," + String.valueOf(playerSpawnPoint.getY())));
			
			for (GameObject go : GameManager.getCurrentScene().getGameObjects()) {
				if (!savedGameObjects.contains(go)) {

					if (go.getTag().equalsIgnoreCase("Tile Anchor")) {
						savedGameObjects.add(go);
						writer.println("Anchor/" + String.valueOf(go.getPosition().getX()) + "," + String.valueOf(go.getPosition().getY()));
						Collider collider = go.getComponent(Collider.class);
						if (collider != null) {
							if (collider instanceof RectCollider) {
								RectCollider rect = (RectCollider)collider;
								writer.println("Rect Collider/" + String.valueOf(rect.getCenter().getX()) + "," 
								+ String.valueOf(rect.getCenter().getY()) + "/" + String.valueOf(rect.getSize().getX()) + "," 
								+ String.valueOf(rect.getSize().getY()));
							} else if (collider instanceof ShapeCollider) {
								ShapeCollider shape = (ShapeCollider)collider;
								writer.print("Shape Collider");
								for (Vector2 point : shape.getPoints()) {
									writer.print("/" + String.valueOf(point.getX()) + "," + String.valueOf(point.getY()));
								}
								writer.println();
							}
						}

						for (GameObject tile : go.getChildren()) {
							TileInfo tileInfo = tile.getComponent(TileInfo.class);
							writer.println("Tile/" + String.valueOf(tile.getLocalPosition().getX()) + "," + String.valueOf(tile.getLocalPosition().getY())
							+ "/" + tileInfo.getTileset() + "," + tileInfo.getTileName() + "/" + String.valueOf((tileInfo.getColor() >> 24) & 0xff) + ","
							+ String.valueOf((tileInfo.getColor() >> 16) & 0xff) + "," + String.valueOf((tileInfo.getColor() >> 8) & 0xff) + ","
							+ String.valueOf(tileInfo.getColor() & 0xff));
							savedGameObjects.add(tile);
						}
					}
				}
			}
			
			writer.close();
			sucessfulSaving = true;
		} catch (IOException e) {
			System.out.println("Erro ao salvar a fase.");
		}
	}
	
	public void remove() {
		GameStateMachine.removePauseListener(pauseListener);
		GameStateMachine.removeResumeListener(resumeListener);
	}
	
	private void selectTool(String tool) {
		anchorTool.setImage(ANCHOR_TOOL_IMAGE);
		tileTool.setImage(TILE_TOOL_IMAGE);
		collisionTool.setImage(COLLISION_TOOL_IMAGE);
		spawnTool.setImage(COLLISION_TOOL_IMAGE);
		selectedTileIcon.getGameObject().setDisabled(true);
		enableCollisionTypeSelectors(false);
		enableTileToolSelectors(false);
		tilesetsPanel.setDisabled(true);
		enableSpawnTypeSelectors(false);
		
		switch (tool) {
		case "Anchor Tool":
			currentTool = Tools.ANCHOR;
			anchorTool.setImage(ANCHOR_TOOL_SELECTED_IMAGE);
			break;
		case "Tile Tool":
			currentTool = Tools.TILE;
			tileTool.setImage(TILE_TOOL_SELECTED_IMAGE);
			enableTileToolSelectors(true);
			updateSelectedTileTool();
			tilesetsPanel.setDisabled(false);
			break;
		case "Collision Tool":
			currentTool = Tools.COLLISION;
			collisionTool.setImage(COLLISION_TOOL_SELECTED_IMAGE);
			enableCollisionTypeSelectors(true);
			break;
		case "Spawn Tool":
			currentTool = Tools.SPAWN;
			spawnTool.setImage(COLLISION_TOOL_SELECTED_IMAGE);
			enableSpawnTypeSelectors(true);
		}
	}
	
	private void selectAnchor(Anchor anchor) {
		if (selectedAnchor != null) {
			selectedAnchor.getIcon().setImage(ANCHOR_IMAGE);
		}
		
		if (selectedAnchor != anchor) {
			rectColliderStartPoint = null;
			
			if (selectedAnchor != null ? selectedAnchor.getCollision() != null : false) {
				selectedAnchor.getCollision().enablePoints(false);
				selectCollisionPoint(null);
			}
		}

		selectedAnchor = anchor;
		selectedAnchor.getIcon().setImage(SELECTED_ANCHOR_IMAGE);
		if (selectedAnchor.getCollision() != null) {
			selectedAnchor.getCollision().enablePoints(true);
		}
	}
	
	private Anchor addAnchor(float posX, float posY) {
		return addAnchor(posX, posY, null);
	}
	private Anchor addAnchor(float posX, float posY, GameObject go) {
		if (go == null) {
			go = GameManager.createGameObject("Tile Anchor");
		}
		Anchor anchor = new Anchor(go, posX, posY);
		anchor.getIcon().addClickListener(anchorSelector);
		anchor.getIcon().getGameObject().setParent(ui);
		anchors.add(anchor);
		selectAnchor(anchor);
		return anchor;
	}
	
	private void copyAnchor(Anchor anchor) {
		Anchor newAnchor = addAnchor(anchor.getPosX(), anchor.getPosY());
		for (Tile tile : anchor.getTiles()) {
			TileInfo tileInfo = tile.getGameObject().getComponent(TileInfo.class);
			addTile(newAnchor, tileInfo.getTileset(), tileInfo.getTileName(), tileInfo.getColor(), tile.getTileX(), tile.getTileY());
		}
		if (anchor.getCollision() != null) {
			if (anchor.getCollision().getCollider() instanceof RectCollider) {
				RectCollider rect = (RectCollider)anchor.getCollision().getCollider();
				RectCollider newRect = newAnchor.getGameObject().createComponent(RectCollider.class);
				newRect.setProperties(new Vector2(rect.getCenter()), new Vector2(rect.getSize()));
				newAnchor.setCollision(new Collision(newRect));
			} else {
				ShapeCollider shape = (ShapeCollider)anchor.getCollision().getCollider();
				ShapeCollider newShape = newAnchor.getGameObject().createComponent(ShapeCollider.class);
				for (Vector2 point : shape.getPoints()) {
					newShape.addPoint(new Vector2(point));
				}
				newAnchor.setCollision(new Collision(newShape));
			}
		}
		if (!movingAnchor) {
			moveAnchor(newAnchor);
		}
	}
	
	private void moveAnchor(Anchor anchor) {
		movingAnchor = !movingAnchor;
	}
	
	private void removeAnchor(Anchor anchor) {
		GameManager.destroyGameObject(anchor.getIcon().getGameObject());
		anchors.remove(anchor);
		GameManager.destroyGameObject(anchor.getGameObject());
		if (anchor.getCollision() != null) {
			anchor.getCollision().clearPoints();
			selectedCollisionPoint = null;
		}
		selectedAnchor = null;
	}
	
	private void fillTilesetsBook() {
		tilesetsBook = UI.createBook("Tilesets Book", 5, 5, 163, GameController.getScaledHeight() - 11, 0, 0, 0, null, 0xff555555, 1, 10, 1, 1);
		tilesetsBook.getGameObject().setParent(tilesetsPanel);
		OpenTileset openTileset = new OpenTileset();
		
		for (Tileset tileset : Tileset.tilesets.values()) {
			if (tileset.getType().contentEquals("environment")) {
				UIElement tilesetBackground = UI.createUIElement("Tileset Background", 0, 0, 163, 52, 0, 0, 1, null, 0xff999999, Types.IMAGE);
				tilesetBackground.addClickListener(openTileset);
				TilesetInfo tilesetInfo = tilesetBackground.getGameObject().createComponent(TilesetInfo.class);
				tilesetInfo.setTilesetName(tileset.getName());
				tilesetsBook.addItem(tilesetBackground);
				UIElement tilesetIcon = UI.createUIElement("Tileset Icon", 0, 0, 48, 48, 0, 0, 2, tileset.getIcon(), 0xffffffff, Types.IMAGE);
				tilesetIcon.setInteractable(false);
				tilesetIcon.getGameObject().setParent(tilesetBackground.getGameObject());
				tilesetIcon.getGameObject().setLocalPosition(new Vector2(2, 2));
				Text tilesetName = UI.createText("Tileset Name", 0, 0, 111, 48, 0, 0, 2, 0xffffffff, tileset.getName(), 2, TextHorizontalAlignment.CENTER, TextVerticalAlignment.CENTER);
				tilesetName.setInteractable(false);
				tilesetName.getGameObject().setParent(tilesetBackground.getGameObject());
				tilesetName.getGameObject().setLocalPosition(new Vector2(52, 2));
			}
		}
	}
	
	private void openTileset(String tilesetName) {
		int i = 0;
		int size = tilesBook.getSize();
		Tileset tileset = Tileset.tilesets.get(tilesetName);
		for (Entry<String, Image> tile : tileset.getTiles().entrySet()) {
			TileInfo tileInfo = null;
			
			if (i >= size) {
				UIElement item = UI.createUIElement("TileItem", 0, 0, 40, 40, 0, 0, 0, tile.getValue(), 0xffffffff, Types.IMAGE);
				tileInfo = item.getGameObject().createComponent(TileInfo.class);
				item.addClickListener(tileSelector);
				tilesBook.addItem(item);
			} else {
				UIElement item = tilesBook.getItem(i);
				item.setImage(tile.getValue());
				tileInfo = item.getGameObject().getComponent(TileInfo.class);
			}
			
			tileInfo.setTileset(tilesetName);
			tileInfo.setTileName(tile.getKey());
			tileInfo.setColor(0xffffffff);
			
			if (i == tileset.getTiles().size() - 1) {
				for (int j = i + 1; j < size; j++) {
					tilesBook.removeItem(j);
				}
			}
			
			i++;
		}
		tilesetItemsPanel.setDisabled(false);
		tilesetsBook.getGameObject().setDisabled(true);
	}
	
	private void backToTilesets() {
		tilesetsBook.getGameObject().setDisabled(false);
		tilesetItemsPanel.setDisabled(true);
	}
	
	private void enableTileToolSelectors(boolean enable) {
		writeTool.getGameObject().setDisabled(!enable);
		eraseTool.getGameObject().setDisabled(!enable);
		pickTool.getGameObject().setDisabled(!enable);
	}
	
	private void updateSelectedTileTool() {
		eraseTool.setImage(ERASE_TOOL_IMAGE);
		writeTool.setImage(WRITE_TOOL_IMAGE);
		pickTool.setImage(ERASE_TOOL_IMAGE);
		selectedTileIcon.getGameObject().setDisabled(true);
		switch (currentTileTool) {
		case WRITE:
			selectedTileIcon.getGameObject().setDisabled(false);
			writeTool.setImage(WRITE_TOOL_SELECTED_IMAGE);
			break;
		case ERASE:
			eraseTool.setImage(ERASE_TOOL_SELECTED_IMAGE);
			break;
		case PICK:
			selectedTileIcon.getGameObject().setDisabled(false);
			pickTool.setImage(ERASE_TOOL_SELECTED_IMAGE);
		}
	}
	
	private void selectTileTool(String tag) {
		currentTileTool = tag.contentEquals("Write Tool") ? TileTools.WRITE : tag.contentEquals("Erase Tool") ? TileTools.ERASE : TileTools.PICK;
		updateSelectedTileTool();
	}
	
	private void selectTile(TileInfo tile) {
		selectedTile = tile;
		selectedTileIcon.setImage(Tileset.tilesets.get(tile.getTileset()).get(tile.getTileName()));
	}
	
	private void addTile(int tileX, int tileY) {
		addTile(selectedAnchor, selectedTile.getTileset(), selectedTile.getTileName(), selectedTile.getColor(), tileX, tileY);
	}
	private void addTile(Anchor anchor, String tileset, String tileName, int color, int tileX, int tileY) {
		GameObject newTile = new GameObject("Tile");
		Sprite sprite = newTile.createComponent(Sprite.class);
		sprite.setImage(Tileset.tilesets.get(tileset).get(tileName));
		sprite.setColor(color);
		newTile.setPosition(new Vector2(tileX / 2f, tileY / 2f));
		newTile.setParent(anchor.getGameObject());
		TileInfo tileInfo = newTile.createComponent(TileInfo.class);
		tileInfo.setTileset(tileset);
		tileInfo.setTileName(tileName);
		tileInfo.setColor(color);

		boolean overrideTile = false;
		for (Tile tile : anchor.getTiles()) {
			if (tile.getTileX() == tileX && tile.getTileY() == tileY) {
				tile.setGameObject(newTile);
				overrideTile = true;
				break;
			}
		}
			
		if (!overrideTile) {
			anchor.getTiles().add(new Tile(newTile, tileX, tileY));
		}
	}
	private void addTile(Anchor anchor, Sprite tile) {
		TileInfo tileInfo = tile.getGameObject().createComponent(TileInfo.class);
		String info[] = Tileset.getTileInfo(tile.getImage());
		tileInfo.setTileset(info[0]);
		tileInfo.setTileName(info[1]);
		tileInfo.setColor(tile.getColor());
		anchor.getTiles().add(new Tile(tile.getGameObject(), (int)(tile.getGameObject().getPosition().getX() * 2), (int)(tile.getGameObject().getPosition().getY() * 2), false));
	}
	
	private int getTileIndex(UIElement tile) {
		return tilesBook.indexOf(tile);
	}
	
	private int getTileColor() {
		int alpha = tileAlphaInput.getText().isEmpty() ? 0 : (int)Mathf.clamp(Integer.parseInt(tileAlphaInput.getText()), 0, 255) << 24;
		int red = tileRedInput.getText().isEmpty() ? 0 : (int)Mathf.clamp(Integer.parseInt(tileRedInput.getText()), 0, 255) << 16;
		int green = tileGreenInput.getText().isEmpty() ? 0 : (int)Mathf.clamp(Integer.parseInt(tileGreenInput.getText()), 0, 255) << 8;
		int blue = tileBlueInput.getText().isEmpty() ? 0 : (int)Mathf.clamp(Integer.parseInt(tileBlueInput.getText()), 0, 255);
		return alpha | red | green | blue;
	}
	private void setTileColor(int color) {
		tileAlphaInput.setText(String.valueOf((color >> 24) & 0xff));
		tileRedInput.setText(String.valueOf((color >> 16) & 0xff));
		tileGreenInput.setText(String.valueOf((color >> 8) & 0xff));
		tileBlueInput.setText(String.valueOf(color & 0xff));
		tileColorPreview.setColor(color);
	}
	
	private void enableCollisionTypeSelectors(boolean enable) {
		rectCollision.getGameObject().setDisabled(!enable);
		shapeCollision.getGameObject().setDisabled(!enable);
	}
	
	private void selectCollisionType(String type) {
		if (type.equalsIgnoreCase("Rect Collision")) {
			collisionType = CollisionTypes.RECT;
			rectCollision.setImage(RECT_COLLISION_SELECTED_IMAGE);
			shapeCollision.setImage(SHAPE_COLLISION_IMAGE);
		} else {
			collisionType = CollisionTypes.SHAPE;
			rectCollision.setImage(RECT_COLLISION_IMAGE);
			shapeCollision.setImage(SHAPE_COLLISION_SELECTED_IMAGE);
		}
	}
	
	private void removeCollisionPoint(Collision.CollisionPoint collisionPoint) {
		int index = selectedAnchor.getCollision().getPoints().indexOf(collisionPoint);
		((ShapeCollider)selectedAnchor.getCollision().getCollider()).getPoints().remove(index);
		selectedAnchor.getCollision().updatePoints();
		selectCollisionPoint(null);
	}
	
	private void selectCollisionPoint(Collision.CollisionPoint collisionPoint) {
		if (movingCollisionPoint) return;
		
		if (selectedCollisionPoint != null) {
			selectedCollisionPoint.getIcon().setImage(COLLISION_POINT_IMAGE);
		}
			
		selectedCollisionPoint = collisionPoint;

		if (selectedCollisionPoint != null) {
			selectedCollisionPoint.getIcon().setImage(SELECTED_COLLISION_POINT_IMAGE);
		}
	}
	
	private void enableSpawnTypeSelectors(boolean enable) {
		playerSpawn.getGameObject().setDisabled(!enable);
		enemySpawn.getGameObject().setDisabled(!enable);
	}
	
	private void selectSpawnType(String tag) {
		playerSpawn.setImage(ERASE_TOOL_IMAGE);
		enemySpawn.setImage(COLLISION_TOOL_IMAGE);
		
		if (tag.contentEquals("Player Spawn")) {
			spawnType = SpawnType.PLAYER;
			playerSpawn.setImage(ERASE_TOOL_SELECTED_IMAGE);
		} else {
			spawnType = SpawnType.ENEMY;
			enemySpawn.setImage(COLLISION_TOOL_SELECTED_IMAGE);
		}
	}
	
	class Save implements UIOnClick {
		public void onClick(UIElement element) {
			openSavePanel(true);
		}
	}
	class ConfirmSave implements UIOnClick {
		public void onClick(UIElement element) {
			if (!levelName.getText().isEmpty()) {
				saveLevel(levelName.getText());	
				
				if (sucessfulSaving) {
					openSavePanel(false);
					sucessfulSaving = false;
				} else {
					levelName.focus();
				}
			}
		}
	}
	class CancelSave implements UIOnClick {
		public void onClick(UIElement element) {
			openSavePanel(false);
		}
	}

	class ToolSelector implements UIOnClick {
		public void onClick(UIElement element) {
			selectTool(element.getGameObject().getTag());
		}
	}
	
	class AnchorSelector implements UIOnClick {
		public void onClick(UIElement element) {
			if (movingAnchor) return;
			
			for (Anchor anchor : anchors) {
				if (anchor.getIcon() == element) {
					selectAnchor(anchor);
				}
			}
		}
	}
	
	class OpenTileset implements UIOnClick {
		public void onClick(UIElement element) {
			openTileset(element.getGameObject().getComponent(TilesetInfo.class).getTilesetName());
		}
	}
	class BackToTilesets implements UIOnClick {
		public void onClick(UIElement element) {
			backToTilesets();
		}
	}
	class TileToolSelector implements UIOnClick {
		public void onClick(UIElement element) {
			selectTileTool(element.getGameObject().getTag());
		}
	}
	class TileSelector implements UIOnClick {
		public void onClick(UIElement element) {
			selectTile(element.getGameObject().getComponent(TileInfo.class));
		}
	}

	class PageNavigation implements UIOnClick {
		public void onClick(UIElement element) {
			if (element.getGameObject().getTag().equalsIgnoreCase("Previous Page Button")) {
				tilesBook.turnPageBack();
			} else {
				tilesBook.turnPage();
			}
		}
	}
	
	class CollisionTypeSelector implements UIOnClick {
		public void onClick(UIElement element) {
			selectCollisionType(element.getGameObject().getTag());
		}
	}
	
	class CollisionPointSelector implements UIOnClick {
		public void onClick(UIElement uiElement) {
			for (Collision.CollisionPoint collisionPoint : selectedAnchor.getCollision().getPoints()) {
				if (collisionPoint.getIcon() == uiElement) {
					selectCollisionPoint(collisionPoint);
				}
			}
		}
	}
	
	class SpawnTypeSelector implements UIOnClick {
		public void onClick(UIElement element) {
			selectSpawnType(element.getGameObject().getTag());
		}
	}
	
	class Anchor {
		private GameObject gameObject;
		private LevelCreator.Collision collision;
		private int tileX, tileY;
		private UIElement icon;
		private ArrayList<Tile> tiles = new ArrayList<Tile>();
		
		public Anchor(GameObject gameObject, float posX, float posY) {
			this.gameObject = gameObject;
			setPosition(posX, posY);
			icon = UI.createUIElement("AnchorIcon", 0, 0, 24, 24, 0.5f, 0.5f, 0, ANCHOR_IMAGE, 0xffffffff, UIElement.Types.IMAGE);
			updateIconPosition();
		}
		
		public void updateIconPosition() {
			Vector2 pos = Camera.worldToScreen(gameObject.getPosition().getX(), gameObject.getPosition().getY());
			icon.getGameObject().setPosition(new Vector2(Mathf.roundTo(pos.getX(), 1f / ((float)GameController.getScaledWidth() * (1f / Renderer.scaledPW()))), Mathf.roundTo(pos.getY(), 1f / ((float)GameController.getScaledHeight() * (1f / Renderer.scaledPH())))));
		}
		
		public void removeAnchor() {
			GameManager.destroyGameObject(gameObject);
		}
		
		public void clearTiles() {
			for (int i = 0; i < tiles.size(); i = i) {
				tiles.get(i).removeTile();
				tiles.remove(i);
			}
		}
		
		public LevelCreator.Collision getCollision() {
			return collision;
		}
		public void setCollision(LevelCreator.Collision collision) {
			this.collision = collision;
		}
		
		public int getTileX() {
			return tileX;
		}

		public int getTileY() {
			return tileY;
		}
		
		public void setPosition(float x, float y) {
			int tileX = (int)(x * 2);
			int tileY = (int)(y * 2);
			
			gameObject.setPosition(new Vector2(x, y));
			this.tileX = tileX;
			this.tileY = tileY;
		}
		
		public float getPosX() {
			return gameObject.getPosition().getX();
		}

		public float getPosY() {
			return gameObject.getPosition().getY();
		}

		public UIElement getIcon() {
			return icon;
		}

		public GameObject getGameObject() {
			return gameObject;
		}
		
		public ArrayList<Tile> getTiles(){
			return tiles;
		}
	}

	class Tile {
		private GameObject gameObject;
		
		public Tile(GameObject gameObject, int tileX, int tileY) {
			this.gameObject = gameObject;
			GameManager.instantiateGameObject(this.gameObject);
		}
		public Tile(GameObject gameObject, int tileX, int tileY, boolean instantiate) {
			this.gameObject = gameObject;
			
			if (instantiate) {
				GameManager.instantiateGameObject(this.gameObject);
			}
		}
		
		public void removeTile() {
			GameManager.destroyGameObject(gameObject);
		}
		
		public GameObject getGameObject() {
			return gameObject;
		}
		public void setGameObject(GameObject gameObject) {
			if (this.gameObject != null) {
				GameManager.destroyGameObject(this.gameObject);
			}
			
			this.gameObject = gameObject;
			
			if (this.gameObject != null) {
				GameManager.instantiateGameObject(this.gameObject);
			}
		}
		public int getTileX() {
			return (int)(gameObject.getPosition().getX() * 2);
		}
		public int getTileY() {
			return (int)(gameObject.getPosition().getY() * 2);
		}
	}
	
	class Collision {
		private Collider collider;
		private ArrayList<CollisionPoint> points = new ArrayList<CollisionPoint>();
		
		public Collision(Collider collider) {
			this.collider = collider;
			updatePoints();
		}
		
		public void enablePoints(boolean enable) {
			for (CollisionPoint collisionPoint : points) {
				collisionPoint.getIcon().getGameObject().setDisabled(!enable);
			}
		}
		
		public void updatePoints() {
			Vector2 points[] = null;
			
			if (collider instanceof RectCollider) {
				points = ((RectCollider)collider).getPoints();
			} else {
				points = ((ShapeCollider)collider).getGlobalPoints();
			}
			
			int biggest = Math.max(points.length, this.points.size());
			
			for (int i = 0; i < biggest; i++) {
				if (i >= this.points.size()) {
					this.points.add(new CollisionPoint(points[i]));
				} else if (i >= points.length) {
					if (i < this.points.size() - 1) {
						i--;
					}
					removePoint(this.points.get(i));
				} else {
					this.points.get(i).setPosition(points[i]);
				}
			}
		}
		
		public void removePoint(CollisionPoint point) {
			GameManager.destroyGameObject(point.getIcon().getGameObject());
			points.remove(point);
		}
		
		public void clearPoints() {
			for (int i = 0; i < points.size(); i++) {
				removePoint(points.get(i));
				i--;
			}
		}
		
		public Collider getCollider() {
			return collider;
		}
		
		public ArrayList<CollisionPoint> getPoints() {
			return points;
		}
		
		class CollisionPoint {
			private UIElement icon;

			public CollisionPoint(Vector2 pos) {
				pos = Camera.worldToScreen(pos.getX(), pos.getY());
				icon = UI.createUIElement("CollisionPoint", 0, 0, 12, 12, 0.5f, 0.5f, 10, COLLISION_POINT_IMAGE, 0xffffffff, UIElement.Types.IMAGE);
				icon.getGameObject().setParent(ui);
				setPosition(pos);
				icon.addClickListener(collisionPointSelector);
			}
			
			public void setPosition(Vector2 pos) {
				pos = Camera.worldToScreen(pos.getX(), pos.getY());
				icon.getGameObject().setPosition(Camera.snapToGamePixels((int)pos.getX(), (int)pos.getY()));
			}
			
			public UIElement getIcon() {
				return icon;
			}
		}
	}
}
