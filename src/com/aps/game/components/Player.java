package com.aps.game.components;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import com.aps.engine.GameController;
import com.aps.engine.Input;
import com.aps.engine.util.Vector2;
import com.aps.game.GameManager;
import com.aps.game.GameObject;
import com.aps.game.GameStateMachine;
import com.aps.game.LayerMask;
import com.aps.game.OnDeath;
import com.aps.game.Physics;
import com.aps.game.ShapecastHit;
import com.aps.game.GameStateMachine.GameStates;
import com.aps.game.animation.AnimationList;
import com.aps.game.animation.AnimatorController;

public class Player extends Entity {
	private PlayerMotor motor;
	private Sprite sprite;
	private Animator animator;
	private PlayerSkillsManager skills;
	
	private float walkSpeed = 3f;
	private float jumpSpeed = 5f;
	private boolean walkDirection;
	
	private boolean attacking;
	private boolean canAttack = true;
	private int attackDamage = 50;
	private float attackTime = 0.2f;
	private float attackCooldown = 0.4f;
	private float attackElapsedTime = 0f;
	
	private boolean canDie = false;
	private PlayerClone rebornClone = null;
	
	public void awake() {
		super.awake();
		motor = gameObject.createComponent(PlayerMotor.class);
		sprite = gameObject.createComponent(Sprite.class);
		sprite.setColor(0xffffffff);
		sprite.setDepth(80);
		animator = gameObject.createComponent(Animator.class);
		animator.setAnimatorController(AnimatorController.newController("petase", "petase"));
		skills = gameObject.createComponent(PlayerSkillsManager.class);
		gameObject.createComponent(PlayerUI.class);
		gameObject.setLayer(2);
	}
	
	public void start() {
		super.start();
	}
	
	public void physicsUpdate() {
		if (GameStateMachine.getGameState() == GameStates.PAUSED) return;
		
		super.physicsUpdate();
		motor.move(0);

		if (!attacking) {
			if (Input.isKey(KeyEvent.VK_D) && !attacking) {
				motor.move(walkSpeed);
				animator.setBool("moving", true);
				walkDirection = false;
			}
			else if (Input.isKey(KeyEvent.VK_A) && !attacking) {
				motor.move(-walkSpeed);
				animator.setBool("moving", true);
				walkDirection = true;
			} else {
				animator.setBool("moving", false);
			}
		}
		
		animator.setBool("walkDirection", walkDirection);
		animator.setBool("attack", attacking);
	}
	
	public void update() {
		if (GameStateMachine.getGameState() == GameStates.PAUSED) return;
		
		super.update();
		if (Input.isKeyDown(KeyEvent.VK_5)) {
			applyHeal(100);
		}
		rebornClone = null;
		
		if (!canAttack) {
			attackElapsedTime += GameController.getDeltaTime();
			
			if (attackElapsedTime > attackTime) {
				attacking = false;
			}
			
			if (attackElapsedTime > attackCooldown) {
				canAttack = true;
				attackElapsedTime = 0;
			}
		}
		
		if (Input.isKeyDown(KeyEvent.VK_SPACE)) {
			motor.jump(jumpSpeed);
		}
		
		if (Input.isMouseButtonDown(MouseEvent.BUTTON1) && canAttack) {
			attack();
		}
	}
	
	public void lateUpdate() {
		if (GameStateMachine.getGameState() == GameStates.PAUSED) return;
		
		GameManager.getCurrentScene().getGameObject("Camera").setPosition(gameObject.getPosition());
	}
	
	private void attack() {
		attacking = true;
		canAttack = false;
		LayerMask attackMask = new LayerMask(1);
		Vector2 pos = gameObject.getPosition();
		ShapecastHit hit = Physics.shapecast(attackMask, pos.add((walkDirection ? -1 : 1) * -0.09375f, -0.09375f), pos.add((walkDirection ? -1 : 1) * 0.5f, -0.09375f), pos.add((walkDirection ? -1 : 1) * 0.5f, 0.03125f), pos.add((walkDirection ? -1 : 1) * 0.15625f, 0.03125f));
		if (hit.getColliders().length > 0) {
			hit.getColliders()[0].getGameObject().getComponent(Entity.class).applyDamage(attackDamage);
			callAttackListeners(attackDamage);
		}
	}
	
	protected void initHealth() {
		setHealth(100);
	}
	
	protected void onDeath() {
		canDie = true;
		
		callDeathListeners();
		
		if (canDie) {
			GameManager.setLoadPlayScene(false);
			GameManager.prepareSceneLoading(GameManager.getScenes().get(0));
		}
	}

	public boolean isCanDie() {
		return canDie;
	}

	public void setCanDie(boolean canDie) {
		this.canDie = canDie;
	}

	public PlayerSkillsManager getSkills() {
		return skills;
	}

	public PlayerClone getRebornClone() {
		return rebornClone;
	}

	public void setRebornClone(PlayerClone rebornClone) {
		this.rebornClone = rebornClone;
	}

	public PlayerMotor getMotor() {
		return motor;
	}
}
