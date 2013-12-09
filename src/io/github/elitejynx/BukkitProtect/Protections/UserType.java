package io.github.elitejynx.BukkitProtect.Protections;

import org.bukkit.Material;

public class UserType {

	private String Name;
	private String Desc;
	private Material CornerDisplay;
	private int CornerMeta;
	private Material SideDisplay;
	private int SideMeta;
	private int Priority;
	private boolean Admin;

	public UserType(String name, String desc, Material cdisplay, int cmeta,
			Material sdisplay, int smeta, int priority, boolean admin) {
		setName(name);
		setDesc(desc);
		setCornerDisplay(cdisplay);
		setCornerMeta(cmeta);
		setSideDisplay(sdisplay);
		setSideMeta(smeta);
		setPriority(priority);
		setAdmin(admin);
	}

	public UserType(String name, String desc, int priority, boolean admin) {
		setName(name);
		setDesc(desc);
		setPriority(priority);
		setAdmin(admin);
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getDesc() {
		return Desc;
	}

	public void setDesc(String desc) {
		Desc = desc;
	}

	public boolean isAdmin() {
		return Admin;
	}

	public void setAdmin(boolean admin) {
		Admin = admin;
	}

	public int getPriority() {
		return Priority;
	}

	public void setPriority(int priority) {
		Priority = priority;
	}

	public Material getCornerDisplay() {
		return CornerDisplay;
	}

	public void setCornerDisplay(Material cornerDisplay) {
		CornerDisplay = cornerDisplay;
	}

	public int getSideMeta() {
		return SideMeta;
	}

	public void setSideMeta(int sideMeta) {
		SideMeta = sideMeta;
	}

	public Material getSideDisplay() {
		return SideDisplay;
	}

	public void setSideDisplay(Material sideDisplay) {
		SideDisplay = sideDisplay;
	}

	public int getCornerMeta() {
		return CornerMeta;
	}

	public void setCornerMeta(int cornerMeta) {
		CornerMeta = cornerMeta;
	}

}