package fr.maner.mwarps.data;

import fr.maner.mwarps.enums.ItemsEditEnum;

public class EditData {
	
	private String warp;
	private ItemsEditEnum itemsEdit = null;
	
	public EditData(String warp) {
		this.warp = warp;
	}

	public String getWarp() {
		return warp;
	}

	public ItemsEditEnum getItemsEdit() {
		return itemsEdit;
	}

	public void setItemsEdit(ItemsEditEnum itemsEdit) {
		this.itemsEdit = itemsEdit;
	}
}
