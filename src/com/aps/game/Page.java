package com.aps.game;

import java.util.ArrayList;

import com.aps.game.components.UIElement;

public class Page {
	private int itemCapacity;
	private ArrayList<UIElement> items = new ArrayList<UIElement>();
	
	public Page(int itemCapacity) {
		this.itemCapacity = itemCapacity;
	}
	
	public void addItem(UIElement item) {
		items.add(item);
	}
	public void removeItem(int index) {
		items.remove(index);
	}
	
	public UIElement getItem(int index) {
		return items.get(index);
	}
	
	public ArrayList<UIElement> getItems(){
		return items;
	}
	
	public int getSize() {
		return items.size();
	}
	
	public boolean isFull() {
		return items.size() >= itemCapacity;
	}
	
	public void enable(boolean enable) {
		for (UIElement item : items) {
			item.getGameObject().setDisabled(!enable);
		}
	}
}
