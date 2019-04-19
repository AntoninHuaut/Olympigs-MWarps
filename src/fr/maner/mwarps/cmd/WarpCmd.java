package fr.maner.mwarps.cmd;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import fr.maner.mwarps.MWarps;
import fr.maner.mwarps.listener.EditListener;

public class WarpCmd implements CommandExecutor, TabExecutor {

	private MWarps pl;
	private EditListener editListener;

	public WarpCmd(MWarps pl, EditListener editListener) {
		this.pl = pl;
		this.editListener = editListener;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!cmd.getName().equalsIgnoreCase("mwarps"))
			return true;

		Player p = null;

		if(sender instanceof Player)
			p = (Player) sender;

		if (args.length < 1) {
			if(sender instanceof Player)
				p.openInventory(pl.getInvManager().getWarpInventory(pl.getPageUser(p.getUniqueId()), false));
			else
				sender.sendMessage("§cVous devez être un joueur");
		}

		else if (args[0].equals("config")) {
			if (args.length < 2)
				sender.sendMessage("§6» §c/warp §aconfig §7<§ewarp§7>");

			else {
				String warp = args[1];

				if(!pl.getEss().getWarps().getList().contains(warp))
					sender.sendMessage("§6» §cLe warp §e" + warp + " §cn'existe pas");

				else if(!canUserModifyWarp(p, warp))
					sender.sendMessage("§6» §cVous n'avez pas la permission d'effectuer des modifications sur le warp des autres joueurs");

				else {
					editListener.setUserWarp(p.getUniqueId(), warp);
					p.openInventory(pl.getInvManager().getEditInventory(warp));
				}
			}
		}

		else if (sender instanceof Player)
			p.performCommand("essentials:warp " + args[0]);
		else 
			pl.getServer().dispatchCommand(sender, "warps");

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> tab = new ArrayList<String>();
		if(args.length > 2)
			return tab;

		if (args[0].equals("config"))
			tab = pl.getWarpsList().stream().filter(warp -> canUserModifyWarp(sender, warp)).collect(Collectors.toList());
		else if(args.length < 2)
			tab = pl.getWarpsList();
		
		if(args.length >= 1)
			tab = tab.stream().filter(warp -> warp.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).collect(Collectors.toList());

		return tab;
	}

	private boolean canUserModifyWarp(CommandSender p, String warp ) {
		return warp.toLowerCase().contains(p.getName().toLowerCase()) || p.hasPermission("mwarps.command.config");
	}
}