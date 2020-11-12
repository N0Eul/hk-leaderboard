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
			"484251785758769152", // 하얀 마법사#0001
			"710256355767877703", // ! ! Lee_Hyun#5326
			"627292715956043785", // 유준#4309
			"718457550038761583", // good#1525
			"608290392177246218", // M63_#0001
			"742333583049359360", // 이프봇#9158
			"684365107261997059", // ʚۣۜ͜✠더༒킹ʚۣۜ͜✠#4146
			"725148859344486450", // 안녕하새여#5017
			"532235338496606219", // 여울#9692
			"375951816384446464", // runkan#0001

			"271639823859580939", // 귀여운 겨울이#3769
			"756782638974369843", // 티칩#0001
			"338652514620538880", // MoPE#0182
			"719118336717226074", // 망장#8602

			"687886541619462293", // 코딩 도우미#6035
			"756792224099991622", // 전적이#8383
			"492857931843371008" // runkan_bot#0587
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