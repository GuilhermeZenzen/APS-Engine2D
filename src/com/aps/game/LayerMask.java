package com.aps.game;

import java.util.ArrayList;

public class LayerMask {
	private ArrayList<Integer> layers = new ArrayList<Integer>();
	
	public LayerMask(int... layers) {
		for (int layer : layers) {
			this.layers.add(layer);
		}
	}
	public LayerMask(String... layers) {
		for (String layer : layers) {
			int index = Physics.layerIndex(layer);
			if (index > 0) {
				this.layers.add(index);
			}
		}
	}
	
	public boolean containsLayer(int layer) {
		return layers.contains(layer);
	}
	public boolean containsLayer(String layer) {
		return layers.contains(Physics.layerIndex(layer));
	}
	
	public ArrayList<Integer> getLayers() {
		return layers;
	}
}
