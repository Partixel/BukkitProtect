package io.github.elitejynx.BukkitProtect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;

import com.google.common.base.Splitter;

public class ProtectionZone {

	private Location Corner1;
	private Location Corner2;
	private String Owner;
	private Map<String, ArrayList<String>> Users = new HashMap<String, ArrayList<String>>();
	private ArrayList<String> Tags = new ArrayList<String>();

	public ProtectionZone(Location CornerA, Location CornerB, String Plr) {
		if (CornerA != null)
			setCorner1(CornerA);
		if (CornerB != null)
			setCorner2(CornerB);
		if (Plr != null)
			setOwner(Plr);
	}

	public ProtectionZone Clone() {
		ProtectionZone newZone = new ProtectionZone(Corner1, Corner2, Owner);
		newZone.setUsers(Users);
		newZone.setTags(Tags);
		return newZone;
	}

	@Override
	public String toString() {
		String Total = "";
		Total = Total + Util.loc2str(Corner1);
		Total = Total + "|" + Util.loc2str(Corner2);
		Total = Total + "|" + Owner;
		Total = Total + "|" + Users.toString();
		Total = Total + "|" + Tags.toString();
		return Total;
	}

	public ArrayList<ProtectionZone> fromString(String Total, ArrayList<ProtectionZone> Zones) {
		String[] Splits = Total.split("\\|");
		try {
			Corner1 = Util.str2loc(Splits[0].trim());
		} catch (Exception e) {
		}
		try {
			Corner2 = Util.str2loc(Splits[1].trim());
		} catch (Exception e) {
		}
		try {
			Owner = Splits[2].trim();
		} catch (Exception e) {
		}
		try {
			if (Splits[3].split("\\{")[1].split("\\}").length == 1){
				Map<String, String> StringUsers = Splitter.on("], ").withKeyValueSeparator("=").split(Splits[3].split("\\{")[1].split("\\}")[0]);
				for (String plr : StringUsers.keySet()) {
					ArrayList<String> ListUsers = new ArrayList<String>();
					String str = StringUsers.get(plr);
					try {
						if (str.split("\\[")[1].split("\\]").length == 1)
							for (String str2 : str.split("\\[")[1].split("\\]")[0].split(", ")){
								ListUsers.add(str2.trim());
							}
					} catch (Exception e) {
					}
					Users.put(plr, ListUsers);
				}
			}
		} catch (Exception e) {
		}
		Zones.add(this);
		return Zones;
	}

	public Location getCorner1() {
		return Corner1;
	}

	public void setCorner1(Location corner1) {
		Corner1 = corner1;
	}

	public Location getCorner2() {
		return Corner2;
	}

	public void setCorner2(Location corner2) {
		Corner2 = corner2;
	}
	
	public int getWidth() {
		return Math.abs(getCorner1().getBlockZ()
				- getCorner2().getBlockZ());
	}
	
	public int getLength() {
		return Math.abs(getCorner1().getBlockX()
				- getCorner2().getBlockX());
	}
	
	public int getSize() {
		return getWidth() * getLength();
	}

	public Map<String, ArrayList<String>> getUsers() {
		return Users;
	}

	public void setUsers(Map<String, ArrayList<String>> newUsers) {
		Users = newUsers;
	}

	public boolean addUsers(String Plr, UserType Type) {
		if (!Users.containsKey(Plr)) {
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
		} else {
			ArrayList<String> Types = Users.get(Plr);
			if (Type == null) {
				Types = new ArrayList<String>();
				for (UserType UT : BukkitProtect.Plugin.Types) {
					Types.add(UT.getName());
				}
			} else {
				if (!Types.contains(Type.getName()))
					Types.add(Type.getName());
			}
			Users.put(Plr, Types);
		}
		return false;
	}

	public boolean removeUsers(String Plr, UserType Type) {
		if (Users.containsKey(Plr)) {
			if (Type == null) {
				Users.remove(Plr);
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

	public boolean addTags(String Tag) {
		if (!Tags.contains(Tag) && BukkitProtect.Plugin.Tags.containsKey(Tag)) {
			Tags.add(Tag);
			return true;
		}
		return false;
	}

	public boolean removeTags(String Tag) {
		if (Tags.contains(Tag)) {
			Tags.remove(Tag);
			return true;
		}
		return false;
	}

	public ArrayList<String> getTags() {
		return Tags;
	}

	public void setTags(ArrayList<String> tags) {
		Tags = tags;
	}
	
	public boolean userHasAdminType(String Plr) {
		if (Owner.equalsIgnoreCase(Plr)){
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

	public boolean hasTag(String Tag) {
		return Tags.contains(Tag);
	}

}
