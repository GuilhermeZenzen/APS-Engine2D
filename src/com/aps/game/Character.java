package com.aps.game;

import java.util.ArrayList;

public class Character {
	private static ArrayList<Character> characters = new ArrayList<Character>();
	
	private CharacterStats stats;
	
	private int baseHealth = 0;
	private int baseDefense = 0;
	private int baseDamage = 0;
	
	private float healthPerLevel = 0;
	private float defensePerLevel = 0;
	private float damagePerLevel = 0;
	
	private Skill skills[] = new Skill[4];
	
	public Character(CharacterStats stats, int baseHealth, int baseDefense, int baseDamage, float healthPerLevel, float defensePerLevel, float damagePerLevel, String... skills) {
		this.stats = stats;
		this.baseHealth = baseHealth;
		this.baseDefense = baseDefense;
		this.baseDamage = baseDamage;
		this.healthPerLevel = healthPerLevel;
		this.defensePerLevel = defensePerLevel;
		this.damagePerLevel = damagePerLevel;
		for (int i = 0; i < skills.length; i++) {
			this.skills[i] = Skill.getSkills().get(skills[i]);
		}
	}
	
	public static ArrayList<Character> getCharacters() {
		return characters;
	}
	
	public CharacterStats getStats() {
		return stats;
	}
	
	public int getHealth() {
		return baseHealth + (int)(baseHealth * (stats.getHealth() - 1) * healthPerLevel);
	}
	public int getDefense() {
		return baseDefense + (int)(baseDefense * (stats.getDefense() - 1) * defensePerLevel);
	}
	public int getDamage() {
		return baseDamage + (int)(baseDamage * (stats.getDamage() - 1) * damagePerLevel);
	}

	public Skill[] getSkills() {
		return skills;
	}

	public void setSkills(Skill... skills) {
		this.skills = skills;
	}

	public void setSkill1Level(int skill1Level) {
		stats.setSkill1Level(skill1Level);
		skills[0].setLevel(stats.getSkill1Level());
	}
	public void increaseSkill1Level() {
		stats.increaseSkill1Level();
		skills[0].setLevel(stats.getSkill1Level());
	}

	public void setSkill2Level(int skill2Level) {
		stats.setSkill2Level(skill2Level);
		skills[1].setLevel(stats.getSkill2Level());
	}
	public void increaseSkill2Level() {
		stats.increaseSkill2Level();
		skills[1].setLevel(stats.getSkill2Level());
	}

	public void setSkill3Level(int skill3Level) {
		stats.setSkill3Level(skill3Level);
		skills[1].setLevel(stats.getSkill2Level());
	}
	public void increaseSkill3Level() {
		stats.increaseSkill3Level();
		skills[1].setLevel(stats.getSkill2Level());
	}

	public void setUltimateSkillLevel(int ultimateSkillLevel) {
		stats.setUltimateSkillLevel(ultimateSkillLevel);
		skills[1].setLevel(stats.getSkill2Level());
	}
	public void increaseUltimateSkillLevel() {
		stats.increaseUltimateSkillLevel();
		skills[1].setLevel(stats.getSkill2Level());
	}
}
