package io.github.elitejynx.BukkitProtect.Protections;

import io.github.elitejynx.BukkitProtect.BukkitProtect;
import io.github.elitejynx.BukkitProtect.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Protection {

	protected String Owner;
	protected Map<String, ArrayList<String>> Users = new HashMap<String, ArrayList<String>>();
	protected Map<String, String> Tags = new HashMap<String, String>();

	public Protection(String Plr) {
		if (Plr != null)
			setOwner(Plr);
	}

	public Protection Clone() {
		Protection newZone = new Protection(Owner);
		newZone.setUsers(Users);
		newZone.setTags(Tags);
		return newZone;
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
