package com.noeul.discord.hk.leaderboard;

import com.noeul.discord.hk.leaderboard.command.CommandExecutor;
import com.noeul.discord.hk.leaderboard.command.LevelTableCommand.ReactionClickEventListener;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class EventListener extends ListenerAdapter {

	public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
		new CommandExecutor(event.getMessage());
	}

	public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
		new ReactionClickEventListener(event);
	}

	public void onGuildMessageReactionRemove(@NotNull GuildMessageReactionRemoveEvent event) {
		new ReactionClickEventListener(event);
	}
}