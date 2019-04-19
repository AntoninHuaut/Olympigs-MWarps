package fr.maner.mwarps.listener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import fr.maner.mwarps.MWarps;
import fr.maner.mwarps.data.EditData;
import fr.maner.mwarps.data.WarpData;
import fr.maner.mwarps.enums.ItemsEditEnum;

public class EditListener implements Listener {

	private MWarps pl;
	private HashMap<UUID, EditData> warpEdit = new HashMap<UUID, EditData>();

	public EditListener(MWarps pl) {
		this.pl = pl;
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		UUID uuid = e.getPlayer().getUniqueId();

		if(isWarpEdit(uuid) && warpEdit.get(uuid).getItemsEdit() != null) {
			e.setCancelled(true);
			Bukkit.getScheduler().runTask(pl, () -> runChat(e.getPlayer(), e.getMessage()));
		}
	}

	public void runChat(Player p, String message) {
		UUID uuid = p.getUniqueId();

		if(message.equals("stop"))
			p.sendMessage("§6» §eL'édition a été annulée");
		else {
			EditData editData = warpEdit.get(uuid);

			switch(editData.getItemsEdit()) {
			case LORES:
				pl.setWarpLores(editData.getWarp(), Arrays.asList(message.split(";")));
				p.sendMessage("§6» §aLe lore représentant le warp §e" + editData.getWarp() + " §aa été modifié");
				break;
			case NOM:
				pl.setWarpName(editData.getWarp(), message);
				p.sendMessage("§6» §aLe nom représentant le warp §e" + editData.getWarp() + " §aa été modifié");
				break;
			default:
				break;
			}
		}

		warpEdit.remove(uuid);
	}

	@EventHandler
	public void onPlayerInteractWInventory(InventoryClickEvent e) {
		if(!e.getView().getTitle().startsWith(pl.getInvManager().INV_EDIT_TITLE)) return;

		e.setCancelled(true);
		Player p = (Player) e.getWhoClicked();

		if(!warpEdit.containsKey(p.getUniqueId())) return;

		EditData editData = warpEdit.get(p.getUniqueId());
		WarpData warpData = pl.getWarp(editData.getWarp());
		ItemStack is = e.getCurrentItem();

		if(is == null)
			return;

		int index = (e.getRawSlot()-2)/2 % 9;

		if(index < 0 || index >= ItemsEditEnum.values().length)
			return;

		if(e.getRawSlot() >= 9 && e.getRawSlot() <= 17)
			return;

		ItemsEditEnum itemsEdit = ItemsEditEnum.values()[index];
		boolean remove = e.getRawSlot() > 17;

		switch(itemsEdit) {
		case ITEM:
			if(remove) {
				if(warpData.getItem() == null)
					p.sendMessage("§6» §cVous n'avez pas d'item customisé défini");
				else {
					pl.setWarpItem(editData.getWarp(), null);
					p.sendMessage("§6» §aL'item représentant le warp §e" + editData.getWarp() + " §aa été supprimé");
				}
				break;
			}

			ItemStack itemHand = p.getInventory().getItemInMainHand().clone();

			if(itemHand == null || itemHand.getType().equals(Material.AIR)) {
				p.sendMessage("§6» §cVous devez porter un item en main");
				break;
			}

			pl.setWarpItem(editData.getWarp(), itemHand);
			p.sendMessage("§6» §aL'item représentant le warp §e" + editData.getWarp() + " §aa été modifié");
			break;
		case NOM:
			if (remove) {
				if(warpData.isDefaultDisplayName())
					p.sendMessage("§6» §cVous n'avez pas de nom customisé défini");
				else {
					pl.setWarpName(editData.getWarp(), null);
					p.sendMessage("§6» §aLe nom représentant le warp §e" + editData.getWarp() + " §aa été supprimé");
				}
				break;
			}

			setUserItemsEdit(p.getUniqueId(), itemsEdit);
			p.closeInventory();
			p.sendMessage("§6» §eVeuillez entrer les lores souhaités");
			p.sendMessage("§6• §7Couleur supportée !");
			p.sendMessage("§6• §7Tappez §estop §7pour annuler l'édition");
			p.sendMessage("§6• §7Vous pouvez séparé les lores par un §epoint-virgule §7pour faire un retour à la ligne");
			break;
		case LORES:
			if (remove) {
				warpData.getLores().forEach(System.out::println);
				if(warpData.isDefaultLores())
					p.sendMessage("§6» §cVous n'avez pas de lores customisés définis");
				else {
					pl.setWarpLores(editData.getWarp(), Arrays.asList());
					p.sendMessage("§6» §aLe lore représentant le warp §e" + editData.getWarp() + " §aa été supprimé");
				}
				break;
			}

			setUserItemsEdit(p.getUniqueId(), itemsEdit);
			p.closeInventory();
			p.sendMessage("§6» §eVeuillez entrer le nom souhaité");
			p.sendMessage("§6• §7Couleur supportée !");
			p.sendMessage("§6• §7Tappez §estop §7pour annuler l'édition");
			break;
		}
	}

	public void setUserWarp(UUID uuid, String warp) {
		warpEdit.put(uuid, new EditData(warp));
	}

	public void setUserItemsEdit(UUID uuid, ItemsEditEnum itemsEdit) {
		if(isWarpEdit(uuid))
			warpEdit.get(uuid).setItemsEdit(itemsEdit);
	}
	
	public boolean isWarpEdit(UUID uuid) {
		return warpEdit.containsKey(uuid);
	}
	
	public EditData getEditData(UUID uuid) {
		if(isWarpEdit(uuid))
			return warpEdit.get(uuid);
		
		return null;
	}
}
