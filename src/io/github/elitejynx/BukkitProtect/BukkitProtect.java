package io.github.elitejynx.BukkitProtect;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Button;
import org.bukkit.material.Lever;
import org.bukkit.material.Openable;
import org.bukkit.material.PressurePlate;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author EliteJynx
 */
@SuppressWarnings("deprecation")
public class BukkitProtect extends JavaPlugin implements Listener {
	// The class handling PVP
	public static PVPHandler PVP;
	public static BukkitProtect Plugin;
	// Files
	public String ProtectionPath;
	public String LandPath;
	// Protections
	public Map<String, ArrayList<ProtectionZone>> Protections = new HashMap<String, ArrayList<ProtectionZone>>();
	public Map<String, Integer> LandOwned = new HashMap<String, Integer>();
	public Map<String, String> Tags = new HashMap<String, String>();
	public ArrayList<UserType> Types = new ArrayList<UserType>();

	public UserType UTAccess = new UserType("Access",
			"Allows the use of blocks to access the area", Material.LOG, 1,
			false);
	public UserType UTEntities = new UserType("Entities",
			"Allows the use of entities", Material.COAL_BLOCK, 2, false);
	public UserType UTBuildBlocks = new UserType("BuildBlocks",
			"Allows the building of blocks", Material.IRON_BLOCK, 3, false);
	public UserType UTUseBlocks = new UserType("UseBlocks",
			"Allows the use of blocks", Material.GOLD_BLOCK, 3, false);
	public UserType UTModerator = new UserType("Moderator",
			"Allows the use of commands", Material.DIAMOND_BLOCK, 5, true);

	public ArrayList<Material> RodTypes = new ArrayList<Material>();

	public ItemStack RodA;

