package com.aps.game;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;

import com.aps.engine.Input;
import com.aps.engine.Renderer;
import com.aps.engine.gfx.Image;
import com.aps.engine.util.Vector2;
import com.aps.game.components.Book;
import com.aps.game.components.Button;
import com.aps.game.components.InputText;
import com.aps.game.components.InputText.InputTextHorizontalAlignment;
import com.aps.game.components.Text;
import com.aps.game.components.UIElement;
import com.aps.game.components.Text.TextHorizontalAlignment;
import com.aps.game.components.Text.TextVerticalAlignment;
import com.aps.game.components.UIElement.Types;

public class UI {
	private static ArrayList<UIElement> uiElements = new ArrayList<UIElement>();
	private static ArrayList<UIElement> hoveredUIElements = new ArrayList<UIElement>();
	
	private static UIElement lockedUIElement;
	
	public static void addUIElement(UIElement uiElement) {
		uiElements.add(uiElement);
	}
	
	public static void removeUIElement(UIElement uiElement) {
		uiElements.remove(uiElement);
	}
	
	public static void clearUIElements() {
		uiElements.clear();
	}
	
	public static void lockUIElement(UIElement element) {
		lockedUIElement = element;
	}
	
	public static UIElement createUIElement(String tag, int posX, int posY, int width, int height, float pivotX, float pivotY, int depth, Image image, int color, UIElement.Types type) {
		GameObject go = GameManager.createGameObject(tag);
		go.setPosition(new Vector2(posX, posY));
		UIElement element;
		if (type == Types.IMAGE) {
			element = go.createComponent(UIElement.class);			
		} else if (type == Types.TEXT) {
			element = go.createComponent(Text.class);
		} else if (type == Types.BUTTON) {
			element = go.createComponent(Button.class);
		} else if (type == Types.BOOK){
			element = go.createComponent(Book.class);
		} else {
			element = go.createComponent(InputText.class);
		}
		
		element.setWidth(width);
		element.setHeight(height);
		element.setPivotX(pivotX);
		element.setPivotY(pivotY);
		element.setDepth(depth);
		element.setImage(image);
		element.setColor(color);
		return element;
	}
	
	public static Text createText(String tag, int posX, int posY,  int width, int height, float pivotX, float pivotY, int depth, int color, String text, float fontScale, TextHorizontalAlignment horizontalAlignment, TextVerticalAlignment verticalAlignment) {
		Text txt = (Text)createUIElement(tag, posX, posY, width, height, pivotX, pivotY, depth, null, color, Types.TEXT);
		txt.setText(text);
		txt.setFontScale(fontScale);
		txt.setHorizontalAlignment(horizontalAlignment);
		txt.setVerticalAlignment(verticalAlignment);
		return txt;
	}
	
	public static InputText createInputText(String tag, int posX, int posY,  int width, int height, float pivotX, float pivotY, int depth, Image image, int color, int fontColor, float fontScale) {
		InputText inputTxt = (InputText)createUIElement(tag, posX, posY, width, height, pivotX, pivotY, depth, image, color, Types.INPUT_TEXT);
		inputTxt.setFontColor(fontColor);
		return inputTxt;
	}
	
	public static Button createButton(String tag, int posX, int posY,  int width, int height, float pivotX, float pivotY, int depth, Image image, int color, String text, float fontScale, int textColor) {
		Button button = (Button)createUIElement(tag, posX, posY, width, height, pivotX, pivotY, depth, null, color, Types.BUTTON);
		Text buttonText = createText(tag + "Text", 0, 0, width, height, 0.5f, 0.5f, depth + 1, textColor, text, fontScale, TextHorizontalAlignment.CENTER, TextVerticalAlignment.CENTER);
		buttonText.getGameObject().setParent(button.getGameObject());
		buttonText.setInteractable(false);
		float buttonTextOffsetX = button.getWidth() * (0.5f - pivotX);
		float buttonTextOffsetY = button.getHeight() * (0.5f - pivotY);
		buttonText.getGameObject().setLocalPosition(new Vector2(buttonTextOffsetX, buttonTextOffsetY));
		button.setText(buttonText);
		return button;
	}
	
	public static Book createBook(String tag, int posX, int posY,  int width, int height, float pivotX, float pivotY, int depth, Image image, int color, int horizontalItems, int verticalItems, int horizontalSpace, int verticalSpace) {
		Book book = (Book)createUIElement(tag, posX, posY, width, height, pivotX, pivotY, depth, image, color, Types.BOOK);
		book.setPages(horizontalItems, verticalItems, horizontalSpace, verticalSpace);
		return book;
	}
	
