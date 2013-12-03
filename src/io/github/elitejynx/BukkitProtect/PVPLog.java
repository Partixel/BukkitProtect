package io.github.elitejynx.BukkitProtect;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class PVPLog {

	private ArrayList<Player> Plrs = new ArrayList<Player>();
	private int Timer = 0;

	public PVPLog(ArrayList<Player> plrs, int timer) {
		setPlrs(plrs);
		setTimer(timer);
	}

	public ArrayList<Player> getPlrs() {
		return Plrs;
	}

	public void setPlrs(ArrayList<Player> plrs) {
		Plrs = plrs;
	}

	public void addPlrs(Player plr) {
		if (!Plrs.contains(plr))
			Plrs.add(plr);
	}

	public void removePlrs(Player plr) {
		if (Plrs.contains(plr))
			Plrs.remove(plr);
	}

	public int getTimer() {
		return Timer;
	}

	public void setTimer(int timer) {
		Timer = timer;
	}

	public void changeTimer(int change) {
		Timer = Timer + change;
	}

}
