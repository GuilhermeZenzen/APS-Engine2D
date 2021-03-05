package com.aps.game;

import com.aps.game.GameStateMachine.GameStates;

public interface GameStateEvent {
	void onStateChange(GameStates gameState);
}
