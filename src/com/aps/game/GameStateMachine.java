package com.aps.game;

import java.util.ArrayList;

public class GameStateMachine {
	public enum GameStates {
		MENU, RUNNING, PAUSED;
	}
	private static GameStates gameState = GameStates.MENU;
	
	private static ArrayList<GameStateEvent> pauseListeners = new ArrayList<GameStateEvent>();
	private static ArrayList<GameStateEvent> resumeListeners = new ArrayList<GameStateEvent>();
	
	public static void addPauseListener(GameStateEvent listener) {
		pauseListeners.add(listener);
	}
	public static void removePauseListener(GameStateEvent listener) {
		pauseListeners.remove(listener);
	}
	
	public static void addResumeListener(GameStateEvent listener) {
		resumeListeners.add(listener);
	}
	public static void removeResumeListener(GameStateEvent listener) {
		resumeListeners.remove(listener);
	}
	
	public static GameStates getGameState() {
		return gameState;
	}
	public static void setGameState(GameStates newGameState) {
		gameState = newGameState;
		
		if (gameState == GameStates.PAUSED) {
			for (GameStateEvent listener : pauseListeners) {
				listener.onStateChange(gameState);
			}
		} else if (gameState == GameStates.RUNNING) {
			for (GameStateEvent listener : resumeListeners) {
				listener.onStateChange(gameState);
			}
		}
	}
}
