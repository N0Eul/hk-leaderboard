package com.noeul.discord.hk.leaderboard.command;

import net.dv8tion.jda.api.entities.Message;

import java.util.Arrays;
import java.util.stream.Stream;

public class CommandExecutor {
	private final String message;
	private String commandline;
	private String commandName;
	private String[] args;

	public CommandExecutor(Message msg) {
		this.message = msg.getContentRaw().trim();

		if (
				(msg.getChannel().getId().equals("726719437725761606") || msg.getAuthor().getId().equals("387858780412706817"))
				&& !msg.getAuthor().isBot() && message.startsWith("!")
		) {
			this.commandline = msg.getContentRaw().trim().substring("!".length());
			String[] cmdLineArray = commandline.split("(?<!(?<!\\\\)\\\\)\\s+");
			this.commandName = cmdLineArray[0];
			this.args = Arrays.copyOfRange(cmdLineArray, 1, cmdLineArray.length);

			if (Stream.of("Leaderboard", "lb", "순위", "ㅣㅠ").anyMatch(s -> commandName.equalsIgnoreCase(s)))
				new LeaderboardCommand(msg.getGuild(), msg.getChannel(), msg.getAuthor(), args).run();

			else if (Stream.of("LevelCalculate", "LevelC", "LvCalc", "Lvc", "lCalc", "lc", "레벨계산", "ㅣㅊ", "ᅟᅵᆾ").anyMatch(s -> commandName.equalsIgnoreCase(s)))
				new LevelCalcCommand(msg.getGuild(), msg.getChannel(), msg.getAuthor(), args).run();

			else if (Stream.of("LevelTable", "ExpTable", "LvTable", "XpTable", "lt", "xpt", "ᅟᅵᆺ", "ㅣㅅ", "텟", "레벨표", "경험치표", "겸치표").anyMatch(s -> commandName.equalsIgnoreCase(s)))
				new LevelTableCommand(msg.getGuild(), msg.getChannel(), msg.getAuthor(), args).run();

			else if (Stream.of("ExpCalculate", "ExpCalc", "ExpC", "XpCalc", "XpC", "경험치계산", "겸치계산", "ㄷ텣", "텣", "ㄷ텣및", "텣및").anyMatch(s -> commandName.equalsIgnoreCase(s)))
				new ExpCalcCommand(msg.getGuild(), msg.getChannel(), msg.getAuthor(), args).run();

		}
	}
}
