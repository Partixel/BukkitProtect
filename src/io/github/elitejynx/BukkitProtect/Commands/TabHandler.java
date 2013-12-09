package io.github.elitejynx.BukkitProtect.Commands;

import io.github.elitejynx.BukkitProtect.BukkitProtect;
import io.github.elitejynx.BukkitProtect.Protections.Tag;

import java.util.ArrayList;
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
	public List<String> onTabComplete(CommandSender Sender, Command Cmd,
			String Label, String[] Args) {
		List<String> list = new ArrayList<String>();
		for (Tag tag : Plugin.Tags) {
			if (tag.getName().toLowerCase()
					.startsWith(Args[Args.length - 1].toLowerCase()))
				list.add(tag.getName());
		}
		return list;
	}
}
