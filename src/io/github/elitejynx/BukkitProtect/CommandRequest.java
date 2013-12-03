package io.github.elitejynx.BukkitProtect;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class CommandRequest {

	private Player Sender;
	private Player Target;
	private boolean Accepted;
	private Command Command;
	private String[] Args;

	public CommandRequest(Player sender, Player target, boolean accepted,
			Command command, String[] args) {
		setSender(sender);
		setTarget(target);
		setAccepted(accepted);
		setCommand(command);
		setArgs(args);
	}

	public void setSender(Player Plr) {
		Sender = Plr;
	}

	public Player getSender() {
		return Sender;
	}

	public void setTarget(Player Plr) {
		Target = Plr;
	}

	public Player getTarget() {
		return Target;
	}

	public void setAccepted(boolean accepted) {
		Accepted = accepted;
	}

	public boolean getAccepted() {
		return Accepted;
	}

	public void setCommand(Command command) {
		Command = command;
	}

	public Command getCommand() {
		return Command;
	}

	public void setArgs(String[] args) {
		Args = args;
	}

	public String[] getArgs() {
		return Args;
	}
}