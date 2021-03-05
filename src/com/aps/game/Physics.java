package com.aps.game;

import java.util.ArrayList;

import com.aps.engine.util.Vector2;
import com.aps.game.components.Collider;
import com.aps.game.components.RectCollider;
import com.aps.game.components.ShapeCollider;

public class Physics {
	private static ArrayList<Collider> colliders = new ArrayList<Collider>();
	
	private static ArrayList<ArrayList<Boolean>> layerMatrix = new ArrayList<ArrayList<Boolean>>();
	private static ArrayList<String> layerNames = new ArrayList<String>();
	
	public static void addCollider(Collider collider) {
		colliders.add(collider);
	}
	
	public static void removeCollider(Collider collider) {
		colliders.remove(collider);
	}
	
	public static void clearColliders() {
		colliders.clear();
	}
	
	public static void addLayer(boolean interact, String layerName, String... layersInteraction) {
		boolean interaction[] = new boolean[layerMatrix.size()];
		
		for (int i = 0; i < interaction.length; i++) {
			interaction[i] = false;
			for (String layer : layersInteraction) {
				if (layerNames.get(i).equalsIgnoreCase(layer)) {
					interaction[i] = interact;
					break;
				}
			}
		}
		
		addLayer(layerName, interaction);
	}
	public static void addLayer(String layerName, boolean... layersInteraction) {
		ArrayList<Boolean> interaction = new ArrayList<Boolean>();
		layerMatrix.add(interaction);
		layerNames.add(layerName);
		
		for (int i = 0; i < layerMatrix.size(); i++) {
			if (i < layersInteraction.length) {
				layerMatrix.get(i).add(layersInteraction[i]);
			} else {
				layerMatrix.get(i).add(true);
			}
		}
	}
	
	public static boolean layerInteraction(String layer1, String layer2) {
		if (layerMatrix.size() == 0) return false;
		
		return layerInteraction(layerIndex(layer1), layerIndex(layer2));
	}
	public static boolean layerInteraction(int layer1, int layer2) {
		if (layerMatrix.size() == 0) return false;
		
		if (layer1 < 0 || layer1 > layerMatrix.size() || layer2 < 0 || layer2 > layerMatrix.size()) return false;
		
		int lowestIndex = Math.min(layer1, layer2), biggestIndex = Math.max(layer1, layer2);
		return layerMatrix.get(lowestIndex).get(biggestIndex - lowestIndex);
	}
	
	public static int layerIndex(String layer) {
		return layerNames.indexOf(layer);
	}
	public static String layerName(int layer) {
		if (layer >= layerNames.size()) return "";
		
		return layerNames.get(layer);
	}
	
	public static ArrayList<Collider> getColliders(){
		return colliders;
	}
	
	public static void update() {
		for (int i = 0; i < colliders.size(); i++) {
			Collider collider = colliders.get(i);

			for (int j = i + 1; j < colliders.size(); j++) {
				Collider other = colliders.get(j);
				collider.setResolve(null);
				other.setResolve(null);
				
				boolean collided = false;
				
				if (layerInteraction(collider.getGameObject().getLayer(), other.getGameObject().getLayer())) {
					if (collider instanceof RectCollider && other instanceof RectCollider) {
						collided = aabbCollision((RectCollider)collider, (RectCollider)other);
					} else {
						collided = satCollisionResolve(collider, other);
					}
				}

				collider.collision(other, collided);
				other.collision(collider, collided);
			}
		}
	}
	
	private static boolean aabbCollision(RectCollider collider, RectCollider other) {
		if(Math.abs(collider.getGlobalCenter().getX() - other.getGlobalCenter().getX()) < collider.getHalfSize().getX() + other.getHalfSize().getX()) {
			if(Math.abs(collider.getGlobalCenter().getY() - other.getGlobalCenter().getY()) < collider.getHalfSize().getY() + other.getHalfSize().getY()) {
				return true;
			}
		}
		
		return false;
	}
	private static boolean aabbCollision(Vector2 colliderCenter, Vector2 colliderSize, Vector2 otherCenter, Vector2 otherSize) {
		if(Math.abs(colliderCenter.getX() - otherCenter.getX()) < colliderSize.getX() / 2 + otherSize.getX() / 2) {
			if(Math.abs(colliderCenter.getY() - otherCenter.getY()) < colliderSize.getY() / 2 + otherSize.getY() / 2) {
				return true;
			}
		}
		
		return false;
	}

