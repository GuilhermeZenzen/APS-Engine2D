package com.aps.game.components;

import javax.xml.stream.events.Characters;

import com.aps.engine.GameController;
import com.aps.engine.gfx.Image;
import com.aps.engine.util.Vector2;
import com.aps.game.Character;
import com.aps.game.CharacterStats;
import com.aps.game.GameData;
import com.aps.game.Tileset;
import com.aps.game.UI;
import com.aps.game.UIOnClick;
import com.aps.game.components.Text.TextHorizontalAlignment;
import com.aps.game.components.Text.TextVerticalAlignment;
import com.aps.game.components.UIElement.Types;

public class StatsMenu extends Component {
	private MainMenu mainMenu;
	private UIElement menuPanel;
	private UIElement characterIcon;
	
	private Button backToMainMenuButton;
	
	private Button characterButtons[] = new Button[1];
	private Character currentCharacter;
	
	private Text availableAttributePointsText;
	private Text availableSkillPointsText;
	
	private UIElement healthIcon;
	private Text healthText;
	private UIElement healthIncreaseButton;
	
	private UIElement defenseIcon;
	private Text defenseText;
	private UIElement defenseIncreaseButton;
	
	private UIElement damageIcon;
	private Text damageText;
	private UIElement damageIncreaseButton;
	
	private UIElement skill1Icon;
	private Text skill1Text;
	private UIElement skill1IncreaseButton;
	
	private UIElement skill2Icon;
	private Text skill2Text;
	private UIElement skill2IncreaseButton;
	
	private UIElement skill3Icon;
	private Text skill3Text;
	private UIElement skill3IncreaseButton;
	
	private UIElement ultimateSkillIcon;
	private Text ultimateSkillText;
	private UIElement ultimateSkillIncreaseButton;
	
