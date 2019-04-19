package fr.maner.mwarps.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import fr.maner.mwarps.MWarps;
import fr.maner.mwarps.data.WarpData;

public class Utils {
	
	public static HashMap<String, ItemStack> cachesPlayers = new HashMap<String, ItemStack>();
	
	public static ItemStack addGlowing(ItemStack is, String...lores) {
		is.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		ItemMeta meta = is.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		if(lores.length > 0)
			meta.setLore(Arrays.asList(lores));
		is.setItemMeta(meta);
		
		return is;
	}
	
	public static boolean isGlowing(ItemStack is) {
		return is.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS);
	}
	
	public static ItemStack removeGlowing(ItemStack is, String...lores) {
		is.removeEnchantment(Enchantment.DURABILITY);
		ItemMeta meta = is.getItemMeta();
		meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
		if(lores.length > 0)
			meta.setLore(Arrays.asList(lores));
		is.setItemMeta(meta);
		
		return is;
	}
	
	public static ItemStack getItem(Material mat, String name, List<String> lores) {
		ItemStack is = new ItemStack(mat);
		ItemMeta meta = is.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lores);
		is.setItemMeta(meta);

		return is;
	}
	
	public static ItemStack getSkull(String name, String headOwner, WarpData warpData) {
		ItemStack is = getDefaultSkull(name, headOwner);
		SkullMeta skullMeta = (SkullMeta) is.getItemMeta();
		skullMeta.setDisplayName(name); 
		if(warpData != null) {
			if(!warpData.getDisplayName().isEmpty())
				skullMeta.setDisplayName(warpData.getDisplayName());
			skullMeta.setLore(warpData.getLores());
		}
		is.setItemMeta(skullMeta);

		return is;
	}

	public static ItemStack getSkull(String name, String headOwner) {
		return getSkull(name, headOwner, null);
	}
	
	@SuppressWarnings("deprecation")
	public static String getSkullName(ItemStack is) {
		if(!is.getType().equals(Material.PLAYER_HEAD))
			return "";
		
		String owner = ((SkullMeta) is.getItemMeta()).getOwner();
		return owner == null ? "" : owner;
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack getDefaultSkull(String name, String headOwner) {
		if(cachesPlayers.containsKey(name))
			return cachesPlayers.get(name);
		
		ItemStack is = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta skullMeta = (SkullMeta) is.getItemMeta();
		skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(headOwner));
		is.setItemMeta(skullMeta);
		cachesPlayers.put(name, is);
		
		return is;
	}

	public static void setDefaultSkull(MWarps pl, List<String> headsList) {
		headsList.addAll(Arrays.asList("MHF_ArrowLeft", "MHF_ArrowRight"));
		
		headsList.forEach(head -> {
			Bukkit.getScheduler().runTaskLater(pl, () -> {
				Utils.getDefaultSkull(head, head);
			}, headsList.indexOf(head));
		});
	}

}
