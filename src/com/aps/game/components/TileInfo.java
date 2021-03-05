package com.aps.game.components;

public class TileInfo extends Component {
	private String tileset;
	private String tileName;
	private int color;
	
	public String getTileset() {
		return tileset;
	}
	public void setTileset(String tileset) {
		this.tileset = tileset;
	}
	
	public String getTileName() {
		return tileName;
	}
	public void setTileName(String tileName) {
		this.tileName = tileName;
	}
	
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}
}