	public void start() {
		currentCharacter = Character.getCharacters().get(0);
		
		menuPanel = UI.createUIElement("Stats Menu Panel", 50, 50, GameController.getScaledWidth() - 100, GameController.getScaledHeight() - 100, 0, 0, 1, null, 0x88444444, Types.IMAGE);
		menuPanel.getGameObject().setParent(this.gameObject);
		
		backToMainMenuButton = UI.createButton("Back To Main Menu Button", 0, 0, 75, 75, 0, 0, 2, null, 0xff00ff9b, "<<<", 3, 0xffffffff);
		backToMainMenuButton.addClickListener(new BackToMainMenu());
		backToMainMenuButton.getGameObject().setParent(menuPanel.getGameObject());
		backToMainMenuButton.getGameObject().setLocalPosition(Vector2.zero());
		
		SelectCharacter selectCharacter = new SelectCharacter();
		for (int i = 0; i < characterButtons.length; i++) {
			characterButtons[i] = UI.createButton("Character Button", 0, 0, (int)((menuPanel.getWidth() - 75) / 3f) - 1, 75, 0, 0, 2, null, 0xaa444444, CharacterStats.getCharactersStats().get(i).getName(), 4, 0xffffffff);
			characterButtons[i].getGameObject().setParent(menuPanel.getGameObject());
			characterButtons[i].getGameObject().setLocalPosition(new Vector2((int)((menuPanel.getWidth() - 75) / 3f) * i + 75, 0));
			characterButtons[i].addClickListener(selectCharacter);
		}
	
		int verticalStartInfo = (int)(75 + (menuPanel.getHeight() - 75) * 0.1f);
		int horizontalStartInfo = (int)((menuPanel.getHeight() - 75) * 0.1f);
		
		characterIcon = UI.createUIElement("Character Icon", 0, 0, (int)((menuPanel.getHeight() - 75) * 0.8f), (int)((menuPanel.getHeight() - 75) * 0.8f), 0, 0, 2, null, 0xffffffff, Types.IMAGE);
		characterIcon.getGameObject().setParent(menuPanel.getGameObject());
		characterIcon.getGameObject().setLocalPosition(new Vector2(horizontalStartInfo, verticalStartInfo));
		
		availableAttributePointsText = UI.createText("Available Attribute Points Text", 0, 0, 468, 32, 0, 0, 2, 0xffffffff, "", 4, TextHorizontalAlignment.CENTER, TextVerticalAlignment.CENTER);
		availableAttributePointsText.getGameObject().setParent(menuPanel.getGameObject());
		availableAttributePointsText.getGameObject().setLocalPosition(new Vector2(horizontalStartInfo + characterIcon.getWidth() + horizontalStartInfo, verticalStartInfo + 15));
		
		availableSkillPointsText = UI.createText("Available Skill Points Text", 0, 0, 468, 32, 0, 0, 2, 0xffffffff, "", 4, TextHorizontalAlignment.CENTER, TextVerticalAlignment.CENTER);
		availableSkillPointsText.getGameObject().setParent(menuPanel.getGameObject());
		availableSkillPointsText.getGameObject().setLocalPosition(new Vector2(horizontalStartInfo + characterIcon.getWidth() + horizontalStartInfo, verticalStartInfo + 50));
		
		healthIcon = UI.createUIElement("Health Icon", 0, 0, 32, 32, 0, 0, 2, null/*new Image("/health-icon.png")*/, 0xffffffff, Types.IMAGE);
		healthIcon.getGameObject().setParent(menuPanel.getGameObject());
		healthIcon.getGameObject().setLocalPosition(new Vector2(horizontalStartInfo + characterIcon.getWidth() + horizontalStartInfo, verticalStartInfo + 43 * 3));
		healthIncreaseButton = UI.createUIElement("Health Increase Button", 0, 0, 32, 32, 1, 0, 2, null/*new Image("/health-icon.png")*/, 0xffffffff, Types.IMAGE);
		healthIncreaseButton.getGameObject().setParent(menuPanel.getGameObject());
		healthIncreaseButton.getGameObject().setLocalPosition(new Vector2(menuPanel.getWidth() - horizontalStartInfo * 8, verticalStartInfo + 43 * 3));
		healthIncreaseButton.addClickListener(new IncreaseHealth());
		healthText = UI.createText("Health Text", 0, 0, 360, 32, 0, 0, 2, 0xffffffff, "", 2, TextHorizontalAlignment.LEFT, TextVerticalAlignment.CENTER);
		healthText.getGameObject().setParent(menuPanel.getGameObject());
		healthText.getGameObject().setLocalPosition(new Vector2(horizontalStartInfo + characterIcon.getWidth() + horizontalStartInfo + 42, verticalStartInfo + 43 * 3));
		
		defenseIcon = UI.createUIElement("Defense Icon", 0, 0, 32, 32, 0, 0, 2, null/*new Image("/health-icon.png")*/, 0xffffffff, Types.IMAGE);
		defenseIcon.getGameObject().setParent(menuPanel.getGameObject());
		defenseIcon.getGameObject().setLocalPosition(new Vector2(horizontalStartInfo + characterIcon.getWidth() + horizontalStartInfo, verticalStartInfo + 43 * 4));
		defenseIncreaseButton = UI.createUIElement("Defense Increase Button", 0, 0, 32, 32, 1, 0, 2, null/*new Image("/health-icon.png")*/, 0xffffffff, Types.IMAGE);
		defenseIncreaseButton.getGameObject().setParent(menuPanel.getGameObject());
		defenseIncreaseButton.getGameObject().setLocalPosition(new Vector2(menuPanel.getWidth() - horizontalStartInfo  * 8, verticalStartInfo + 43 * 4));
		defenseIncreaseButton.addClickListener(new IncreaseDefense());
		defenseText = UI.createText("Defense Text", 0, 0, 360, 32, 0, 0, 2, 0xffffffff, "", 2, TextHorizontalAlignment.LEFT, TextVerticalAlignment.CENTER);
		defenseText.getGameObject().setParent(menuPanel.getGameObject());
		defenseText.getGameObject().setLocalPosition(new Vector2(horizontalStartInfo + characterIcon.getWidth() + horizontalStartInfo + 42, verticalStartInfo + 43 * 4));
		
		damageIcon = UI.createUIElement("Damage Icon", 0, 0, 32, 32, 0, 0, 2, null/*new Image("/health-icon.png")*/, 0xffffffff, Types.IMAGE);
		damageIcon.getGameObject().setParent(menuPanel.getGameObject());
		damageIcon.getGameObject().setLocalPosition(new Vector2(horizontalStartInfo + characterIcon.getWidth() + horizontalStartInfo, verticalStartInfo + 43 * 5));
		damageIncreaseButton = UI.createUIElement("Damage Increase Button", 0, 0, 32, 32, 1, 0, 2, null/*new Image("/health-icon.png")*/, 0xffffffff, Types.IMAGE);
		damageIncreaseButton.getGameObject().setParent(menuPanel.getGameObject());
		damageIncreaseButton.getGameObject().setLocalPosition(new Vector2(menuPanel.getWidth() - horizontalStartInfo * 8, verticalStartInfo + 43 * 5));
		damageIncreaseButton.addClickListener(new IncreaseDamage());
		damageText = UI.createText("Damage Text", 0, 0, 360, 32, 0, 0, 2, 0xffffffff, "", 2, TextHorizontalAlignment.LEFT, TextVerticalAlignment.CENTER);
		damageText.getGameObject().setParent(menuPanel.getGameObject());
		damageText.getGameObject().setLocalPosition(new Vector2(horizontalStartInfo + characterIcon.getWidth() + horizontalStartInfo + 42, verticalStartInfo + 43 * 5));
		
		skill1Icon = UI.createUIElement("Skill 1 Icon", 0, 0, 32, 32, 0, 0, 2, null, 0xffffffff, Types.IMAGE);
		skill1Icon.getGameObject().setParent(menuPanel.getGameObject());
		skill1Icon.getGameObject().setLocalPosition(new Vector2(menuPanel.getWidth() - horizontalStartInfo * 8 + 52, verticalStartInfo + 43 * 3));
		skill1IncreaseButton = UI.createUIElement("Skill 1 Increase Button", 0, 0, 32, 32, 1, 0, 2, null/*new Image("/health-icon.png")*/, 0xffffffff, Types.IMAGE);
		skill1IncreaseButton.getGameObject().setParent(menuPanel.getGameObject());
		skill1IncreaseButton.getGameObject().setLocalPosition(new Vector2(menuPanel.getWidth() - horizontalStartInfo * 8 + 260, verticalStartInfo + 43 * 3));
		skill1IncreaseButton.addClickListener(new IncreaseSkill1());
		skill1Text = UI.createText("Skill 1 Text", 0, 0, 360, 32, 0, 0, 2, 0xffffffff, "", 2, TextHorizontalAlignment.LEFT, TextVerticalAlignment.CENTER);
		skill1Text.getGameObject().setParent(menuPanel.getGameObject());
		skill1Text.getGameObject().setLocalPosition(new Vector2(menuPanel.getWidth() - horizontalStartInfo * 8 + 94, verticalStartInfo + 43 * 3));
		
		skill2Icon = UI.createUIElement("Skill 2 Icon", 0, 0, 32, 32, 0, 0, 2, null, 0xffffffff, Types.IMAGE);
		skill2Icon.getGameObject().setParent(menuPanel.getGameObject());
		skill2Icon.getGameObject().setLocalPosition(new Vector2(menuPanel.getWidth() - horizontalStartInfo * 8 + 52, verticalStartInfo + 43 * 4));
		skill2IncreaseButton = UI.createUIElement("Skill 2 Increase Button", 0, 0, 32, 32, 1, 0, 2, null/*new Image("/health-icon.png")*/, 0xffffffff, Types.IMAGE);
		skill2IncreaseButton.getGameObject().setParent(menuPanel.getGameObject());
		skill2IncreaseButton.getGameObject().setLocalPosition(new Vector2(menuPanel.getWidth() - horizontalStartInfo * 8 + 260, verticalStartInfo + 43 * 4));
		skill2IncreaseButton.addClickListener(new IncreaseSkill2());
		skill2Text = UI.createText("Skill 2 Text", 0, 0, 360, 32, 0, 0, 2, 0xffffffff, "", 2, TextHorizontalAlignment.LEFT, TextVerticalAlignment.CENTER);
		skill2Text.getGameObject().setParent(menuPanel.getGameObject());
		skill2Text.getGameObject().setLocalPosition(new Vector2(menuPanel.getWidth() - horizontalStartInfo * 8 + 94, verticalStartInfo + 43 * 4));

		skill3Icon = UI.createUIElement("Skill 3 Icon", 0, 0, 32, 32, 0, 0, 2, null, 0xffffffff, Types.IMAGE);
		skill3Icon.getGameObject().setParent(menuPanel.getGameObject());
		skill3Icon.getGameObject().setLocalPosition(new Vector2(menuPanel.getWidth() - horizontalStartInfo * 8 + 52, verticalStartInfo + 43 * 5));
		skill3IncreaseButton = UI.createUIElement("Skill 3 Increase Button", 0, 0, 32, 32, 1, 0, 2, null/*new Image("/health-icon.png")*/, 0xffffffff, Types.IMAGE);
		skill3IncreaseButton.getGameObject().setParent(menuPanel.getGameObject());
		skill3IncreaseButton.getGameObject().setLocalPosition(new Vector2(menuPanel.getWidth() - horizontalStartInfo * 8 + 260, verticalStartInfo + 43 * 5));
		skill3IncreaseButton.addClickListener(new IncreaseSkill3());
		skill3Text = UI.createText("Skill 3 Text", 0, 0, 360, 32, 0, 0, 2, 0xffffffff, "", 2, TextHorizontalAlignment.LEFT, TextVerticalAlignment.CENTER);
		skill3Text.getGameObject().setParent(menuPanel.getGameObject());
		skill3Text.getGameObject().setLocalPosition(new Vector2(menuPanel.getWidth() - horizontalStartInfo * 8 + 94, verticalStartInfo + 43 * 5));
		
		ultimateSkillIcon = UI.createUIElement("Ultimate Skill Icon", 0, 0, 32, 32, 0, 0, 2, null, 0xffffffff, Types.IMAGE);
		ultimateSkillIcon.getGameObject().setParent(menuPanel.getGameObject());
		ultimateSkillIcon.getGameObject().setLocalPosition(new Vector2(menuPanel.getWidth() - horizontalStartInfo * 8 + 52, verticalStartInfo + 43 * 6));
		ultimateSkillIncreaseButton = UI.createUIElement("Ultimate Skill Increase Button", 0, 0, 32, 32, 1, 0, 2, null/*new Image("/health-icon.png")*/, 0xffffffff, Types.IMAGE);
		ultimateSkillIncreaseButton.getGameObject().setParent(menuPanel.getGameObject());
		ultimateSkillIncreaseButton.getGameObject().setLocalPosition(new Vector2(menuPanel.getWidth() - horizontalStartInfo * 8 + 260, verticalStartInfo + 43 * 6 + 2));
		ultimateSkillIncreaseButton.addClickListener(new IncreaseUltimateSkill());
		ultimateSkillText = UI.createText("Ultimate Skill Text", 0, 0, 360, 32, 0, 0, 2, 0xffffffff, "", 2, TextHorizontalAlignment.LEFT, TextVerticalAlignment.CENTER);
		ultimateSkillText.getGameObject().setParent(menuPanel.getGameObject());
		ultimateSkillText.getGameObject().setLocalPosition(new Vector2(menuPanel.getWidth() - horizontalStartInfo * 8 + 94, verticalStartInfo + 43 * 6));
		
		updateCharacterInfo();
	}
	
