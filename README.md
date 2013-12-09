BukkitProtect
=============
Bukkit Protect is unique as it focuses on usability by the average server user, and features that are helpful while playing.
This plugin is intended to be used by servers that don't want to have admins or mods handle protection of land, preferring to use an automated plugin that manages itself and is easy to use by anyone joining the server.


public static String[] wordBlacklist = {};
public static int maxCapsPercent = 60;
public static int maxWordLength = 15;
public static Map<Player, String> playerLastChat = new HashMap<Player, String>();
public static Map<Player, String> playerSpam = new HashMap<Player, String>();

public static void PlayerChat(AsyncPlayerChatEvent event) {
	Player player = event.getPlayer();
	String message = event.getMessage();
	double caps = 0;
	double total = message.length();
	for (int i = 0; i < total; i++) {
		char chara = message.charAt(i);
		if (chara >= 'A' && chara <= 'Z') {
			caps += 1;
		}
	}
	int percent = (int) Math.round((caps / total) * 100);
	String[] words = message.split(" ");
	if (percent > maxCapsPercent)
		message = message.toLowerCase();
	for (int i = 0; i < words.length; i++) {
		String word = words[i];
		if (word.length() > maxWordLength) {
			message = message.replaceAll(word, "****");
		}
	}
	for (String word : wordBlacklist)
		message = message.replaceAll("(?i)" + word, "****");
	event.setMessage(message);
	double time = System.currentTimeMillis();
	if (playerLastChat.containsKey(player)) {
		double lastChat = Double.parseDouble(playerLastChat.get(player));
		playerLastChat.put(player, String.valueOf(time));
		double chatTime = (time - lastChat) / 1000;
		if (!playerSpam.containsKey(player)) {
			playerSpam.put(player, "0");
			return;
		}
		if (chatTime >= 2.5) {
			int spam = Integer.parseInt(playerSpam.get(player));
			playerSpam.put(player, String.valueOf(spam - 1));
			return;
		}
		int spam = Integer.parseInt(playerSpam.get(player));
		playerSpam.put(player, String.valueOf(spam + 1));
		if (spam == 4)
			player.sendMessage(ChatColor.GOLD + "Do not spam");
		else if (spam == 6)
			player.sendMessage(ChatColor.GOLD + "Last warning");
		else if (spam == 7) {
			player.kickPlayer("Do not spam!");
			getServer().broadcastMessage(player.getDisplayName()
					+ ChatColor.RED + " has been kicked for spamming!");
		}
	} else
		playerLastChat.put(player, String.valueOf(time));
}