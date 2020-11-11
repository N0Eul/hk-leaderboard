package com.noeul.discord.hk.leaderboard.command;

import com.noeul.discord.hk.leaderboard.LeaderBoard;
import com.noeul.discord.hk.leaderboard.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;

public class ExpCalc extends Command {
	public ExpCalc(Guild guild, MessageChannel room, User sender, String[] args) {
		super(guild, room, sender, args);
	}

	public void run() {
		EmbedBuilder embedBuilder = new EmbedBuilder()
				.setColor(Color.HSBtoRGB((float) Math.random(), 1.0F, 1.0F))
				.setFooter(Main.getProfile().getAsTag() + " • Powered by 노을", Main.getProfile().getAvatarUrl());

		if (args.length < 1) {
			room.sendMessage(
					embedBuilder.setAuthor("매개변수를 입력해 주세요", null, guild.getIconUrl())
							.setDescription("매개변수는 0에서 9223372036854775807(2^63-1) 사이의 자연수여야 합니다.")
							.build()
			).queue();
		} else {
			try {
				long exp = Long.parseLong(args[0]);
				if (exp < 0) throw new NumberFormatException();

				long level = LeaderBoard.getLevel(exp);
				room.sendMessage(
						embedBuilder.setAuthor("HK ExperiENCE Point Calculator", null, guild.getIconUrl())
								.addField("레벨", String.format("%d Lv. %d xp", level, exp - LeaderBoard.getTotalExp(level, 0)), true)
								.addField("누적 경험치", exp + " xp", true)
								.addBlankField(true)

								.addField("현재 레벨까지", (level == 0 ? 0 : LeaderBoard.getExpUpTo(level - 1)) + " xp", true)
								.addField("다음 레벨까지", LeaderBoard.getExpUpTo(level) + " xp", true)
								.addBlankField(true)
								.build()
				).queue();

			} catch (NumberFormatException e) {
				room.sendMessage(
						embedBuilder.setAuthor(args[0] + "은(는) 올바른 매개변수가 아닙니다", null, guild.getIconUrl())
								.setDescription("0에서 9223372036854775807(2^63-1) 사이의 자연수를 입력해 주세요.")
								.build()
				).queue();
			}

		}
	}
}