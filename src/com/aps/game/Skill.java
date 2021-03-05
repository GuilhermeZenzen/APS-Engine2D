package com.aps.game;

import java.util.LinkedHashMap;
import java.util.Map;

import com.aps.engine.gfx.Image;
import com.aps.engine.util.Mathf;

public class Skill {
	private static Map<String, Skill> skills = new LinkedHashMap<>();
	
	private String name;
	private Image icon;
	private boolean ultimate = false;
	private int level = 1;
	private float minCooldown = 0f;
	private float maxCooldown = 0f;
	
	public Skill(String name, String iconPath, boolean ultimate, int level, float maxCooldown, float minCooldown) {
		this.name = name;
		this.ultimate = ultimate;
		this.level = level;
		this.minCooldown = minCooldown;
		this.maxCooldown = maxCooldown;
		this.icon = new Image("/" + iconPath + ".png");
		skills.put(name, this);
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public float getCooldown() {
		return Mathf.lerp(minCooldown, maxCooldown, 1 - ((level - 1) / 3f));
	}

	public String getName() {
		return name;
	}

	public boolean isUltimate() {
		return ultimate;
	}

	public Image getIcon() {
		return icon;
	}

	public static Map<String, Skill> getSkills() {
		return skills;
	}
}
