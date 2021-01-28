package com.noeul.discord.hk.leaderboard;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class Utils {
	public static String repeat(String s, int count) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < count; i++)
			builder.append(s);
		return builder.toString();
	}

	public static BufferedImage downloadImage(String url) {
		try {
			URLConnection connection = new URL(url).openConnection();
			connection.setConnectTimeout(10000);
			connection.setReadTimeout(10000);
			connection.setRequestProperty("User-Agent", "");
			connection.connect();

			return ImageIO.read(connection.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void fillCircle(Graphics2D g, double cx, double cy, double r) {
		g.fillOval((int) (cx - r), (int) (cy - r), (int) (r * 2), (int) (r * 2));
	}

	public static void fillEllipse(Graphics2D g, double cx, double cy, double rx, double ry) {
		g.fillOval((int) (cx - rx), (int) (cy - ry), (int) (rx * 2), (int) (ry * 2));
	}

	public static void fillRect(Graphics2D g, double x, double y, double width, double height) {
		g.fillRect((int) x, (int) y, (int) width, (int) height);
	}

	public static void fillRect(Graphics2D g, double x, double y, double width, double height, double rx, double ry) {
		g.fillRoundRect((int) x, (int) y, (int) width, (int) height, (int) (rx * 2), (int) (ry * 2));
	}

	public static void fillPolygon(Graphics2D g, Pair<Double, Double>[] points) {
		int[] xPoints = new int[points.length];
		int[] yPoints = new int[points.length];
		for (int i = 0; i < points.length; i++) {
			xPoints[i] = (int) ((double) points[i].left);
			yPoints[i] = (int) ((double) points[i].right);
		}
		g.fillPolygon(xPoints, yPoints, points.length);
	}
}