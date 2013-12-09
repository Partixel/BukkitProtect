package io.github.elitejynx.BukkitProtect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Splitter;

public class ProtectionZone {

	private Region Cube;
	private String Owner;
	private Map<String, ArrayList<String>> Users = new HashMap<String, ArrayList<String>>();
	private Map<String, String> Tags = new HashMap<String, String>();

	public ProtectionZone(Region cube, String Plr) {
		if (cube != null)
			setCube(cube);
		if (Plr != null)
			setOwner(Plr);
	}

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

	public Map<String, ArrayList<String>> getUsers() {
		return Users;
	}

	public void setUsers(Map<String, ArrayList<String>> newUsers) {
		Users = newUsers;
	}

	public boolean addUsers(String Plr, UserType Type) {
		if (!Users.containsKey(Plr) && !Owner.equalsIgnoreCase(Plr)) {
			ArrayList<String> Types = new ArrayList<String>();
			if (Type == null) {
				for (UserType UT : BukkitProtect.Plugin.Types) {
					Types.add(UT.getName());
				}
			} else {
				Types.add(Type.getName());
			}
			Users.put(Plr, Types);
			return true;
		} else if (!Owner.equalsIgnoreCase(Plr)) {
			ArrayList<String> Types = Users.get(Plr);
			if (Type == null) {
				Types = new ArrayList<String>();
				for (UserType UT : BukkitProtect.Plugin.Types) {
					Types.add(UT.getName());
				}
			} else {
				if (!Types.contains(Type.getName())) {
					Types.add(Type.getName());
				} else {
					return false;
				}
			}
			Users.put(Plr, Types);
			return true;
		}
		return false;
	}

	public boolean removeUsers(String Plr, UserType Type) {
		if (Users.containsKey(Plr)) {
			if (Type == null) {
				Users.remove(Plr);
				return true;
			} else {
				if (Users.get(Plr).contains(Type.getName())) {
					ArrayList<String> Types = Users.get(Plr);
					Types.remove(Type.getName());
					if (Types.isEmpty()) {
						Users.remove(Plr);
						return true;
					} else {
						Users.put(Plr, Types);
						return true;
					}
				}
			}
		}
		return false;
	}

	public Region getCube() {
		return Cube;
	}

	public void setCube(Region cube) {
		Cube = cube;
	}

	public String getOwner() {
		return Owner;
	}

	public void setOwner(String owner) {
		Owner = owner;
	}

	public boolean setTags(String Tag, String Value) {
		if (!getTag(Tag.toLowerCase()).equalsIgnoreCase(Value)) {
			Tags.put(Tag.toLowerCase(), Value);
			return true;
		}
		return false;
	}

	public boolean removeTags(String Tag) {
		if (getTag(Tag.toLowerCase()) != "") {
			Tags.remove(Tag.toLowerCase());
			return true;
		}
		return false;
	}

	public Map<String, String> getTags() {
		return Tags;
	}

	public void setTags(Map<String, String> tags) {
		Tags = tags;
	}

	public boolean userHasAdminType(String Plr) {
		if (Owner.equalsIgnoreCase(Plr)) {
			return true;
		} else if (Users.containsKey(Plr)) {
			for (String str : Users.get(Plr)) {
				UserType UT = Util.parseUserType(str);
				if (UT != null) {
					return UT.isAdmin();
				}
			}
		}
		return false;
	}

	public boolean userHasType(String Plr, UserType Type) {
		if (Owner.equalsIgnoreCase(Plr)) {
			return true;
		} else if (Users.containsKey(Plr)) {
			if (Users.get(Plr).contains(Type.getName()))
				return true;
		} else if (Users.containsKey("*")) {
			if (Users.get("*").contains(Type.getName()))
				return true;
		}
		return false;
	}

	public String getTag(String Type) {
		if (Tags.containsKey(Type.toLowerCase())) {
			return Tags.get(Type.toLowerCase());
		} else {
			return "";
		}
	}
}
