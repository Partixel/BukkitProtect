package io.github.elitejynx.BukkitProtect.Protections;

import io.github.elitejynx.BukkitProtect.Util;

import java.util.ArrayList;
import java.util.Map;

import com.google.common.base.Splitter;

public class ProtectionZone extends Protection {

	protected Region Cube;

	public ProtectionZone(Region cube, String Plr) {
		super(Plr);
		if (cube != null)
			setCube(cube);
	}

	@Override
	public ProtectionZone Clone() {
		ProtectionZone newZone = new ProtectionZone(Cube.Clone(), Owner);
		newZone.setUsers(Users);
		newZone.setTags(Tags);
		return newZone;
	}

	@Override
	public String toString() {
		String Total = "";
		Total = Total + Cube.toString();
		Total = Total + "|" + Owner;
		Total = Total + "|" + Users.toString();
		Total = Total + "|" + Tags.toString();
		return Total;
	}

	public ArrayList<ProtectionZone> fromString(String Total,
			ArrayList<ProtectionZone> Zones) {
		String[] Splits = Total.split("\\|");
		try {
			Cube = Util.regionfromString(Splits[0].trim(), Splits[1].trim());
		} catch (Exception e) {
		}
		try {
			Owner = Splits[2].trim();
		} catch (Exception e) {
		}
		try {
			if (Splits[3].split("\\{")[1].split("\\}").length == 1) {
				Map<String, String> StringUsers = Splitter.on("], ")
						.withKeyValueSeparator("=")
						.split(Splits[3].split("\\{")[1].split("\\}")[0]);
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
			if (Splits[4].split("\\{")[1].split("\\}").length == 1) {
				Map<String, String> StringTags = Splitter.on(", ")
						.withKeyValueSeparator("=")
						.split(Splits[4].split("\\{")[1].split("\\}")[0]);
				for (String str : StringTags.keySet()) {
					Tags.put(str, StringTags.get(str));
				}
			}
		} catch (Exception e) {
		}
		Zones.add(this);
		return Zones;
	}

	public Region getCube() {
		return Cube;
	}

	public void setCube(Region cube) {
		Cube = cube;
	}
}
