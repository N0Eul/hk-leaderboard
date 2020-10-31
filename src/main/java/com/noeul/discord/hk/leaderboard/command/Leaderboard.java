package com.noeul.discord.hk.leaderboard.command;

import com.noeul.discord.hk.leaderboard.LeaderBoard;
import com.noeul.discord.hk.leaderboard.LeaderBoard.Header;
import com.noeul.discord.hk.leaderboard.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public class Leaderboard extends Command {
	public Leaderboard(Guild guild, MessageChannel room, User sender, String[] args) {
		super(guild, room, sender, args);
	}

	public void run() {
		String format = "%3s %3s %5s /%5s %7s %s\n";
		StringBuilder builder = new StringBuilder(String.format(format, "Rnk", "Lv", "Exp", "UpTo", "TotalXp", "Tag"))
				.append(LeaderBoard.LINE_SEPARATOR).append('\n');
		EmbedBuilder embedBuilder = new EmbedBuilder()
				.setColor(Color.HSBtoRGB((float) Math.random(), 1.0F, 1.0F))
				.setFooter(Main.getProfile().getAsTag() + " • Powered by 노을", Main.getProfile().getAvatarUrl());

		try {
			int count = args.length < 1 ? 10 : Integer.parseInt(args[0]);
			embedBuilder.setAuthor("HK Level Leaderboard", LeaderBoard.LEADERBOARD_URL, guild.getIconUrl());

			if (count < 1) {
				embedBuilder.getDescriptionBuilder().append("항목 개수는 1에서 20 사이의 정수여야 합니다. 기본값인 10으로 설정됩니다.\n");
				count = 10;
			} else if (count > 20) {
				embedBuilder.getDescriptionBuilder().append("항목 개수는 1에서 20 사이의 정수여야 합니다. 도배 방지를 위해 최대값인 20으로 설정됩니다.\n");
				count = 20;
			}

			List<Map<Header, Object>> data = LeaderBoard.getData(count, 1);
			if (data == null) {
				room.sendMessage(
						embedBuilder.setAuthor("파싱 중 문제가 생겨 순위표를 가져올 수 없습니다", null, guild.getIconUrl())
								.setDescription("다시 시도해 주세요.")
								.addField("직접 보러 가기 링크", LeaderBoard.LEADERBOARD_URL, false)
								.build()
				).queue();
				return;
			}

			data.forEach(($) -> builder.append(String.format(format, $.get(Header.RANK), $.get(Header.LEVEL), $.get(Header.EXP), $.get(Header.UP_TO), $.get(Header.TOTAL_XP), $.get(Header.TAG))));
			embedBuilder.getDescriptionBuilder()
					.append("```cs\n").append(builder.toString()).append("```");
			room.sendMessage(embedBuilder.setTimestamp(OffsetDateTime.now()).build()).queue();
		} catch (NumberFormatException e) {
			room.sendMessage(
					embedBuilder.setAuthor(args[0] + "은(는) 올바른 정수가 아닙니다", null, guild.getIconUrl())
							.setDescription("1에서 20 사이의 정수를 입력해 주세요.")
							.build()
			).queue();
		}

	}
}
