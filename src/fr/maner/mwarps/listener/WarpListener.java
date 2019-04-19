package fr.maner.mwarps.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import fr.maner.mwarps.MWarps;
import fr.maner.mwarps.utils.Utils;

public class WarpListener implements Listener {

	private MWarps pl;

	public WarpListener(MWarps pl) {
		this.pl = pl;
	}

	@EventHandler
	public void onPlayerInteractWInventory(InventoryClickEvent e) {
		if(!e.getView().getTitle().startsWith(pl.getInvManager().INV_WARP_TITLE)) return;

		e.setCancelled(true);
		Player p = (Player) e.getWhoClicked();
		ItemStack is = e.getCurrentItem();

		if(is == null || is.getType().equals(Material.AIR))
			return;
		
		if(e.getRawSlot() == 49) {
			if(Utils.isGlowing(is))
				Utils.removeGlowing(is, "§4[§cEdition désactivée§4]");
			else
				Utils.addGlowing(is, "§2[§aEdition activée§2]");
			
			return;
		}

		String skullName = Utils.getSkullName(is);
		int currentPage = getPage(e.getView().getTitle());
		boolean arrowLeft = false;
		boolean isEditing = Utils.isGlowing(e.getInventory().getItem(49));

		switch(skullName) {
		case "MHF_ArrowLeft":
			arrowLeft = true;
		case "MHF_ArrowRight":
			int newPage = currentPage + (arrowLeft ? -1 : 1);
			
			if(newPage < 0 || newPage > pl.getInvManager().getWarpInvMaxPage())
				newPage = currentPage;
			else
				p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1F);
			
			pl.setPageUser(p.getUniqueId(), newPage);
			p.openInventory(pl.getInvManager().getWarpInventory(newPage, isEditing));
			break;

		default:
			int index = e.getRawSlot() + currentPage * 45;
			if(index >= pl.getWarpsList().size()) {
				p.openInventory(pl.getInvManager().getWarpInventory(currentPage, isEditing));
				p.sendMessage("§6» §cLe warp semble invalide, veuillez ressayer");
				return;
			}
			
			String warp = pl.getWarpsList().get(index);
			p.closeInventory();
			
			if(isEditing)
				p.performCommand("warp config " + warp );
			else
				p.performCommand("warp " + warp);
			
			break;
		}
	}
	
	private int getPage(String title) {
		title = ChatColor.stripColor(title.replace(pl.getInvManager().INV_WARP_TITLE, ""));
		
		try {
			return Integer.parseInt(title) - 1;
		} catch(NumberFormatException e) {
			return 0;
		}
	}
}
