package com.aps.game.components;

import java.awt.event.KeyEvent;

import com.aps.engine.GameController;
import com.aps.engine.Input;
import com.aps.engine.util.Vector2;
import com.aps.game.GameManager;
import com.aps.game.GameStateMachine;
import com.aps.game.UI;
import com.aps.game.UIOnClick;
import com.aps.game.GameStateMachine.GameStates;
import com.aps.game.components.UIElement.Types;

public class PauseMenu extends Component {
	private UIElement pausePanel;
	private Button resumeButton;
	private Button menuButton;
	
	public void awake() {
		pausePanel = UI.createUIElement("Pause Panel", GameController.getScaledWidth() / 2, GameController.getScaledHeight() / 2, 370, 280, 0.5f, 0.5f, 1, null, 0x34000000, Types.IMAGE);
		resumeButton = UI.createButton("Resume Button", 0, 0, 350, 80, 0.5f, 1, 2, null, 0xffffffff, "Continuar", 3, 0xff000000);
		resumeButton.addClickListener(new Resume());
		resumeButton.getGameObject().setParent(pausePanel.getGameObject());
		resumeButton.getGameObject().setLocalPosition(new Vector2(0, -50));
		menuButton = UI.createButton("Menu Button", 0, 0, 350, 80, 0.5f, 0, 2, null, 0xffffffff, "Menu", 3, 0xff000000);
		menuButton.addClickListener(new Menu());
		menuButton.getGameObject().setParent(pausePanel.getGameObject());
		menuButton.getGameObject().setLocalPosition(new Vector2(0, 50));
	}
	
	public void start() {
		resume();
	}
	
	public void update() {
		if (Input.isKeyDown(KeyEvent.VK_ESCAPE)) {
			if (GameStateMachine.getGameState() == GameStates.RUNNING) {
				pause();
			} else {
				resume();
			}
		}
	}
	
	private void pause() {
		GameStateMachine.setGameState(GameStates.PAUSED);
		pausePanel.getGameObject().setDisabled(false);
		GameController.setTimeScale(0f);
	}
	
	private void resume() {
		GameStateMachine.setGameState(GameStates.RUNNING);
		pausePanel.getGameObject().setDisabled(true);
		GameController.setTimeScale(1f);
	}
	
	private void goToMenu() {
		GameStateMachine.setGameState(GameStates.MENU);
		GameManager.setLoadPlayScene(false);
		GameManager.prepareSceneLoading(GameManager.getScenes().get(0));
	}
	
	class Resume implements UIOnClick {
		public void onClick(UIElement uiElement) {
			resume();
		}
	}
	class Menu implements UIOnClick {
		public void onClick(UIElement uiElement) {
			goToMenu();
		}
	}
}
