package com.aps.game.components;

import com.aps.engine.GameController;
import com.aps.engine.gfx.Image;
import com.aps.engine.util.Mathf;
import com.aps.game.GameManager;
import com.aps.game.UI;
import com.aps.game.components.Text.TextHorizontalAlignment;
import com.aps.game.components.Text.TextVerticalAlignment;
import com.aps.game.components.UIElement.Types;

public class PlayerUI extends Component {
	private Player player;
	
	private UIElement healthBarBackground;
	private UIElement healthBar;
	private Text healthText;
	
	private final Image SKILL_BACKGROUND = new Image("/skill-background.png");
	private UIElement skillsBackground[] = new UIElement[3];
	private UIElement skillsIcon[] = new UIElement[3];
	private Text skillsCooldown[] = new Text[3];
	private UIElement ultimateBackground;
	private UIElement ultimateIcon;
	private Text ultimateCooldownText;
	
	public void awake() {
		healthBarBackground = UI.createUIElement("HealthBarBackground", 5, GameController.getScaledHeight() - 5, 300, 40, 0, 1, 1, null, 0xff555555, Types.IMAGE);
		healthBar = UI.createUIElement("HealthBar", 5, GameController.getScaledHeight() - 5, 300, 40, 0, 1, 1, null, 0xffee0000, Types.IMAGE);
		healthText = UI.createText("HealthText", 10, GameController.getScaledHeight() - 5, 300, 40, 0, 1, 2, 0xffffffff, "", 4, TextHorizontalAlignment.LEFT, TextVerticalAlignment.CENTER);
	}
	
	public void start() {
		player = GameManager.getCurrentScene().getGameObject("Player").getComponent(Player.class);
		
		for (int i = 0; i < skillsIcon.length; i++) {
			skillsBackground[i] = UI.createUIElement("Skill " + (i + 1) + " Background", 320 + (45 * i), GameController.getScaledHeight() - 5, 40, 40, 0, 1, 1, SKILL_BACKGROUND, 0xffffffff, Types.IMAGE);
			skillsIcon[i] = UI.createUIElement("Skill " + (i + 1) + " Icon", 320 + (45 * i), GameController.getScaledHeight() - 5, 40, 40, 0, 1, 1, player.getSkills().getSkillSlots()[i].getSkill().getIcon(), 0xffffffff, Types.IMAGE);
			skillsCooldown[i] = UI.createText("Skill " + (i + 1) + " Cooldown", 320 + (45 * i), GameController.getScaledHeight() - 5, 40, 40, 0, 1, 2, 0xffffffff, "", 2.5f, TextHorizontalAlignment.CENTER, TextVerticalAlignment.CENTER);
		}
		
		ultimateBackground = UI.createUIElement("Ultimate Background", 455, GameController.getScaledHeight() - 5, 40, 40, 0, 1, 1, SKILL_BACKGROUND, 0xffffffff, Types.IMAGE);
		ultimateIcon = UI.createUIElement("Ultimate Icon", 455, GameController.getScaledHeight() - 5, 40, 40, 0, 1, 1, player.getSkills().getUltimateSkillSlot().getSkill().getIcon(), 0xffffffff, Types.IMAGE);
		ultimateCooldownText = UI.createText("Ultimate Cooldown", 455, GameController.getScaledHeight() - 5, 40, 40, 0, 1, 2, 0xffffffff, "", 2.5f, TextHorizontalAlignment.CENTER, TextVerticalAlignment.CENTER);
	}
	
	public void update() {
		healthBar.setWidth(Math.round(300 * ((float)player.getCurrentHealth() / player.getHealth())));
		healthText.setText("+" + (int)Mathf.clamp(player.getCurrentHealth(), 0, player.getHealth()));
		
		for (int i = 0; i < skillsIcon.length; i++) {
			if (player.getSkills().getSkillSlots()[i].isInCooldown()) {
				skillsBackground[i].setColor(0xff0000ff);
				skillsCooldown[i].getGameObject().setDisabled(false);
				int cooldown = (int)Math.max(1, Math.floor(player.getSkills().getSkillSlots()[i].getSkill().getCooldown() - player.getSkills().getSkillSlots()[i].getCooldown()));
				skillsCooldown[i].setText(cooldown + "s");
			} else {
				skillsBackground[i].setColor(0xffffffff);
				skillsCooldown[i].getGameObject().setDisabled(true);
			}
		}

		ultimateIcon.setImage(player.getGameObject().getComponent(PlayerSkillsManager.class).getUltimateSkillSlot().getSkill().getIcon());
		if (player.getSkills().getUltimateSkillSlot().isInCooldown()) {
			ultimateBackground.setColor(0xff0000ff);
			ultimateCooldownText.getGameObject().setDisabled(false);
			ultimateCooldownText.setText((int)Math.floor(player.getSkills().getUltimateSkillSlot().getCooldown() / player.getSkills().getUltimateSkillSlot().getSkill().getCooldown() * 100) + "%");
		} else {
			ultimateBackground.setColor(0xffffffff);
			ultimateCooldownText.getGameObject().setDisabled(true);
		}
	}
}
