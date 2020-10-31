package com.noeul.discord.hk.leaderboard;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;

import javax.security.auth.login.LoginException;
import java.util.Arrays;
import java.util.List;

public class Main {
	private static JDA bot;
	private static final List<String> blacklist = Arrays.asList(
			"762504305760141352",

			"757567341503447131",
			"710256355767877703",
			"718457550038761583",
			"627292715956043785",
			"608290392177246218",
			"742333583049359360",
			"484251785758769152"
	);
	public static final String PREFIX = "!";

	public static void main(String[] args) {
		if (args.length == 0)
			System.out.println("\u001b[30;31m토큰을 입력해 주세요\u001b[0m");
		else {
			try {
				bot = JDABuilder.createDefault(args[0])
						.addEventListeners(new EventListener())
						.build();
			} catch (LoginException e) {
				System.out.println("\u001b[30;31m봇이 로그인을 할 수 없습니다. 토큰을 다시 한 번 확인해 주세요\u001b[0m");
				System.exit(0);
			}
		}
	}

	public static JDA getBot() {
		return bot;
	}

	public static SelfUser getProfile() {
		return bot.getSelfUser();
	}

	public static boolean isBlockedUser(User user) {
		return blacklist.contains(user.getId());
	}

	public static boolean isBlockedUser(String userId) {
		return blacklist.contains(userId);
	}
}