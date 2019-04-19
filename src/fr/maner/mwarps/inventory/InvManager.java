package fr.maner.mwarps.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.maner.mwarps.MWarps;
import fr.maner.mwarps.data.WarpData;
import fr.maner.mwarps.enums.ItemsEditEnum;
import fr.maner.mwarps.utils.Utils;

public class InvManager {

	private MWarps pl;
	public final String INV_WARP_TITLE = "§6• §eMWarps §6» §ePage ";
	public final String INV_EDIT_TITLE = "§6• §eMWarps §6» §eConfiguration ";

	public InvManager(MWarps pl) {
		this.pl = pl;
	}
	
	public Inventory getWarpInventory(int page, boolean editWarp) {
		final int numeroPage = page > getWarpInvMaxPage() ? 0 : page;
		Inventory inv = Bukkit.createInventory(null, 54, INV_WARP_TITLE + (page + 1));

		Bukkit.getScheduler().runTaskLater(pl, () -> {
			List<String> warpsList = pl.getWarpsList();

			for(int i = 45 * numeroPage; i < 45 * (numeroPage + 1) && i < warpsList.size(); i++) {
				String warp = warpsList.get(i);
				inv.setItem(i%45, getItemWarp(warp));
			}

			if(numeroPage > 0)
				inv.setItem(47, Utils.getSkull("§ePage précédante", "MHF_ArrowLeft"));
			if(numeroPage < getWarpInvMaxPage())
				inv.setItem(51, Utils.getSkull("§ePage suivante", "MHF_ArrowRight"));
		}, 1L);
		
		ItemStack editItem = new ItemStack(Material.WRITABLE_BOOK);
		ItemMeta meta = editItem.getItemMeta();
		meta.setDisplayName("§eEditer un warp");
		meta.setLore(Arrays.asList("§4[§cEdition désactivée§4]"));
		editItem.setItemMeta(meta);
		if(editWarp)
			Utils.addGlowing(editItem, "§2[§aEdition activée§2]");
		inv.setItem(49, editItem);

		return inv;
	}
	
	public Inventory getEditInventory(String warp) {
		Inventory inv = Bukkit.createInventory(null, InventoryType.CHEST, INV_EDIT_TITLE);
		WarpData warpData = pl.getWarp(warp);

		for(int i = 0; i < 3; i++)
			inv.setItem(2 + 2 * i, Utils.getItem(Material.WRITABLE_BOOK, "§eEditer " + ItemsEditEnum.values()[i], Arrays.asList()));

		for(int i = 0; i < 3; i++) {
			List<String> lores = new ArrayList<String>();
			ItemStack is;

			if(i == 0)
				is = pl.getInvManager().getItemWarp(warp);
			else {
				if(i == 1)
					lores.add(warpData.getDisplayName());
				else if(i == 2)
					lores = new ArrayList<String>(warpData.getLores());
				
				if(lores.isEmpty() || lores.get(0).isEmpty()) {
					lores.clear();
					lores.add("§7Non défini");
				}
				
				is = Utils.getItem(Material.PAPER, "§e" +ItemsEditEnum.values()[i].getNom() + " actuel : ", lores);
			}

			inv.setItem(11 + 2 * i, is);
		}
		
		for(int i = 0; i < 3; i++)
			inv.setItem(20 + 2 * i, Utils.getItem(Material.BARRIER, "§eSupprimer " + ItemsEditEnum.values()[i], Arrays.asList()));

		return inv;
	}
	
	public ItemStack getItemWarp(String warp) {
		WarpData warpData = pl.getWarp(warp);
		
		if(warpData == null)
			return Utils.getSkull("§e" + warp, warp, null);
		else if (warpData.getItem() == null)
			return Utils.getSkull("§e" + warp, warp, warpData);
		else
			return warpData.getItem();
	}
	
	public int getWarpInvMaxPage() {
		return (int) Math.ceil((double) pl.getEss().getWarps().getList().size() / 45) - 1;
	}
}
