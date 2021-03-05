package com.aps.game.components;

import com.aps.engine.GameController;
import com.aps.engine.gfx.Image;
import com.aps.engine.util.Vector2;
import com.aps.game.GameData;
import com.aps.game.GameManager;
import com.aps.game.LevelCreator;
import com.aps.game.LevelsManager;
import com.aps.game.Scene;
import com.aps.game.UI;
import com.aps.game.UIOnClick;
import com.aps.game.components.Text.TextHorizontalAlignment;
import com.aps.game.components.Text.TextVerticalAlignment;
import com.aps.game.components.UIElement.Types;

public class MainMenu extends Component {
	private UIElement mainPanel;
	private Button playButton;
	private Button levelCreatorButton;
	
	private UIElement levelsPanel;
	private Book levelsBook;
	private UIElement levels[] = new UIElement[10];
	private Button playLevelButton;
	private Button backButton;
	
	private boolean selectingLevel = false;
	private int selectedLevel = 0;
	private UIElement selectedLevelPanel;
	
	private UIElement levelCreatorLoadPanel;
	private InputText levelName;
	private Button cancelLevelCreatorLoadButton;
	private Button confirmLevelCreatorLoadButton;
	
	private Button statsButton;
	private StatsMenu statsMenu;
	
	public void awake() {
		mainPanel = UI.createUIElement("Main Panel", 0, 0, GameController.getScaledWidth(), GameController.getScaledHeight(), 0, 0, 0, null, 0x00000000, Types.IMAGE);
		playButton = UI.createButton("Play Button", GameController.getScaledWidth() / 2, GameController.getScaledHeight() / 2 - 70, 460, 120, 0.5f, 1, 1, null, 0xff333333, "Fases", 9, 0xffffffff);
		playButton.addClickListener(new Play());
		playButton.getGameObject().setParent(mainPanel.getGameObject());
		levelCreatorButton = UI.createButton("Level Creator Button", GameController.getScaledWidth() / 2, GameController.getScaledHeight() / 2, 460, 120, 0.5f, 0.5f, 1, null, 0xff333333, "Criar fase", 8, 0xffffffff);
		levelCreatorButton.addClickListener(new LoadLevelCreator());
		levelCreatorButton.getGameObject().setParent(mainPanel.getGameObject());
		statsButton = UI.createButton("Stats Button", GameController.getScaledWidth() / 2, GameController.getScaledHeight() / 2 + 70, 460, 120, 0.5f, 0, 1, null, 0xff333333, "Status", 8, 0xffffffff);
		statsButton.addClickListener(new OpenStatsMenu());
		statsButton.getGameObject().setParent(mainPanel.getGameObject());
		
		levelsPanel = UI.createUIElement("Levels Panel", GameController.getScaledWidth() / 2, GameController.getScaledHeight() / 2, 900, 480, 0.5f, 0.5f, 10, null, 0xcc333333, Types.IMAGE);
	
		playLevelButton = UI.createButton("Play Level Button", 0, 0, 573, 35, 1, 1, 11, null, 0xffff7c00, "Jogar", 3, 0xffffffff);
		playLevelButton.getGameObject().setParent(levelsPanel.getGameObject());
		playLevelButton.getGameObject().setLocalPosition(new Vector2((levelsPanel.getWidth() - 40) / 2, levelsPanel.getHeight() / 2 - 20));
		playLevelButton.addClickListener(new PlayLevel());
		
		backButton = UI.createButton("Back Button", 0, 0, 281, 35, 0, 1, 11, null, 0xff00ff9b, "<<<", 3, 0xffffffff);
		backButton.getGameObject().setParent(levelsPanel.getGameObject());
		backButton.addClickListener(new Back());
		
		backButton.getGameObject().setLocalPosition(new Vector2(-(levelsPanel.getWidth() - 40) / 2, levelsPanel.getHeight() / 2 - 20));
		levelsBook = UI.createBook("Levels Book", 0, 0, 860, 400, 0.5f, 0, 11, null, 0x00000000, 5, 2, 10, 10);
		levelsBook.getGameObject().setParent(levelsPanel.getGameObject());
		levelsBook.getGameObject().setLocalPosition(new Vector2(0, -levelsPanel.height / 2 + 20));
		
		Image lockImage = new Image("/lock.png");
		SelectLevel levelSelector = new SelectLevel();
		
		for (int i = 0; i < levels.length; i++) {
			levels[i] = UI.createUIElement("Level", 0, 0, 164, 195, 0, 0, 12, null, 0xcc777777, Types.IMAGE);
			levels[i].addClickListener(levelSelector);
			Text levelName = UI.createText("Level Name", 0, 0, 154, 30, 0, 0, 13, 0xffffffff, LevelsManager.levelName(i), 2, TextHorizontalAlignment.CENTER, TextVerticalAlignment.CENTER);
			levelName.getGameObject().setParent(levels[i].getGameObject());
			levelName.getGameObject().setLocalPosition(new Vector2(5, 5));
			levelName.setInteractable(false);
			UIElement levelIcon = UI.createUIElement("Level Icon", 0, 0, 154, 150, 0, 0, 13, null, 0xffdddddd, Types.IMAGE);
			levelIcon.getGameObject().setParent(levels[i].getGameObject());
			levelIcon.getGameObject().setLocalPosition(new Vector2(5, 40));
			levelIcon.setInteractable(false);
			UIElement levelLock = UI.createUIElement("Level Lock", 0, 0, 120, 120, 0, 0, 14, lockImage, 0xffffffff, Types.IMAGE);
			levelLock.getGameObject().setParent(levelIcon.getGameObject());
			levelLock.getGameObject().setLocalPosition(new Vector2(17, 15));
			levelLock.setInteractable(false);
			if (i < GameData.getUnlockedLevel()) {
				levelLock.getGameObject().setDisabled(true);
			}
			levelsBook.addItem(levels[i]);
		}
		
		levelCreatorLoadPanel = UI.createUIElement("Level Creator Load Panel", GameController.getScaledWidth() / 2, GameController.getScaledHeight() / 2, 518, 178, 0.5f, 0.5f, 20, null, 0xcc333333, Types.IMAGE);
		levelName = UI.createInputText("Level Name", 0, 0, 503, 80, 0.5f, 1, 21, null, 0xcc888888, 0xffffffff, 1);
		levelName.getGameObject().setParent(levelCreatorLoadPanel.getGameObject());
		levelName.getGameObject().setLocalPosition(new Vector2(0, -3f));
		cancelLevelCreatorLoadButton = UI.createButton("Cancel Level Creator Load Button", 0, 0, 250, 80, 1, 0, 21, null, 0xcc888888, "Cancelar", 4, 0xffffffff);
		cancelLevelCreatorLoadButton.getGameObject().setParent(levelCreatorLoadPanel.getGameObject());
		cancelLevelCreatorLoadButton.getGameObject().setLocalPosition(new Vector2(-3f, 3f));
		cancelLevelCreatorLoadButton.addClickListener(new CancelLevelCreatorLoad());
		cancelLevelCreatorLoadButton.setIgnorable(false);
		confirmLevelCreatorLoadButton = UI.createButton("Cancel Level Creator Load Button", 0, 0, 250, 80, 0, 0, 21, null, 0xcc888888, "Confirmar", 4, 0xffffffff);
		confirmLevelCreatorLoadButton.getGameObject().setParent(levelCreatorLoadPanel.getGameObject());
		confirmLevelCreatorLoadButton.getGameObject().setLocalPosition(new Vector2(3f, 3f));
		confirmLevelCreatorLoadButton.addClickListener(new ConfirmLevelCreatorLoad());
		confirmLevelCreatorLoadButton.setIgnorable(false);
		
		statsMenu = GameManager.createGameObject("Stats Menu").createComponent(StatsMenu.class);
		statsMenu.setMainMenu(this);
		statsMenu.getGameObject().setDisabled(true);
		
		openLevelsPanel(false);
		openLevelCreatorPanel(false);
	}

