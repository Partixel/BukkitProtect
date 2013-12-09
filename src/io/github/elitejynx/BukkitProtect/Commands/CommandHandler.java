package io.github.elitejynx.BukkitProtect.Commands;

import io.github.elitejynx.BukkitProtect.BukkitProtect;
import io.github.elitejynx.BukkitProtect.Util;
import io.github.elitejynx.BukkitProtect.Protections.ProtectionZone;
import io.github.elitejynx.BukkitProtect.Protections.Tag;
import io.github.elitejynx.BukkitProtect.Protections.UserType;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {

	protected BukkitProtect Plugin;

	public CommandHandler(BukkitProtect plugin) {
		Plugin = plugin;
	}

	public boolean GiveRod(CommandSender Sender, Command Cmd, String Label,
			String[] Args) {
		if (!(Sender instanceof Player)) {
			Sender.sendMessage("You must be a player to use this");
			return true;
		}
		if (Args.length == 1) {
			Player Target = (Bukkit.getPlayer(Args[0]));
			if (Target == null) {
				Sender.sendMessage("Could not find that player");
				return true;
			}
			Target.getInventory().addItem(Plugin.RodA);
			Target.sendMessage("Given " + Target.getDisplayName() + " the "
					+ Plugin.RodA.getItemMeta().getDisplayName());
		} else if (Args.length > 1) {
			Sender.sendMessage("Too many arguements, please retry");
			return false;
		} else {
			((Player) Sender).getInventory().addItem(Plugin.RodA);
			Sender.sendMessage("Given " + ((Player) Sender).getDisplayName()
					+ " the " + Plugin.RodA.getItemMeta().getDisplayName());
		}
		return false;
	}

	public boolean SetOwner(CommandSender Sender, Command Cmd, String Label,
			String[] Args) {
		if (!(Sender instanceof Player)) {
			Sender.sendMessage("You must be a player to use this");
			return true;
		}
		if (Args.length == 1) {
			Player Target = (Bukkit.getPlayer(Args[0]));
			if (Target == null) {
				Sender.sendMessage("Could not find that player");
				return true;
			}
			if (BukkitProtect.PVP.PlayerSelectedZone.containsKey(Sender)) {
				if (((ProtectionZone) BukkitProtect.PVP.PlayerSelectedZone
						.get(Sender).keySet().toArray()[0]).getOwner()
						.equalsIgnoreCase(Sender.getName())
						|| Sender
								.hasPermission("BukkitProtect.Protection.EditOthers")) {
					if (Plugin.getConfig().getBoolean("BuyableLand")) {
						if (Plugin.LandOwned.containsKey(Target.getName())) {
							if ((Plugin.LandOwned.get(Target.getName()) - Plugin
									.getTotalLandUsed(Target)) < ((ProtectionZone) BukkitProtect.PVP.PlayerSelectedZone
									.get(Sender).keySet().toArray()[0])
									.getCube().getSize()
									&& !((ProtectionZone) BukkitProtect.PVP.PlayerSelectedZone
											.get(Sender).keySet().toArray()[0])
											.getTag("ServerOwned")
											.equalsIgnoreCase("true")) {
								Sender.sendMessage(Target.getName()
										+ " needs "
										+ (((ProtectionZone) BukkitProtect.PVP.PlayerSelectedZone
												.get(Sender).keySet().toArray()[0])
												.getCube().getSize() - (Plugin.LandOwned
												.get(Target.getName()) - Plugin
												.getTotalLandUsed(Target)))
										+ " more blocks of land to have the protection");
								return true;
							}
						}
					}
					if (Plugin.Protections
							.containsKey(((ProtectionZone) BukkitProtect.PVP.PlayerSelectedZone
									.get(Sender).keySet().toArray()[0])
									.getOwner())) {
						ProtectionZone Zone = ((ProtectionZone) BukkitProtect.PVP.PlayerSelectedZone
								.get(Sender).keySet().toArray()[0]).Clone();
						ArrayList<ProtectionZone> ZonesA = Plugin.Protections
								.get(((ProtectionZone) BukkitProtect.PVP.PlayerSelectedZone
										.get(Sender).keySet().toArray()[0])
										.getOwner());
						ArrayList<ProtectionZone> ZonesB = Plugin.Protections
								.get(Target.getName());
						if (ZonesB == null) {
							ZonesB = new ArrayList<ProtectionZone>();
						}
						ZonesA.remove((BukkitProtect.PVP.PlayerSelectedZone
								.get(Sender).keySet().toArray()[0]));
						Zone.setOwner(Target.getName());
						ZonesB.add(Zone);
						Plugin.Protections.put(Sender.getName(), ZonesA);
						Plugin.Protections.put(Target.getName(), ZonesB);
						Sender.sendMessage("Set the owner of the protection to "
								+ Target.getDisplayName());

						Target.sendMessage("You have recieved the protection from "
								+ ((Player) Sender).getDisplayName());
					}
				}
			} else {
				Sender.sendMessage("You have not selected a protection, to do this right click with a stick inside a protection");
				return true;
			}
		} else if (Args.length > 1) {
			Sender.sendMessage("Too many arguements, please retry");
			return false;
		} else {
			return false;
		}
		return false;
	}

	public boolean Accept(CommandSender Sender, Command Cmd, String Label,
			String[] Args) {
		if (!(Sender instanceof Player)) {
			Sender.sendMessage("You must be a player to use this");
			return true;
		}
		if (Args.length == 0) {
			for (Player Plr : BukkitProtect.PVP.CommandTrades.keySet()) {
				CommandRequest Request = BukkitProtect.PVP.CommandTrades
						.get(Plr);
				if (Request.getTarget() == (Player) Sender) {
					if (!Request.getAccepted()) {
						Request.setAccepted(true);
						onCommand(Request.getSender(), Request.getCommand(),
								Request.getCommand().getLabel(),
								Request.getArgs());
						Request.getSender().sendMessage(
								"The command has been accepted");
						Request.getTarget().sendMessage(
								"You have accepted the command");
						BukkitProtect.PVP.CommandTrades.put(Plr, Request);
						return true;
					}
				}
			}
			Sender.sendMessage("You have no pending requests");
			return true;
		} else if (Args.length > 0) {
			Sender.sendMessage("Too many arguements, please retry");
			return false;
		} else {
			((Player) Sender).getInventory().addItem(Plugin.RodA);
		}
		return false;
	}

	public boolean Transfer(CommandSender Sender, Command Cmd, String Label,
			String[] Args) {
		if (!(Sender instanceof Player)) {
			Sender.sendMessage("You must be a player to use this");
			return true;
		}
		if (Args.length == 1) {
			Player Target = (Bukkit.getPlayer(Args[0]));
			if (Target == null) {
				Sender.sendMessage("Could not find that player");
				return true;
			}
			if (Target == (Player) Sender) {
				Sender.sendMessage("Cannot transfer to yourself");
				return true;
			}
			if (BukkitProtect.PVP.PlayerSelectedZone.containsKey(Sender)) {
				if (BukkitProtect.PVP.CommandTrades.containsKey(Sender)
						&& BukkitProtect.PVP.CommandTrades.get(Sender)
								.getTarget() == Target
						&& BukkitProtect.PVP.CommandTrades.get(Sender)
								.getAccepted()) {
					if (((ProtectionZone) BukkitProtect.PVP.PlayerSelectedZone
							.get(Sender).keySet().toArray()[0]).getOwner()
							.equalsIgnoreCase(Sender.getName())
							|| Sender
									.hasPermission("BukkitProtect.Protection.EditOthers")) {
						int Length = Math
								.abs(((ProtectionZone) BukkitProtect.PVP.PlayerSelectedZone
										.get(Sender).keySet().toArray()[0])
										.getCube().getCorner1().getBlockX()
										- ((ProtectionZone) BukkitProtect.PVP.PlayerSelectedZone
												.get(Sender).keySet().toArray()[0])
												.getCube().getCorner2()
												.getBlockX());
						int Width = Math
								.abs(((ProtectionZone) BukkitProtect.PVP.PlayerSelectedZone
										.get(Sender).keySet().toArray()[0])
										.getCube().getCorner1().getBlockZ()
										- ((ProtectionZone) BukkitProtect.PVP.PlayerSelectedZone
												.get(Sender).keySet().toArray()[0])
												.getCube().getCorner2()
												.getBlockZ());
						if (Plugin.getConfig().getBoolean("BuyableLand")) {
							if (Plugin.LandOwned.containsKey(Target.getName())) {
								if ((Plugin.LandOwned.get(Target.getName()) - Plugin
										.getTotalLandUsed(Target)) < (Length * Width)
										&& !((ProtectionZone) BukkitProtect.PVP.PlayerSelectedZone
												.get(Sender).keySet().toArray()[0])
												.getTag("ServerOwned")
												.equalsIgnoreCase("true")) {
									Sender.sendMessage(Target.getName()
											+ " needs "
											+ ((Length * Width) - (Plugin.LandOwned
													.get(Target.getName()) - Plugin
													.getTotalLandUsed(Target)))
											+ " more blocks of land to have the protection");
									return true;
								}
							}
						}
						if (Plugin.Protections
								.containsKey(((ProtectionZone) BukkitProtect.PVP.PlayerSelectedZone
										.get(Sender).keySet().toArray()[0])
										.getOwner())) {
							ProtectionZone Zone = ((ProtectionZone) BukkitProtect.PVP.PlayerSelectedZone
									.get(Sender).keySet().toArray()[0]).Clone();
							ArrayList<ProtectionZone> ZonesA = Plugin.Protections
									.get(((ProtectionZone) BukkitProtect.PVP.PlayerSelectedZone
											.get(Sender).keySet().toArray()[0])
											.getOwner());
							ArrayList<ProtectionZone> ZonesB = Plugin.Protections
									.get(Target.getName());
							if (ZonesB == null) {
								ZonesB = new ArrayList<ProtectionZone>();
							}
							ZonesA.remove((BukkitProtect.PVP.PlayerSelectedZone
									.get(Sender).keySet().toArray()[0]));
							Zone.setOwner(Target.getName());
							ZonesB.add(Zone);
							Plugin.Protections.put(Sender.getName(), ZonesA);
							Plugin.Protections.put(Target.getName(), ZonesB);
							Sender.sendMessage("Transfered the protection to "
									+ Target.getDisplayName());
							Target.sendMessage("You have recieved the protection from "
									+ ((Player) Sender).getDisplayName());
						}
					}
				} else {
					CommandRequest Trades = new CommandRequest((Player) Sender,
							Target, false, Cmd, Args);
					BukkitProtect.PVP.CommandTrades
							.put((Player) Sender, Trades);
					BukkitProtect.PVP.CommandTimers.put((Player) Sender, 10);
					Sender.sendMessage("Waiting for player to accept");
					Target.sendMessage(Sender.getName()
							+ " wishes to give you a protection, say /accept to accept or wait 10 seconds for it to time out");
				}
			} else {
				Sender.sendMessage("You have not selected a protection, to do this right click with a stick inside a protection");
				return true;
			}
		} else if (Args.length > 1) {
			Sender.sendMessage("Too many arguements, please retry");
			return false;
		} else {
			return false;
		}
		return false;
	}

	public boolean AddUsers(CommandSender Sender, Command Cmd, String label,
			String[] Args) {
		if (!(Sender instanceof Player)) {
			Sender.sendMessage("You must be a player to use this");
			return true;
		}
		if (Args.length == 2) {
			String Target;
			if (Args[0].equalsIgnoreCase("*")) {
				Target = "*";
			} else if (Bukkit.getPlayer(Args[0]) != null) {
				Target = Bukkit.getPlayer(Args[0]).getName();
			} else {
				Sender.sendMessage("Could not find that player");
				return true;
			}
			UserType UT = Util.parseUserType(Args[1]);

			if (UT == null && !Args[1].equalsIgnoreCase("*")) {
				for (UserType UType : Plugin.Types) {
					Sender.sendMessage(UType.getName() + " : "
							+ UType.getDesc());
				}
				Sender.sendMessage("That is not a valid type");
				return true;
			}

			if (BukkitProtect.PVP.PlayerSelectedZone.containsKey(Sender)) {
				if (((ProtectionZone) BukkitProtect.PVP.PlayerSelectedZone
						.get(Sender).keySet().toArray()[0])
						.userHasAdminType(((Player) Sender).getName())
						|| Sender
								.hasPermission("BukkitProtect.Protection.EditOthers")) {
					if (((ProtectionZone) BukkitProtect.PVP.PlayerSelectedZone
							.get(Sender).keySet().toArray()[0]).addUsers(
							Target, UT)) {
						Sender.sendMessage("Added " + Target
								+ " to the protection as " + Args[1]);
					} else {
						Sender.sendMessage("Could not add " + Target
								+ " to the protection as " + Args[1]);
					}
				}
			} else {
				Sender.sendMessage("You have not selected a protection, to do this right click with a stick inside a protection");
				return true;
			}
		} else if (Args.length > 2) {
			Sender.sendMessage("Too many arguements, please retry");
			return false;
		} else {
			return false;
		}
		return false;
	}

	public boolean AddTag(CommandSender Sender, Command Cmd, String label,
			String[] Args) {
		if (!(Sender instanceof Player)) {
			Sender.sendMessage("You must be a player to use this");
			return true;
		}
		if (!Sender.hasPermission("BukkitProtect.Commands.Tag")) {
			Sender.sendMessage("You do not have permission to use this");
			return true;
		}
		if (Args.length == 2) {
			if (!Util.isTagAndValue(Args[0], Args[1])) {
				for (Tag tag : Plugin.Tags) {
					Sender.sendMessage(tag.getName() + " : " + tag.getDesc()
							+ " : " + tag.getValues().toString());
				}
				Sender.sendMessage("That is not a valid tag");
				return true;
			}
			if (BukkitProtect.PVP.PlayerSelectedZone.containsKey(Sender)) {
				if (((ProtectionZone) BukkitProtect.PVP.PlayerSelectedZone
						.get(Sender).keySet().toArray()[0])
						.userHasAdminType(((Player) Sender).getName())
						|| Sender
								.hasPermission("BukkitProtect.Protection.EditOthers")) {
					if (((ProtectionZone) BukkitProtect.PVP.PlayerSelectedZone
							.get(Sender).keySet().toArray()[0]).setTags(
							Args[0].toLowerCase(), Args[1].toLowerCase())) {
						Sender.sendMessage("Added the tag " + Args[0]
								+ " to the protection");

					} else {
						Sender.sendMessage("Could not add the tag " + Args[0]
								+ " to the protection");
					}
				}
			} else {
				Sender.sendMessage("You have not selected a protection, to do this right click with a stick inside a protection");
				return true;
			}
		} else if (Args.length > 2) {
			Sender.sendMessage("Too many arguements, please retry");
			return false;
		} else {
			return false;
		}
		return false;
	}

	public boolean GetTags(CommandSender Sender, Command Cmd, String label,
			String[] Args) {
		if (!(Sender instanceof Player)) {
			Sender.sendMessage("You must be a player to use this");
			return true;
		}
		if (!Sender.hasPermission("BukkitProtect.Commands.Tag")) {
			Sender.sendMessage("You do not have permission to use this");
			return true;
		}
		if (Args.length == 0) {
			if (BukkitProtect.PVP.PlayerSelectedZone.containsKey(Sender)) {
				if (((ProtectionZone) BukkitProtect.PVP.PlayerSelectedZone
						.get(Sender).keySet().toArray()[0])
						.userHasAdminType(((Player) Sender).getName())
						|| Sender
								.hasPermission("BukkitProtect.Protection.EditOthers")) {
					if (((ProtectionZone) BukkitProtect.PVP.PlayerSelectedZone
							.get(Sender).keySet().toArray()[0]).getTags()
							.isEmpty()) {
						Sender.sendMessage("This protection has no tags");
					} else {
						for (String Name : ((ProtectionZone) BukkitProtect.PVP.PlayerSelectedZone
								.get(Sender).keySet().toArray()[0]).getTags()
								.keySet()) {
							String Value = ((ProtectionZone) BukkitProtect.PVP.PlayerSelectedZone
									.get(Sender).keySet().toArray()[0])
									.getTags().get(Name);
							Sender.sendMessage(Name + " : " + Value);
						}
					}
				}
			} else {
				Sender.sendMessage("You have not selected a protection, to do this right click with a stick inside a protection");
				return true;
			}
		} else if (Args.length > 0) {
			Sender.sendMessage("Too many arguements, please retry");
			return false;
		} else {
			return false;
		}
		return false;
	}

	public boolean GetUsers(CommandSender Sender, Command Cmd, String label,
			String[] Args) {
		if (!(Sender instanceof Player)) {
			Sender.sendMessage("You must be a player to use this");
			return true;
		}
		if (!Sender.hasPermission("BukkitProtect.Commands.Users")) {
			Sender.sendMessage("You do not have permission to use this");
			return true;
		}
		if (Args.length == 0) {
			if (BukkitProtect.PVP.PlayerSelectedZone.containsKey(Sender)) {
				if (((ProtectionZone) BukkitProtect.PVP.PlayerSelectedZone
						.get(Sender).keySet().toArray()[0])
						.userHasAdminType(((Player) Sender).getName())
						|| Sender
								.hasPermission("BukkitProtect.Protection.EditOthers")) {
					Sender.sendMessage(((ProtectionZone) BukkitProtect.PVP.PlayerSelectedZone
							.get(Sender).keySet().toArray()[0]).getOwner()
							+ " : Owner");
					for (String User : ((ProtectionZone) BukkitProtect.PVP.PlayerSelectedZone
							.get(Sender).keySet().toArray()[0]).getUsers()
							.keySet()) {
						String UserHas = ((ProtectionZone) BukkitProtect.PVP.PlayerSelectedZone
								.get(Sender).keySet().toArray()[0]).getUsers()
								.get(User).toString().split("\\[")[1]
								.split("\\]")[0];
						Sender.sendMessage(User + " : " + UserHas);
					}
				}
			} else {
				Sender.sendMessage("You have not selected a protection, to do this right click with a stick inside a protection");
				return true;
			}
		} else if (Args.length > 0) {
			Sender.sendMessage("Too many arguements, please retry");
			return false;
		} else {
			return false;
		}
		return false;
	}

	public boolean RemoveUsers(CommandSender Sender, Command Cmd, String label,
			String[] Args) {
		if (!(Sender instanceof Player)) {
			Sender.sendMessage("You must be a player to use this");
			return true;
		}
		if (!Sender.hasPermission("BukkitProtect.Commands.Users")) {
			Sender.sendMessage("You do not have permission to use this");
			return true;
		}
		if (Args.length == 2) {
			String Target;
			if (Args[0].equalsIgnoreCase("*")) {
				Target = "*";
			} else if (Bukkit.getPlayer(Args[0]) != null) {
				Target = Bukkit.getPlayer(Args[0]).getName();
			} else {
				Sender.sendMessage("Could not find that player");
				return true;
			}
			UserType UT = Util.parseUserType(Args[1]);
			if (UT == null && !Args[1].equalsIgnoreCase("*")) {
				for (UserType UType : Plugin.Types) {
					Sender.sendMessage(UType.getName() + " : "
							+ UType.getDesc());
				}
				Sender.sendMessage("That is not a valid type");
				return true;
			}
			if (BukkitProtect.PVP.PlayerSelectedZone.containsKey(Sender)) {
				if (((ProtectionZone) BukkitProtect.PVP.PlayerSelectedZone
						.get(Sender).keySet().toArray()[0])
						.userHasAdminType(((Player) Sender).getName())
						|| Sender
								.hasPermission("BukkitProtect.Protection.EditOthers")) {
					if (((ProtectionZone) BukkitProtect.PVP.PlayerSelectedZone
							.get(Sender).keySet().toArray()[0]).removeUsers(
							Target, UT)) {
						Sender.sendMessage("Removed " + Target
								+ " from the protection as " + Args[1]);
					} else {
						Sender.sendMessage("Could not remove " + Target
								+ " from the protection as " + Args[1]);
					}
				}
			} else {
				Sender.sendMessage("You have not selected a protection, to do this right click with a stick inside a protection");
				return true;
			}
		} else if (Args.length > 2) {
			Sender.sendMessage("Too many arguements, please retry");
			return false;
		} else {
			return false;
		}
		return false;
	}

	public boolean RemoveTag(CommandSender Sender, Command Cmd, String label,
			String[] Args) {
		if (!(Sender instanceof Player)) {
			Sender.sendMessage("You must be a player to use this");
			return true;
		}
		if (!Sender.hasPermission("BukkitProtect.Commands.Tag")) {
			Sender.sendMessage("You do not have permission to use this");
			return true;
		}
		if (Args.length == 1) {
			if (!Util.isTag(Args[0])) {
				for (UserType UType : Plugin.Types) {
					Sender.sendMessage(UType.getName() + " : "
							+ UType.getDesc());
				}
				Sender.sendMessage("That is not a valid tag");
				return true;
			}
			if (BukkitProtect.PVP.PlayerSelectedZone.containsKey(Sender)) {
				if (((ProtectionZone) BukkitProtect.PVP.PlayerSelectedZone
						.get(Sender).keySet().toArray()[0])
						.userHasAdminType(((Player) Sender).getName())
						|| Sender
								.hasPermission("BukkitProtect.Protection.EditOthers")) {
					if (((ProtectionZone) BukkitProtect.PVP.PlayerSelectedZone
							.get(Sender).keySet().toArray()[0])
							.removeTags(Args[0].toLowerCase())) {
						Sender.sendMessage("Removed " + Args[0]
								+ " from the protection");
					} else {
						Sender.sendMessage("Could not remove " + Args[0]
								+ " from the protection");
					}
				}
			} else {
				Sender.sendMessage("You have not selected a protection, to do this right click with a stick inside a protection");
				return true;
			}
		} else if (Args.length > 1) {
			Sender.sendMessage("Too many arguements, please retry");
			return false;
		} else {
			return false;
		}
		return false;
	}

	public boolean RemoveProtection(CommandSender Sender, Command Cmd,
			String label, String[] Args) {
		if (!(Sender instanceof Player)) {
			Sender.sendMessage("You must be a player to use this");
			return true;
		}
		if (!Sender.hasPermission("BukkitProtect.Commands.RemoveProtections")) {
			Sender.sendMessage("You do not have permission to use this");
			return true;
		}
		if (Args.length == 0) {
			if (BukkitProtect.PVP.PlayerSelectedZone.containsKey(Sender)) {
				if (((ProtectionZone) BukkitProtect.PVP.PlayerSelectedZone
						.get(Sender).keySet().toArray()[0]).getOwner()
						.equalsIgnoreCase(Sender.getName())
						|| Sender
								.hasPermission("BukkitProtect.Protection.RemoveOthers")) {
					if (BukkitProtect.PVP.CommandTimers.containsKey(Sender)) {
						ArrayList<ProtectionZone> Zones = Plugin.Protections
								.get(((ProtectionZone) BukkitProtect.PVP.PlayerSelectedZone
										.get(Sender).keySet().toArray()[0])
										.getOwner());
						Zones.remove((BukkitProtect.PVP.PlayerSelectedZone
								.get(Sender).keySet().toArray()[0]));
						Plugin.Protections
								.put(((ProtectionZone) BukkitProtect.PVP.PlayerSelectedZone
										.get(Sender).keySet().toArray()[0])
										.getOwner(), Zones);
						BukkitProtect.PVP.CommandTimers
								.put((Player) Sender, -1);
						BukkitProtect.PVP.updateFakeBlocks((Player) Sender);
						Sender.sendMessage("Removed the protection");
						return true;
					} else {
						BukkitProtect.PVP.CommandTimers
								.put((Player) Sender, 10);
						Sender.sendMessage("Say the command again within 10 seconds to accept");
						return true;
					}
				}
			} else {
				Sender.sendMessage("You have not selected a protection, to do this right click with a stick inside a protection");
				return true;
			}
		} else if (Args.length > 0) {
			Sender.sendMessage("Too many arguements, please retry");
			return false;
		} else {
			return false;
		}
		return false;
	}

	public boolean RemoveAllProtections(CommandSender Sender, Command Cmd,
			String label, String[] Args) {
		if (!(Sender instanceof Player)) {
			Sender.sendMessage("You must be a player to use this");
			return true;
		}
		if (!Sender.hasPermission("BukkitProtect.Commands.RemoveProtections")) {
			Sender.sendMessage("You do not have permission to use this");
			return true;
		}
		if (Args.length == 0) {
			if (BukkitProtect.PVP.CommandTimers.containsKey(Sender)) {
				Plugin.Protections.remove(Sender.getName());
				BukkitProtect.PVP.updateFakeBlocks((Player) Sender);
				BukkitProtect.PVP.CommandTimers.put((Player) Sender, -1);
				Sender.sendMessage("Removed all your protections");
				return true;
			} else {
				BukkitProtect.PVP.CommandTimers.put((Player) Sender, 10);
				Sender.sendMessage("Say the command again within 10 seconds to accept");
				return true;
			}
		} else if (Args.length == 1) {
			Player Target = Bukkit.getPlayer(Args[0]);
			if (Target == null) {
				Sender.sendMessage("Could not find that player");
				return true;
			}
			if (BukkitProtect.PVP.CommandTimers.containsKey(Sender)) {
				Plugin.Protections.remove(Target.getName());
				BukkitProtect.PVP.updateFakeBlocks((Player) Sender);
				BukkitProtect.PVP.CommandTimers.put((Player) Sender, -1);
				Sender.sendMessage("Removed all of " + Target.getDisplayName()
						+ "'s protections");
				return true;
			} else {
				BukkitProtect.PVP.CommandTimers.put((Player) Sender, 10);
				Sender.sendMessage("Say the command again within 10 seconds to accept");
				return true;
			}
		} else if (Args.length > 1) {
			Sender.sendMessage("Too many arguements, please retry");
			return false;
		} else {
			return false;
		}
	}

	public boolean GiveLand(CommandSender Sender, Command Cmd, String label,
			String[] Args) {
		if (!(Sender instanceof Player)) {
			Sender.sendMessage("You must be a player to use this");
			return true;
		}
		if (!Sender.hasPermission("BukkitProtect.Commands.AdminLand")) {
			Sender.sendMessage("You do not have permission to use this");
			return true;
		}
		if (Args.length == 2) {
			Player Target = (Bukkit.getPlayer(Args[0]));
			if (Target == null) {
				Sender.sendMessage("Could not find that player");
				return true;
			}
			int Num = 0;
			try {
				Num = Integer.parseInt(Args[1]);
			} catch (Exception e) {
				Sender.sendMessage("The second arguement must be an integer");
				return true;
			}
			if (Num != 0) {
				if (Plugin.LandOwned.containsKey(Target.getName())) {
					if (Plugin.LandOwned.get(Target.getName()).intValue() + Num < 0) {
						Plugin.LandOwned.put(Target.getName(), 0);
						Sender.sendMessage("Set "
								+ Target.getDisplayName()
								+ "'s land to 0 because the integer you specified was more then the land they owned");
					} else {
						Plugin.LandOwned.put(Target.getName(), Plugin.LandOwned
								.get(Target.getName()).intValue() + Num);
						Sender.sendMessage("Gave " + Target.getDisplayName()
								+ " " + Num + " land");
					}
				}
			}
		} else if (Args.length > 2) {
			Sender.sendMessage("Too many arguements, please retry");
			return false;
		} else {
			return false;
		}
		return false;
	}

	public boolean Stuck(CommandSender Sender, Command Cmd, String label,
			String[] Args) {
		if (!(Sender instanceof Player)) {
			Sender.sendMessage("You must be a player to use this");
			return true;
		}
		if (!Sender.hasPermission("BukkitProtect.Commands.Stuck")) {
			Sender.sendMessage("You do not have permission to use this");
			return true;
		}
		if (Args.length == 0) {
			ProtectionZone Zone = Plugin.isInsideProtection(((Player) Sender)
					.getLocation());
			if (Zone != null) {
				if (Zone.userHasType(Sender.getName(), Plugin.UTBuildBlocks)) {
					Sender.sendMessage("You can build or break blocks to leave this protection");
				} else {
					((Player) Sender).teleport(((Player) Sender).getWorld()
							.getHighestBlockAt(Zone.getCube().getCorner1())
							.getLocation());
					Sender.sendMessage("Teleported you out of the protection");
				}
			}
		} else if (Args.length > 0) {
			Sender.sendMessage("Too many arguements, please retry");
			return false;
		} else {
			return false;
		}
		return false;
	}

	public boolean SetLand(CommandSender Sender, Command Cmd, String label,
			String[] Args) {
		if (!(Sender instanceof Player)) {
			Sender.sendMessage("You must be a player to use this");
			return true;
		}
		if (!Sender.hasPermission("BukkitProtect.Commands.AdminLand")) {
			Sender.sendMessage("You do not have permission to use this");
			return true;
		}
		if (Args.length == 2) {
			Player Target = (Bukkit.getPlayer(Args[0]));
			if (Target == null) {
				Sender.sendMessage("Could not find that player");
				return true;
			}
			int Num = 0;
			try {
				Num = Integer.parseInt(Args[1]);
			} catch (Exception e) {
				Sender.sendMessage("The second arguement must be an integer");
				return true;
			}
			if (Num > 0) {
				if (Plugin.LandOwned.containsKey(Target.getName())) {
					Plugin.LandOwned.put(Target.getName(), Num);
					Sender.sendMessage("Set " + Target.getDisplayName()
							+ "'s land to " + Num);
				}
			} else {
				Sender.sendMessage("The sencond arguement must be an integer more then 0");
				return true;
			}
		} else if (Args.length > 2) {
			Sender.sendMessage("Too many arguements, please retry");
			return false;
		} else {
			return false;
		}
		return false;
	}

	public boolean GetLand(CommandSender Sender, Command Cmd, String label,
			String[] Args) {
		if (!(Sender instanceof Player)) {
			Sender.sendMessage("You must be a player to use this");
			return true;
		}
		if (Args.length == 1
				&& Sender.hasPermission("BukkitProtect.Commands.AdminLand")) {
			Player Target = (Bukkit.getPlayer(Args[0]));
			if (Target == null) {
				Sender.sendMessage("Could not find that player");
				return true;
			}
			if (Plugin.LandOwned.containsKey(Target.getName())) {
				if (Plugin.Protections.containsKey(Target.getName())) {
					Sender.sendMessage(Target.getName() + " has "
							+ Plugin.LandOwned.get(Target.getName())
							+ " land, of which "
							+ Plugin.getTotalLandUsed(Target)
							+ " is used by a total of "
							+ Plugin.Protections.get(Target.getName()).size()
							+ " protections.");
				} else {
					Sender.sendMessage(Target.getName()
							+ " has "
							+ Plugin.LandOwned.get(Target.getName())
							+ " land, of which 0 is used by a total of 0 protections.");
				}
			}
		} else if (Args.length == 0
				&& Sender.hasPermission("BukkitProtect.Commands.GetLand")) {
			if (Plugin.LandOwned.containsKey(Sender.getName())) {
				if (Plugin.Protections.containsKey(Sender.getName())) {
					Sender.sendMessage("You have "
							+ Plugin.LandOwned.get(Sender.getName())
							+ " land, of which "
							+ Plugin.getTotalLandUsed((Player) Sender)
							+ " is used by a total of "
							+ Plugin.Protections.get(Sender.getName()).size()
							+ " protections.");
				} else {
					Sender.sendMessage("You have "
							+ Plugin.LandOwned.get(Sender.getName())
							+ " land, of which 0 is used by a total of 0 protections.");
				}
			}
		} else if (Args.length == 0 || Args.length == 1) {
			Sender.sendMessage("You do not have permission to use this");
			return true;
		} else if (Args.length > 1) {
			Sender.sendMessage("Too many arguements, please retry");
			return false;
		} else {
			return false;
		}
		return false;
	}

	@Override
	public boolean onCommand(CommandSender Sender, Command Cmd, String Label,
			String[] Args) {
		if (Cmd.getName().equalsIgnoreCase("giverod")) {
			return GiveRod(Sender, Cmd, Label, Args);
		} else if (Cmd.getName().equalsIgnoreCase("setowner")) {
			return SetOwner(Sender, Cmd, Label, Args);
		} else if (Cmd.getName().equalsIgnoreCase("accept")) {
			return Accept(Sender, Cmd, Label, Args);
		} else if (Cmd.getName().equalsIgnoreCase("transfer")) {
			return Transfer(Sender, Cmd, Label, Args);
		} else if (Cmd.getName().equalsIgnoreCase("addUsers")) {
			return AddUsers(Sender, Cmd, Label, Args);
		} else if (Cmd.getName().equalsIgnoreCase("addtag")) {
			return AddTag(Sender, Cmd, Label, Args);
		} else if (Cmd.getName().equalsIgnoreCase("gettags")) {
			return GetTags(Sender, Cmd, Label, Args);
		} else if (Cmd.getName().equalsIgnoreCase("getusers")) {
			return GetUsers(Sender, Cmd, Label, Args);
		} else if (Cmd.getName().equalsIgnoreCase("removeUsers")) {
			return RemoveUsers(Sender, Cmd, Label, Args);
		} else if (Cmd.getName().equalsIgnoreCase("removetag")) {
			return RemoveTag(Sender, Cmd, Label, Args);
		} else if (Cmd.getName().equalsIgnoreCase("removeprotection")) {
			return RemoveProtection(Sender, Cmd, Label, Args);
		} else if (Cmd.getName().equalsIgnoreCase("removeallprotections")) {
			return RemoveAllProtections(Sender, Cmd, Label, Args);
		} else if (Cmd.getName().equalsIgnoreCase("giveland")) {
			return GiveLand(Sender, Cmd, Label, Args);
		} else if (Cmd.getName().equalsIgnoreCase("stuck")) {
			return Stuck(Sender, Cmd, Label, Args);
		} else if (Cmd.getName().equalsIgnoreCase("setland")) {
			return SetLand(Sender, Cmd, Label, Args);
		} else if (Cmd.getName().equalsIgnoreCase("getland")) {
			return GetLand(Sender, Cmd, Label, Args);
		}
		return true;
	}
}
