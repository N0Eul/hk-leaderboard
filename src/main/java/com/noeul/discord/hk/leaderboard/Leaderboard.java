package com.noeul.discord.hk.leaderboard;

import ch.obermuhlner.math.big.BigDecimalMath;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Leaderboard {
	public static final String LINE_SEPARATOR = "=============================================================";
	public static final int EMBED_SRC_MAX_LENGTH = "=============================================================".length();
	public static final int MAX_PAGE = 33;
	public static final String LEADERBOARD_URL = "https://level.hkdev.xyz/leaderboard";
	private static final MathContext mc = new MathContext(200);

	public static List<Map<Leaderboard.Header, Object>> getLeaderboard() {
		try {
			List<Map<Leaderboard.Header, Object>> leaderboard = new ArrayList<>();
			Elements rawData = Jsoup.connect(LEADERBOARD_URL).timeout(5000)
					.get().getElementsByClass("leaders").get(0).children();

			for (int i = 0; i < rawData.size(); i++) {
				Element element = rawData.get(i);
				Map<Header, ? super Object> data = new HashMap<>();

				Matcher matcher = Pattern.compile("\\d+").matcher(element.getElementsByClass("leader-score").text());
				data.put(Header.RANK, i + 1);
				data.put(Header.TAG, element.getElementsByClass("leader-name").text().replaceFirst("\\d+?\\. ", ""));
				matcher.find();
				data.put(Header.LEVEL, Long.parseLong(matcher.group()));
				matcher.find();
				data.put(Header.EXP, Long.parseLong(matcher.group()));
				data.put(Header.UP_TO, getExpUpTo((long) data.get(Header.LEVEL)));
				data.put(Header.TOTAL_XP, getTotalExp((long) data.get(Header.LEVEL), (long) data.get(Header.EXP)));

				leaderboard.add(data);
			}

			return leaderboard;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static long getExpUpTo(long level) {
		return level * level + 5;
	}

	public static long getTotalExp(long level, long currentExp) {
		return new BigDecimal(1, mc).divide(new BigDecimal(6, mc), mc).multiply(new BigDecimal(level, mc), mc)
				.multiply(
						new BigDecimal(2, mc).multiply(new BigDecimal(level, mc).pow(2, mc), mc)
						.subtract(new BigDecimal(3, mc).multiply(new BigDecimal(level, mc), mc), mc)
						.add(new BigDecimal(31, mc), mc)
				, mc)
				.add(new BigDecimal(currentExp, mc), mc)
				.longValue();
	}

	public static long getLevel(long totalExp) {
		BigDecimal pow = BigDecimalMath.root(
				BigDecimalMath.sqrt(new BigDecimal(3, mc), mc).multiply(
						BigDecimalMath.sqrt(
								new BigDecimal(3888, mc).multiply(new BigDecimal(totalExp, mc).pow(2, mc), mc)
								.subtract(new BigDecimal(19440, mc).multiply(new BigDecimal(totalExp, mc), mc), mc)
								.add(new BigDecimal(229679, mc), mc), mc)
						, mc)
						.subtract(new BigDecimal(108, mc).multiply(new BigDecimal(totalExp, mc), mc), mc)
						.add(new BigDecimal(270, mc))
		, new BigDecimal(3, mc), mc);

		return pow.divide(new BigDecimal(2, mc).multiply(BigDecimalMath.root(new BigDecimal(9, mc), new BigDecimal(3, mc), mc), mc), mc).negate(mc)
				.add(new BigDecimal(59, mc).divide(new BigDecimal(2, mc).multiply(BigDecimalMath.root(new BigDecimal(3, mc), new BigDecimal(3, mc), mc), mc).multiply(pow, mc), mc), mc)
				.add(new BigDecimal(1, mc).divide(new BigDecimal(2, mc), mc), mc)
				.longValue();
	}

	public static String getLevelTable(int page) {
		String format = page <= 5
				? "| %3s %5s %7s | %3s %5s %7s | %3s %5s %7s |\n"
				: "|| %5s  %7s  %9s  | %5s  %7s  %9s  ||\n";
		String separator = page <= 5
				? "+-------------------+-------------------+-------------------+\n"
				: "++----------------------------+----------------------------++\n";
		int count = 20;
		StringBuilder builder = new StringBuilder(separator);
		if (page <= 5) {
			builder.append(String.format(format, "Lvl", "Upto", "TotalXp", "Lvl", "Upto", "TotalXp", "Lvl", "Upto", "TotalXp")).append(separator);

			for(int i = 60 * (page - 1) + 1; i <= 60 * (page - 1) + count; i++)
				builder.append(String.format(format,
						i, getExpUpTo(i), getTotalExp(i, 0),
						i + count, getExpUpTo(i + count), getTotalExp(i + count, 0L),
						i + count * 2, getExpUpTo(i + count * 2), getTotalExp(i + count * 2, 0L))
				);
		} else {
			builder.append(String.format(format, "Level", "UptoNxt", "TotalExp.", "Level", "UptoNxt", "TotalExp.")).append(separator);

			for(int i = 40 * (page - 6) + 301; i <= 40 * (page - 6) + count + 300; i++) {
				builder.append(String.format(format,
						i, getExpUpTo(i), getTotalExp(i, 0),
						i + count, getExpUpTo(i + count), getTotalExp(i + count, 0L))
				);
			}
		}

		return builder.append(separator).toString();
	}

	public enum Header {
		RANK,
		TAG,
		LEVEL,
		EXP,
		UP_TO,
		TOTAL_XP,;
	}
}
