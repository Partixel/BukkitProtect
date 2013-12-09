package io.github.elitejynx.BukkitProtect.Commands;

import io.github.elitejynx.BukkitProtect.BukkitProtect;
import io.github.elitejynx.BukkitProtect.Util;
import io.github.elitejynx.BukkitProtect.Protections.Tag;
import io.github.elitejynx.BukkitProtect.Protections.UserType;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class TabHandler implements TabCompleter {

	protected BukkitProtect Plugin;

	public TabHandler(BukkitProtect plugin) {
		Plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender Sender, Command Cmd,
			String Label, String[] Args) {
		if (Cmd.getName().equalsIgnoreCase("giverod")
				|| Cmd.getName().equalsIgnoreCase("setowner")
				|| Cmd.getName().equalsIgnoreCase("transfer")
				|| Cmd.getName().equalsIgnoreCase("removeallprotections")
				|| Cmd.getName().equalsIgnoreCase("getland")
				|| Cmd.getName().equalsIgnoreCase("setland")
				|| Cmd.getName().equalsIgnoreCase("giveland")) {
			if (Args.length == 1) {
				List<String> list = new ArrayList<String>();
				for (Player plr : Plugin.getServer().getOnlinePlayers()) {
					if (plr.getName().toLowerCase()
							.startsWith(Args[Args.length - 1].toLowerCase()))
						list.add(plr.getName());
				}
				return list;
			}
		} else if (Cmd.getName().equalsIgnoreCase("accept")) {
			return null;
		} else if (Cmd.getName().equalsIgnoreCase("addUsers")) {
			if (Args.length == 1) {
				List<String> list = new ArrayList<String>();
				for (Player plr : Plugin.getServer().getOnlinePlayers()) {
					if (plr.getName().toLowerCase()
							.startsWith(Args[Args.length - 1].toLowerCase()))
						list.add(plr.getName());
				}
				return list;
			} else if (Args.length == 2) {
				List<String> list = new ArrayList<String>();
				for (UserType UT : Plugin.Types) {
					if (UT.getName().toLowerCase()
							.startsWith(Args[Args.length - 1].toLowerCase()))
						list.add(UT.getName());
				}
				return list;
			}
		} else if (Cmd.getName().equalsIgnoreCase("addtag")) {
			if (Args.length == 1) {
				List<String> list = new ArrayList<String>();
				for (Tag tag : Plugin.Tags) {
					if (tag.getName().toLowerCase()
							.startsWith(Args[Args.length - 1].toLowerCase()))
						list.add(tag.getName());
				}
				return list;
			} else if (Args.length == 2) {
				List<String> list = new ArrayList<String>();
				if (Util.parseTag(Args[0]) != null) {
					list = Util.parseTag(Args[0]).getValues();
				}
				return list;
			}
		} else if (Cmd.getName().equalsIgnoreCase("gettags")) {
			return null;
		} else if (Cmd.getName().equalsIgnoreCase("getusers")) {
			return null;
		} else if (Cmd.getName().equalsIgnoreCase("removeUsers")) {
			if (Args.length == 1) {
				List<String> list = new ArrayList<String>();
				for (Player plr : Plugin.getServer().getOnlinePlayers()) {
					if (plr.getName().toLowerCase()
							.startsWith(Args[Args.length - 1].toLowerCase()))
						list.add(plr.getName());
				}
				return list;
			} else if (Args.length == 2) {
				List<String> list = new ArrayList<String>();
				for (UserType UT : Plugin.Types) {
					list.add(UT.getName());
				}
				return list;
			}
		} else if (Cmd.getName().equalsIgnoreCase("removetag")) {
			if (Args.length == 1) {
				List<String> list = new ArrayList<String>();
				for (Tag tag : Plugin.Tags) {
					if (tag.getName().toLowerCase()
							.startsWith(Args[Args.length - 1].toLowerCase()))
						list.add(tag.getName());
				}
				return list;
			}
		} else if (Cmd.getName().equalsIgnoreCase("removeprotection")) {
			return null;
		} else if (Cmd.getName().equalsIgnoreCase("stuck")) {
			return null;
		}
		return null;
	}
}