	private void updateCharacterInfo() {
		characterIcon.setImage(Tileset.tilesets.get(currentCharacter.getStats().getName().toLowerCase()).getIcon());

		updateAttributesInfo();
		skill1Icon.setImage(currentCharacter.getSkills()[0].getIcon());
		skill2Icon.setImage(currentCharacter.getSkills()[1].getIcon());
		skill3Icon.setImage(currentCharacter.getSkills()[2].getIcon());
		ultimateSkillIcon.setImage(currentCharacter.getSkills()[3].getIcon());
		updateSkillsInfo();
	}
	
	private void updateAttributesInfo() {
		healthText.setText(String.valueOf(currentCharacter.getHealth()));
		defenseText.setText(String.valueOf(currentCharacter.getDefense()));
		damageText.setText(String.valueOf(currentCharacter.getDamage()));
		healthIncreaseButton.getGameObject().setDisabled(CharacterStats.getAttributePoints() <= 0);
		defenseIncreaseButton.getGameObject().setDisabled(CharacterStats.getAttributePoints() <= 0);
		damageIncreaseButton.getGameObject().setDisabled(CharacterStats.getAttributePoints() <= 0);
		availableAttributePointsText.setText("Attribute Points: " + CharacterStats.getAttributePoints());
		GameData.saveData();
	}
	
