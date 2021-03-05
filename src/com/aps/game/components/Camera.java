package com.aps.game.components;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;

import com.aps.engine.GameController;
import com.aps.engine.Input;
import com.aps.engine.Renderer;
import com.aps.engine.gfx.Image;
import com.aps.engine.util.Mathf;
import com.aps.engine.util.Vector2;
import com.aps.game.GameManager;
import com.aps.game.GameObject;
import com.aps.game.Physics;
import com.aps.game.UI;

public class Camera extends Component {
	public static Camera getMainCamera() {
		return mainCamera;
	}

	private static Camera mainCamera;
	
	private static ArrayList<Sprite> sprites = new ArrayList<Sprite>();

	private boolean showColliders = false;
	private boolean showCollidersCenter = false;
	private boolean showCollidersNormal = false;
	
	private float scale = 1f;
	
	public void awake() {
		mainCamera = this;
	}
	
	public void start() {

	}
	
	public void update() {
		
	}
	
	public void lateUpdate() {

	}
	
	public void render(Renderer renderer) {
		Renderer.clear();

		sprites.sort(Comparator.comparing(Sprite::getDepth));

		for (Sprite sprite : sprites) {
			if (!sprite.getGameObject().isDisabled() && !sprite.isDisabled() && sprite.getImage() != null) {
				int offX = (int)Math.ceil((sprite.getGameObject().getPosition().getX() - gameObject.getPosition().getX()) * GameManager.PIXELS_PER_METER)
						- sprite.getImage().getWidth() / 2 + GameController.getWidth() / 2;
				int offY = (int)Math.ceil((sprite.getGameObject().getPosition().getY() - gameObject.getPosition().getY()) * GameManager.PIXELS_PER_METER)
						- sprite.getImage().getHeight() / 2 + GameController.getHeight() / 2;
				
				Renderer.drawImage(sprite.getImage(), offX, offY, sprite.getColor(), 1, false);							
			}
		}

		if (showColliders) {
			for (Collider collider : Physics.getColliders()) {
				Vector2 points[] = null;
				
				if (collider instanceof RectCollider) {
					points = ((RectCollider)collider).getPoints();
				} else {
					points = ((ShapeCollider)collider).getGlobalPoints();
				}
				
				if (showCollidersCenter) {
					Vector2 center = worldToScreen(collider.getGlobalCenter());
					Renderer.drawLine((int)center.getX(), (int)center.getY(), (int)center.getX(), (int)center.getY(), 20, 0xffffff00);
				}

				for (int i = 0; i < points.length; i++) {
					int next = (i + 1) % points.length;
					Vector2 screenPoint = snapToGamePixels(worldToScreen(points[i].getX(), points[i].getY()));
					Vector2 nextScreenPoint = snapToGamePixels(worldToScreen(points[next].getX(), points[next].getY()));
					
					if (showCollidersNormal) {
						Vector2 centerScreenPoint = Vector2.center(points[i], points[next]);
						Vector2 normalScreenPoint = worldToScreen(Vector2.normal(points[i], points[next]).normalized().add(centerScreenPoint));
						centerScreenPoint = worldToScreen(centerScreenPoint);
						Renderer.drawLine((int)centerScreenPoint.getX(), (int)centerScreenPoint.getY(), (int)normalScreenPoint.getX(), (int)normalScreenPoint.getY(), (int)Math.ceil(4 * (1 - Renderer.getScale())), 0xffff00ff);
					}

					Renderer.drawLine((int)screenPoint.getX(), (int)screenPoint.getY(), (int)nextScreenPoint.getX(), (int)nextScreenPoint.getY(), (int)Math.ceil(4 * (1 - Renderer.getScale())), 0xff00ff00);
				}
			}			
		}
	}

	public static void addSprite(Sprite sprite) {
		sprites.add(sprite);
	}
	
	public static void removeSprite(Sprite sprite) {
		sprites.remove(sprite);
	}
	
	public static void clearSprites() {
		sprites.clear();
	}
	
	/*public static Vector2 worldToGameScreen(float x, float y) {
		
	}*/

	public static Vector2 snapToGamePixels(Vector2 point) {
		return snapToGamePixels((int)point.getX(), (int)point.getY());
	}
	public static Vector2 snapToGamePixels(int x, int y) {
		return new Vector2(Mathf.roundTo(x, 1f / ((float)GameController.getScaledWidth() * (1f / Renderer.scaledPW()))), Mathf.roundTo(y, 1f / ((float)GameController.getScaledHeight() * (1f / Renderer.scaledPH()))));
	}
	
	public static Vector2 screenToWorld(Vector2 point) {
		return screenToWorld((int)point.getX(), (int)point.getY());
	}
	public static Vector2 screenToWorld(int x, int y) {
		return new Vector2(x / GameController.getScale() / GameManager.pixelsPerMeter() + mainCamera.getGameObject().getPosition().getX() - (5 * Renderer.getScale()),
				y / GameController.getScale() / GameManager.pixelsPerMeter() + mainCamera.getGameObject().getPosition().getY() - (2.8125f * Renderer.getScale()));
	}
	
	public static Vector2 worldToScreen(Vector2 point) {
		return worldToScreen(point.getX(), point.getY());
	}
	public static Vector2 worldToScreen(float x, float y) {
		return new Vector2(Math.round((x - mainCamera.getGameObject().getPosition().getX()) * GameManager.pixelsPerMeter() 
				          * GameController.getScale() + (GameController.getScaledWidth() / 2)),
						  Math.round((y - mainCamera.getGameObject().getPosition().getY()) * GameManager.pixelsPerMeter()
						  * GameController.getScale() + GameController.getScaledHeight() / 2));
	}
	
	public boolean isShowingColliders() {
		return showColliders;
	}

	public void setShowColliders(boolean showColliders) {
		setShowColliders(showColliders, false, false);
	}
	public void setShowColliders(boolean showColliders, boolean showCenter) {
		setShowColliders(showColliders, showCenter, false);
	}
	public void setShowColliders(boolean showColliders, boolean showCenter, boolean showNormals) {
		this.showColliders = showColliders;
		this.showCollidersCenter = showCenter;
		this.showCollidersNormal = showNormals;
	}
}
