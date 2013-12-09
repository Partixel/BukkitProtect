package io.github.elitejynx.BukkitProtect.Commands;

import io.github.elitejynx.BukkitProtect.BukkitProtect;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class TabHandler implements TabCompleter {

	protected BukkitProtect Plugin;

	public TabHandler(BukkitProtect plugin) {
		Plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1,
			String arg2, String[] arg3) {
		return null;
	}

}
