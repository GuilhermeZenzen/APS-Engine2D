package com.aps.game;

import java.util.ArrayList;

public class CharacterStats {
	private static ArrayList<CharacterStats> charactersStats = new ArrayList<CharacterStats>();
	private static int attributePoints = 0;
	private static int skillPoints = 0;
	
	private String name;
	private int health = 1;
	private int defense = 1;
	private int damage = 1;
	
	private int skill1Level = 1;
	private int skill2Level = 1;
	private int skill3Level = 1;
	private int ultimateSkillLevel = 1;
	
	public CharacterStats(String name) {
		this.name = name;
		charactersStats.add(this);
	}
	
	public void setStats(int... stats) {
		this.health = stats[0];
		this.defense = stats[1];
		this.damage = stats[2];
		this.skill1Level = stats[3];
		this.skill2Level = stats[4];
		this.skill3Level = stats[5];
		this.ultimateSkillLevel = stats[6];
	}

	public static ArrayList<CharacterStats> getCharactersStats() {
		return charactersStats;
	}

	public static int getAttributePoints() {
		return attributePoints;
	}
	public static void setAttributePoints(int attributePoints) {
		CharacterStats.attributePoints = attributePoints;
	}
	public static void decreaseAttributePoints() {
		attributePoints--;
	}
	public static void increaseAttributePoints() {
		attributePoints++;
	}

	public static int getSkillPoints() {
		return skillPoints;
	}
	public static void setSkillPoints(int skillPoints) {
		CharacterStats.skillPoints = skillPoints;
	}
	public static void decreaseSkillPoints() {
		skillPoints--;
	}
	public static void increaseSkillPoints() {
		skillPoints++;
	}

	public String getName() {
		return name;
	}

	public int getHealth() {
		return health;
	}
	public void increaseHealth() {
		health++;
		decreaseAttributePoints();
	}

	public int getDefense() {
		return defense;
	}
	public void increaseDefense() {
		defense++;
		decreaseAttributePoints();
	}

	public int getDamage() {
		return damage;
	}
	public void increaseDamage() {
		damage++;
		decreaseAttributePoints();
	}

	public int getSkill1Level() {
		return skill1Level;
	}
	public void increaseSkill1Level() {
		skill1Level++;
		decreaseSkillPoints();
	}

	public int getSkill2Level() {
		return skill2Level;
	}
	public void increaseSkill2Level() {
		skill2Level++;
		decreaseSkillPoints();
	}

	public int getSkill3Level() {
		return skill3Level;
	}
	public void increaseSkill3Level() {
		skill3Level++;
		decreaseSkillPoints();
	}

	public int getUltimateSkillLevel() {
		return ultimateSkillLevel;
	}
	public void increaseUltimateSkillLevel() {
		ultimateSkillLevel++;
		decreaseSkillPoints();
	}

	public void setSkill1Level(int skill1Level) {
		this.skill1Level = skill1Level;
	}

	public void setSkill2Level(int skill2Level) {
		this.skill2Level = skill2Level;
	}

	public void setSkill3Level(int skill3Level) {
		this.skill3Level = skill3Level;
	}

	public void setUltimateSkillLevel(int ultimateSkillLevel) {
		this.ultimateSkillLevel = ultimateSkillLevel;
	}
}
