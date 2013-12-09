package io.github.elitejynx.BukkitProtect.Protections;

import io.github.elitejynx.BukkitProtect.Util;

import java.util.ArrayList;
import java.util.Map;

import org.bukkit.World;

import com.google.common.base.Splitter;

public class ProtectionWorld extends Protection {

	protected World ProtWorld;

	public ProtectionWorld(World protworld, String Plr) {
		super(Plr);
		if (protworld != null)
			setProtWorld(protworld);
	}

	@Override
	public ProtectionWorld Clone() {
		ProtectionWorld newZone = new ProtectionWorld(ProtWorld, Owner);
		newZone.setUsers(Users);
		newZone.setTags(Tags);
		return newZone;
	}

	@Override
	public String toString() {
		String Total = "";
		Total = Total + ProtWorld.getUID().toString();
		Total = Total + "|" + Owner;
		Total = Total + "|" + Users.toString();
		Total = Total + "|" + Tags.toString();
		return Total;
	}

	public ArrayList<ProtectionWorld> fromString(String Total,
			ArrayList<ProtectionWorld> Zones) {
		String[] Splits = Total.split("\\|");
		try {
			ProtWorld = Util.worldFromUUID(Splits[0]);
		} catch (Exception e) {
		}
		try {
			Owner = Splits[1].trim();
		} catch (Exception e) {
		}
		try {
			if (Splits[2].split("\\{")[1].split("\\}").length == 1) {
				Map<String, String> StringUsers = Splitter.on("], ")
						.withKeyValueSeparator("=")
						.split(Splits[2].split("\\{")[1].split("\\}")[0]);
				for (String plr : StringUsers.keySet()) {
					ArrayList<String> ListUsers = new ArrayList<String>();
					String str = StringUsers.get(plr);
					try {
						if (str.split("\\[")[1].split("\\]").length == 1)
							for (String str2 : str.split("\\[")[1].split("\\]")[0]
									.split(", ")) {
								ListUsers.add(str2.trim());
							}
					} catch (Exception e) {
					}
					Users.put(plr, ListUsers);
				}
			}
		} catch (Exception e) {
		}
		try {
			if (Splits[3].split("\\{")[1].split("\\}").length == 1) {
				Map<String, String> StringTags = Splitter.on(", ")
						.withKeyValueSeparator("=")
						.split(Splits[3].split("\\{")[1].split("\\}")[0]);
				for (String str : StringTags.keySet()) {
					Tags.put(str, StringTags.get(str));
				}
			}
		} catch (Exception e) {
		}
		Zones.add(this);
		return Zones;
	}

	public World getProtWorld() {
		return ProtWorld;
	}

	public void setProtWorld(World protWorld) {
		ProtWorld = protWorld;
	}
}
