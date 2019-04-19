package fr.maner.mwarps;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.earth2me.essentials.Essentials;

import fr.maner.mwarps.cmd.WarpCmd;
import fr.maner.mwarps.data.WarpData;
import fr.maner.mwarps.inventory.InvManager;
import fr.maner.mwarps.listener.EditListener;
import fr.maner.mwarps.listener.WarpListener;
import fr.maner.mwarps.utils.WarpConfig;

public class MWarps extends JavaPlugin {

	private Essentials ess;
	private EditListener editListener;
	private InvManager invManager;
	private WarpConfig warpConfig;
	
	private HashMap<UUID, Integer> usersPages = new HashMap<UUID, Integer>();
	private HashMap<String, WarpData> warps = new HashMap<String, WarpData>();

	@Override
	public void onEnable() {
		this.ess = (Essentials) getServer().getPluginManager().getPlugin("Essentials");
		this.invManager = new InvManager(this);

		editListener = new EditListener(this);
		getServer().getPluginManager().registerEvents(editListener, this);
		getServer().getPluginManager().registerEvents(new WarpListener(this), this);

		WarpCmd warpCmd = new WarpCmd(this, editListener);
		getCommand("mwarps").setExecutor(warpCmd);
		getCommand("mwarps").setTabCompleter(warpCmd);

		warpConfig = new WarpConfig(this);
	}

	@Override
	public void onDisable() {
		warpConfig.saveWarps();
	}

	public WarpData getWarp(String warp) {
		if(!warps.containsKey(warp))
			warps.put(warp, new WarpData(warp));

		return warps.get(warp);
	}

	public int getPageUser(UUID uuid) {
		if(usersPages.containsKey(uuid))
			return usersPages.get(uuid);

		return 0;
	}

	public void setPageUser(UUID uuid, int page) {
		usersPages.put(uuid, page);
	}

	public void setWarpItem(String warp, ItemStack is) {
		WarpData warpData = warps.containsKey(warp) ? warps.get(warp) : new WarpData(warp);
		warpData.setItem(is);
		warps.put(warp, warpData);
		warpData.updateInvView(this, editListener);
		warpConfig.saveWarps();
	}

	public void setWarpLores(String warp, List<String> lore) {
		WarpData warpData = warps.containsKey(warp) ? warps.get(warp) : new WarpData(warp);
		warpData.setLores(lore);
		warps.put(warp, warpData);
		warpData.updateInvView(this, editListener);
		warpConfig.saveWarps();
	}

	public void setWarpName(String warp, String name) {
		WarpData warpData = warps.containsKey(warp) ? warps.get(warp) : new WarpData(warp);
		warpData.setDisplayName(name);
		warps.put(warp, warpData);
		warpData.updateInvView(this, editListener);
		warpConfig.saveWarps();
	}
	
	public HashMap<String, WarpData> getWarpsData() {
		return warps;
	}

	public List<String> getWarpsList() {
		return getEss().getWarps().getList().stream().sorted((str1, str2) -> str1.compareToIgnoreCase(str2)).collect(Collectors.toList());
	}

	public InvManager getInvManager() {
		return invManager;
	}

	public Essentials getEss() {
		return ess;
	}
}