	public ItemStack addRod(ItemStack baseRod, String Name, int MaxUses,
			int Level, Material baseMaterial) {
		ItemStack Rod = baseRod.clone();
		ItemMeta RodMeta = Rod.getItemMeta();
		RodMeta.setDisplayName(Name);
		ArrayList<String> Lore = new ArrayList<String>();
		Lore.add("Protect your land");
		Lore.add(MaxUses + " / " + MaxUses);
		RodMeta.setLore(Lore);
		Rod.setItemMeta(RodMeta);
		Rod.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, Level);
		if (baseMaterial != null) {
			ShapedRecipe RodRecipe = new ShapedRecipe(Rod);
			RodRecipe.shape(" GG", " SG", "C  ");
			RodRecipe.setIngredient('G', Material.GLASS);
			RodRecipe.setIngredient('S', baseRod.getType());
			RodRecipe.setIngredient('C', baseMaterial);
			getServer().addRecipe(RodRecipe);
		}
		if (!RodTypes.contains(baseRod.getType()))
			RodTypes.add(baseRod.getType());
		return Rod;
	}

	public void SetupRods() {
		ItemStack baseStick = new ItemStack(Material.STICK);
		ItemStack baseRod = new ItemStack(Material.BLAZE_ROD);
		addRod(baseStick, "Rod of Stone", 4, 1, Material.COBBLESTONE);
		addRod(baseStick, "Rod of Iron", 10, 2, Material.IRON_INGOT);
		addRod(baseStick, "Rod of Gold", 25, 3, Material.GOLD_INGOT);
		addRod(baseStick, "Rod of Diamond", 40, 4, Material.DIAMOND);
		addRod(baseRod, "Rod of Obsidian", 50, 5, Material.OBSIDIAN);
		RodA = addRod(baseRod, "Rod of the Admin", -1, 6, null);
	}

	/**
	 * 
	 * @param Type
	 *            - The UserType you defined
	 */
	public void addUserType(UserType Type) {
		Types.add(Type);
	}

	@Override
	public void onEnable() {
		Plugin = this;
		this.saveDefaultConfig();
		Tags.put("PreventPVP", "Prevent PVP");
		Tags.put("PreventFireSpread", "Prevent fire spread");
		Tags.put("PreventIceMeltForm", "Prevent ice melting or forming");
		Tags.put("PreventSnowMeltForm", "Prevent snow melting or forming");
		Tags.put("PreventEntitySpawn", "Prevent entities spawning");
		Tags.put("ServerOwned",
				"Prevents this protection from being counted in the owners land blocks");
		addUserType(UTAccess);
		addUserType(UTEntities);
		addUserType(UTBuildBlocks);
		addUserType(UTUseBlocks);
		addUserType(UTModerator);
		ProtectionPath = getDataFolder() + File.separator + "Protections.yml";
		File FileP = new File(ProtectionPath);
		LandPath = getDataFolder() + File.separator + "Land.yml";
		File FileL = new File(ProtectionPath);
		if (FileL.exists())
			try {
				LandOwned = load(LandPath);
			} catch (Exception e) {
			}
		Map<String, String> StringProtections = new HashMap<String, String>();
		if (FileP.exists())
			try {
				StringProtections = load(ProtectionPath);
			} catch (Exception e) {
			}
		if (!StringProtections.isEmpty())
			for (String Player : StringProtections.keySet()) {
				ArrayList<ProtectionZone> Zones = new ArrayList<ProtectionZone>();
				for (String Zone : StringProtections.get(Player).split(" / ")) {
					if (Zone.length() > 0) {
						ArrayList<ProtectionZone> newZones = new ArrayList<ProtectionZone>();
						new ProtectionZone(null, null, null).fromString(Zone,
								newZones);
						for (ProtectionZone newZone : newZones) {
							Zones.add(newZone);
						}
					}
				}
				Protections.put(Player, Zones);
			}
		SetupRods();
		getServer().getPluginManager().registerEvents(this, this);
		PVP = new PVPHandler(this);
		getServer().getPluginManager().registerEvents(PVP, this);
		new UpdateHandler(this, 68440, this.getFile(),
				UpdateHandler.UpdateType.DEFAULT, true);
	}

	@Override
	public void onDisable() {
		try {
			save(LandOwned, LandPath);
		} catch (Exception e) {
			try {
				save(LandOwned, LandPath);
			} catch (Exception e1) {
			}
		}
		Map<String, String> StringProtections = new HashMap<String, String>();
		if (!Protections.isEmpty())
			for (String Player : Protections.keySet()) {
				String Total = "";
				for (ProtectionZone Zone : Protections.get(Player)) {
					Total = Total + " / " + Zone.toString();
				}
				StringProtections.put(Player, Total);
			}
		try {
			save(StringProtections, ProtectionPath);
		} catch (Exception e) {
			try {
				save(StringProtections, ProtectionPath);
			} catch (Exception e1) {
			}
		}
	}

	public static <T extends Object> void save(T obj, String path)
			throws Exception {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
				path));
		oos.writeObject(obj);
		oos.flush();
		oos.close();
	}

	@SuppressWarnings("unchecked")
	public static <T extends Object> T load(String path) throws Exception {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
		T result = (T) ois.readObject();
		ois.close();
		return result;
	}

	@Override
	public boolean onCommand(CommandSender Sender, Command Cmd, String Label,
			String[] Args) {
		if (Cmd.getName().equalsIgnoreCase("giverod")) {
			if (!(Sender instanceof Player)) {
				Sender.sendMessage("You must be a player to use this");
				return true;
			}
			if (!Sender.hasPermission("BukkitProtect.Commands.GiveRod")) {
				Sender.sendMessage("You do not have permission to use this");
				return true;
			}
			if (Args.length == 1) {
				Player Target = (Bukkit.getPlayer(Args[0]));
				if (Target == null) {
					Sender.sendMessage("Could not find that player");
					return true;
				}
				Target.getInventory().addItem(RodA);
				Target.sendMessage("Given " + Target.getDisplayName() + " the "
						+ RodA.getItemMeta().getDisplayName());
			} else if (Args.length > 1) {
				Sender.sendMessage("Too many arguements, please retry");
				return false;
			} else {
				((Player) Sender).getInventory().addItem(RodA);
				Sender.sendMessage("Given "
						+ ((Player) Sender).getDisplayName() + " the "
						+ RodA.getItemMeta().getDisplayName());
			}
		} else if (Cmd.getName().equalsIgnoreCase("setowner")) {
			if (!(Sender instanceof Player)) {
				Sender.sendMessage("You must be a player to use this");
				return true;
			}
			if (!Sender.hasPermission("BukkitProtect.Commands.EditOthers")) {
				Sender.sendMessage("You do not have permission to use this");
				return true;
			}
			if (Args.length == 1) {
				Player Target = (Bukkit.getPlayer(Args[0]));
				if (Target == null) {
					Sender.sendMessage("Could not find that player");
					return true;
				}
				if (PVP.PlayerSelectedZone.containsKey(Sender)) {
					if (((ProtectionZone) PVP.PlayerSelectedZone.get(Sender)
							.keySet().toArray()[0]).getOwner()
							.equalsIgnoreCase(Sender.getName())
							|| Sender
									.hasPermission("BukkitProtect.Protection.EditOthers")) {
						if (getConfig().getBoolean("BuyableLand")) {
							if (LandOwned.containsKey(Target.getName())) {
								if ((LandOwned.get(Target.getName()) - getTotalLandUsed(Target)) < ((ProtectionZone) PVP.PlayerSelectedZone
										.get(Sender).keySet().toArray()[0])
										.getSize()
										&& !((ProtectionZone) PVP.PlayerSelectedZone
												.get(Sender).keySet().toArray()[0])
												.hasTag("ServerOwned")) {
									Sender.sendMessage(Target.getName()
											+ " needs "
											+ (((ProtectionZone) PVP.PlayerSelectedZone
													.get(Sender).keySet()
													.toArray()[0]).getSize() - (LandOwned
													.get(Target.getName()) - getTotalLandUsed(Target)))
											+ " more blocks of land to have the protection");
									return true;
								}
							}
						}
						if (Protections
								.containsKey(((ProtectionZone) PVP.PlayerSelectedZone
										.get(Sender).keySet().toArray()[0])
										.getOwner())) {
							ProtectionZone Zone = ((ProtectionZone) PVP.PlayerSelectedZone
									.get(Sender).keySet().toArray()[0]).Clone();
							ArrayList<ProtectionZone> ZonesA = Protections
									.get(((ProtectionZone) PVP.PlayerSelectedZone
											.get(Sender).keySet().toArray()[0])
											.getOwner());
							ArrayList<ProtectionZone> ZonesB = Protections
									.get(Target.getName());
							if (ZonesB == null) {
								ZonesB = new ArrayList<ProtectionZone>();
							}
							ZonesA.remove((PVP.PlayerSelectedZone.get(Sender)
									.keySet().toArray()[0]));
							Zone.setOwner(Target.getName());
							ZonesB.add(Zone);
							Protections.put(Sender.getName(), ZonesA);
							Protections.put(Target.getName(), ZonesB);
							Sender.sendMessage("Set the owner of the protection to "
									+ Target.getDisplayName());

							Target.sendMessage("You have recieved the protection from "
									+ ((Player) Sender).getDisplayName());
						}
					}
				} else {
					Sender.sendMessage("You have not selected a protection, to do this right click with a stick inside a protection");
					return true;
				}
			} else if (Args.length > 1) {
				Sender.sendMessage("Too many arguements, please retry");
				return false;
			} else {
				return false;
			}
		} else if (Cmd.getName().equalsIgnoreCase("accept")) {
			if (!(Sender instanceof Player)) {
				Sender.sendMessage("You must be a player to use this");
				return true;
			}
			if (!Sender.hasPermission("BukkitProtect.Commands.Accept")) {
				Sender.sendMessage("You do not have permission to use this");
				return true;
			}
			if (Args.length == 0) {
				for (Player Plr : PVP.CommandTrades.keySet()) {
					CommandRequest Request = PVP.CommandTrades.get(Plr);
					if (Request.getTarget() == (Player) Sender) {
						if (!Request.getAccepted()) {
							Request.setAccepted(true);
							onCommand(Request.getSender(),
									Request.getCommand(), Request.getCommand()
											.getName(), Request.getArgs());
							Request.getSender().sendMessage(
									"The command has been accepted");
							Request.getTarget().sendMessage(
									"You have accepted the command");
							PVP.CommandTrades.put(Plr, Request);
							return true;
						}
					}
				}
				Sender.sendMessage("You have no pending requests");
				return true;
			} else if (Args.length > 0) {
				Sender.sendMessage("Too many arguements, please retry");
				return false;
			} else {
				((Player) Sender).getInventory().addItem(RodA);
			}
		} else if (Cmd.getName().equalsIgnoreCase("transfer")) {
			if (!(Sender instanceof Player)) {
				Sender.sendMessage("You must be a player to use this");
				return true;
			}
			if (!Sender.hasPermission("BukkitProtect.Commands.Transfer")) {
				Sender.sendMessage("You do not have permission to use this");
				return true;
			}
			if (Args.length == 1) {
				Player Target = (Bukkit.getPlayer(Args[0]));
				if (Target == null) {
					Sender.sendMessage("Could not find that player");
					return true;
				}
				if (Target == (Player) Sender) {
					Sender.sendMessage("Cannot transfer to yourself");
					return true;
				}
				if (PVP.PlayerSelectedZone.containsKey(Sender)) {
					if (PVP.CommandTrades.containsKey(Sender)
							&& PVP.CommandTrades.get(Sender).getTarget() == Target
							&& PVP.CommandTrades.get(Sender).getAccepted()) {
						if (((ProtectionZone) PVP.PlayerSelectedZone
								.get(Sender).keySet().toArray()[0]).getOwner()
								.equalsIgnoreCase(Sender.getName())
								|| Sender
										.hasPermission("BukkitProtect.Protection.EditOthers")) {
							int Length = Math
									.abs(((ProtectionZone) PVP.PlayerSelectedZone
											.get(Sender).keySet().toArray()[0])
											.getCorner1().getBlockX()
											- ((ProtectionZone) PVP.PlayerSelectedZone
													.get(Sender).keySet()
													.toArray()[0]).getCorner2()
													.getBlockX());
							int Width = Math
									.abs(((ProtectionZone) PVP.PlayerSelectedZone
											.get(Sender).keySet().toArray()[0])
											.getCorner1().getBlockZ()
											- ((ProtectionZone) PVP.PlayerSelectedZone
													.get(Sender).keySet()
													.toArray()[0]).getCorner2()
													.getBlockZ());
							if (getConfig().getBoolean("BuyableLand")) {
								if (LandOwned.containsKey(Target.getName())) {
									if ((LandOwned.get(Target.getName()) - getTotalLandUsed(Target)) < (Length * Width)
											&& !((ProtectionZone) PVP.PlayerSelectedZone
													.get(Sender).keySet()
													.toArray()[0])
													.hasTag("ServerOwned")) {
										Sender.sendMessage(Target.getName()
												+ " needs "
												+ ((Length * Width) - (LandOwned
														.get(Target.getName()) - getTotalLandUsed(Target)))
												+ " more blocks of land to have the protection");
										return true;
									}
								}
							}
							if (Protections
									.containsKey(((ProtectionZone) PVP.PlayerSelectedZone
											.get(Sender).keySet().toArray()[0])
											.getOwner())) {
								ProtectionZone Zone = ((ProtectionZone) PVP.PlayerSelectedZone
										.get(Sender).keySet().toArray()[0])
										.Clone();
								ArrayList<ProtectionZone> ZonesA = Protections
										.get(((ProtectionZone) PVP.PlayerSelectedZone
												.get(Sender).keySet().toArray()[0])
												.getOwner());
								ArrayList<ProtectionZone> ZonesB = Protections
										.get(Target.getName());
								if (ZonesB == null) {
									ZonesB = new ArrayList<ProtectionZone>();
								}
								ZonesA.remove((PVP.PlayerSelectedZone
										.get(Sender).keySet().toArray()[0]));
								Zone.setOwner(Target.getName());
								ZonesB.add(Zone);
								Protections.put(Sender.getName(), ZonesA);
								Protections.put(Target.getName(), ZonesB);
								Sender.sendMessage("Transfered the protection to "
										+ Target.getDisplayName());
								Target.sendMessage("You have recieved the protection from "
										+ ((Player) Sender).getDisplayName());
							}
						}
					} else {
						CommandRequest Trades = new CommandRequest(
								(Player) Sender, Target, false, Cmd, Args);
						PVP.CommandTrades.put((Player) Sender, Trades);
						PVP.CommandTimers.put((Player) Sender, 10);
						Sender.sendMessage("Waiting for player to accept");
						Target.sendMessage(Sender.getName()
								+ " wishes to give you a protection, say /accept to accept or wait 10 seconds for it to time out");
					}
				} else {
					Sender.sendMessage("You have not selected a protection, to do this right click with a stick inside a protection");
					return true;
				}
			} else if (Args.length > 1) {
				Sender.sendMessage("Too many arguements, please retry");
				return false;
			} else {
				return false;
			}
		} else if (Cmd.getName().equalsIgnoreCase("addUsers")) {
			if (!(Sender instanceof Player)) {
				Sender.sendMessage("You must be a player to use this");
				return true;
			}
			if (!Sender.hasPermission("BukkitProtect.Commands.Users")) {
				Sender.sendMessage("You do not have permission to use this");
				return true;
			}
			if (Args.length == 2) {
				String Target;
				if (Args[0].equalsIgnoreCase("*")) {
					Target = "*";
				} else if (Bukkit.getPlayer(Args[0]) != null) {
					Target = Bukkit.getPlayer(Args[0]).getName();
				} else {
					Sender.sendMessage("Could not find that player");
					return true;
				}
				UserType UT = Util.parseUserType(Args[1]);

				if (UT == null && !Args[1].equalsIgnoreCase("*")) {
					for (UserType UType : Types) {
						Sender.sendMessage(UType.getName() + " : "
								+ UType.getDesc());
					}
					Sender.sendMessage("That is not a valid type");
					return true;
				}

				if (PVP.PlayerSelectedZone.containsKey(Sender)) {
					if (((ProtectionZone) PVP.PlayerSelectedZone.get(Sender)
							.keySet().toArray()[0])
							.userHasAdminType(((Player) Sender).getName())
							|| Sender
									.hasPermission("BukkitProtect.Protection.EditOthers")) {
						if (((ProtectionZone) PVP.PlayerSelectedZone
								.get(Sender).keySet().toArray()[0]).addUsers(
								Target, UT)) {
							Sender.sendMessage("Added " + Target
									+ " to the protection as " + Args[1]);
						} else {
							Sender.sendMessage("Could not add " + Target
									+ " to the protection as " + Args[1]);
						}
					}
				} else {
					Sender.sendMessage("You have not selected a protection, to do this right click with a stick inside a protection");
					return true;
				}
			} else if (Args.length > 2) {
				Sender.sendMessage("Too many arguements, please retry");
				return false;
			} else {
				return false;
			}
		} else if (Cmd.getName().equalsIgnoreCase("addtag")) {
			if (!(Sender instanceof Player)) {
				Sender.sendMessage("You must be a player to use this");
				return true;
			}
			if (!Sender.hasPermission("BukkitProtect.Commands.Tag")) {
				Sender.sendMessage("You do not have permission to use this");
				return true;
			}
			if (Args.length == 1) {
				if (Util.isTag(Args[0]) == null) {
					for (int i = 0; i < Tags.keySet().size(); i++) {
						String TagName = ((String) Tags.keySet().toArray()[i]);
						String TagDesc = Tags.get(TagName);
						Sender.sendMessage(TagName + " : " + TagDesc);
					}
					Sender.sendMessage("That is not a valid tag");
					return true;
				}
				if (PVP.PlayerSelectedZone.containsKey(Sender)) {
					if (((ProtectionZone) PVP.PlayerSelectedZone.get(Sender)
							.keySet().toArray()[0])
							.userHasAdminType(((Player) Sender).getName())
							|| Sender
									.hasPermission("BukkitProtect.Protection.EditOthers")) {
						if (((ProtectionZone) PVP.PlayerSelectedZone
								.get(Sender).keySet().toArray()[0])
								.addTags(Util.isTag(Args[0]))) {
							Sender.sendMessage("Added the tag "
									+ Util.isTag(Args[0])
									+ " to the protection");

						} else {
							Sender.sendMessage("Could not add the tag "
									+ Util.isTag(Args[0])
									+ " to the protection");
						}
					}
				} else {
					Sender.sendMessage("You have not selected a protection, to do this right click with a stick inside a protection");
					return true;
				}
			} else if (Args.length > 1) {
				Sender.sendMessage("Too many arguements, please retry");
				return false;
			} else {
				for (int i = 0; i < Tags.keySet().size(); i++) {
					String TagName = ((String) Tags.keySet().toArray()[i]);
					String TagDesc = Tags.get(TagName);
					Sender.sendMessage(TagName + " : " + TagDesc);
				}
				return false;
			}
		} else if (Cmd.getName().equalsIgnoreCase("gettags")) {
			if (!(Sender instanceof Player)) {
				Sender.sendMessage("You must be a player to use this");
				return true;
			}
			if (!Sender.hasPermission("BukkitProtect.Commands.Tag")) {
				Sender.sendMessage("You do not have permission to use this");
				return true;
			}
			if (Args.length == 0) {
				if (PVP.PlayerSelectedZone.containsKey(Sender)) {
					if (((ProtectionZone) PVP.PlayerSelectedZone.get(Sender)
							.keySet().toArray()[0])
							.userHasAdminType(((Player) Sender).getName())
							|| Sender
									.hasPermission("BukkitProtect.Protection.EditOthers")) {
						if (((ProtectionZone) PVP.PlayerSelectedZone
								.get(Sender).keySet().toArray()[0]).getTags()
								.isEmpty()) {
							Sender.sendMessage("This protection has no tags");
						} else {
							for (String Tag : ((ProtectionZone) PVP.PlayerSelectedZone
									.get(Sender).keySet().toArray()[0])
									.getTags()) {
								Sender.sendMessage(Tag);
							}
						}
					}
				} else {
					Sender.sendMessage("You have not selected a protection, to do this right click with a stick inside a protection");
					return true;
				}
			} else if (Args.length > 0) {
				Sender.sendMessage("Too many arguements, please retry");
				return false;
			} else {
				return false;
			}
		} else if (Cmd.getName().equalsIgnoreCase("getusers")) {
			if (!(Sender instanceof Player)) {
				Sender.sendMessage("You must be a player to use this");
				return true;
			}
			if (!Sender.hasPermission("BukkitProtect.Commands.Users")) {
				Sender.sendMessage("You do not have permission to use this");
				return true;
			}
			if (Args.length == 0) {
				if (PVP.PlayerSelectedZone.containsKey(Sender)) {
					if (((ProtectionZone) PVP.PlayerSelectedZone.get(Sender)
							.keySet().toArray()[0])
							.userHasAdminType(((Player) Sender).getName())
							|| Sender
									.hasPermission("BukkitProtect.Protection.EditOthers")) {
						if (((ProtectionZone) PVP.PlayerSelectedZone
								.get(Sender).keySet().toArray()[0]).getUsers()
								.keySet().isEmpty()) {
							Sender.sendMessage("This protection has no users");
						} else {
							for (String User : ((ProtectionZone) PVP.PlayerSelectedZone
									.get(Sender).keySet().toArray()[0])
									.getUsers().keySet()) {
								String UserHas = ((ProtectionZone) PVP.PlayerSelectedZone
										.get(Sender).keySet().toArray()[0])
										.getUsers().get(User).toString()
										.split("\\[")[1].split("\\]")[0];
								Sender.sendMessage(User + " : " + UserHas);
							}
						}
					}
				} else {
					Sender.sendMessage("You have not selected a protection, to do this right click with a stick inside a protection");
					return true;
				}
			} else if (Args.length > 0) {
				Sender.sendMessage("Too many arguements, please retry");
				return false;
			} else {
				return false;
			}
		} else if (Cmd.getName().equalsIgnoreCase("removeUsers")) {
			if (!(Sender instanceof Player)) {
				Sender.sendMessage("You must be a player to use this");
				return true;
			}
			if (!Sender.hasPermission("BukkitProtect.Commands.Users")) {
				Sender.sendMessage("You do not have permission to use this");
				return true;
			}
			if (Args.length == 2) {
				String Target;
				if (Args[0].equalsIgnoreCase("*")) {
					Target = "*";
				} else if (Bukkit.getPlayer(Args[0]) != null) {
					Target = Bukkit.getPlayer(Args[0]).getName();
				} else {
					Sender.sendMessage("Could not find that player");
					return true;
				}
				UserType UT = Util.parseUserType(Args[1]);
				if (UT == null && !Args[1].equalsIgnoreCase("*")) {
					for (UserType UType : Types) {
						Sender.sendMessage(UType.getName() + " : "
								+ UType.getDesc());
					}
					Sender.sendMessage("That is not a valid type");
					return true;
				}
				if (PVP.PlayerSelectedZone.containsKey(Sender)) {
					if (((ProtectionZone) PVP.PlayerSelectedZone.get(Sender)
							.keySet().toArray()[0])
							.userHasAdminType(((Player) Sender).getName())
							|| Sender
									.hasPermission("BukkitProtect.Protection.EditOthers")) {
						if (((ProtectionZone) PVP.PlayerSelectedZone
								.get(Sender).keySet().toArray()[0])
								.removeUsers(Target, UT)) {
							Sender.sendMessage("Removed " + Target
									+ " from the protection as " + Args[1]);
						} else {
							Sender.sendMessage("Could not remove " + Target
									+ " from the protection as " + Args[1]);
						}
					}
				} else {
					Sender.sendMessage("You have not selected a protection, to do this right click with a stick inside a protection");
					return true;
				}
			} else if (Args.length > 2) {
				Sender.sendMessage("Too many arguements, please retry");
				return false;
			} else {
				return false;
			}
		} else if (Cmd.getName().equalsIgnoreCase("removetag")) {
			if (!(Sender instanceof Player)) {
				Sender.sendMessage("You must be a player to use this");
				return true;
			}
			if (!Sender.hasPermission("BukkitProtect.Commands.Tag")) {
				Sender.sendMessage("You do not have permission to use this");
				return true;
			}
			if (Args.length == 1) {
				if (Util.isTag(Args[0]) == null) {
					for (int i = 0; i < Tags.keySet().size(); i++) {
						String TagName = ((String) Tags.keySet().toArray()[i]);
						String TagDesc = Tags.get(TagName);
						Sender.sendMessage(TagName + " : " + TagDesc);
					}
					Sender.sendMessage("That is not a valid tag");
					return true;
				}
				if (PVP.PlayerSelectedZone.containsKey(Sender)) {
					if (((ProtectionZone) PVP.PlayerSelectedZone.get(Sender)
							.keySet().toArray()[0])
							.userHasAdminType(((Player) Sender).getName())
							|| Sender
									.hasPermission("BukkitProtect.Protection.EditOthers")) {
						if (((ProtectionZone) PVP.PlayerSelectedZone
								.get(Sender).keySet().toArray()[0])
								.removeTags(Util.isTag(Args[0]))) {
							Sender.sendMessage("Removed " + Util.isTag(Args[0])
									+ " from the protection");
						} else {
							Sender.sendMessage("Could not remove "
									+ Util.isTag(Args[0])
									+ " from the protection");
						}
					}
				} else {
					Sender.sendMessage("You have not selected a protection, to do this right click with a stick inside a protection");
					return true;
				}
			} else if (Args.length > 1) {
				Sender.sendMessage("Too many arguements, please retry");
				return false;
			} else {
				return false;
			}
		} else if (Cmd.getName().equalsIgnoreCase("removeprotection")) {
			if (!(Sender instanceof Player)) {
				Sender.sendMessage("You must be a player to use this");
				return true;
			}
			if (!Sender
					.hasPermission("BukkitProtect.Commands.RemoveProtections")) {
				Sender.sendMessage("You do not have permission to use this");
				return true;
			}
			if (Args.length == 0) {
				if (PVP.PlayerSelectedZone.containsKey(Sender)) {
					if (((ProtectionZone) PVP.PlayerSelectedZone.get(Sender)
							.keySet().toArray()[0]).getOwner()
							.equalsIgnoreCase(Sender.getName())
							|| Sender
									.hasPermission("BukkitProtect.Protection.RemoveOthers")) {
						if (PVP.CommandTimers.containsKey(Sender)) {
							ArrayList<ProtectionZone> Zones = Protections
									.get(((ProtectionZone) PVP.PlayerSelectedZone
											.get(Sender).keySet().toArray()[0])
											.getOwner());
							Zones.remove((PVP.PlayerSelectedZone.get(Sender)
									.keySet().toArray()[0]));
							Protections.put(
									((ProtectionZone) PVP.PlayerSelectedZone
											.get(Sender).keySet().toArray()[0])
											.getOwner(), Zones);
							PVP.CommandTimers.put((Player) Sender, -1);
							PVP.updateFakeBlocks((Player) Sender);
							Sender.sendMessage("Removed the protection");
							return true;
						} else {
							PVP.CommandTimers.put((Player) Sender, 10);
							Sender.sendMessage("Say the command again within 10 seconds to accept");
							return true;
						}
					}
				} else {
					Sender.sendMessage("You have not selected a protection, to do this right click with a stick inside a protection");
					return true;
				}
			} else if (Args.length > 0) {
				Sender.sendMessage("Too many arguements, please retry");
				return false;
			} else {
				return false;
			}
		} else if (Cmd.getName().equalsIgnoreCase("removeallprotections")) {
			if (!(Sender instanceof Player)) {
				Sender.sendMessage("You must be a player to use this");
				return true;
			}
			if (!Sender
					.hasPermission("BukkitProtect.Commands.RemoveProtections")) {
				Sender.sendMessage("You do not have permission to use this");
				return true;
			}
			if (Args.length == 0) {
				if (PVP.CommandTimers.containsKey(Sender)) {
					Protections.remove(Sender.getName());
					PVP.updateFakeBlocks((Player) Sender);
					PVP.CommandTimers.put((Player) Sender, -1);
					Sender.sendMessage("Removed all your protections");
					return true;
				} else {
					PVP.CommandTimers.put((Player) Sender, 10);
					Sender.sendMessage("Say the command again within 10 seconds to accept");
					return true;
				}
			} else if (Args.length == 1) {
				Player Target = Bukkit.getPlayer(Args[0]);
				if (Target == null) {
					Sender.sendMessage("Could not find that player");
					return true;
				}
				if (PVP.CommandTimers.containsKey(Sender)) {
					Protections.remove(Target.getName());
					PVP.updateFakeBlocks((Player) Sender);
					PVP.CommandTimers.put((Player) Sender, -1);
					Sender.sendMessage("Removed all of "
							+ Target.getDisplayName() + "'s protections");
					return true;
				} else {
					PVP.CommandTimers.put((Player) Sender, 10);
					Sender.sendMessage("Say the command again within 10 seconds to accept");
					return true;
				}
			} else if (Args.length > 1) {
				Sender.sendMessage("Too many arguements, please retry");
				return false;
			} else {
				return false;
			}
		} else if (Cmd.getName().equalsIgnoreCase("giveland")) {
			if (!(Sender instanceof Player)) {
				Sender.sendMessage("You must be a player to use this");
				return true;
			}
			if (!Sender.hasPermission("BukkitProtect.Commands.AdminLand")) {
				Sender.sendMessage("You do not have permission to use this");
				return true;
			}
			if (Args.length == 2) {
				Player Target = (Bukkit.getPlayer(Args[0]));
				if (Target == null) {
					Sender.sendMessage("Could not find that player");
					return true;
				}
				int Num = 0;
				try {
					Num = Integer.parseInt(Args[1]);
				} catch (Exception e) {
					Sender.sendMessage("The second arguement must be an integer");
					return true;
				}
				if (Num != 0) {
					if (LandOwned.containsKey(Target.getName())) {
						if (LandOwned.get(Target.getName()).intValue() + Num < 0) {
							LandOwned.put(Target.getName(), 0);
							Sender.sendMessage("Set "
									+ Target.getDisplayName()
									+ "'s land to 0 because the integer you specified was more then the land they owned");
						} else {
							LandOwned.put(Target.getName(),
									LandOwned.get(Target.getName()).intValue()
											+ Num);
							Sender.sendMessage("Gave "
									+ Target.getDisplayName() + " " + Num
									+ " land");
						}
					}
				}
			} else if (Args.length > 2) {
				Sender.sendMessage("Too many arguements, please retry");
				return false;
			} else {
				return false;
			}
		} else if (Cmd.getName().equalsIgnoreCase("stuck")) {
			if (!(Sender instanceof Player)) {
				Sender.sendMessage("You must be a player to use this");
				return true;
			}
			if (!Sender.hasPermission("BukkitProtect.Commands.Stuck")) {
				Sender.sendMessage("You do not have permission to use this");
				return true;
			}
			if (Args.length == 0) {
				ProtectionZone Zone = isInsideProtection(((Player) Sender)
						.getLocation());
				if (Zone != null) {
					if (Zone.userHasType(Sender.getName(), UTBuildBlocks)) {
						Sender.sendMessage("You can build or break blocks to leave this protection");
					} else {
						((Player) Sender).teleport(((Player) Sender).getWorld()
								.getHighestBlockAt(Zone.getCorner1())
								.getLocation());
						Sender.sendMessage("Teleported you out of the protection");
					}
				}
			} else if (Args.length > 0) {
				Sender.sendMessage("Too many arguements, please retry");
				return false;
			} else {
				return false;
			}
		} else if (Cmd.getName().equalsIgnoreCase("setland")) {
			if (!(Sender instanceof Player)) {
				Sender.sendMessage("You must be a player to use this");
				return true;
			}
			if (!Sender.hasPermission("BukkitProtect.Commands.AdminLand")) {
				Sender.sendMessage("You do not have permission to use this");
				return true;
			}
			if (Args.length == 2) {
				Player Target = (Bukkit.getPlayer(Args[0]));
				if (Target == null) {
					Sender.sendMessage("Could not find that player");
					return true;
				}
				int Num = 0;
				try {
					Num = Integer.parseInt(Args[1]);
				} catch (Exception e) {
					Sender.sendMessage("The second arguement must be an integer");
					return true;
				}
				if (Num > 0) {
					if (LandOwned.containsKey(Target.getName())) {
						LandOwned.put(Target.getName(), Num);
						Sender.sendMessage("Set " + Target.getDisplayName()
								+ "'s land to " + Num);
					}
				} else {
					Sender.sendMessage("The sencond arguement must be an integer more then 0");
					return true;
				}
			} else if (Args.length > 2) {
				Sender.sendMessage("Too many arguements, please retry");
				return false;
			} else {
				return false;
			}
		} else if (Cmd.getName().equalsIgnoreCase("getland")) {
			if (!(Sender instanceof Player)) {
				Sender.sendMessage("You must be a player to use this");
				return true;
			}
			if (Args.length == 1
					&& Sender.hasPermission("BukkitProtect.Commands.AdminLand")) {
				Player Target = (Bukkit.getPlayer(Args[0]));
				if (Target == null) {
					Sender.sendMessage("Could not find that player");
					return true;
				}
				if (LandOwned.containsKey(Target.getName())) {
					if (Protections.containsKey(Target.getName())) {
						Sender.sendMessage(Target.getName() + " has "
								+ LandOwned.get(Target.getName())
								+ " land, of which " + getTotalLandUsed(Target)
								+ " is used by a total of "
								+ Protections.get(Target.getName()).size()
								+ " protections.");
					} else {
						Sender.sendMessage(Target.getName()
								+ " has "
								+ LandOwned.get(Target.getName())
								+ " land, of which 0 is used by a total of 0 protections.");
					}
				}
			} else if (Args.length == 0
					&& Sender.hasPermission("BukkitProtect.Commands.GetLand")) {
				if (LandOwned.containsKey(Sender.getName())) {
					if (Protections.containsKey(Sender.getName())) {
						Sender.sendMessage("You have "
								+ LandOwned.get(Sender.getName())
								+ " land, of which "
								+ getTotalLandUsed((Player) Sender)
								+ " is used by a total of "
								+ Protections.get(Sender.getName()).size()
								+ " protections.");
					} else {
						Sender.sendMessage("You have "
								+ LandOwned.get(Sender.getName())
								+ " land, of which 0 is used by a total of 0 protections.");
					}
				}
			} else if (Args.length == 0 || Args.length == 1) {
				Sender.sendMessage("You do not have permission to use this");
				return true;
			} else if (Args.length > 1) {
				Sender.sendMessage("Too many arguements, please retry");
				return false;
			} else {
				return false;
			}
		}
		return true;
	}

	public int getTotalLandUsed(Player Plr) {
		if (Protections.containsKey(Plr.getName())) {
			ArrayList<ProtectionZone> Zones = Protections.get(Plr.getName());
			int Total = 0;
			for (ProtectionZone Zone : Zones) {
				if (!Zone.hasTag("ServerOwned")) {
					Total = Total + Zone.getSize();
				}
			}
			return Total;
		} else {
			return 0;
		}
	}

	public ProtectionZone isInsideBiggestProtection(Location Loc) {
		if (Protections.isEmpty())
			return null;
		ProtectionZone Zone = null;
		for (ArrayList<ProtectionZone> Zones : Protections.values()) {
			for (int i = 0; i < Zones.size(); i++) {
				if (Zone == null) {
					if (Util.isInside(Loc, Zones.get(i).getCorner1(), Zones
							.get(i).getCorner2()))
						Zone = Zones.get(i);
				} else {
					if (Zone.getSize() < Zones.get(i).getSize()) {
						if (Util.isInside(Loc, Zones.get(i).getCorner1(), Zones
								.get(i).getCorner2()))
							Zone = Zones.get(i);
					} else {
						if (!Util.isInside(Loc, Zone.getCorner1(),
								Zone.getCorner2()))
							Zone = Zones.get(i);
					}
				}
			}
		}
		return Zone;
	}

	public ProtectionZone isInsideProtection(Location Loc) {
		if (Protections.isEmpty())
			return null;
		ProtectionZone Zone = null;
		for (ArrayList<ProtectionZone> Zones : Protections.values()) {
			for (ProtectionZone ProtZone : Zones) {
				if (Zone == null) {
					if (Util.isInside(Loc, ProtZone.getCorner1(),
							ProtZone.getCorner2()))
						Zone = ProtZone;
				} else {
					if (Zone.getSize() > ProtZone.getSize()) {
						if (Util.isInsideY(Loc, ProtZone.getCorner1(),
								ProtZone.getCorner2()))
							Zone = ProtZone;
					} else {
						if (Util.isInside(Loc, ProtZone.getCorner1(),
								ProtZone.getCorner2())
								&& !Util.isInsideY(Loc, Zone.getCorner1(),
										Zone.getCorner2())) {
							Zone = ProtZone;
						}
					}
				}
			}
		}
		return Zone;
	}

	public void CornerRod(Player Plr, Location blockLoc, ItemStack Rod) {
		Block Left = Util.GetLowestBlock(blockLoc.clone().add(1, 0, 0));
		Block Right = Util.GetLowestBlock(blockLoc.clone().add(-1, 0, 0));
		Block Center = Util.GetLowestBlock(blockLoc.clone());
		Block Forward = Util.GetLowestBlock(blockLoc.clone().add(0, 0, 1));
		Block Backward = Util.GetLowestBlock(blockLoc.clone().add(0, 0, -1));
		Plr.sendBlockChange(Left.getLocation(), Material.GLASS, (byte) 0);
		Plr.sendBlockChange(Right.getLocation(), Material.GLASS, (byte) 0);
		Plr.sendBlockChange(Center.getLocation(), Material.BEACON, (byte) 0);
		Plr.sendBlockChange(Forward.getLocation(), Material.GLASS, (byte) 0);
		Plr.sendBlockChange(Backward.getLocation(), Material.GLASS, (byte) 0);
		Location EffectsLoc = Center.getLocation().add(0, 1, 0);
		ItemMeta RodMeta = Rod.getItemMeta();
		boolean infinite = false;
		if (RodMeta.getLore().get(1).split("/").length == 2) {
			String[] Nums = RodMeta.getLore().get(1).replaceAll(" ", "")
					.split("/");
			int Num1 = Integer.parseInt(Nums[0]);
			if (Num1 > 1) {
				Num1 = Num1 - 1;
				ArrayList<String> Lore = new ArrayList<String>();
				Lore.add(RodMeta.getLore().get(0));
				Lore.add(Num1 + " / " + Nums[1]);
				RodMeta.setLore(Lore);
				Rod.setItemMeta(RodMeta);
				Plr.playEffect(EffectsLoc, Effect.ENDER_SIGNAL, 1);
				Plr.playSound(EffectsLoc, Sound.ENDERMAN_TELEPORT, 1, 1);
			} else if (Num1 == -1) {
				Plr.playEffect(EffectsLoc, Effect.ENDER_SIGNAL, 1);
				Plr.playSound(EffectsLoc, Sound.ENDERMAN_TELEPORT, 1, 1);
				infinite = true;
			} else {
				Plr.sendMessage("Your " + Rod.getItemMeta().getDisplayName()
						+ " has broken!");
				Plr.getInventory().setItemInHand(new ItemStack(Material.STICK));
				Plr.playEffect(EffectsLoc, Effect.ENDER_SIGNAL, 1);
				Plr.getWorld().playSound(Plr.getLocation(), Sound.ITEM_BREAK,
						1, 1);
			}
		}
		Map<Block, Integer> Blocks = PVP.UpdateBlock.get(Plr);
		if (Blocks == null)
			Blocks = new HashMap<Block, Integer>();
		Blocks.put(Left, 60);
		Blocks.put(Right, 60);
		Blocks.put(Center, 60);
		Blocks.put(Forward, 60);
		Blocks.put(Backward, 60);
		PVP.UpdateBlock.put(Plr, Blocks);
		Map<Location, Integer> Selections = PVP.PlayerSelection.get(Plr);
		if (Selections == null)
			Selections = new HashMap<Location, Integer>();
		Selections.put(Center.getLocation(), 60);
		if (Selections.size() >= 3) {
			Selections.remove(Selections.keySet().toArray()[0]);
		} else if (Selections.size() == 2) {
			Location Sel1 = null;
			Location Sel2 = null;
			if (((Location) Selections.keySet().toArray()[0]).equals(Center
					.getLocation())) {
				Sel1 = (Location) Selections.keySet().toArray()[1];
				Sel2 = (Location) Selections.keySet().toArray()[0];
			} else {
				Sel1 = (Location) Selections.keySet().toArray()[0];
				Sel2 = (Location) Selections.keySet().toArray()[1];
			}
			ProtectionZone newProt = new ProtectionZone(Sel1, Sel2,
					Plr.getName());
			boolean resizeZone = false;
			if (Protections.containsKey(Plr.getName())) {
				for (ProtectionZone Zone : Protections.get(Plr.getName())) {
					for (Location loc : GetCorners(Zone)) {
						if (Util.isInsideY(loc, Sel1, Sel1)) {
							newProt = Zone.Clone();
							if (loc == newProt.getCorner1()) {
								newProt.setCorner1(Sel2);
								resizeZone = true;
							} else if (loc == newProt.getCorner2()) {
								newProt.setCorner2(Sel2);
								resizeZone = true;
							} else if (loc.getBlockX() == newProt.getCorner1()
									.getBlockX()
									&& loc.getBlockZ() == newProt.getCorner2()
											.getBlockZ()) {
								Location newLoc = newProt.getCorner2();
								newLoc.setZ(newProt.getCorner1().getZ());
								newProt.setCorner1(newLoc);
								newProt.setCorner2(Sel2);
								resizeZone = true;
							} else if (loc.getBlockX() == newProt.getCorner2()
									.getBlockX()
									&& loc.getBlockZ() == newProt.getCorner1()
											.getBlockZ()) {
								Location newLoc = newProt.getCorner1();
								newLoc.setZ(newProt.getCorner2().getZ());
								newProt.setCorner1(newLoc);
								newProt.setCorner2(Sel2);
								resizeZone = true;
							}
						}
					}
				}
			}
			int Length = newProt.getLength();
			int Width = newProt.getWidth();
			if (Length >= getConfig().getInt("MinimumZoneSize")
					&& Width >= getConfig().getInt("MinimumZoneSize")) {
				ArrayList<ProtectionZone> Intersecting = new ArrayList<ProtectionZone>();
				for (ArrayList<ProtectionZone> Zones : Protections.values()) {
					for (ProtectionZone Zone : Zones) {
						if (Util.zonesIntersect(newProt, Zone)) {
							if (!Zone.userHasAdminType(Plr.getName())) {
								Intersecting.add(Zone);
							} else {
								ProtectionZone InsideZone = isInsideBiggestProtection(newProt
										.getCorner1());
								if (InsideZone == Zone) {
									if (!Util.isInside(newProt.getCorner1(),
											Zone.getCorner1(),
											Zone.getCorner2())
											|| !Util.isInside(
													newProt.getCorner2(),
													Zone.getCorner1(),
													Zone.getCorner2()))
										Intersecting.add(Zone);
								} else if (Util.zonesIntersectY(newProt, Zone)) {
									if (!Util.isInsideY(newProt.getCorner1(),
											Zone.getCorner1(),
											Zone.getCorner2())
											|| !Util.isInsideY(
													newProt.getCorner2(),
													Zone.getCorner1(),
													Zone.getCorner2()))
										Intersecting.add(Zone);
								}
							}
						}
					}
				}
				if (!Intersecting.isEmpty() && Intersecting.size() == 1
						&& resizeZone) {
					if (Intersecting.get(0) == ((ProtectionZone) PVP.PlayerSelectedZone
							.get(Plr).keySet().toArray()[0])) {
						Intersecting.remove(0);
					}
				}
				if (Intersecting.isEmpty()) {
					if (getConfig().getBoolean("BuyableLand") && !infinite) {
						if (LandOwned.containsKey(Plr.getName())) {
							int oldLength = 0;
							int oldWidth = 0;
							if (resizeZone) {
								oldLength = Math
										.abs(((ProtectionZone) PVP.PlayerSelectedZone
												.get(Plr).keySet().toArray()[0])
												.getCorner1().getBlockX()
												- ((ProtectionZone) PVP.PlayerSelectedZone
														.get(Plr).keySet()
														.toArray()[0])
														.getCorner2()
														.getBlockX());
								oldWidth = Math
										.abs(((ProtectionZone) PVP.PlayerSelectedZone
												.get(Plr).keySet().toArray()[0])
												.getCorner1().getBlockZ()
												- ((ProtectionZone) PVP.PlayerSelectedZone
														.get(Plr).keySet()
														.toArray()[0])
														.getCorner2()
														.getBlockZ());
							}
							if ((LandOwned.get(Plr.getName())
									- getTotalLandUsed(Plr) + (oldLength * oldWidth)) < (Length * Width)) {
								Plr.sendMessage("You need "
										+ ((Length * Width) - (LandOwned
												.get(Plr.getName())
												- getTotalLandUsed(Plr) + (oldLength * oldWidth)))
										+ " more blocks of land");
								PVP.PlayerSelection.remove(Plr);
								PVP.updateFakeBlocks(Plr);
								return;
							}
						}
					}
					if (resizeZone) {
						Protections.get(Plr.getName()).remove(
								(PVP.PlayerSelectedZone.get(Plr).keySet()
										.toArray()[0]));
					}
					ArrayList<ProtectionZone> Prots = Protections.get(Plr
							.getName());
					if (Prots == null)
						Prots = new ArrayList<ProtectionZone>();
					if (infinite)
						newProt.addTags("ServerOwned");
					Prots.add(newProt);
					Protections.put(Plr.getName(), Prots);
					PVP.PlayerSelection.remove(Plr);
					PVP.updateFakeBlocks(Plr);
					return;
				} else {
					for (ProtectionZone Zone : Intersecting) {
						DisplayProtection(Plr, Zone.getCorner1());
					}
					Plr.sendMessage("The protection intersects another protection");
				}
			} else {
				Plr.sendMessage("The protection must be atleast "
						+ getConfig().getInt("MinimumZoneSize") + " x "
						+ +getConfig().getInt("MinimumZoneSize"));
				PVP.PlayerSelection.remove(Plr);
				PVP.updateFakeBlocks(Plr);
				return;
			}
		}
		PVP.PlayerSelection.put(Plr, Selections);
	}

	public ArrayList<Location> GetCorners(ProtectionZone Zone) {
		Location Corner1 = Zone.getCorner1();
		Location Corner2 = Zone.getCorner2();
		Location Corner3 = Corner1.clone();
		Corner3.setZ(Corner2.getZ());
		Location Corner4 = Corner1.clone();
		Corner4.setX(Corner2.getX());

		ArrayList<Location> Corners = new ArrayList<Location>();

		if (Corner1.getBlockX() < Corner2.getBlockX()
				&& Corner1.getBlockZ() < Corner2.getBlockZ()) {
			Corners.add(Corner1);
			if (Corner3.getBlockX() < Corner4.getBlockX()) {
				Corners.add(Corner3);
				Corners.add(Corner2);
				Corners.add(Corner4);
			} else {
				Corners.add(Corner4);
				Corners.add(Corner2);
				Corners.add(Corner3);
			}
		} else if (Corner2.getBlockX() < Corner1.getBlockX()
				&& Corner2.getBlockZ() < Corner1.getBlockZ()) {
			Corners.add(Corner2);
			if (Corner3.getBlockX() < Corner4.getBlockX()) {
				Corners.add(Corner3);
				Corners.add(Corner1);
				Corners.add(Corner4);
			} else {
				Corners.add(Corner4);
				Corners.add(Corner1);
				Corners.add(Corner3);
			}
		} else if (Corner3.getBlockX() < Corner4.getBlockX()
				&& Corner3.getBlockZ() < Corner4.getBlockZ()) {
			Corners.add(Corner3);
			if (Corner1.getBlockX() < Corner2.getBlockX()) {
				Corners.add(Corner1);
				Corners.add(Corner4);
				Corners.add(Corner2);
			} else {
				Corners.add(Corner2);
				Corners.add(Corner4);
				Corners.add(Corner1);
			}
		} else {
			Corners.add(Corner4);
			if (Corner1.getBlockX() < Corner2.getBlockX()) {
				Corners.add(Corner1);
				Corners.add(Corner3);
				Corners.add(Corner2);
			} else {
				Corners.add(Corner2);
				Corners.add(Corner3);
				Corners.add(Corner1);
			}
		}

		return Corners;
	}

	public void DisplayProtection(Player Plr, Location blockLoc) {
		ProtectionZone Zone = isInsideProtection(blockLoc);
		if (Zone == null)
			return;

		Material Type = Material.REDSTONE_BLOCK;
		int priority = 0;
		boolean Admin = false;

		for (UserType UType : Types) {
			if (Zone.userHasType(Plr.getName(), UType)) {
				if (UType.getPriority() > priority) {
					if (UType.getDisplay() != null) {
						Type = UType.getDisplay();
						priority = UType.getPriority();
					}
				}
				if (UType.isAdmin()) {
					Admin = true;
				}
			}
		}
		if (Admin) {
			Map<ProtectionZone, Integer> SelZone = new HashMap<ProtectionZone, Integer>();
			SelZone.put(Zone, 60);
			PVP.PlayerSelectedZone.put(Plr, SelZone);
		}
		if (Plr.hasPermission("BukkitProtect.Protection.SelectOthers")) {
			Map<ProtectionZone, Integer> SelZone = new HashMap<ProtectionZone, Integer>();
			SelZone.put(Zone, 60);
			PVP.PlayerSelectedZone.put(Plr, SelZone);
		}

		Location LocUseL;
		Location LocUseR;
		Location LocUseC;

		ArrayList<Location> Corners = GetCorners(Zone);

		Map<Block, Integer> Blocks = PVP.UpdateBlock.get(Plr);
		if (Blocks == null) {
			Blocks = new HashMap<Block, Integer>();
		}

		PVP.updateFakeBlocks(Plr);

		for (int i = 0; i < Corners.size(); i++) {

			LocUseC = Corners.get(i);
			if (i == 0) {
				LocUseR = Corners.get(i).clone().add(1, 0, 0);
				LocUseL = Corners.get(i).clone().add(0, 0, 1);
			} else if (i == 1) {
				LocUseR = Corners.get(i).clone().add(1, 0, 0);
				LocUseL = Corners.get(i).clone().add(0, 0, -1);
			} else if (i == 2) {
				LocUseR = Corners.get(i).clone().add(-1, 0, 0);
				LocUseL = Corners.get(i).clone().add(0, 0, -1);
			} else {
				LocUseR = Corners.get(i).clone().add(-1, 0, 0);
				LocUseL = Corners.get(i).clone().add(0, 0, 1);
			}

			Block Left = Util
					.GetLowestBlockRelative(LocUseL, Plr.getLocation());
			Block Right = Util.GetLowestBlockRelative(LocUseR,
					Plr.getLocation());
			Block Center = Util.GetLowestBlockRelative(LocUseC,
					Plr.getLocation());

			Plr.sendBlockChange(Left.getLocation(), Type, (byte) 0);
			Plr.sendBlockChange(Right.getLocation(), Type, (byte) 0);
			Plr.sendBlockChange(Center.getLocation(), Type, (byte) 0);

			Blocks.put(Left, 60);
			Blocks.put(Right, 60);
			Blocks.put(Center, 60);
		}

		PVP.UpdateBlock.put(Plr, Blocks);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void WorldSave(WorldSaveEvent Event) {

		try {
			save(LandOwned, LandPath);
		} catch (Exception e) {
			try {
				save(LandOwned, LandPath);
			} catch (Exception e1) {
			}
		}

		Map<String, String> StringProtections = new HashMap<String, String>();
		if (!Protections.isEmpty())
			for (String Player : Protections.keySet()) {
				String Total = "";
				for (ProtectionZone Zone : Protections.get(Player)) {
					Total = Total + " / " + Zone.toString();
				}
				StringProtections.put(Player, Total);
			}
		try {
			save(StringProtections, ProtectionPath);
		} catch (Exception e) {
			try {
				save(StringProtections, ProtectionPath);
			} catch (Exception e1) {
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void CreatePortal(EntityCreatePortalEvent Event) {
		if (Event.getEntity() == null)
			return;
		if (!(Event.getEntity() instanceof Player))
			return;
		if (!getConfig().getBoolean("PlayersCanCreatePortals")) {
			Event.setCancelled(true);
			return;
		}
		ArrayList<ProtectionZone> ProtectionZones = new ArrayList<ProtectionZone>();
		for (BlockState block : Event.getBlocks()) {
			if (isInsideProtection(block.getLocation()) != null)
				ProtectionZones.add(isInsideProtection(block.getLocation()));
		}
		if (ProtectionZones.isEmpty())
			return;
		boolean canUseBlock = true;
		for (ProtectionZone Zone : ProtectionZones) {
			if (!Zone.userHasType(((Player) Event.getEntity()).getName(),
					UTBuildBlocks))
				canUseBlock = false;
		}
		if (!canUseBlock)
			Event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void PlayerJoin(PlayerLoginEvent Event) {
		if (Event.getPlayer() == null)
			return;
		if (getConfig().getBoolean("BuyableLand")) {
			if (!LandOwned.containsKey(Event.getPlayer().getName())) {
				LandOwned.put(Event.getPlayer().getName(),
						getConfig().getInt("StartLand"));
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void PistonRetract(BlockPistonRetractEvent Event) {
		if (Event.getBlock() == null)
			return;
		if (Event.getBlock().getType() == Material.PISTON_BASE
				|| Event.getBlock().getWorld()
						.getBlockAt(Event.getRetractLocation()).getType() == Material.AIR)
			return;
		ProtectionZone Zone = isInsideProtection(Event.getBlock().getLocation());
		ProtectionZone Zone2 = isInsideProtection(Event.getRetractLocation());
		if (Zone == null && Zone2 == null)
			return;
		if (Zone == Zone2)
			return;
		if (Zone != null && Zone2 == null)
			return;
		if (Zone != null && Zone2 != null) {
			if (Zone2.userHasType(Zone.getOwner(), UTBuildBlocks))
				return;
		}
		Event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void StructureGrow(StructureGrowEvent Event) {
		if (Event.getBlocks().isEmpty())
			return;
		if (Event.getPlayer() == null)
			return;
		ArrayList<ProtectionZone> Zones = new ArrayList<ProtectionZone>();
		for (BlockState block : Event.getBlocks()) {
			ProtectionZone Zone = isInsideProtection(block.getLocation());
			if (Zone != null)
				Zones.add(Zone);
		}
		boolean insideZone = false;
		for (ProtectionZone Zone : Zones) {
			if (!Zone.getOwner().equalsIgnoreCase(Event.getPlayer().getName())) {
				insideZone = true;
			} else if (!Zone.userHasType(Event.getPlayer().getName(),
					UTBuildBlocks)) {
				insideZone = true;
			}
		}
		if (insideZone)
			Event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void PistonExtend(BlockPistonExtendEvent Event) {
		if (Event.getBlock() == null)
			return;
		if (Event.getBlocks().isEmpty())
			return;
		List<Block> Blocks = new ArrayList<Block>();
		Blocks.addAll(Event.getBlocks());
		Blocks.add(Event.getBlock());
		for (Block block : Blocks) {
			boolean insideZone = false;
			ProtectionZone Zone = isInsideProtection(block.getLocation());
			ProtectionZone Zone2 = isInsideProtection(block.getRelative(
					Event.getDirection()).getLocation());
			if (Zone == null && Zone2 == null)
				insideZone = true;
			if (Zone == Zone2)
				insideZone = true;
			if (Zone != null && Zone2 == null)
				insideZone = true;
			if (Zone != null && Zone2 != null) {
				if (Zone2.userHasType(Zone.getOwner(), UTBuildBlocks))
					insideZone = true;
			}
			if (!insideZone)
				Event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void BlockFromTo(BlockFromToEvent Event) {
		if (Event.getBlock() == null)
			return;
		ProtectionZone Protection = isInsideProtection(Event.getBlock()
				.getLocation());
		ProtectionZone Protection2 = isInsideProtection(Event.getToBlock()
				.getLocation());
		if (Protection == null && Protection2 == null)
			return;
		if (Protection == Protection2)
			return;
		if (Protection != null && Protection2 == null)
			return;
		if (Protection != null && Protection2 != null) {
			if (Protection2.userHasType(Protection.getOwner(), UTBuildBlocks))
				return;
		}
		Event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void BlockForm(BlockFormEvent Event) {
		if (Event.getBlock() == null)
			return;
		ProtectionZone Protection = isInsideProtection(Event.getBlock()
				.getLocation());
		if (Protection == null)
			return;
		if (Protection.hasTag("PreventIceMeltForm")
				&& Event.getNewState().getType() == Material.ICE) {
			Event.setCancelled(true);
		}
		if (Protection.hasTag("PreventSnowMeltForm")
				&& Event.getNewState().getType() == Material.SNOW) {
			Event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void BlockFade(BlockFadeEvent Event) {
		if (Event.getBlock() == null)
			return;
		ProtectionZone Protection = isInsideProtection(Event.getBlock()
				.getLocation());
		if (Protection == null)
			return;
		if (Protection.hasTag("PreventIceMeltForm")
				&& Event.getBlock().getType() == Material.ICE) {
			Event.setCancelled(true);
		}
		if (Protection.hasTag("PreventSnowMeltForm")
				&& Event.getBlock().getType() == Material.SNOW) {
			Event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void HoldItem(PlayerItemHeldEvent Event) {
		if (Event.getPlayer() == null)
			return;
		if (Event.getPlayer().getInventory().getItem(Event.getNewSlot()) != null)
			if (RodTypes.contains(Event.getPlayer().getInventory()
					.getItem(Event.getNewSlot()).getType())) {
				if (Event.getPlayer().getInventory()
						.getItem(Event.getNewSlot()).getItemMeta().getLore() != null
						&& Event.getPlayer().getInventory()
								.getItem(Event.getNewSlot()).getItemMeta()
								.getLore().get(0).equals("Protect your land")) {
					Event.getPlayer()
							.sendMessage(
									"You have "
											+ (LandOwned.get(Event.getPlayer()
													.getName()) - getTotalLandUsed(Event
													.getPlayer()))
											+ " blocks of land");
				}
			}
		if (PVP.PlayerSelection.containsKey(Event.getPlayer())) {
			PVP.PlayerSelection.remove(Event.getPlayer());
			PVP.updateFakeBlocks(Event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void InteractPlayer(final PlayerInteractEvent Event) {
		if (Event.getPlayer() == null)
			return;
		if (Event.getAction() == Action.LEFT_CLICK_AIR
				|| Event.getAction() == Action.RIGHT_CLICK_AIR)
			return;
		if (Event.getClickedBlock().getType() == Material.SOIL
				&& Event.getAction() == Action.PHYSICAL) {
			if (!getConfig().getBoolean("PlayersTrampleCrops")) {
				Event.setCancelled(true);
			}
		}
		ProtectionZone Protection = isInsideProtection(Event.getClickedBlock()
				.getLocation());
		UserType requiredPerm = UTUseBlocks;
		if (Event.getClickedBlock() != null) {
			if (Event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (Event.getClickedBlock().getState().getData() instanceof Openable) {
					requiredPerm = UTAccess;
				} else if (Event.getClickedBlock().getState().getData() instanceof Lever) {
					Block Attached = Event.getClickedBlock().getRelative(
							((Lever) Event.getClickedBlock().getState()
									.getData()).getAttachedFace());
					if (Util.poweringDoor(Event.getClickedBlock())
							|| Util.poweringDoor(Attached))
						requiredPerm = UTAccess;
				} else if (Event.getClickedBlock().getState().getData() instanceof Button) {
					Block Attached = Event.getClickedBlock().getRelative(
							((Button) Event.getClickedBlock().getState()
									.getData()).getAttachedFace());
					if (Util.poweringDoor(Event.getClickedBlock())
							|| Util.poweringDoor(Attached))
						requiredPerm = UTAccess;
				}
			} else if (Event.getAction() == Action.PHYSICAL) {
				if (Event.getClickedBlock().getState().getData() instanceof PressurePlate) {
					if (Util.poweringDoor(Event.getClickedBlock())
							|| Util.poweringDoor(Event.getClickedBlock()
									.getRelative(BlockFace.DOWN)))
						requiredPerm = UTAccess;
				}
			}
		}
		if (Protection == null
				|| Protection.userHasType(Event.getPlayer().getName(),
						UTUseBlocks)
				|| Protection.userHasType(Event.getPlayer().getName(),
						requiredPerm)) {
			if (Event.getAction() == Action.RIGHT_CLICK_BLOCK)
				if (Event.getPlayer().getItemInHand().getType() != Material.AIR
						&& Event.getPlayer().isSneaking()) {

				} else if (Event.getClickedBlock() != null) {
					if (getConfig().getBoolean("RightClickIronDoor")) {
						if (Event.getClickedBlock().getType() == Material.IRON_DOOR_BLOCK) {
							if (Event.getClickedBlock()
									.getRelative(BlockFace.DOWN).getType() == Material.IRON_DOOR_BLOCK) {
								if (Event.getClickedBlock()
										.getRelative(BlockFace.DOWN).getData() <= 3) {
									Event.getClickedBlock()
											.getRelative(BlockFace.DOWN)
											.setData(
													(byte) (Event
															.getClickedBlock()
															.getRelative(
																	BlockFace.DOWN)
															.getData() + 4));
								} else {
									Event.getClickedBlock()
											.getRelative(BlockFace.DOWN)
											.setData(
													(byte) (Event
															.getClickedBlock()
															.getRelative(
																	BlockFace.DOWN)
															.getData() - 4));
								}
								Event.getClickedBlock()
										.getWorld()
										.playEffect(
												Event.getClickedBlock()
														.getLocation(),
												Effect.DOOR_TOGGLE, 0);
							} else {
								if (Event.getClickedBlock().getData() <= 3) {
									Event.getClickedBlock().setData(
											(byte) (Event.getClickedBlock()
													.getData() + 4));
								} else {
									Event.getClickedBlock().setData(
											(byte) (Event.getClickedBlock()
													.getData() - 4));
								}
								Event.getClickedBlock()
										.getWorld()
										.playEffect(
												Event.getClickedBlock()
														.getLocation(),
												Effect.DOOR_TOGGLE, 0);
							}
						}
					}
				}
			if (Event.hasBlock()) {
				if (Event.getItem() != null) {
					if (Event.getAction() == Action.RIGHT_CLICK_BLOCK
							&& Event.getPlayer().hasPermission(
									"MakeProtections")) {
						Bukkit.getServer().getScheduler()
								.scheduleSyncDelayedTask(this, new Runnable() {
									@Override
									public void run() {
										ItemStack Rod = Event.getPlayer()
												.getInventory().getItemInHand();
										if (RodTypes.contains(Rod.getType())) {
											if (Rod.getItemMeta().getLore() != null
													&& Rod.getItemMeta()
															.getLore()
															.get(0)
															.equals("Protect your land")) {
												if (Event.getPlayer()
														.isSneaking()) {
													if (Rod.getAmount() > 1) {
														Event.getPlayer()
																.sendMessage(
																		"You must not have more then one "
																				+ Rod.getItemMeta()
																						.getDisplayName()
																				+ " in a stack");
														return;
													}
													CornerRod(
															Event.getPlayer(),
															Event.getClickedBlock()
																	.getLocation(),
															Rod);
												}
											} else {
												DisplayProtection(Event
														.getPlayer(), Event
														.getClickedBlock()
														.getLocation());
											}
										}
									}
								}, 2);
					}
				}
			}
		} else if (!Protection.userHasType(Event.getPlayer().getName(),
				requiredPerm)) {
			Event.setUseItemInHand(Result.ALLOW);
			if (Event.hasBlock()) {
				if (Event.getItem() != null) {
					Bukkit.getServer().getScheduler()
							.scheduleSyncDelayedTask(this, new Runnable() {
								@Override
								public void run() {
									ItemStack Rod = Event.getPlayer()
											.getInventory().getItemInHand();
									if (RodTypes.contains(Rod.getType())) {
										if (Rod.getItemMeta().getLore() == null) {
											DisplayProtection(
													Event.getPlayer(), Event
															.getClickedBlock()
															.getLocation());
										}
									}
								}
							}, 2);
					if (Event.getAction() == Action.RIGHT_CLICK_BLOCK) {
						if (Event.getItem().getType().isRecord())
							Event.setUseItemInHand(Result.DENY);
						if (Event.getItem().getType() == Material.EYE_OF_ENDER)
							Event.setUseItemInHand(Result.DENY);
						if (Event.getItem().getType() == Material.FIREBALL)
							Event.setUseItemInHand(Result.DENY);
						if (Event.getItem()
								.isSimilar(
										new ItemStack(Material.INK_SACK, 1,
												(short) 15)))
							Event.setUseItemInHand(Result.DENY);
					}
				}
			}
			Event.setUseInteractedBlock(Result.DENY);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void CreatureSpawn(CreatureSpawnEvent Event) {
		if (Event.getEntity() == null)
			return;
		if (Event.getEntity() instanceof Player)
			return;
		ProtectionZone Protection = isInsideProtection(Event.getLocation());
		if (Protection != null) {
			if (Protection.hasTag("PreventEntitySpawn")) {
				Event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void BlockBurn(BlockBurnEvent Event) {
		if (Event.getBlock() == null)
			return;
		if (!getConfig().getBoolean("FireBurn"))
			Event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void BlockIgnite(BlockIgniteEvent Event) {
		if (Event.getBlock() == null)
			return;
		if (!getConfig().getBoolean("FireSpread"))
			if (Event.getCause() == IgniteCause.SPREAD) {
				Event.setCancelled(true);
			}
		ProtectionZone Protection = isInsideProtection(Event.getBlock()
				.getLocation());
		if (Protection != null)
			if (Protection.hasTag("PreventFireSpread")) {
				if (Event.getCause() == IgniteCause.SPREAD) {
					Event.setCancelled(true);
				}
			}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void HangingPlace(HangingPlaceEvent Event) {
		if (Event.getPlayer() == null)
			return;
		ProtectionZone Protection = isInsideProtection(Event.getBlock()
				.getLocation());
		if (Protection == null
				|| Protection.userHasType(Event.getPlayer().getName(),
						UTBuildBlocks))
			return;
		Event.setCancelled(true);
		Event.getPlayer().updateInventory();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void BlockBreak(BlockBreakEvent Event) {
		if (Event.getPlayer() == null)
			return;
		ProtectionZone Protection = isInsideProtection(Event.getBlock()
				.getLocation());
		if (Protection == null
				|| Protection.userHasType(Event.getPlayer().getName(),
						UTBuildBlocks))
			return;
		Event.setCancelled(true);
		Event.getPlayer().updateInventory();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void BlockPlace(BlockPlaceEvent Event) {
		if (Event.getPlayer() == null)
			return;
		ProtectionZone Protection = isInsideProtection(Event.getBlock()
				.getLocation());
		if (Protection == null
				|| Protection.userHasType(Event.getPlayer().getName(),
						UTBuildBlocks)) {
			if (getConfig().getBoolean("ProtectChests"))
				if (Event.getBlock().getType() == Material.CHEST) {
					if (!Protections.containsKey(Event.getPlayer().getName())
							|| Protections.get(Event.getPlayer().getName())
									.isEmpty()) {
						ProtectionZone newProt = new ProtectionZone(Event
								.getBlock().getLocation().clone().add(5, 0, 5),
								Event.getBlock().getLocation().clone()
										.add(-5, 0, -5), Event.getPlayer()
										.getName());
						boolean Intersecting = false;
						for (ArrayList<ProtectionZone> Zones : Protections
								.values()) {
							for (ProtectionZone Zone : Zones) {
								if (Util.zonesIntersect(newProt, Zone)) {
									Intersecting = true;
								}
							}
						}
						if (!Intersecting) {
							ArrayList<ProtectionZone> Prots = new ArrayList<ProtectionZone>();
							Prots.add(newProt);
							Protections.put(Event.getPlayer().getName(), Prots);
							PVP.PlayerSelection.remove(Event.getPlayer());
							PVP.updateFakeBlocks(Event.getPlayer());
						}
					}
				}
			return;
		}
		Event.setCancelled(true);
		Event.getPlayer().updateInventory();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void InteractEntity(PlayerInteractEntityEvent Event) {
		if (Event.getPlayer() == null)
			return;
		if (Event.getRightClicked() instanceof Tameable) {
			if (((Tameable) Event.getRightClicked()).getOwner() == null
					|| ((Tameable) Event.getRightClicked()).getOwner() == Event
							.getPlayer())
				return;
		} else {
			ProtectionZone Protection = isInsideProtection(Event
					.getRightClicked().getLocation());
			if (Protection == null
					|| Protection.userHasType(Event.getPlayer().getName(),
							UTEntities))
				return;
		}
		Event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void DamageVehicle(VehicleDamageEvent Event) {
		if (Event.getVehicle() == null)
			return;
		if (Event.getAttacker() instanceof Player) {
			ProtectionZone Protection = isInsideProtection(Event.getVehicle()
					.getLocation());
			if (Protection == null
					|| Protection.userHasType(
							((Player) Event.getAttacker()).getName(),
							UTEntities))
				return;
		}
		Event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void BreakVehicle(VehicleDestroyEvent Event) {
		if (Event.getVehicle() == null)
			return;
		if (Event.getAttacker() instanceof Player) {
			ProtectionZone Protection = isInsideProtection(Event.getVehicle()
					.getLocation());
			if (Protection == null
					|| Protection.userHasType(
							((Player) Event.getAttacker()).getName(),
							UTEntities))
				return;
		}
		Event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void PlayerBucketFill(PlayerBucketFillEvent Event) {
		if (Event.getPlayer() == null)
			return;
		ProtectionZone Protection = isInsideProtection(Event.getBlockClicked()
				.getLocation());
		if (Protection == null
				|| Protection.userHasType(Event.getPlayer().getName(),
						UTBuildBlocks))
			return;
		Event.setCancelled(true);
		Event.getPlayer().updateInventory();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void PlayerBucketEmpty(PlayerBucketEmptyEvent Event) {
		if (Event.getPlayer() == null)
			return;
		ProtectionZone Protection = isInsideProtection(Event.getBlockClicked()
				.getLocation());
		if (Protection == null
				|| Protection.userHasType(Event.getPlayer().getName(),
						UTBuildBlocks))
			return;
		Event.setCancelled(true);
		Event.getPlayer().updateInventory();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void BreakHanging(HangingBreakByEntityEvent Event) {
		if (Event.getEntity() == null)
			return;
		if (Event.getRemover() instanceof Player) {
			ProtectionZone Protection = isInsideProtection(Event.getEntity()
					.getLocation());
			if (Protection == null
					|| Protection.userHasType(
							((Player) Event.getRemover()).getName(),
							UTBuildBlocks))
				return;
		}
		Event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void DamageEntity(EntityDamageEvent Event) {
		if (Event.getEntity() == null)
			return;
		if (Event.getEntity() instanceof Monster)
			return;
		if (Event.getCause() == DamageCause.ENTITY_EXPLOSION
				|| Event.getCause() == DamageCause.BLOCK_EXPLOSION) {
			if (!getConfig().getBoolean("TNTDamageEntity")
					&& Event.getEntityType() == EntityType.PRIMED_TNT)
				Event.setCancelled(true);
			if (!getConfig().getBoolean("TNTCartDamageEntity")
					&& Event.getEntityType() == EntityType.MINECART_TNT)
				Event.setCancelled(true);
			if (!getConfig().getBoolean("CreeperDamageEntity")
					&& Event.getEntityType() == EntityType.CREEPER)
				Event.setCancelled(true);
			if (!getConfig().getBoolean("GhastDamageEntity")
					&& Event.getEntityType() == EntityType.FIREBALL)
				Event.setCancelled(true);
			if (!getConfig().getBoolean("WitherDamageEntity")
					&& Event.getEntityType() == EntityType.WITHER)
				Event.setCancelled(true);
			if (!getConfig().getBoolean("WitherHeadDamageEntity")
					&& Event.getEntityType() == EntityType.WITHER_SKULL)
				Event.setCancelled(true);
			if (!getConfig().getBoolean("EnderCrystalDamageEntity")
					&& Event.getEntityType() == EntityType.ENDER_CRYSTAL)
				Event.setCancelled(true);
		}
		if (!(Event instanceof EntityDamageByEntityEvent))
			return;
		EntityDamageByEntityEvent SubEvent = (EntityDamageByEntityEvent) Event;
		if (SubEvent.getDamager() == null)
			return;
		Player Attacker = null;
		if (SubEvent.getDamager() instanceof Player)
			Attacker = (Player) SubEvent.getDamager();
		if (SubEvent.getDamager() instanceof Projectile)
			if (((Projectile) SubEvent.getDamager()).getShooter() != null
					&& ((Projectile) SubEvent.getDamager()).getShooter() instanceof Player)
				Attacker = (Player) ((Projectile) SubEvent.getDamager())
						.getShooter();
		if (Attacker == null)
			return;
		if (Event.getEntity() instanceof Tameable) {
			if (((Tameable) Event.getEntity()).getOwner() == Attacker) {
				return;
			} else if (((Tameable) Event.getEntity()).getOwner() == null) {
				ProtectionZone Protection = isInsideProtection(Event
						.getEntity().getLocation());
				if (Protection == null
						|| Protection.userHasType(Attacker.getName(),
								UTEntities))
					return;
			} else if (Event.getEntity() instanceof Wolf) {
				if (PVP.isPlayerInPVPWith(Attacker,
						(Player) ((Wolf) Event.getEntity()).getOwner()))
					return;
			}
		} else {
			if (!(Event.getEntity() instanceof Player)) {
				ProtectionZone Protection = isInsideProtection(Event
						.getEntity().getLocation());
				if (Protection == null
						|| Protection.userHasType(Attacker.getName(),
								UTEntities))
					return;
			}
		}
		if (Event.getEntity() instanceof Player) {
			if (PVP.isPlayerInPVPWith((Player) Event.getEntity(), Attacker))
				return;
			ProtectionZone Protection = isInsideProtection(Event.getEntity()
					.getLocation());
			ProtectionZone Protection2 = isInsideProtection(Attacker
					.getLocation());

			if (Protection != null && Protection2 != null) {
				if (!Protection.hasTag("PreventPVP")
						&& !Protection2.hasTag("PreventPVP"))
					return;
			} else if (Protection != null) {
				if (!Protection.hasTag("PreventPVP"))
					return;
			} else if (Protection2 != null) {
				if (!Protection2.hasTag("PreventPVP"))
					return;
			} else {
				return;
			}
		}

		Event.setCancelled(true);
		Attacker.updateInventory();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void EntityInteract(EntityInteractEvent Event) {
		if (Event.getEntity() == null || Event.getBlock() == null)
			return;
		if (Event.getEntity() instanceof Projectile) {
			if (((Projectile) Event.getEntity()).getShooter() instanceof Player) {
				ProtectionZone Protection = isInsideProtection(Event
						.getEntity().getLocation());
				if (Protection == null
						|| Protection.userHasType(((Player) ((Projectile) Event
								.getEntity()).getShooter()).getName(),
								UTEntities))
					return;
				if (Event.getBlock().getType() == Material.TRIPWIRE
						|| Event.getBlock().getType() == Material.WOOD_BUTTON
						|| Event.getBlock().getType() == Material.WOOD_PLATE)
					Event.setCancelled(true);
			}
		}
		if (Event.getEntity() instanceof Tameable)
			if (!((Tameable) Event.getEntity()).isTamed()) {
				return;
			} else {
				ProtectionZone Protection = isInsideProtection(Event
						.getEntity().getLocation());
				if (Protection == null
						|| Protection.userHasType(
								((Tameable) Event.getEntity()).getOwner()
										.getName(), UTEntities)) {
					return;
				} else {
					Event.setCancelled(true);
				}
			}
		if (Event.getBlock().getType() == Material.SOIL) {
			if (!(Event.getEntity() instanceof Player)) {
				if (!getConfig().getBoolean("EntitiesTrampleCrops"))
					Event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void EntityChangeBlock(EntityChangeBlockEvent Event) {
		if (Event.getEntity() == null)
			return;
		if (!getConfig().getBoolean("EndermenBlockChange")
				&& Event.getEntityType() == EntityType.ENDERMAN)
			Event.setCancelled(true);
		if (!getConfig().getBoolean("ZombiesBreakDoors")
				&& Event.getEntityType() == EntityType.ZOMBIE)
			Event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void EntityExplode(EntityExplodeEvent Event) {
		if (Event.getEntity() == null)
			return;
		if (!getConfig().getBoolean("TNTBlockDamage")
				&& Event.getEntityType() == EntityType.PRIMED_TNT)
			Event.setCancelled(true);
		if (!getConfig().getBoolean("TNTCartBlockDamage")
				&& Event.getEntityType() == EntityType.MINECART_TNT)
			Event.setCancelled(true);
		if (!getConfig().getBoolean("CreeperBlockDamage")
				&& Event.getEntityType() == EntityType.CREEPER)
			Event.setCancelled(true);
		if (!getConfig().getBoolean("GhastBlockDamage")
				&& Event.getEntityType() == EntityType.FIREBALL)
			Event.setCancelled(true);
		if (!getConfig().getBoolean("WitherBlockDamage")
				&& Event.getEntityType() == EntityType.WITHER)
			Event.setCancelled(true);
		if (!getConfig().getBoolean("WitherHeadBlockDamage")
				&& Event.getEntityType() == EntityType.WITHER_SKULL)
			Event.setCancelled(true);
		if (!getConfig().getBoolean("EnderCrystalBlockDamage")
				&& Event.getEntityType() == EntityType.ENDER_CRYSTAL)
			Event.setCancelled(true);
	}
}
