package com.aps.game.animation;

import java.util.ArrayList;

import com.aps.engine.gfx.Image;

public class Animation {
	public static final float SAMPLE_TIME = 1f / 24f;
	
	private String name;
	private ArrayList<Image> frames = new ArrayList<Image>();
	private boolean loop = false;

	public Animation() {
		
	}
	public Animation(String name, boolean loop, Image... frames) {
		this.name = name;
		this.loop = loop;
		
		for (Image frame : frames) {
			this.frames.add(frame);
		}
	}
	
	public void addFrame(Image image) {
		frames.add(image);
	}
	
	public ArrayList<Image> getFrames() {
		return frames;
	}
	
	public float getDuration() {
		return (frames.size() - 1) * SAMPLE_TIME;
	}

	public boolean canLoop() {
		return loop;
	}

	public String getName() {
		return name;
	}
}
