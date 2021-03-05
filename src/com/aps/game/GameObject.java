package com.aps.game;

import java.util.ArrayList;
import java.util.Comparator;

import com.aps.engine.GameController;
import com.aps.engine.Renderer;
import com.aps.engine.util.Vector2;
import com.aps.game.components.Component;
import com.aps.game.components.Sprite;

public class GameObject {
	private String tag;
	private int layer = 0;
	
	private GameObject parent;
	private ArrayList<GameObject> children = new ArrayList<GameObject>();
	
	private Vector2 position;
	private Vector2 localPosition;
	
	private boolean disabled = false;
	
	private ArrayList<Component> components = new ArrayList<Component>();
	private ArrayList<Component> componentsToAdd = new ArrayList<Component>();
	private ArrayList<Component> componentsToRemove = new ArrayList<Component>();

	public GameObject(String tag) {
		this.tag = tag;
		position = new Vector2();
		localPosition = new Vector2();
	}
	
	public void physicsUpdate() {
		for (Component component : componentsToRemove) {
			components.remove(component);
		}
		
		componentsToRemove.clear();
		components.sort(Comparator.comparing(Component::getExecutionOrder));
		
		for (Component component : components) {
			if (!component.isDisabled()) {
				if (component.isInitialized()) {
					component.physicsUpdate();
				}
			}
		}
	}
	
	public void update(GameController gameController, float deltaTime) {
		for (Component component : componentsToAdd) {
			components.add(component);
		}
		
		componentsToAdd.clear();
		components.sort(Comparator.comparing(Component::getExecutionOrder));
		
		for (Component component : components) {
			if (!component.isDisabled()) {
				if (component.isInitialized()) {
					component.update();
				} else {
					component.start();
					component.setInitialized(true);
				}
			}
		}
	}
	
	public void lateUpdate() {
		for (Component component : components) {
			if (!component.isDisabled()) {
				if (component.isInitialized()) {
					component.lateUpdate();
				}
			}
		}
	}

	public void render(GameController gameController, Renderer renderer) {
		for(Component component : components) {
			if(!component.isDisabled()) {
				component.render(renderer);
			}
		}
	}
	
	public void onDestroy() {
		for(Component component : components) {
			if(!component.isDisabled()) {
				component.onDestroy();
			}
		}
	}

	public void addChild(GameObject child) {
		children.add(child);
	}
	
	public GameObject getChild(String tag) {
		for (GameObject child : children) {
			if (child.tag.equals(tag)) {
				return child;
			}
		}
		
		return null;
	}
	
	public void removeChild(GameObject child) {
		children.remove(child);
	}
	
	public void move(Vector2 movement) {
		move(movement.getX(), movement.getY());
	}
	public void move(float x, float y) {
		setPosition(new Vector2(position.getX() + x, position.getY() + y));
	}
	
	private void updatePosition() {
		if(parent != null) {
			position = parent.getPosition().add(localPosition);
			updateChildrenPosition();
		}
	}
	
	private void updateLocalPosition() {
		if(parent != null) {
			localPosition = this.position.subtract(parent.getPosition());
		}
		else {
			localPosition = position;
		}
		
		updateChildrenPosition();
	}
	
	private void updateChildrenPosition() {
		children.forEach(x -> x.updatePosition());
	}
	
	public <T extends Component> T createComponent(Class<T> componentClass) {
		try {
			T component = componentClass.newInstance();
			addComponent(component);
			return component;
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void addComponent(Component component) {
		component.setGameObject(this);
		componentsToAdd.add(component);
		component.awake();
	}
	
	public void removeComponent(Component component) {
		removeComponent(component, true);
	}
	public void removeComponent(Component component, boolean checkBeforeRemoving) {
		if (checkBeforeRemoving) {
			if (!components.contains(component)) {
				return;
			}
		}
		
		component.onRemove();
		componentsToRemove.add(component);
	}
	
	public <T extends Component> Component removeComponent(Class<T> componentClass) {
		for (Component component : components) {
			if (componentClass.isInstance(component)) {
				removeComponent(component, false);
				return (T)component;
			}
		}
		
		return null;
	}
	
	public <T extends Component> T getComponent(Class<T> componentClass) {
		for (Component component : components) {
			if (componentClass.isInstance(component)) {
				return (T)component;
			}
		}
		
		for (Component component : componentsToAdd) {
			if (componentClass.isInstance(component)) {
				return (T)component;
			}
		}
		
		return null;
	}
	
	public ArrayList<Component> getComponents() {
		return components;
	}

	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	
	public int getLayer() {
		return layer;
	}
	public String getLayerName() {
		return Physics.layerName(layer);
	}
	public void setLayer(int layer) {
		this.layer = layer;
	}
	public void setLayer(String layer) {
		this.layer = Physics.layerIndex(layer);
	}

	public GameObject getParent() {
		return parent;
	}
	
	public void setParent(GameObject parent) {
		if(this.parent != null) {
			this.parent.removeChild(this);
		}
		
		this.parent = parent;
		
		if(this.parent != null) {
			this.parent.addChild(this);
		}
		
		updateLocalPosition();
	}
	
	public ArrayList<GameObject> getChildren(){
		return children;
	}
	
	public Vector2 getPosition() {
		return position;
	}
	public void setPosition(Vector2 position) {
		this.position = position;
		updateLocalPosition();
	}

	public Vector2 getLocalPosition() {
		return localPosition;
	}
	public void setLocalPosition(Vector2 localPosition) {
		this.localPosition = localPosition;
		updatePosition();
	}

	public boolean isDisabled() {
		if (disabled) {
			return true;
		}
		
		if (parent != null) {
			return parent.isDisabled();
		}
		
		return false;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
}
