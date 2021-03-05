package com.aps.engine;

public abstract class AbstractGame {
	public abstract void update(GameController gameController, float deltaTime);
	public abstract void render(GameController gameController, Renderer renderer);
}
