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
	
	public static boolean isBlockSolid(Block block) {
		return block.getType().isOccluding() || block.getType() == Material.SOIL
				|| block.getType() == Material.LEAVES || block.getType() == Material.SNOW
				|| block.getType() == Material.STAINED_GLASS || block.getType() == Material.STAINED_GLASS_PANE
				|| block.getType() == Material.GLASS;
	}
	
	public static Block GetLowestBlockRelative(Location Loc, Location rLoc) {
		Location loc = Loc.clone();
		Location rloc = rLoc.clone();
		loc.setY(rloc.getY());
		Block Use = loc.getWorld().getBlockAt(loc);
		if (isBlockSolid(Use) || Use.isLiquid()) {
			return Use;
		} else {
			while (!isBlockSolid(Use) && !Use.isLiquid() && loc.getBlockY() > 0) {
				Use = loc.getWorld().getBlockAt(loc.add(0, -1, 0));
			}
			return loc.getWorld().getBlockAt(loc);
		}
	}
	
	public static Block GetLowestBlock(Location Loc) {
		Location loc = Loc.clone();
		Block Use = loc.getWorld().getBlockAt(loc);
		if (isBlockSolid(Use) || Use.isLiquid()) {
			return Use;
		} else {
			while (!isBlockSolid(Use) && !Use.isLiquid() && loc.getBlockY() > 0) {
				Use = loc.getWorld().getBlockAt(loc.add(0, -1, 0));
			}
			return loc.getWorld().getBlockAt(loc);
		}
	}

	public static Boolean isInsideY(Location Loc, Location Corner1, Location Corner2) {
		Location loc = Loc.clone();
		Location corner1 = Corner1.clone();
		Location corner2 = Corner2.clone();
		double xMin = 0;
		double xMax = 0;
		double zMin = 0;
		double zMax = 0;
		double yMin = 0;
		double yMax = 0;
		double x = loc.getX();
		double z = loc.getZ();
		double y = loc.getY();
		xMin = Math.min(corner1.getX(), corner2.getX());
		xMax = Math.max(corner1.getX(), corner2.getX());
		zMin = Math.min(corner1.getZ(), corner2.getZ());
		zMax = Math.max(corner1.getZ(), corner2.getZ());
		yMin = Math.min(corner1.getY(), corner2.getY());
		yMax = Math.max(corner1.getY(), corner2.getY());
		return (x >= xMin && x <= xMax && z >= zMin && z <= zMax && y >= yMin && y <= yMax);
	}
	
	public static Boolean isInside(Location Loc, Location Corner1, Location Corner2) {
		Location loc = Loc.clone();
		Location corner1 = Corner1.clone();
		Location corner2 = Corner2.clone();
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
	
	public static Boolean zonesIntersectY(ProtectionZone Zone1,
			ProtectionZone Zone2) {
		Location Corner1 = Zone1.getCorner1().clone();
		Location Corner2 = Zone1.getCorner2().clone();
		Location Corner3 = Zone2.getCorner1().clone();
		Location Corner4 = Zone2.getCorner2().clone();
		int MaxX1 = Math.max(Corner1.getBlockX(), Corner2.getBlockX());
		int MinX1 = Math.min(Corner1.getBlockX(), Corner2.getBlockX());
		int MaxZ1 = Math.max(Corner1.getBlockZ(), Corner2.getBlockZ());
		int MinZ1 = Math.min(Corner1.getBlockZ(), Corner2.getBlockZ());
		int MaxY1 = Math.max(Corner1.getBlockY(), Corner2.getBlockY());
		int MinY1 = Math.min(Corner1.getBlockY(), Corner2.getBlockY());
		int MaxX2 = Math.max(Corner3.getBlockX(), Corner4.getBlockX());
		int MinX2 = Math.min(Corner3.getBlockX(), Corner4.getBlockX());
		int MaxZ2 = Math.max(Corner3.getBlockZ(), Corner4.getBlockZ());
		int MinZ2 = Math.min(Corner3.getBlockZ(), Corner4.getBlockZ());
		int MaxY2 = Math.max(Corner3.getBlockY(), Corner4.getBlockY());
		int MinY2 = Math.min(Corner3.getBlockY(), Corner4.getBlockY());
		return (MaxX1 >= MinX2 && MinX1 <= MaxX2 && MaxZ1 >= MinZ2 && MinZ1 <= MaxZ2 && MaxY1 >= MinY2 && MinY1 <= MaxY2);
	}

	public static Boolean zonesIntersect(ProtectionZone Zone1,
			ProtectionZone Zone2) {
		Location Corner1 = Zone1.getCorner1().clone();
		Location Corner2 = Zone1.getCorner2().clone();
		Location Corner3 = Zone2.getCorner1().clone();
		Location Corner4 = Zone2.getCorner2().clone();
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