	private void updateSkillsInfo() {
		skill1Text.setText(String.valueOf(currentCharacter.getStats().getSkill1Level()));
		skill1IncreaseButton.getGameObject().setDisabled(CharacterStats.getSkillPoints() <= 0 || currentCharacter.getStats().getSkill1Level() >= 4);
		skill2Text.setText(String.valueOf(currentCharacter.getStats().getSkill2Level()));
		skill2IncreaseButton.getGameObject().setDisabled(CharacterStats.getSkillPoints() <= 0 || currentCharacter.getStats().getSkill2Level() >= 4);
		skill3Text.setText(String.valueOf(currentCharacter.getStats().getSkill3Level()));
		skill3IncreaseButton.getGameObject().setDisabled(CharacterStats.getSkillPoints() <= 0 || currentCharacter.getStats().getSkill3Level() >= 4);
		ultimateSkillText.setText(String.valueOf(currentCharacter.getStats().getUltimateSkillLevel()));
		ultimateSkillIncreaseButton.getGameObject().setDisabled(CharacterStats.getSkillPoints() <= 0 || currentCharacter.getStats().getUltimateSkillLevel() >= 4);
		availableSkillPointsText.setText("Skill Points: " + CharacterStats.getSkillPoints());
		GameData.saveData();
	}
	
