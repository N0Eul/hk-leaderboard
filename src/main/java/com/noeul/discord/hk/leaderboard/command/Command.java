package com.noeul.discord.hk.leaderboard.command;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public abstract class Command {
	protected final Guild guild;
	protected final MessageChannel room;
	protected final User sender;
	protected final String[] args;

	public Command(Guild guild, MessageChannel room, User sender, String[] args) {
		this.guild = guild;
		this.room = room;
		this.sender = sender;
		this.args = args;
	}

	public abstract void run();
}
