package com.aps.game.components;

import java.awt.event.KeyEvent;

import com.aps.engine.GameController;
import com.aps.engine.Input;
import com.aps.engine.Renderer;
import com.aps.engine.util.Vector2;
import com.aps.game.GameManager;
import com.aps.game.GameObject;
import com.aps.game.UI;
import com.aps.game.components.Text.TextHorizontalAlignment;

public class InputText extends UIElement {
	private String text = "";
	private int fontColor;
	public enum InputTextHorizontalAlignment {
		LEFT, CENTER;
	}
	public InputTextHorizontalAlignment horizontalAlignment;
	private int maxCharacters = -1;
	private boolean focused;
	private int textOffset = 0;
	private int pointer = 0;
	private float scale = 1;
	
	private boolean digitOnly;
	private boolean integer;
	private boolean positiveOnly;
	
	private float pointerTickTime = 0.5f;
	private float pointerTickElapsedTime = 0;
	private boolean showPointer = false;

	public void start() {
		super.start();
		defocus();
		horizontalAlignment = InputTextHorizontalAlignment.CENTER;
	}
	
	public void update() {
		float fontScale = getFontScale();
		int usableWidth = usableWidth();
		
		if (focused) {
			pointerTickElapsedTime += GameController.getDeltaTime();
			
			if (pointerTickElapsedTime > pointerTickTime) {
				pointerTickElapsedTime = 0;
				showPointer = !showPointer;
			}
			
			if (Input.getPressedKeysString().length() > 0) {
				for (int i = 0; i < Input.getPressedKeysString().length(); i++) {
					char keyChar = Input.getPressedKeysString().charAt(i);

					if (keyChar >= 32 && keyChar <= 126) {
						int charWidth = Renderer.getFont().getWidth(keyChar - 32, fontScale);
						if ((maxCharacters < 0 ? true : text.length() < maxCharacters) && (horizontalAlignment == InputTextHorizontalAlignment.LEFT ? true : Renderer.getFont().getStringWidth(text, fontScale) + charWidth < usableWidth)) {
							if (digitOnly) {
								String keyString = String.valueOf(keyChar);
								
								if (!"0123456789".contains(keyString)) {
									if ((integer && keyChar == '.') || keyChar != '.') {
										if ((positiveOnly && keyChar == '-') || keyChar != '-') {
											continue;
										}
									}
								}
							}
									
							if (pointer == text.length()) {
								text += keyChar;
							} else if (pointer == 0) {
								text = keyChar + text;
							} else {
								StringBuilder sb = new StringBuilder(text);
								sb.insert(pointer, keyChar);
								text = sb.toString();
							}
							
							if (!Input.isKey(KeyEvent.VK_ALT)) {
								pointer++;
							}
						}
					} else {
						if (keyChar == 8 && text.length() > 0) {
							if (Input.isKey(KeyEvent.VK_CONTROL)) {
								text = "";
								pointer = 0;
							} else {	
								if (pointer <= text.length()) {
									if (pointer > 0 && !Input.isKey(KeyEvent.VK_ALT)) {
										if (pointer == text.length()) {
											text = text.substring(0, text.length() - 1);
										} else if (pointer == 1) {
											text = text.substring(1);
										} else if (pointer > 1) {
											StringBuilder sb = new StringBuilder(text);
											sb.deleteCharAt(pointer - 1);
											text = sb.toString();
										}
										
										pointer--;
									} else if (Input.isKey(KeyEvent.VK_ALT)) {
										if (pointer == 0) {
											text = text.substring(1);
										} else if (pointer > 0 && pointer < text.length()) {
											StringBuilder sb = new StringBuilder(text);
											sb.deleteCharAt(pointer);
											text = sb.toString();
										}
									}
								}
							}
						}
					}
				}
				
				if (Input.isKey(KeyEvent.VK_RIGHT) && pointer < text.length()) {
					pointer++;
				}
				if (Input.isKey(KeyEvent.VK_LEFT) && pointer > 0) {
					pointer--;
				}
			}
		}
		
		int textWidth = Renderer.getFont().getStringWidth(text, fontScale);
		int pointerPos = Renderer.getFont().getStringWidth(text, fontScale, pointer);
		
		if (horizontalAlignment == InputTextHorizontalAlignment.LEFT) {
			if (pointerPos > usableWidth) {
				textOffset = pointerPos - usableWidth;
			} else {
				textOffset = 0;
			}
		}
	}
	
	public void onDisable() {
		defocus();
	}
	
	public void onClick() {
		super.onClick();
		focus();
	}
	
	public void setTypingRules(boolean digitOnly) {
		setTypingRules(digitOnly, false, false);
	}
	public void setTypingRules(boolean digitOnly, boolean integer) {
		setTypingRules(digitOnly, integer, false);
	}
	public void setTypingRules(boolean digitOnly, boolean integer, boolean positiveOnly) {
		this.digitOnly = digitOnly;
		
		if (digitOnly) {
			this.integer = integer;
			this.positiveOnly = positiveOnly;
		} else {
			this.integer = false;
			this.positiveOnly = false;
		}
	}

	public void focus() {
		pointerTickElapsedTime = 0;
		focused = true;
		showPointer = true;
		pointer = text.length();
	}
	
	public void defocus() {
		focused = false;
		pointerTickElapsedTime = 0;
		showPointer = false;
	}
	
	public int usableWidth() {
		return (int)Math.floor(width * 0.99f);
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
		if (pointer > text.length()) {
			pointer = text.length();
		}
	}
	
	public int getFontColor() {
		return fontColor;
	}

	public void setFontColor(int fontColor) {
		this.fontColor = fontColor;
	}

	public float getFontScale() {
		return (height * 0.8f) / Renderer.getFont().getHeight() * scale;
	}
	
	public InputTextHorizontalAlignment getHorizontalAlignment() {
		return horizontalAlignment;
	}

	public void setHorizontalAlignment(InputTextHorizontalAlignment horizontalAlignment) {
		this.horizontalAlignment = horizontalAlignment;
	}

	public boolean isFocused() {
		return focused;
	}

	public int getTextOffset() {
		return textOffset;
	}

	public int getPointer() {
		return pointer;
	}

	public boolean isShowPointer() {
		return showPointer;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}
}
