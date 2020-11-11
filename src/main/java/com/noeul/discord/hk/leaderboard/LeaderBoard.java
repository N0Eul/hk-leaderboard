package com.noeul.discord.hk.leaderboard;

import ch.obermuhlner.math.big.BigDecimalMath;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaderBoard {
	public static final String LINE_SEPARATOR = "=============================================================";
	public static final int EMBED_SRC_MAX_LENGTH = "=============================================================".length();
	public static final int MAX_PAGE = 33;
	public static final String LEADERBOARD_URL = "https://hkdev.services/leaderboard";
	private static final MathContext mc = new MathContext(200);

	public static List<Map<LeaderBoard.Header, Object>> getData(int count, int page) {
		try {
			List<Map<LeaderBoard.Header, Object>> data = new ArrayList<>();
			Elements rawData = Jsoup.connect(LEADERBOARD_URL).timeout(5000)
					.get().getElementsByClass("table table-hover table-condensed").select("tbody").select("tr");

			for (int i = count * (page - 1); i < count * page && i < rawData.size(); i++) {
				List<String> nextRow = rawData.get(i).select("td").eachText();
				int index = i;
				data.add(new HashMap<LeaderBoard.Header, Object>() {{
					put(LeaderBoard.Header.RANK, index + 1);
					put(LeaderBoard.Header.TAG, nextRow.get(0).replaceFirst("\\d+?\\. ", ""));
					put(LeaderBoard.Header.LEVEL, Long.parseLong(nextRow.get(1)));
					put(LeaderBoard.Header.EXP, Long.parseLong(nextRow.get(2)));
					put(LeaderBoard.Header.UP_TO, LeaderBoard.getExpUpTo((long) get(LeaderBoard.Header.LEVEL)));
					put(LeaderBoard.Header.TOTAL_XP, LeaderBoard.getTotalExp((long) get(LeaderBoard.Header.LEVEL), (long) get(Header.EXP)));
				}});
			}

			return data;
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