	private static boolean satCollision(Vector2 colliderPoints[], Vector2 otherPoints[]) {
		for (int i = 0; i < 2; i++) {
			Vector2 axesPoints[] = i == 0 ? colliderPoints : otherPoints;
			Vector2 comparePoints[] = i == 0 ? otherPoints : colliderPoints;
			
			for (int j = 0; j < axesPoints.length; j++) {
				int next = (j + 1) % axesPoints.length;
				
				Vector2 axisProj = Vector2.normal(axesPoints[j], axesPoints[next]).normalized();
				
				float minAxes = Float.POSITIVE_INFINITY, maxAxes = Float.NEGATIVE_INFINITY;
				for (int k = 0; k < axesPoints.length; k++) {
					float dot = Vector2.dot(axesPoints[k], axisProj);
					minAxes = Math.min(minAxes, dot);
					maxAxes = Math.max(maxAxes, dot);
				}
				
				float minCompare = Float.POSITIVE_INFINITY, maxCompare = Float.NEGATIVE_INFINITY;
				for (int k = 0; k < comparePoints.length; k++) {
					float dot = Vector2.dot(comparePoints[k], axisProj);
					minCompare = Math.min(minCompare, dot);
					maxCompare = Math.max(maxCompare, dot);
				}

				if (!(Math.min(maxAxes, maxCompare) >= Math.max(minAxes, minCompare))) return false;
			}
		}
		
		return true;
	}
	private static boolean satCollisionResolve(Collider collider, Collider other) {
		Vector2 colliderPoints[] = collider instanceof ShapeCollider ? ((ShapeCollider)collider).getGlobalPoints() :
								   ((RectCollider)collider).getPoints();
		Vector2 otherPoints[] = other instanceof ShapeCollider ? ((ShapeCollider)other).getGlobalPoints() :
			   ((RectCollider)other).getPoints();
		
		float overlap = Float.POSITIVE_INFINITY;
		Vector2 resolve = null;
		
		for (int i = 0; i < 2; i++) {
			Vector2 axesPoints[] = i == 0 ? colliderPoints : otherPoints;
			Vector2 comparePoints[] = i == 0 ? otherPoints : colliderPoints;
			
			for (int j = 0; j < axesPoints.length; j++) {
				int next = (j + 1) % axesPoints.length;
				
				Vector2 axisProj = Vector2.normal(axesPoints[j], axesPoints[next]).normalized();
				
				float minAxes = Float.POSITIVE_INFINITY, maxAxes = Float.NEGATIVE_INFINITY;
				for (int k = 0; k < axesPoints.length; k++) {
					float dot = Vector2.dot(axesPoints[k], axisProj);
					minAxes = Math.min(minAxes, dot);
					maxAxes = Math.max(maxAxes, dot);
				}
				
				float minCompare = Float.POSITIVE_INFINITY, maxCompare = Float.NEGATIVE_INFINITY;
				for (int k = 0; k < comparePoints.length; k++) {
					float dot = Vector2.dot(comparePoints[k], axisProj);
					minCompare = Math.min(minCompare, dot);
					maxCompare = Math.max(maxCompare, dot);
				}
				
				float over = Math.min(maxAxes, maxCompare) - Math.max(minAxes, minCompare);
	            if (over < overlap) {
	                overlap = over;
	                resolve = axisProj;
	            }

				if (!(Math.min(maxAxes, maxCompare) >= Math.max(minAxes, minCompare))) return false;
			}
		}
		
		if (Vector2.dot(other.getGlobalCenter().subtract(collider.getGlobalCenter()).normalized(), resolve) < 0) {
			resolve = resolve.inverse();
		}

		collider.setOverlap(overlap);
		collider.setResolve(resolve);
		other.setOverlap(overlap);
		other.setResolve(resolve.inverse());
		
		return true;
	}
	
	// CASTING
	public static LinecastHit raycast(Vector2 origin, Vector2 direction, float distance) {
		return raycast(origin, direction, distance, null);
	}
	public static LinecastHit raycast(Vector2 origin, Vector2 direction, float distance, Collider colliderToIgnore) {
		direction = direction.normalized();
		Vector2 endPoint = new Vector2(origin.getX() + direction.getX() * distance, origin.getY() + direction.getY() * distance);
		
		return linecast(origin, endPoint, colliderToIgnore);
	}
	
