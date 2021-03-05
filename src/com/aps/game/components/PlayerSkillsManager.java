package com.aps.game.components;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.aps.engine.GameController;
import com.aps.engine.Input;
import com.aps.engine.gfx.Image;
import com.aps.engine.util.Vector2;
import com.aps.game.GameManager;
import com.aps.game.GameObject;
import com.aps.game.GameStateMachine;
import com.aps.game.OnAttack;
import com.aps.game.OnDeath;
import com.aps.game.Skill;
import com.aps.game.GameStateMachine.GameStates;

public class PlayerSkillsManager extends Component {
	private static Map<String, SkillExecution> skills = new HashMap<>();
	
	private Player player;
	
	private SkillSlot skillSlots[] = new SkillSlot[3];
	private SkillSlot ultimateSkillSlot;

	public void setSkills() {
		skills.put("Reborn 1", new RebornSkill(player, "Reborn 1"));
		skills.put("Reborn 2", new RebornSkill(player, "Reborn 1"));
		skills.put("Reborn 3", new RebornSkill(player, "Reborn 1"));
		skills.put("Reborn 4", new RebornSkill(player, "Reborn 1"));
	}
	
	public void awake() {
		player = gameObject.getComponent(Player.class);
		player.addAttackListener(new UltimateRecharge());
		setSkills();
		skillSlots[0] = new SkillSlot(Skill.getSkills().get("Reborn 1"));
		skillSlots[1] = new SkillSlot(Skill.getSkills().get("Reborn 2"));
		skillSlots[2] = new SkillSlot(Skill.getSkills().get("Reborn 3"));
		ultimateSkillSlot = new SkillSlot(Skill.getSkills().get("Reborn 4"));
	}
	
	public void update() {
		if (GameStateMachine.getGameState() == GameStates.PAUSED) return;
		
		for (SkillSlot skillSlot : skillSlots) {
			if (skillSlot == null) continue;
			
			if (skillSlot.isInCooldown()) {
				skillSlot.updateCooldown(GameController.getDeltaTime());
			}
		}
		
		if (Input.isKeyDown(KeyEvent.VK_1)) {
			useSkill(0);
		} else if (Input.isKeyDown(KeyEvent.VK_2)) {
			useSkill(1);
		} else if (Input.isKeyDown(KeyEvent.VK_3)) {
			useSkill(2);
		} else if (Input.isKeyDown(KeyEvent.VK_4)) {
			useSkill(3);
		}
	}
	
	public void useSkill(int index) {
		index %= 4;
		SkillSlot skillSlot = index < 3 ? skillSlots[index] : ultimateSkillSlot;
		
		if (!skillSlot.isInCooldown()) {
			String skillName = skillSlot.getSkill().getName();
			SkillExecution skill = skills.get(skillName);
			skill.execute(player);
			if (skill.getEnterCooldown()) {
				skillSlot.setInCooldown(true);
			}
			skill.setEnterCooldown(true);
		}
	}
	
	class SkillSlot {
		private Skill skill;
		private boolean inCooldown;
		private float cooldown = 0f;
		
		public SkillSlot(Skill skill) {
			this.skill = skill;
		}
		
		public void updateCooldown(float amount) {
			cooldown += amount;
			
			if (cooldown >= skill.getCooldown()) {
				inCooldown = false;
				cooldown = 0f;
			}
		}

		public Skill getSkill() {
			return skill;
		}

		public void setSkill(Skill skill) {
			this.skill = skill;
		}

		public boolean isInCooldown() {
			return inCooldown;
		}

		public void setInCooldown(boolean inCooldown) {
			this.inCooldown = inCooldown;
		}

		public float getCooldown() {
			return cooldown;
		}

		public void setCooldown(float cooldown) {
			this.cooldown = cooldown;
		}
	}

	class UltimateRecharge implements OnAttack {
		public void onAttack(Entity entity, int damage) {
			if (ultimateSkillSlot.isInCooldown()) {
				ultimateSkillSlot.updateCooldown(damage);
			}
		}
	}

	public SkillSlot[] getSkillSlots() {
		return skillSlots;
	}

	public SkillSlot getUltimateSkillSlot() {
		return ultimateSkillSlot;
	}
}

abstract class SkillExecution {
	protected boolean enterCooldown = true;
	
	public abstract void execute(Player player);
	
	public boolean getEnterCooldown() {
		return enterCooldown;
	}
	public void setEnterCooldown(boolean enterCooldown) {
		this.enterCooldown = enterCooldown;
	}
}

class RebornSkill extends SkillExecution {
	private Player player;
	private Skill skill;
	private ArrayList<PlayerClone> playerClones = new ArrayList<PlayerClone>();
	private PlayerDeath playerDeath = new PlayerDeath();
	private CloneDeath cloneDeath = new CloneDeath();
	private ArrayList<Vector2> rebornPositions = new ArrayList<Vector2>();
	private int healths[] = { 20, 30, 40, 50 };
	
	public RebornSkill(Player player, String skill) {
		this.player = player;
		this.skill = Skill.getSkills().get(skill);
		player.addDeathListener(playerDeath);
	}
	
	public void execute(Player player) {
		if (player.getCurrentHealth() - healths[0] <= 0) {
			enterCooldown = false;
			return;
		}
		
		rebornPositions.add(player.getGameObject().getPosition());
		player.applyDamage(healths[0]);
		GameObject cloneGO = GameManager.createGameObject("Player Clone");
		PlayerClone clone = cloneGO.createComponent(PlayerClone.class);
		playerClones.add(clone);
		clone.setCloneHealth(healths[skill.getLevel() - 1]);
		clone.setPlayer(player);
		Sprite sprite = cloneGO.createComponent(Sprite.class);
		sprite.setImage(new Image("/petase-clone.png"));
		RectCollider collider = cloneGO.createComponent(RectCollider.class);
		collider.setSize(new Vector2(0.25f, 0.25f));
		cloneGO.createComponent(Rigidbody.class);
		cloneGO.setPosition(player.getGameObject().getPosition());
		clone.addDeathListener(cloneDeath);
	}
	
	private void removeReborn(int index) {
		GameManager.destroyGameObject(playerClones.get(index).getGameObject());
		playerClones.remove(index);
		rebornPositions.remove(index);
	}
	
	class PlayerDeath implements OnDeath {
		public void onDeath(Entity entity) {
			if (rebornPositions.size() <= 0) return;

			Player player = (Player)entity;
			PlayerClone clone = playerClones.get(playerClones.size() - 1);
			RectCollider cloneCollider = clone.getGameObject().getComponent(RectCollider.class);
			player.setRebornClone(clone);
			player.getGameObject().setPosition(clone.getGameObject().getPosition().add(0, -player.getMotor().getCollider().getHalfSize().getY() + cloneCollider.getHalfSize().getY()));
			player.applyHeal(playerClones.get(playerClones.size() - 1).getCurrentHealth());
			player.setCanDie(false);
			removeReborn(rebornPositions.size() - 1);
		}
	}
	class CloneDeath implements OnDeath {
		public void onDeath(Entity entity) {
			int index = playerClones.indexOf(entity);
			
			if (index >= 0) {
				removeReborn(index);
			}
		}
	}
}