	private void openLevelsPanel(boolean open) {
		selectingLevel = open;
		levelsPanel.getGameObject().setDisabled(!open);
		mainPanel.getGameObject().setDisabled(open);
		
		if (open) {
			selectedLevel = 0;
			selectLevel(levels[0]);
		}
	}
	
	private void selectLevel(UIElement uiElement) {
		if (selectedLevelPanel != null) {
			selectedLevelPanel.setColor(0xcc777777);
		}
		
		if (uiElement != null) {
			uiElement.setColor(0xffff7c00);
		}
		
		selectedLevelPanel = uiElement;
	}
	
	private void openLevelCreatorPanel(boolean open) {
		levelCreatorLoadPanel.getGameObject().setDisabled(!open);
		
		if (open) {
			levelName.focus();
			levelName.setText("");
		} else {
			levelName.defocus();
		}
	}
	
	public void openStatsMenu(boolean open) {
		statsMenu.getGameObject().setDisabled(!open);
		mainPanel.getGameObject().setDisabled(open);
	}
	
	class Play implements UIOnClick {
		public void onClick(UIElement element) {
			openLevelsPanel(true);
		}
	}
	class PlayLevel implements UIOnClick {
		public void onClick(UIElement element) {
			openLevelsPanel(false);
			GameManager.loadLevel(LevelsManager.levelPath(selectedLevel), false);
		}
	}
	class SelectLevel implements UIOnClick {
		public void onClick(UIElement uiElement) {
			for (int i = 0; i < levels.length; i++) {
				if (levels[i] == uiElement && i < GameData.getUnlockedLevel()) {
					selectedLevel = i;
					selectLevel(uiElement);
				}
			}
		}
	}
	class Back implements UIOnClick {
		public void onClick(UIElement uiElement) {
			openLevelsPanel(false);
		}
	}
	
	class LoadLevelCreator implements UIOnClick {
		public void onClick(UIElement element) {
			openLevelCreatorPanel(true);
		}
	}
	class CancelLevelCreatorLoad implements UIOnClick {
		public void onClick(UIElement element) {
			openLevelCreatorPanel(false);
		}
	}
	class ConfirmLevelCreatorLoad implements UIOnClick {
		public void onClick(UIElement element) {
			if (GameManager.loadLevel("/levels/" + levelName.getText() + ".txt", true)) {
				openLevelCreatorPanel(false);
			}
		}
	}
	
	class OpenStatsMenu implements UIOnClick {
		public void onClick(UIElement element) {
			openStatsMenu(true);
		}
	}
}
