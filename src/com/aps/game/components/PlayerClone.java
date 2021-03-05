package com.aps.game.components;

public class PlayerClone extends Entity {
	private Player player;
	private int cloneHealth;

	protected void initHealth() {
		setHealth(cloneHealth);
	}

	protected void onDeath() {
		callDeathListeners();
	}

	public void setCloneHealth(int cloneHealth) {
		this.cloneHealth = cloneHealth;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
}