	public static LinecastHit linecast(Vector2 from, Vector2 to) {
		return linecast(from, to, null);
	}
	public static LinecastHit linecast(Vector2 from, Vector2 to, Collider colliderToIgnore) {
		Line line = Line.calculateLine(from, to);

		Collider nearestCollider = null;
		Vector2 nearestColliderHitPoint = null;
		float nearestColliderHitPointDistance = 0;
		
		for (Collider collider : colliders) {
			if (collider == colliderToIgnore) continue;
			
			Vector2 points[] = null;
			if (collider instanceof RectCollider) {
				points = ((RectCollider)collider).getPoints();
			} else {
				points = ((ShapeCollider)collider).getGlobalPoints();
			}

			Vector2 nearestHitPoint = null;
			float nearestHitPointDistance = 0;
			
			for (int i = 0; i < points.length; i++) {
				Line line2 = Line.calculateLine(points[i], points[(i + 1) % points.length]);
				
				Vector2 intersection = Line.intersection(line, line2);

				if (intersection == null) {
					continue;
				}
				
				if (nearestHitPoint == null) {
					nearestHitPoint = intersection;
					nearestHitPointDistance = Vector2.sqrDistance(from, nearestHitPoint);								
				} else if (Vector2.sqrDistance(from, intersection) < nearestHitPointDistance) {
					nearestHitPoint = intersection;
					nearestHitPointDistance = Vector2.sqrDistance(from, nearestHitPoint);								
				}
			}
			
			if (nearestHitPoint != null) {
				if (nearestCollider == null) {
					nearestCollider = collider;
					nearestColliderHitPoint = nearestHitPoint;
					nearestColliderHitPointDistance = nearestHitPointDistance;
				} else {
					if (nearestHitPointDistance < nearestColliderHitPointDistance) {
						nearestCollider = collider;
						nearestColliderHitPoint = nearestHitPoint;
						nearestColliderHitPointDistance = nearestHitPointDistance;
					}
				}
			}
		}
		
		if (nearestCollider == null) {
			return null;
		} else {
			return new LinecastHit(nearestCollider, nearestColliderHitPoint, nearestColliderHitPointDistance);
		}
	}
	
	public static ShapecastHit rectcast(Vector2 startPos, float width, float height, LayerMask mask) {
		ArrayList<Collider> hitColliders = new ArrayList<Collider>();
		Vector2 points[] = { startPos.add(-width, -height), startPos.add(width, -height), startPos.add(width, height), startPos.add(-width, height) };
		
		for (Collider collider : colliders) {
			if (mask.containsLayer(collider.getGameObject().getLayer())) {
				boolean collided = false;
				
				if (collider instanceof RectCollider) {
					collided = aabbCollision(startPos, new Vector2(width, height), collider.getGlobalCenter(), ((RectCollider)collider).getSize());
				} else {
					collided = satCollision(points, ((ShapeCollider)collider).getGlobalPoints());
				}
				
				if (collided) {
					hitColliders.add(collider);
				}
			}
		}
		
		return new ShapecastHit(hitColliders);
	}
	
	public static ShapecastHit shapecast(LayerMask mask, Vector2... points) {
		ArrayList<Collider> hitColliders = new ArrayList<Collider>();

		for (Collider collider : colliders) {
			if (mask.containsLayer(collider.getGameObject().getLayer())) {
				Vector2 otherPoints[];
				
				if (collider instanceof RectCollider) {
					otherPoints = ((RectCollider)collider).getPoints();
				} else {
					otherPoints = ((ShapeCollider)collider).getGlobalPoints();
				}
				
				if (satCollision(points, otherPoints)) {
					hitColliders.add(collider);
				}
			}
		}
		
		return new ShapecastHit(hitColliders);
	}
}

class LinecastHit {
	private Collider collider;
	private Vector2 point;
	private float distance;
	
	public LinecastHit(Collider collider, Vector2 point, float distance) {
		this.collider = collider;
		this.point = point;
		this.distance = distance;
	}

	public Collider getCollider() {
		return collider;
	}

	public Vector2 getPoint() {
		return point;
	}

	public float getDistance() {
		return distance;
	}
}
