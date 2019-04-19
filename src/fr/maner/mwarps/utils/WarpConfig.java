package fr.maner.mwarps.utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;

import fr.maner.mwarps.MWarps;
import fr.maner.mwarps.data.WarpData;

public class WarpConfig {

	private MWarps pl;

	public WarpConfig(MWarps pl) {
		this.pl = pl;

		loadWarps();
	}

	public void loadWarps() {
		File config = new File(pl.getDataFolder() + "/config.yml");

		if(!config.exists())
			try {
				config.createNewFile();
			} catch (IOException e) {
				pl.getLogger().log(Level.SEVERE, "§cImpossible de créer le fichier de configuration");
				e.printStackTrace();
				return;
			}

		if(pl.getConfig().isConfigurationSection("Warps")) {
			for(String key : pl.getConfig().getConfigurationSection("Warps").getKeys(false)) {
				WarpData warpData = new WarpData(key);
				warpData.setDisplayName(pl.getConfig().getString("Warps." + key  + ".DisplayName"));
				warpData.setLores(pl.getConfig().getStringList("Warps." + key + ".Lores"));
				warpData.setItem(pl.getConfig().getItemStack("Warps." + key + ".Item"));
				pl.getWarpsData().put(key, warpData);
			}
		}
		
		Utils.setDefaultSkull(pl, pl.getWarpsList());
	}

	public void saveWarps() {
		pl.getConfig().set("Warps", null);

		pl.getConfig().createSection("Warps");
		ConfigurationSection cs = pl.getConfig().getConfigurationSection("Warps");

		for(String key : pl.getWarpsData().keySet()) {
			WarpData warpData = pl.getWarpsData().get(key);
			ConfigurationSection csKey = cs.createSection(key);
			csKey.set("DisplayName", warpData.getDisplayName());
			csKey.set("Lores", warpData.getLores());
			csKey.set("Item", warpData.getItem());
		}

		pl.saveConfig();
	}

}
