package io.github.elitejynx.BukkitProtect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
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
import org.bukkit.event.entity.EntityDeathEvent;

public class PVPHandler implements Listener {
	public Map<Player, Integer> CommandTimers = new HashMap<Player, Integer>();
	public Map<Player, CommandRequest> CommandTrades = new HashMap<Player, CommandRequest>();
	public Map<Player, Integer> PVPTimers = new HashMap<Player, Integer>();
	public Map<Player, ArrayList<Player>> PVPLogs = new HashMap<Player, ArrayList<Player>>();
	/**TODO
	 * Combine PVPTimers/Logs into one
	 */
	public Map<Player, Map<Block, Integer>> UpdateBlock = new HashMap<Player, Map<Block, Integer>>();
	public Map<Player, Map<Location, Integer>> PlayerSelection = new HashMap<Player, Map<Location, Integer>>();
	public Map<Player, Map<ProtectionZone, Integer>> PlayerSelectedZone = new HashMap<Player, Map<ProtectionZone, Integer>>();
	public Map<Player, Integer> PlayerGain = new HashMap<Player, Integer>();

	public PVPHandler(final BukkitProtect main) {
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
			if (PVPTimers.containsKey(Plr)) {
				PVPTimers.put(Plr, PVPTimers.get(Plr).intValue() - 1);
				if (PVPTimers.get(Plr).intValue() <= 0) {
					PVPTimers.remove(Plr);
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
		return PVPTimers.containsKey(plr);
	}

	public boolean isPlayerInPVPWith(Player plr, Player att) {
		if (PVPTimers.containsKey(plr)) {
			return PVPLogs.get(plr).contains(att);
		}
		return false;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void PlayerDied(EntityDeathEvent Event) {
		if (Event.getEntity() == null)
			return;
		if (!(Event.getEntity() instanceof Player))
			return;
		if (PVPTimers.containsKey(Event.getEntity())) {
			PVPTimers.remove(Event.getEntity());
			if (!PVPLogs.isEmpty() && PVPLogs.containsKey(Event.getEntity())) {
				for (Player plr : PVPLogs.get(Event.getEntity())) {
					if (PVPLogs.containsKey(plr)) {
						ArrayList<Player> PLog = PVPLogs.get(plr);
						PLog.remove(Event.getEntity());
						if (PLog.isEmpty()) {
							PVPLogs.remove(plr);
							PVPTimers.remove(plr);
							plr.sendMessage("You are no longer in PVP");
						} else {
							PVPLogs.put(plr, PLog);
						}
					}
				}
				PVPLogs.remove(Event.getEntity());
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void DamageEntity(EntityDamageByEntityEvent Event) {
		if (Event.getEntity() == null || Event.getDamager() == null)
			return;
		if (!(Event.getEntity() instanceof Player))
			return;
		if (((Player) Event.getEntity()).getHealth() <= 0)
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
		if (!PVPTimers.containsKey(Attacker))
			Attacker.sendMessage("You have entered PVP");
		if (!PVPTimers.containsKey(Event.getEntity()))
			((Player) Event.getEntity()).sendMessage("You have entered PVP");
		PVPTimers.put(Attacker, 120);
		ArrayList<Player> Atts;
		if (!PVPLogs.containsKey(Attacker)) {
			Atts = new ArrayList<Player>();
		} else {
			Atts = PVPLogs.get(Attacker);
		}
		Atts.add((Player) Event.getEntity());
		PVPLogs.put(Attacker, Atts);
		PVPTimers.put((Player) Event.getEntity(), 120);
		ArrayList<Player> Def;
		if (!PVPLogs.containsKey(Event.getEntity())) {
			Def = new ArrayList<Player>();
		} else {
			Def = PVPLogs.get(Event.getEntity());
		}
		Def.add(Attacker);
		PVPLogs.put((Player) Event.getEntity(), Def);
	}
}
