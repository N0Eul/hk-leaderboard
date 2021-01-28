package com.noeul.discord.hk.leaderboard.command;

import com.noeul.discord.hk.leaderboard.Leaderboard;
import com.noeul.discord.hk.leaderboard.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;

public class LevelCalcCommand extends Command {
	public LevelCalcCommand(Guild guild, MessageChannel room, User sender, String[] args) {
		super(guild, room, sender, args);
	}

	public void run() {
		EmbedBuilder embedBuilder = new EmbedBuilder()
				.setColor(Color.HSBtoRGB((float) Math.random(), 1.0F, 1.0F))
				.setFooter(Main.getProfile().getAsTag() + " • Powered by 노을", Main.getProfile().getAvatarUrl());

		if (args.length < 1) {
			room.sendMessage(
					embedBuilder.setAuthor("매개변수를 입력해 주세요", null, guild.getIconUrl())
							.setDescription("매개변수는 0에서 3024617 사이의 자연수여야 합니다.")
							.build()
			).queue();
		} else {
			try {
				int level = Integer.parseInt(args[0]);
				if (level < 0 || level > 3024617) throw new NumberFormatException();

				room.sendMessage(
						embedBuilder.setAuthor("HK Level Calculator", null, guild.getIconUrl())
								.addField("레벨", level + " Lv.", true)
								.addField("누적 경험치", Leaderboard.getTotalExp(level, 0) + " xp", true)
								.addBlankField(true)

								.addField("현재 레벨까지", (level == 0 ? 0 : Leaderboard.getExpUpTo((long) level - 1)) + " xp", true)
								.addField("다음 레벨까지", Leaderboard.getExpUpTo(level) + " xp", true)
								.addBlankField(true)
								.build()
				).queue();
			} catch (NumberFormatException e) {
				room.sendMessage(
						embedBuilder.setAuthor(args[0] + "은(는) 올바른 매개변수가 아닙니다", null, guild.getIconUrl())
								.setDescription("0에서 3024617 사이의 자연수를 입력해 주세요.")
								.build()
				).queue();
			}
		}
	}
}