	class BackToMainMenu implements UIOnClick {
		public void onClick(UIElement element) {
			mainMenu.openStatsMenu(false);
		}
	}
	class SelectCharacter implements UIOnClick {
		public void onClick(UIElement element) {
			for (Character character : Character.getCharacters()) {
				if (character.getStats().getName().contentEquals(((Button)element).getText().getText())) {
					currentCharacter = character;
					updateCharacterInfo();
				}
			}
		}
	}
	class IncreaseHealth implements UIOnClick {
		public void onClick(UIElement element) {
			currentCharacter.getStats().increaseHealth();
			updateAttributesInfo();
		}
	}
	class IncreaseDefense implements UIOnClick {
		public void onClick(UIElement element) {
			currentCharacter.getStats().increaseDefense();
			updateAttributesInfo();
		}
	}
	class IncreaseDamage implements UIOnClick {
		public void onClick(UIElement element) {
			currentCharacter.getStats().increaseDamage();
			updateAttributesInfo();
		}
	}
	class IncreaseSkill1 implements UIOnClick {
		public void onClick(UIElement element) {
			currentCharacter.increaseSkill1Level();
			updateSkillsInfo();
		}
	}
	class IncreaseSkill2 implements UIOnClick {
		public void onClick(UIElement element) {
			currentCharacter.increaseSkill2Level();
			updateSkillsInfo();
		}
	}
	class IncreaseSkill3 implements UIOnClick {
		public void onClick(UIElement element) {
			currentCharacter.increaseSkill3Level();
			updateSkillsInfo();
		}
	}
	class IncreaseUltimateSkill implements UIOnClick {
		public void onClick(UIElement element) {
			currentCharacter.increaseUltimateSkillLevel();
			updateSkillsInfo();
		}
	}
	public MainMenu getMainMenu() {
		return mainMenu;
	}

	public void setMainMenu(MainMenu mainMenu) {
		this.mainMenu = mainMenu;
	}
}
