package io.github.elitejynx.BukkitProtect;

import java.util.ArrayList;

public class Tag {

	private String Name;
	private String Desc;
	private ArrayList<String> Values = new ArrayList<String>();

	/**
	 * 
	 * @param name
	 *            - The name of the Tag
	 * @param desc
	 *            - The description of the Tag
	 * @param values
	 *            - The values the Tag can have
	 */
	public Tag(String name, String desc) {
		this.setName(name);
		this.setDesc(desc);
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

	public ArrayList<String> getValues() {
		return Values;
	}

	public void setValues(ArrayList<String> values) {
		Values = values;
	}

	public Tag addValues(String value) {
		Values.add(value);
		return this;
	}

}