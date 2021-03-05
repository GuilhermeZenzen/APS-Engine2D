package com.aps.game;

import java.util.ArrayList;

import com.aps.game.components.Collider;

public class ShapecastHit {
	private Collider colliders[];
	
	public ShapecastHit(Collider... colliders) {
		this.colliders = colliders;
	}
	public ShapecastHit(ArrayList<Collider> colliders) {
		this.colliders = new Collider[colliders.size()];
		for (int i = 0; i < this.colliders.length; i++) {
			this.colliders[i] = colliders.get(i);
		}
	}
	public Collider[] getColliders() {
		return colliders;
	}
}
