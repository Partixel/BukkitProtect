package io.github.elitejynx.BukkitProtect;

import org.bukkit.Material;

public class UserType {

	private String Name;
	private String Desc;
	private Material Display;
	private int Priority;
	private boolean Admin;

	/**
	 * 
	 * @param name - The name of the UserType
	 * @param desc - The description of the UserType
	 * @param display - The material the UserType will be
	 * @param priority - The priority of the UserType during display - 5 for highest, 1 for lowest - 0 won't show
	 * @param admin - Whether or not the UserType will allow access to commands
	 */
	public UserType(String name, String desc, Material display, int priority, boolean admin) {
		this.setName(name);
		this.setDesc(desc);
		this.setDisplay(display);
		this.setPriority(priority);
		this.setAdmin(admin);
	}
	
	/**
	 * 
	 * @param name - The name of the UserType
	 * @param desc - The description of the UserType
	 * @param priority - The priority of the UserType - 5 for highest, 1 for lowest
	 * @param admin - Whether or not the UserType will allow access to commands
	 */
	public UserType(String name, String desc, int priority, boolean admin) {
		this.setName(name);
		this.setDesc(desc);
		this.setPriority(priority);
		this.setAdmin(admin);
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

	public Material getDisplay() {
		return Display;
	}

	public void setDisplay(Material display) {
		Display = display;
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

}