	public static void update() {
		int mouseX = Input.getMouseX();
		int mouseY = Input.getMouseY();
		
		boolean clicked = Input.isMouseButtonDown(MouseEvent.BUTTON1);
		boolean rightClicked =Input.isMouseButtonDown(MouseEvent.BUTTON3);
		boolean hit = false;
		
		uiElements.sort(Comparator.comparing(UIElement::getDepth).reversed());

		for (UIElement uiElement : uiElements) {
			//System.out.println(GameManager.getCurrentScene().getGameObjects().contains(uiElement.getGameObject()));
			if (uiElement.getGameObject().isDisabled() || uiElement.isDisabled() || !uiElement.isInteractable() || (lockedUIElement != null ? uiElement != lockedUIElement && uiElement.isIgnorable() : false)) continue;
			
			boolean clickedOnElement = false;
			boolean hovering = Math.abs(mouseX - (uiElement.getGameObject().getPosition().getX() + uiElement.getWidth() * (0.5f - uiElement.getPivotX())))
							   < uiElement.getWidth() / 2
							   && Math.abs(mouseY - (uiElement.getGameObject().getPosition().getY() + uiElement.getHeight() * (0.5f - uiElement.getPivotY())))
							   < uiElement.getHeight() / 2;
			
			if (hovering) {
				if (!hoveredUIElements.contains(uiElement)) {
					uiElement.onEnter();
					hoveredUIElements.add(uiElement);
				}
				
				uiElement.onHover();
				
				if (clicked) {
					if (!hit) {
						clickedOnElement = true;
						hit = true;
						uiElement.onClick();
					}
				}
			} else {
				if (hoveredUIElements.contains(uiElement)) {
					uiElement.onExit();
					hoveredUIElements.remove(uiElement);
				}
			}
			
			if ((clicked || rightClicked) && !clickedOnElement) {
				if (uiElement instanceof InputText) {
					((InputText)uiElement).defocus();
				}
			}
		}
	}

	public static void render() {
		uiElements.sort(Comparator.comparing(UIElement::getDepth));
		
		for (UIElement uiElement : uiElements) {
			if (!uiElement.getGameObject().isDisabled()) {
				if (!uiElement.isDisabled()) {
					int offsetX = (int)(uiElement.getGameObject().getPosition().getX() - uiElement.getWidth() * uiElement.getPivotX());
					int offsetY = (int)(uiElement.getGameObject().getPosition().getY() - uiElement.getHeight() * uiElement.getPivotY());
					
					if (uiElement instanceof Text) {
						Text text = (Text)uiElement;
						int newOffsetX = offsetX;
						int newOffsetY = offsetY;
						if (text.getHorizontalAlignment() == TextHorizontalAlignment.CENTER) {
							newOffsetX += Math.round(text.getWidth() / 2 - Renderer.getFont().getStringWidth(text.getText(), text.getFontScale()) / 2);
						} else if (text.getHorizontalAlignment() == TextHorizontalAlignment.RIGHT) {
							newOffsetX += text.getWidth() - Renderer.getFont().getStringWidth(text.getText(), text.getFontScale());
						}
						
						if (text.getVerticalAlignment() == TextVerticalAlignment.CENTER) {
							newOffsetY += Math.round(text.getHeight() / 2 - Renderer.getFont().getStringHeight(text.getText(), text.getFontScale()) / 2);
						} else if (text.getVerticalAlignment() == TextVerticalAlignment.BOTTOM) {
							newOffsetY += text.getHeight() - Renderer.getFont().getStringHeight(text.getText(), text.getFontScale());
						}
						
						Renderer.drawText(text.getText(), newOffsetX, newOffsetY, offsetX, offsetY, offsetX + text.getWidth(), offsetY + text.getHeight(), text.getColor(), text.getFontScale());
					} else {
						if (uiElement.getImage() != null) {
							Renderer.drawImage(uiElement.getImage(), offsetX, offsetY, uiElement.getWidth(), uiElement.getHeight(), uiElement.getColor(), true);
						} else {
							Renderer.drawFillRect(offsetX, offsetY, uiElement.getWidth(), uiElement.getHeight(), uiElement.getColor(), true);
						}
						
						if (uiElement instanceof InputText) {
							InputText inputText = (InputText)uiElement;
							
							offsetX += Math.round(inputText.getWidth() * 0.01f);
							int newOffsetX = offsetX;
							if (inputText.getHorizontalAlignment() == InputTextHorizontalAlignment.CENTER) {
								newOffsetX += Math.round((inputText.getWidth() - inputText.getTextOffset()) / 2 - Renderer.getFont().getStringWidth(inputText.getText(), inputText.getFontScale()) / 2);
							}
							Renderer.drawText(inputText.getText(), newOffsetX, Math.round(offsetY + inputText.getHeight() * 0.1f),  offsetX, offsetY, offsetX + inputText.usableWidth(), offsetY + inputText.getHeight(), inputText.getFontColor(), inputText.getFontScale(), inputText.isFocused() && inputText.isShowPointer(), inputText.getPointer());
						}
					}
				}
			}
		}
	}

	public static UIElement getLockedUIElement() {
		return lockedUIElement;
	}
}
