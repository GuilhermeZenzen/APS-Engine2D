package com.aps.game.components;

import com.aps.engine.GameController;
import com.aps.engine.util.Vector2;
import com.aps.game.GameManager;
import com.aps.game.Physics;

public class Rigidbody extends Component {
	private Collider collider;
	private Vector2 velocity = new Vector2();
	
	private boolean gravity = true;
	
	private float horizontalFriction = 1f;
	private float verticalFriction = 1f;
	
	public void start() {
		collider = gameObject.getComponent(Collider.class);
	}
	
	public void physicsUpdate() {
		if(gravity) {
			accelerate(GameManager.getCurrentScene().getGravity().multiply(GameController.getDeltaTime() * GameController.getTimeScale()));			
		}

		velocity = velocity.multiply(horizontalFriction, verticalFriction);
        
		gameObject.move(velocity.multiply(GameController.getDeltaTime() * GameController.getTimeScale()));
	}
	
	public void accelerate(Vector2 acceleration) {
		velocity = velocity.add(acceleration);
	}
	public void accelerate(float x, float y) {
		velocity = velocity.add(x, y);
	}
	
	private void adjustVelocityToCollision() {
		if (collider.getResolve() != null) {
			float normalAngle = this.collider.getResolve().getAngle();
            float normalSin = (float)Math.sin(normalAngle);
            float normalCos = (float)Math.cos(normalAngle);

            normalCos = Math.abs(normalCos) < 0.001 ? 0 : normalCos;
            normalSin = Math.abs(normalSin) < 0.001 ? 0 : normalSin;

            float velXMultiplier = (velocity.getX() > 0 && normalCos >= 0) || (velocity.getX() < 0 && normalCos <= 0) ? Math.abs(normalSin) : 1;
            float velYMultiplier = (velocity.getY() < 0 && normalSin <= 0) || (velocity.getY() > 0 && normalSin >= 0) ? Math.abs(normalCos) : 1;
            velocity = velocity.multiply(velXMultiplier, velYMultiplier);
		}
	}

	public void onCollision(Collider collider) {
		if (this.collider == null) return;
		
		if (this.collider instanceof ShapeCollider || collider instanceof ShapeCollider) {
			gameObject.move(this.collider.getResolve().multiply(-this.collider.getOverlap()));
			adjustVelocityToCollision();
		} else if (collider instanceof RectCollider) {
			RectCollider selfRect = (RectCollider)this.collider;
			RectCollider colliderRect = (RectCollider)collider;
			
			if (Math.abs(selfRect.getPreviousCenter().getY() - colliderRect.getPreviousCenter().getY()) < selfRect.getHalfSize().getY() + colliderRect.getHalfSize().getY()) {
				if (selfRect.getPreviousCenter().getX() < colliderRect.getGlobalCenter().getX()) {
					float dist = (selfRect.getGlobalCenter().getX() + selfRect.getHalfSize().getX()) 
							   - (colliderRect.getGlobalCenter().getX() - colliderRect.getHalfSize().getX());
					gameObject.move(-dist, 0);
					velocity = new Vector2(0, velocity.getY());
					this.collider.updateGlobalCenter();
				}
				
				if (selfRect.getPreviousCenter().getX() > colliderRect.getGlobalCenter().getX()) {
					float dist = (colliderRect.getGlobalCenter().getX() + colliderRect.getHalfSize().getX()) 
							   - (selfRect.getGlobalCenter().getX() - selfRect.getHalfSize().getX());
					gameObject.move(dist, 0);
					velocity = new Vector2(0, velocity.getY());
					this.collider.updateGlobalCenter();
				}
			} else {
				if (selfRect.getPreviousCenter().getY() < colliderRect.getGlobalCenter().getY()) {
					float dist = (selfRect.getGlobalCenter().getY() + selfRect.getHalfSize().getY()) 
							   - (colliderRect.getGlobalCenter().getY() - colliderRect.getHalfSize().getY());
					gameObject.move(0, -dist);
					velocity = new Vector2(velocity.getX(), 0);
					this.collider.updateGlobalCenter();
				}
				
				if (selfRect.getPreviousCenter().getY() > colliderRect.getGlobalCenter().getY()) {
					float dist = (colliderRect.getGlobalCenter().getY() + colliderRect.getHalfSize().getY()) 
							   - (selfRect.getGlobalCenter().getY() - selfRect.getHalfSize().getY());
					gameObject.move(0, dist);
					velocity = new Vector2(velocity.getX(), 0);
					this.collider.updateGlobalCenter();
				}
			}
		}
	}

	public Vector2 getVelocity() {
		return velocity;
	}
	public void setVelocity(Vector2 velocity) {
		this.velocity = velocity;
	}
	
	public boolean hasGravity() {
		return gravity;
	}

	public void setGravity(boolean gravity) {
		this.gravity = gravity;
	}

	public float getHorizontalFriction() {
		return horizontalFriction;
	}
	public void setHorizontalFriction(float horizontalFriction) {
		this.horizontalFriction = horizontalFriction;
	}
	
	public float getVerticalFriction() {
		return verticalFriction;
	}
	public void setVerticalFriction(float verticalFriction) {
		this.verticalFriction = verticalFriction;
	}
	
	public void setFriction(float friction) {
		setHorizontalFriction(friction);
		setVerticalFriction(friction);
	}
}
