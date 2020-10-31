package com.noeul.discord.hk.leaderboard;

public class Utils {
	public static String repeat(String s, int count) {
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < count; ++i)
			builder.append(s);
		return builder.toString();
	}
}