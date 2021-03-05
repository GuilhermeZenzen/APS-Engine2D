package com.aps.game.components;

public class Text extends UIElement {
	public enum TextHorizontalAlignment {
		LEFT, CENTER, RIGHT;
	}
	public enum TextVerticalAlignment {
		TOP, CENTER, BOTTOM;
	}
	
	private String text;
	private float fontScale;
	private TextHorizontalAlignment horizontalAlignment = TextHorizontalAlignment.LEFT;
	private TextVerticalAlignment verticalAlignment = TextVerticalAlignment.TOP;
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	public float getFontScale() {
		return fontScale;
	}
	public void setFontScale(float fontScale) {
		this.fontScale = fontScale;
	}
	
	public TextHorizontalAlignment getHorizontalAlignment() {
		return horizontalAlignment;
	}
	public void setHorizontalAlignment(TextHorizontalAlignment horizontalAlignment) {
		this.horizontalAlignment = horizontalAlignment;
	}
	
	public TextVerticalAlignment getVerticalAlignment() {
		return verticalAlignment;
	}
	public void setVerticalAlignment(TextVerticalAlignment verticalAlignment) {
		this.verticalAlignment = verticalAlignment;
	}
}
