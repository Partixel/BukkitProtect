package io.github.elitejynx.BukkitProtect;

import org.bukkit.Location;

public class Region {

	private Location Corner1;
	private Location Corner2;

	public Region(Location corner1, Location corner2) {
		setCorner1(corner1);
		setCorner2(corner2);
	}

	public Region Clone() {
		return new Region(Corner1.clone(), Corner2.clone());
	}

	@Override
	public String toString() {
		return Util.loc2str(Corner1) + "|" + Util.loc2str(Corner2);
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

	public Location getCorner3() {
		Location Corner3 = Corner1.clone();
		Corner3.setZ(Corner2.getZ());
		return Corner3;
	}

	public void setCorner3(Location corner3) {
		setCorner1(getCorner4());
		setCorner2(corner3);
	}

	public Location getCorner4() {
		Location Corner4 = Corner1.clone();
		Corner4.setX(Corner2.getX());
		return Corner4;
	}

	public void setCorner4(Location corner4) {
		setCorner1(getCorner3());
		setCorner2(corner4);
	}

	public int getWidth() {
		return Math.abs(getCorner1().getBlockZ() - getCorner2().getBlockZ());
	}

	public int getLength() {
		return Math.abs(getCorner1().getBlockX() - getCorner2().getBlockX());
	}

	public int getSize() {
		return getWidth() * getLength();
	}

	public Boolean isInside(Location Loc, boolean Y) {
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
		if (Y) {
			yMin = Math.min(corner1.getY(), corner2.getY());
			yMax = Math.max(corner1.getY(), corner2.getY());
		} else {
			yMin = y;
			yMax = y;
		}
		return (x >= xMin && x <= xMax && z >= zMin && z <= zMax && y >= yMin && y <= yMax);
	}

	public Boolean zonesIntersect(Region Region1, boolean Y) {
		Location corner1 = Corner1.clone();
		Location corner2 = Corner2.clone();
		Location corner3 = Region1.getCorner1().clone();
		Location corner4 = Region1.getCorner2().clone();
		int MaxX1 = Math.max(corner1.getBlockX(), corner2.getBlockX());
		int MinX1 = Math.min(corner1.getBlockX(), corner2.getBlockX());
		int MaxZ1 = Math.max(corner1.getBlockZ(), corner2.getBlockZ());
		int MinZ1 = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
		int MaxX2 = Math.max(corner3.getBlockX(), corner4.getBlockX());
		int MinX2 = Math.min(corner3.getBlockX(), corner4.getBlockX());
		int MaxZ2 = Math.max(corner3.getBlockZ(), corner4.getBlockZ());
		int MinZ2 = Math.min(corner3.getBlockZ(), corner4.getBlockZ());
		int MaxY1 = 10;
		int MinY1 = 0;
		int MaxY2 = 10;
		int MinY2 = 0;
		if (Y) {
			MaxY2 = Math.max(corner3.getBlockY(), corner4.getBlockY());
			MinY2 = Math.min(corner3.getBlockY(), corner4.getBlockY());
			MaxY1 = Math.max(corner1.getBlockY(), corner2.getBlockY());
			MinY1 = Math.min(corner1.getBlockY(), corner2.getBlockY());
		}
		return (MaxX1 >= MinX2 && MinX1 <= MaxX2 && MaxZ1 >= MinZ2
				&& MinZ1 <= MaxZ2 && MaxY1 >= MinY2 && MinY1 <= MaxY2);
	}
}
