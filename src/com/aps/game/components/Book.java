package com.aps.game.components;

import java.util.ArrayList;

import com.aps.engine.gfx.Image;
import com.aps.engine.util.Vector2;
import com.aps.game.Page;

public class Book extends UIElement {
	private int pageIndex = 0;
	private int horizontalItemsAmount;
	private int verticalItemsAmount;
	private int horizontalSpace;
	private int verticalSpace;
	private ArrayList<Page> pages = new ArrayList<Page>();
	private Page currentPage;
	
	public void setPages(int horizontalItemsAmount, int verticalItemsAmount, int horizontalSpace, int verticalSpace) {
		this.horizontalItemsAmount = horizontalItemsAmount;
		this.verticalItemsAmount = verticalItemsAmount;
		this.horizontalSpace = horizontalSpace;
		this.verticalSpace = verticalSpace;
	}
	
	private void loadItems() {
		if (currentPage != null) {
			currentPage.enable(false);			
		}
		
		currentPage = pages.get(pageIndex);
		currentPage.enable(true);
	}
	
	public void addItem(UIElement item) {
		item.getGameObject().setParent(gameObject);
		
		int lastPageIndex = pages.size() - 1;
		Page itemPage;
		
		if (lastPageIndex > -1 ? pages.get(lastPageIndex).isFull() : true) {
			Page newPage = new Page(itemsPerPage());
			newPage.addItem(item);
			itemPage = newPage;
			pages.add(newPage);

			if (lastPageIndex < 0) {
				loadItems();
			}
		} else {
			pages.get(lastPageIndex).addItem(item);
			itemPage = pages.get(lastPageIndex);
		}
		
		int itemIndex = itemPage.getSize() - 1;
		int horizontalIndex = itemIndex % horizontalItemsAmount;
		int verticalIndex = itemIndex / horizontalItemsAmount;
		int posX = (int)(((width - horizontalSpace * (horizontalItemsAmount - 1)) / horizontalItemsAmount * horizontalIndex) + (horizontalSpace * horizontalIndex) - (pivotX * width));
		int posY = (int)(((height - verticalSpace * (verticalItemsAmount - 1)) / verticalItemsAmount * verticalIndex) + (verticalSpace * verticalIndex) - (pivotY * height));
		item.getGameObject().setLocalPosition(new Vector2(posX, posY));
		item.setDepth(depth + 1 + pages.size() - 1);

		if (itemPage != currentPage) {
			item.gameObject.setDisabled(true);
		}
	}
	
	public void removeItem(int index) {
		int itemsPerPage = itemsPerPage();
		Page page = pages.get(index / itemsPerPage);
		page.removeItem(index % itemsPerPage);
		if (page.getSize() <= 0) {
			pages.remove(pages.size() - 1);
		}
	}
	
	public int indexOf(UIElement item) {
		int pageIndex = -1;
		int itemIndex = -1;
		for (int i = 0; i < pages.size(); i++) {
			Page page = pages.get(i);
			for (int j = 0; j < page.getItems().size(); j++) {
				if (page.getItem(j) == item) {
					pageIndex = i;
					itemIndex = j;
				}
			}
		}
		
		return pageIndex != -1 ? pageIndex * itemsPerPage() + itemIndex : -1;
	}
	
	public void turnPage() {
		if (pageIndex < pages.size() - 1) {
			pageIndex++;
			loadItems();			
		}
	}
	
	public void turnPageBack() {
		if (pageIndex > 0) {
			pageIndex--;
			loadItems();			
		}
	}

	public UIElement getItem(int page, int index) {
		return pages.get(page).getItem(index);
	}
	
	public UIElement getItem(int index) {
		if (index < 0) return null;
		
		int itemsPerPage = itemsPerPage();
		int pageIndex = index / itemsPerPage;
		int itemIndex = index % itemsPerPage;
		
		if (pageIndex >= pages.size()) return null;
		
		if (itemIndex >= pages.get(pageIndex).getSize()) return null;

		return pages.get(pageIndex).getItem(itemIndex);
	}
	
	public int getSize() {
		int size = 0;
		for (Page page : pages) {
			page.getSize();
		}
		return size;
	}
	
	public int itemsPerPage() {
		return horizontalItemsAmount * verticalItemsAmount;
	}
}