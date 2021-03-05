package com.aps.game.components;

import java.util.ArrayList;

import com.aps.game.GameManager;
import com.aps.game.GameObject;
import com.aps.game.OnAttack;
import com.aps.game.OnDeath;

public abstract class Entity extends Component {
	protected int health;
	protected int currentHealth;
	
	protected ArrayList<OnAttack> attackListeners = new ArrayList<OnAttack>();
	protected ArrayList<OnDeath> deathListeners = new ArrayList<OnDeath>();
	protected ArrayList<OnDeath> deathListenersToRemove = new ArrayList<OnDeath>();
	
	public void awake() {
		gameObject.setLayer(1);
	}
	
	public void start() {
		initHealth();
	}
	
	public void update() {
		if (gameObject.getPosition().getY() > GameManager.LEVEL_BOTTOM_LIMIT) {
			onDeath();
		}
	}
	
	protected abstract void initHealth();
	
	protected void setHealth(int health) {
		currentHealth = this.health = health;
	}
	
	public void applyDamage(int damage) {
		if (damage <= 0) return;
		
		currentHealth = Math.max(0, currentHealth - damage);
		
		if (currentHealth <= 0) {
			onDeath();
		}
	}
	public void applyHeal(int heal) {
		if (heal <= 0) return;
		
		currentHealth = Math.min(health, currentHealth + heal);
	}
	
	protected void callAttackListeners(int damage) {
		for (OnAttack listener : attackListeners) {
			listener.onAttack(this, damage);
		}
	}
	public void addAttackListener(OnAttack listener) {
		attackListeners.add(listener);
	}
	public void removeAttackListeners(OnAttack listener) {
		attackListeners.remove(listener);
	}
	
	protected void callDeathListeners() {
		removeDeathListeners();
		for (OnDeath listener : deathListeners) {
			listener.onDeath(this);
		}
	}
	protected void removeDeathListeners() {
		for (OnDeath listener : deathListenersToRemove) {
			deathListeners.remove(listener);
		}
		deathListenersToRemove.clear();
	}
	public void addDeathListener(OnDeath listener) {
		deathListeners.add(listener);
	}
	public void removeDeathListener(OnDeath listener) {
		deathListenersToRemove.add(listener);
	}
	public void clearDeathListeners() {
		deathListeners.clear();
	}
	
	protected abstract void onDeath();
	
	public static ArrayList<Entity> getEntities() {
		ArrayList<Entity> entities = new ArrayList<Entity>();
		
		for (GameObject go : GameManager.getCurrentScene().getGameObjects()) {
			Entity entity = go.getComponent(Entity.class);
			if (entity != null) {
				entities.add(entity);
			}
		}
		
		return entities;
	}
	
	public int getHealth() {
		return health;
	}

	public int getCurrentHealth() {
		return currentHealth;
	}
}
