package io.github.elitejynx.BukkitProtect;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class Util {
	
	public static UserType parseUserType(String str) {
		for (UserType Type : BukkitProtect.Plugin.Types) {
			if (Type.getName().equalsIgnoreCase(str)) {
				return Type;
			}
		}
		return null;
	}

	public static Location str2loc(String str) {

		String str2loc[] = str.split("\\:");

		Location loc = new Location(BukkitProtect.Plugin.getServer().getWorld(
				str2loc[0]), 0, 0, 0);

		loc.setX(Double.parseDouble(str2loc[1]));

		loc.setY(Double.parseDouble(str2loc[2]));

		loc.setZ(Double.parseDouble(str2loc[3]));

		return loc;

	}

	public static String loc2str(Location loc) {

		return loc.getWorld().getName() + ":" + loc.getBlockX() + ":"
				+ loc.getBlockY() + ":" + loc.getBlockZ();

	}

	public static Block GetHighestBlockRelative(Location loc, Location rel) {
		loc.setY(rel.getY());
		Block Use = loc.getWorld().getBlockAt(loc);
		if (Use.getType().isOccluding() || Use.getType() == Material.SOIL
				|| Use.getType() == Material.SNOW || Use.isLiquid()
				|| Use.getType() == Material.LEAVES) {
			while (Use.getType().isOccluding()
					|| Use.getType() == Material.SOIL
					|| Use.getType() == Material.SNOW || Use.isLiquid()
					|| Use.getType() == Material.LEAVES
					|| loc.getBlockY() > loc.getWorld().getMaxHeight()) {
				Use = loc.getWorld().getBlockAt(loc.add(0, 1, 0));
			}
			return loc.getWorld().getBlockAt(loc.add(0, -1, 0));
		} else {
			while (!Use.getType().isOccluding()
					&& Use.getType() != Material.SOIL
					&& Use.getType() != Material.SNOW && !Use.isLiquid()
					&& Use.getType() != Material.LEAVES && loc.getBlockY() > 0) {
				Use = loc.getWorld().getBlockAt(loc.add(0, -1, 0));
			}
			return loc.getWorld().getBlockAt(loc);
		}
	}

	public static Boolean isInside(Location loc, Location corner1,
			Location corner2) {
		double xMin = 0;
		double xMax = 0;
		double zMin = 0;
		double zMax = 0;
		double x = loc.getX();
		double z = loc.getZ();

		xMin = Math.min(corner1.getX(), corner2.getX());
		xMax = Math.max(corner1.getX(), corner2.getX());

		zMin = Math.min(corner1.getZ(), corner2.getZ());
		zMax = Math.max(corner1.getZ(), corner2.getZ());

		return (x >= xMin && x <= xMax && z >= zMin && z <= zMax);
	}

	public static Boolean zonesIntersect(ProtectionZone Zone1,
			ProtectionZone Zone2) {
		Location Corner1 = Zone1.getCorner1();
		Location Corner2 = Zone1.getCorner2();
		Location Corner3 = Zone2.getCorner1();
		Location Corner4 = Zone2.getCorner2();

		int MaxX1 = Math.max(Corner1.getBlockX(), Corner2.getBlockX());
		int MinX1 = Math.min(Corner1.getBlockX(), Corner2.getBlockX());
		int MaxZ1 = Math.max(Corner1.getBlockZ(), Corner2.getBlockZ());
		int MinZ1 = Math.min(Corner1.getBlockZ(), Corner2.getBlockZ());

		int MaxX2 = Math.max(Corner3.getBlockX(), Corner4.getBlockX());
		int MinX2 = Math.min(Corner3.getBlockX(), Corner4.getBlockX());
		int MaxZ2 = Math.max(Corner3.getBlockZ(), Corner4.getBlockZ());
		int MinZ2 = Math.min(Corner3.getBlockZ(), Corner4.getBlockZ());

		return (MaxX1 >= MinX2 && MinX1 <= MaxX2 && MaxZ1 >= MinZ2 && MinZ1 <= MaxZ2);
	}

}
