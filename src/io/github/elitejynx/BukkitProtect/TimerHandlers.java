package io.github.elitejynx.BukkitProtect;

import io.github.elitejynx.BukkitProtect.Commands.CommandRequest;
import io.github.elitejynx.BukkitProtect.Protections.ProtectionZone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class TimerHandlers implements Listener {
	public Map<Player, Integer> CommandTimers = new HashMap<Player, Integer>();
	public Map<Player, CommandRequest> CommandTrades = new HashMap<Player, CommandRequest>();
	public Map<Player, PVPLog> PVPLogs = new HashMap<Player, PVPLog>();
	public Map<Player, Map<Block, Integer>> UpdateBlock = new HashMap<Player, Map<Block, Integer>>();
	public Map<Player, Map<Location, Integer>> PlayerSelection = new HashMap<Player, Map<Location, Integer>>();
	public Map<Player, Map<ProtectionZone, Integer>> PlayerSelectedZone = new HashMap<Player, Map<ProtectionZone, Integer>>();
	public Map<Player, Integer> PlayerGain = new HashMap<Player, Integer>();
	public Map<Player, Integer> playerLastChat = new HashMap<Player, Integer>();
	public Map<Player, String> playerLastChatMessage = new HashMap<Player, String>();
	public Map<Player, Integer> playerSpam = new HashMap<Player, Integer>();
	public Map<String, Long> loginSpam = new HashMap<String, Long>();

	public void updateFakeBlocks(Player Plr) {
		if (UpdateBlock.containsKey(Plr)) {
			Map<Block, Integer> Blocks = UpdateBlock.get(Plr);
			for (Block block : Blocks.keySet()) {
				Blocks.put(block, 0);
			}
			UpdateBlock.put(Plr, Blocks);
		}
	}

	public TimerHandlers(final BukkitProtect main) {
		Bukkit.getServer().getScheduler()
				.scheduleSyncRepeatingTask(main, new Runnable() {
					@Override
					public void run() {
						UpdatePVP(main);
					}
				}, 20, 20);
	}

	public void UpdatePVP(final BukkitProtect main) {
		for (Player Plr : Bukkit.getServer().getOnlinePlayers()) {
			if (PlayerGain.containsKey(Plr)) {
				PlayerGain.put(Plr, PlayerGain.get(Plr).intValue() - 1);
				if (PlayerGain.get(Plr).intValue() <= 0) {
					PlayerGain.put(Plr, main.getConfig().getInt("LandDelay"));
					if (main.LandOwned.containsKey(Plr.getName())) {
						main.LandOwned.put(Plr.getName(),
								main.LandOwned.get(Plr.getName())
										+ main.getConfig().getInt("LandGain"));
					} else {
						main.LandOwned.put(Plr.getName(), main.getConfig()
								.getInt("LandGain"));
					}
					if (main.getConfig().getBoolean("SendMessage"))
						Plr.sendMessage("You have gained "
								+ main.getConfig().getInt("LandGain")
								+ " blocks of land");
				}
			} else {
				PlayerGain.put(Plr, main.getConfig().getInt("LandDelay"));
			}
			if (PVPLogs.containsKey(Plr)) {
				PVPLogs.get(Plr).changeTimer(-1);
				if (PVPLogs.get(Plr).getTimer() <= 0) {
					PVPLogs.remove(Plr);
					Plr.sendMessage("You are no longer in PVP");
				}
			}
			if (CommandTimers.containsKey(Plr)) {
				CommandTimers.put(Plr, CommandTimers.get(Plr).intValue() - 1);
				if (CommandTimers.get(Plr).intValue() <= 0) {
					if (CommandTimers.get(Plr).intValue() == 0)
						Plr.sendMessage("Your command has timed out");
					if (CommandTrades.containsKey(Plr)) {
						CommandTrades.get(Plr).getSender()
								.sendMessage("The request has timed out");
						CommandTrades.remove(Plr);
					}
					CommandTimers.remove(Plr);
				}
			}
			if (UpdateBlock.containsKey(Plr)) {
				ArrayList<Block> removeBlocks = new ArrayList<Block>();
				for (Block block : UpdateBlock.get(Plr).keySet()) {
					UpdateBlock.get(Plr).put(block,
							UpdateBlock.get(Plr).get(block).intValue() - 1);
					if (UpdateBlock.get(Plr).get(block).intValue() <= 0) {
						block.getState().update();
						removeBlocks.add(block);
					}
				}
				if (!removeBlocks.isEmpty()) {
					for (Block block : removeBlocks) {
						if (UpdateBlock.get(Plr).containsKey(block))
							UpdateBlock.get(Plr).remove(block);
					}
				}
				if (UpdateBlock.get(Plr).isEmpty()) {
					UpdateBlock.remove(Plr);
				}
			}
			if (PlayerSelection.containsKey(Plr)) {
				if (!PlayerSelection.get(Plr).keySet().isEmpty()) {
					ArrayList<Location> removeLoc = new ArrayList<Location>();
					for (Location Loc : PlayerSelection.get(Plr).keySet()) {
						if (PlayerSelection.get(Plr).containsKey(Loc)) {
							PlayerSelection.get(Plr).put(
									Loc,
									PlayerSelection.get(Plr).get(Loc)
											.intValue() - 1);
							if (PlayerSelection.get(Plr).get(Loc).intValue() <= 0) {
								removeLoc.add(Loc);
							}
						}
					}
					if (!removeLoc.isEmpty()) {
						for (Location Loc : removeLoc) {
							if (PlayerSelection.get(Plr).containsKey(Loc))
								PlayerSelection.get(Plr).remove(Loc);
						}
					}
					if (PlayerSelection.get(Plr).isEmpty()) {
						PlayerSelection.remove(Plr);
					}
				} else {
					PlayerSelection.remove(Plr);
				}
			}
			if (PlayerSelectedZone.containsKey(Plr)) {
				boolean Remove = false;
				for (ProtectionZone Prot : PlayerSelectedZone.get(Plr).keySet()) {
					PlayerSelectedZone.get(Plr)
							.put(Prot,
									PlayerSelectedZone.get(Plr).get(Prot)
											.intValue() - 1);
					if (PlayerSelectedZone.get(Plr).get(Prot).intValue() <= 0) {
						Remove = true;
					}
				}
				if (Remove) {
					PlayerSelectedZone.remove(Plr);
				}
			}
		}
	}

	public boolean isPlayerInPVP(Player plr) {
		return PVPLogs.containsKey(plr);
	}

	public boolean isPlayerInPVPWith(Player plr, Player att) {
		if (PVPLogs.containsKey(plr)) {
			return PVPLogs.get(plr).getPlrs().contains(att);
		}
		return false;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void PlayerLogin(PlayerLoginEvent event) {
		if (loginSpam.containsKey(event.getAddress().getHostAddress())) {
			if ((System.currentTimeMillis() - loginSpam.get(event.getAddress()
					.getHostAddress())) <= BukkitProtect.Plugin.getConfig()
					.getDouble("LimitLogins")) {
				event.disallow(Result.KICK_OTHER,
						"You have been kicked for joining more then once in a short period of time");
				loginSpam.put(event.getAddress().getHostAddress(),
						System.currentTimeMillis());
			} else {
				loginSpam.remove(event.getAddress().getHostAddress());
			}
		} else {
			loginSpam.put(event.getAddress().getHostAddress(),
					System.currentTimeMillis());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void PlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		String message = event.getMessage();
		int totalCensored = 0;
		double caps = 0;
		double total = message.length();
		for (int i = 0; i < total; i++) {
			char chara = message.charAt(i);
			if (chara >= 'A' && chara <= 'Z') {
				caps += 1;
			}
		}
		int percent = (int) Math.round((caps / total) * 100);
		String[] words = message.split(" ");
		if (percent > BukkitProtect.Plugin.getConfig().getDouble(
				"CapsPercentage"))
			message = message.toLowerCase();
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			if (word.length() > BukkitProtect.Plugin.getConfig().getDouble(
					"MaxWordLength")) {
				totalCensored = totalCensored + word.length();
				message = message.replaceAll(word, "****");
			}
		}
		for (String word : BukkitProtect.Plugin.getConfig().getStringList(
				"BannedWords")) {
			totalCensored = totalCensored + word.length();
			message = message.replaceAll("(?i)" + word, "****");
		}
		percent = (int) Math.round((totalCensored / total) * 100);
		if (percent > BukkitProtect.Plugin.getConfig().getDouble("CensorLimit"))
			event.setCancelled(true);
		event.setMessage(message);
		int time = (int) System.currentTimeMillis();
		if (playerLastChat.containsKey(player)
				&& playerLastChatMessage.containsKey(player)) {
			int lastChat = playerLastChat.get(player);
			String lastMessage = playerLastChatMessage.get(player);
			playerLastChat.put(player, time);
			playerLastChatMessage.put(player, message);
			double chatTime = (time - lastChat) / 1000;
			if (!playerSpam.containsKey(player)) {
				playerSpam.put(player, 0);
				return;
			}
			if (lastMessage.equalsIgnoreCase(message)) {
				event.setCancelled(true);
			} else if (chatTime >= BukkitProtect.Plugin.getConfig().getDouble(
					"ChatSpam")) {
				int spam = playerSpam.get(player);
				playerSpam.put(player, spam - 1);
				return;
			} else {
				if (playerSpam.get(player) >= 2)
					event.setCancelled(true);
			}
			int spam = playerSpam.get(player);
			playerSpam.put(player, spam + 1);
			if (spam == 2)
				player.sendMessage(ChatColor.GOLD + "Do not spam");
			else if (spam == 5)
				player.sendMessage(ChatColor.GOLD + "Last warning");
			else if (spam == 10) {
				player.kickPlayer("Do not spam!");
				BukkitProtect.Plugin.getServer().broadcastMessage(
						player.getDisplayName() + ChatColor.RED
								+ " has been kicked for spamming!");
				event.setCancelled(true);
			}
		} else {
			playerLastChat.put(player, time);
			playerLastChatMessage.put(player, message);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void PlayerDied(EntityDeathEvent Event) {
		if (Event.getEntity() == null)
			return;
		if (!(Event.getEntity() instanceof Player))
			return;
		if (PVPLogs.containsKey(Event.getEntity())) {
			if (!PVPLogs.get(Event.getEntity()).getPlrs().isEmpty()) {
				for (Player plr : PVPLogs.get(Event.getEntity()).getPlrs()) {
					if (PVPLogs.containsKey(plr)) {
						PVPLog PVPlog = PVPLogs.get(plr);
						PVPlog.removePlrs((Player) Event.getEntity());
						if (PVPlog.getPlrs().isEmpty()) {
							PVPLogs.remove(plr);
							plr.sendMessage("You are no longer in PVP");
						} else {
							PVPLogs.put(plr, PVPlog);
						}
					}
				}
				PVPLogs.remove(Event.getEntity());
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void PlayerTeleport(PlayerTeleportEvent Event) {
		if (Event.getPlayer() == null)
			return;
		if (!BukkitProtect.Plugin.getConfig().getBoolean("PreventPVPTeleport"))
			return;
		if (Event.getCause() == TeleportCause.PLUGIN) {
			if (isPlayerInPVP(Event.getPlayer())) {
				Event.setCancelled(true);
				Event.getPlayer().sendMessage("You cannot teleport in PVP");
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void PlayerLogout(PlayerQuitEvent Event) {
		if (Event.getPlayer() == null)
			return;
		if (!BukkitProtect.Plugin.getConfig().getBoolean("PreventPVPLog"))
			return;
		if (isPlayerInPVP(Event.getPlayer())) {
			Event.getPlayer().setHealth(0);
			Event.setQuitMessage(Event.getPlayer().getDisplayName()
					+ " has been killed for PVP logging");
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void DamageEntity(EntityDamageByEntityEvent Event) {
		if (Event.getEntity() == null || Event.getDamager() == null)
			return;
		if (!(Event.getEntity() instanceof Player))
			return;
		if (Event.getDamage() <= 0)
			return;
		if (Event.getCause() == DamageCause.THORNS)
			return;
		Player Attacker = null;
		if (Event.getDamager() instanceof Player)
			Attacker = (Player) Event.getDamager();
		if (Event.getDamager() instanceof Projectile)
			if (((Projectile) Event.getDamager()).getShooter() != null
					&& ((Projectile) Event.getDamager()).getShooter() instanceof Player)
				Attacker = (Player) ((Projectile) Event.getDamager())
						.getShooter();
		if (Event.getDamager() instanceof Tameable)
			if (Event.getDamager() instanceof Wolf) {
				if (((Wolf) Event.getDamager()).getOwner() == ((Player) Event
						.getEntity()))
					return;
				Attacker = (Player) ((Wolf) Event.getDamager()).getOwner();
			}
		if (Attacker == null)
			return;
		PVPLog AttLog = new PVPLog(new ArrayList<Player>(), 120);
		PVPLog DefLog = new PVPLog(new ArrayList<Player>(), 120);
		if (!PVPLogs.containsKey(Attacker)) {
			Attacker.sendMessage("You have entered PVP");
		} else {
			AttLog = PVPLogs.get(Attacker);
		}
		AttLog.addPlrs((Player) Event.getEntity());
		AttLog.setTimer(120);

		if (!PVPLogs.containsKey(Event.getEntity())) {
			((Player) Event.getEntity()).sendMessage("You have entered PVP");
		} else {
			DefLog = PVPLogs.get(Event.getEntity());
		}
		DefLog.addPlrs(Attacker);
		DefLog.setTimer(120);

		PVPLogs.put(Attacker, AttLog);
		PVPLogs.put((Player) Event.getEntity(), DefLog);
	}
}
