package com.noeul.discord.hk.leaderboard;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.SelfUser;

import javax.security.auth.login.LoginException;
import java.util.*;

public class Main {
	private static JDA bot;
	private static final Timer timer = new Timer();

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

}