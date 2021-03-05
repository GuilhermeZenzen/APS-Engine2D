package com.aps.game.components;

import java.util.Comparator;
import java.util.Map;

import com.aps.engine.GameController;
import com.aps.engine.util.Mathf;
import com.aps.engine.util.Vector2;
import com.aps.game.GameManager;
import com.aps.game.GameObject;
import com.aps.game.GameStateMachine;
import com.aps.game.GameStateMachine.GameStates;
import com.aps.game.animation.Animation;
import com.aps.game.animation.AnimationList;
import com.aps.game.animation.AnimatorController;

public class RadioactiveWasteEnemy extends Entity {
	private Player player;
	private Rigidbody rigidbody;
	private RectCollider collider;
	private Sprite sprite;
	private Animator animator;
	
	private float walkSpeed = 1.5f;
	private boolean walkDirection = true;
	
	private float detectionDistance = 5f;
	private float ignitionStartDistance = 1f;
	private float ignitionEndDistance = 2f;
	private float ignitionTime = 1f;
	private float ignitionElapsedTime = 0;
	private float explosionRange = 2f;
	private float explosionDamage = 200;
	private boolean inIngnition = false;
	
	private int ignitionStage = 0;
	
	private float playerDistance = 0f;
	private float explosionDuration = 8f / 24f;
	private float explosionElapsedTime = 0f;
	private boolean exploded = false;

	public void awake() {
		super.awake();
		collider = gameObject.createComponent(RectCollider.class);
		collider.setProperties(new Vector2(0, 0), new Vector2(1, 1));
		rigidbody = gameObject.createComponent(Rigidbody.class);
		sprite = gameObject.createComponent(Sprite.class);
		sprite.setColor(0xffffffff);
		animator = gameObject.createComponent(Animator.class);
		animator.setAnimatorController(AnimatorController.newController("radioactive-waste-enemy", "radioactive-waste-enemy"));
	}
	
	public void start() {
		super.start();
		player = GameManager.getCurrentScene().getGameObject("Player").getComponent(Player.class);
	}
	
	public void update() {
		if (GameStateMachine.getGameState() == GameStates.PAUSED) return;
		
		super.update();

		if (exploded) {
			explosionElapsedTime += GameController.getDeltaTime();
			
			if (explosionElapsedTime > explosionDuration) {
				onDeath();
			}
			
			return;
		}
		
		playerDistance = Vector2.sqrDistance(player.getGameObject().getPosition(), gameObject.getPosition());
		rigidbody.setVelocity(new Vector2(0, rigidbody.getVelocity().getY()));
		if (playerDistance < detectionDistance * detectionDistance) {
			if (playerDistance < ignitionStartDistance * ignitionStartDistance) {
				inIngnition = true;
				animator.setBool("moving", false);
			} else if (inIngnition ? playerDistance > ignitionEndDistance * ignitionEndDistance : true) {
				inIngnition = false;
				ignitionElapsedTime = Mathf.clamp(ignitionElapsedTime - GameController.getDeltaTime() * 2, 0, ignitionTime);
				animator.setBool("moving", true);
				
				if (ignitionStage <= 0) {
					float xDelta = player.getGameObject().getPosition().getX() - gameObject.getPosition().getX();
					if (xDelta < 0) {
						rigidbody.setVelocity(new Vector2(-walkSpeed, rigidbody.getVelocity().getY()));
						walkDirection = true;
					} else if (xDelta > 0) {
						rigidbody.setVelocity(new Vector2(walkSpeed, rigidbody.getVelocity().getY()));
						walkDirection = false;
					}
				}
			}
		}
		
		ignitionStage = Math.round(ignitionElapsedTime / ignitionTime * 4);
		
		if (ignitionStage > 0) {
			ignitionStage = (int)Mathf.clamp(ignitionStage, 0, 3);
		}
		
		animator.setInt("ignitionStage", ignitionStage);
		animator.setBool("walkDirection", walkDirection);

		if (inIngnition) {			
			ignitionElapsedTime += GameController.getDeltaTime();
			
			if (ignitionElapsedTime > ignitionTime) {
				explode();
			}
		}
	}

	private void explode() {
		sprite.setDepth(100);
		animator.setBool("explode", true);
		exploded = true;
		gameObject.removeComponent(collider);
		gameObject.removeComponent(rigidbody);
		
		for (int i = GameManager.getCurrentScene().getGameObjects().size() - 1; i >= 0; i--) {
			GameObject go = GameManager.getCurrentScene().getGameObjects().get(i);
			if (go == gameObject) continue;
			
			Entity entity = go.getComponent(Entity.class);
			if (entity != null) {
				float entityDistance = Vector2.sqrDistance(gameObject.getPosition(), go.getPosition());
				
				if (entityDistance < explosionRange * explosionRange) {
					if (entity instanceof RadioactiveWasteEnemy) {
						RadioactiveWasteEnemy radioactiveWaste = (RadioactiveWasteEnemy)entity;
						if (!radioactiveWaste.exploded) {
							radioactiveWaste.explode();
						}
					} else if (entity instanceof Player || entity instanceof PlayerClone) {
						int damage = damageByExplosion(entityDistance);
						entity.applyDamage(damage);
					}
				}
			}
		}
	}
	
	private int damageByExplosion(float entityDistance) {
		return Math.round(explosionDamage * (1 - Mathf.clamp((float)Math.sqrt(entityDistance) / explosionRange, 0.01f, 1)));
	}
	
	protected void initHealth() {
		setHealth(100);
	}
	
	public void onDeath() {
		GameManager.destroyGameObject(gameObject);
	}
}
