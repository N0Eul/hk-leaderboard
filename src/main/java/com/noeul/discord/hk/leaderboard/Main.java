package com.noeul.discord.hk.leaderboard;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;

import javax.security.auth.login.LoginException;
import java.util.*;

public class Main {
	private static JDA bot;
	private static final Timer timer = new Timer();

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
			"611003028631453717", // 김박스#0001
			"756828069649580074", // Hypercube#6620
			"513618402145992704", // koder_ko#8504

			"588988294961037322", // ENCE#7777

			"687886541619462293", // 코딩 도우미#6035
			"756792224099991622", // 전적이#8383
			"492857931843371008" // runkan_bot#0587
	);
	public static final String PREFIX = "!";

	public static final List<String> STATUS_MESSAGES = Arrays.asList(
			"점진적으로 서비스를 넓혀나갈 계획입니다",
			"현재 HK에서만 서비스 하는 중",
			"리더보드 hkdev.services/leaderboard",
			"Powered by 노을"
	);

	public static void main(String[] args) {
		if (args.length == 0)
			System.out.println("\u001b[30;31m토큰을 입력해 주세요\u001b[0m");
		else {
			try {
				bot = JDABuilder.createDefault(args[0])
						.addEventListeners(new EventListener())
						.build();
				final Iterator<String>[] iterator = new Iterator[]{STATUS_MESSAGES.iterator()};
				Main.timer.scheduleAtFixedRate(new TimerTask() {
					@Override
					public void run() {
						if (!iterator[0].hasNext()) iterator[0] = STATUS_MESSAGES.iterator();
						bot.getPresence().setPresence(Activity.of(Activity.ActivityType.DEFAULT, iterator[0].next(), "https://hkdev.services/leaderboard"), false);
					}
				}, 15000, 15000);
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