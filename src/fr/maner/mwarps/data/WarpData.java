package fr.maner.mwarps.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.maner.mwarps.MWarps;
import fr.maner.mwarps.listener.EditListener;

public class WarpData {

	private String warp;
	private ItemStack item = null;
	private String displayName;
	private List<String> lores = new ArrayList<String>();

	public WarpData(String warp) {
		this.warp = warp;
		setDisplayName(null);
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		if(displayName == null)
			this.displayName = "§e" + warp;
		else
			this.displayName = "§e" + ChatColor.translateAlternateColorCodes('&', displayName);

		updateMeta();
	}
	
	public boolean isDefaultDisplayName() {
		return this.displayName.equals("§e" + warp);
	}

	public List<String> getLores() {
		return lores;
	}

	public void setLores(List<String> lores) {
		if(lores == null)
			this.lores.clear();
		else
			this.lores = lores.stream().map(get -> ChatColor.translateAlternateColorCodes('&', get)).collect(Collectors.toList());

		updateMeta();
	}
	
	public boolean isDefaultLores() {
		return getLores().isEmpty();
	}

	public ItemStack getItem() {
		return item;
	}

	public void setItem(ItemStack item) {
		this.item = item;

		updateMeta();
	}

	public void updateInvView(MWarps pl, EditListener editListener) {
		for(Player p : Bukkit.getOnlinePlayers())
			if(p.getOpenInventory().getTitle().equals(pl.getInvManager().INV_EDIT_TITLE) && editListener.getEditData(p.getUniqueId()).getWarp().equals(warp))
				p.openInventory(pl.getInvManager().getEditInventory(warp));
	}

	private void updateMeta() {
		if(item == null)
			return;

		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		if(!lores.isEmpty())
			meta.setLore(lores);
		item.setItemMeta(meta);
	}
}
