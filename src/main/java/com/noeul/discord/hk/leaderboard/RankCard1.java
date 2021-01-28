package com.noeul.discord.hk.leaderboard;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.ClientType;
import net.dv8tion.jda.api.entities.Member;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.function.Function;

public class RankCard1 {
	private final int width, height;

	private int bgColor = 0x212121;
	private int primaryColor = 0xbdbdbd;
	private int secondaryColor = 0x757575;
	private int gaugeBgColor = 0x424242;
	private int gaugeColor = 0x5c6bc0;
	private int primaryFontColor = 0xeeeeee;
	private int secondaryFontColor = 0x757575;
	private Font font;

	{
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, RankCard1.class.getResourceAsStream("/font/HANDotumB.ttf"));
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
	}

	public RankCard1(int width, int height) {
		this.width = width;
		this.height = height;
	}

	private void fillBackground(Graphics2D graphics, int color) {
		graphics.setColor(new Color(color));
		graphics.fillRect(0, 0, width, height);
	}

	private void drawBorder(Graphics2D graphics, int color, int stroke, int margin, int radius) {
		graphics.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		graphics.setColor(new Color(color));
		graphics.drawRoundRect(margin, margin, width - margin*2, height - margin*2, radius, radius);
	}

	private static BufferedImage resizeImage(Image img, int width, int height) {
		BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = resized.createGraphics();
		graphics.drawImage(img.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
		graphics.dispose();
		return resized;
	}

	private static BufferedImage cropToCircle(BufferedImage img) {
		BufferedImage cropped = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = cropped.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.fillOval(2, 2, img.getWidth() - 4, img.getHeight() - 4);
		graphics.setComposite(AlphaComposite.SrcIn);
		graphics.drawImage(img, 0, 0, null);
		graphics.dispose();
		return cropped;
	}

	private static BufferedImage attachStatusIcon(BufferedImage img, StatusImage icon) {
		Graphics2D graphics = img.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		BufferedImage iconImage = icon.getIcon((int) (img.getWidth() / 5.0));
		graphics.setComposite(AlphaComposite.Clear);
		switch (icon) {
		case ONLINE:
		case IDLE:
		case DO_NOT_DISTURB:
		case STREAMING:
		case OFFLINE:
			graphics.fillOval((int) (img.getWidth() * 0.675), (int) (img.getHeight() * 0.675), (int) (img.getWidth() * 0.35), (int) (img.getHeight() * 0.35));
			graphics.setComposite(AlphaComposite.SrcOver);
			graphics.drawImage(iconImage, (int) (img.getWidth() * 3.0 / 4), (int) (img.getHeight() * 3.0 / 4), null);
		default:
			graphics.fillRoundRect(
					(int) (img.getWidth() * 0.675), (int) (img.getHeight() * 0.575),
					(int) (img.getWidth() * 0.35), (int) (img.getHeight() * 0.45),
					(int) (img.getWidth() * 0.09), (int) (img.getHeight() * 0.09)
			);
			graphics.setComposite(AlphaComposite.SrcOver);
			graphics.drawImage(iconImage, (int) (img.getWidth() * 3.0 / 4), (int) (img.getHeight() * 13.0 / 20), null);
		}
		graphics.dispose();
		return img;
	}

	private static void drawImage(Graphics2D graphics, Image img, int x, int y, Anchor anchor) {
		int width = img.getWidth(null);
		int height = img.getHeight(null);
		switch (anchor) {
		case LEFT_TOP:		graphics.drawImage(img, x,				    y, null); break;
		case TOP:			graphics.drawImage(img, x - (width / 2),    y, null); break;
		case RIGHT_TOP:		graphics.drawImage(img, x - width,		    y, null); break;
		case LEFT:			graphics.drawImage(img, x,				    y - (height / 2), null); break;
		case CENTER:		graphics.drawImage(img, x - (width / 2),    y - (height / 2), null); break;
		case RIGHT:			graphics.drawImage(img, x - width,		    y - (height / 2), null); break;
		case LEFT_BOTTOM:	graphics.drawImage(img, x,				    y - height, null); break;
		case BOTTOM:		graphics.drawImage(img, x - (width / 2),    y - height, null); break;
		case RIGHT_BOTTOM:	graphics.drawImage(img, x - width,		    y - height, null); break;
		}
	}

	private static void drawGauge(Graphics2D graphics, Point from, Point to, int bgColor, int fgColor, int stroke, double amount, double max) {
		graphics.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		graphics.setColor(new Color(bgColor));
		graphics.drawLine(from.x, from.y, to.x, to.y);
		graphics.setColor(new Color(fgColor));
		graphics.drawLine(from.x, from.y, (int) (from.x + (to.x - from.x) * amount/max), (int) (from.y + (to.y - from.y) * amount/max));
	}

	public BufferedImage complete(String tag, Image profileImage, OnlineStatus status, int level, long exp, long upToExp, int rank) {
		BufferedImage card = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = card.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		fillBackground(graphics, bgColor);
		drawBorder(graphics, secondaryColor, 50, 0, 5);
		drawImage(graphics, attachStatusIcon(cropToCircle(resizeImage(profileImage, 250, 250)), StatusImage.MOBILE_DND), height/2, height/2, Anchor.CENTER);
		drawGauge(graphics, new Point(375, 300), new Point(1100, 300), gaugeBgColor, gaugeColor, 40, 2, 3);

		/*graphics.getTransform().;
		graphics.drawString(new AttributedString(tag) {{
			addAttribute(TextAttribute.FONT, font.deriveFont(80f), 0, tag.lastIndexOf('#'));
			addAttribute(TextAttribute.FOREGROUND, new Color(primaryFontColor), 0, tag.lastIndexOf('#'));
			addAttribute(TextAttribute.FONT, font.deriveFont(50f), tag.lastIndexOf('#'), tag.length());
			addAttribute(TextAttribute.FOREGROUND, new Color(secondaryFontColor), tag.lastIndexOf('#'), tag.length());
		}}.getIterator(), 375, 250);
		graphics.dispose();*/

		return card;
	}

	public enum Anchor {
		LEFT_TOP,		TOP,		RIGHT_TOP,
		LEFT,			CENTER,		RIGHT,
		LEFT_BOTTOM,	BOTTOM,		RIGHT_BOTTOM,;
	}

	public enum StatusImage {
		ONLINE(OnlineStatus.ONLINE, $ ->
				new BufferedImage($, $, BufferedImage.TYPE_INT_ARGB) {{
					Graphics2D graphics = createGraphics();
					graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					graphics.setColor(new Color(0x43b581));
					graphics.fillOval(2, 2, getWidth() - 4, getHeight() - 4);
					graphics.dispose();
				}}
		),
		IDLE(OnlineStatus.IDLE, $ ->
				new BufferedImage($, $, BufferedImage.TYPE_INT_ARGB) {{
					Graphics2D graphics = createGraphics();
					graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					graphics.setColor(new Color(0xfaa61a));
					graphics.fillOval(2, 2, getWidth() - 4, getHeight() - 4);
					graphics.setComposite(AlphaComposite.Clear);
					graphics.fillOval((int) ($ * -0.125), (int) ($ * -0.125), (int) ($ * 0.75), (int) ($ * 0.75));
					graphics.dispose();
				}}
		),
		DO_NOT_DISTURB(OnlineStatus.DO_NOT_DISTURB, $ ->
				new BufferedImage($, $, BufferedImage.TYPE_INT_ARGB) {{
					Graphics2D graphics = createGraphics();
					graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					graphics.setColor(new Color(0xf04747));
					graphics.fillOval(2, 2, getWidth() - 4, getHeight() - 4);
					graphics.setComposite(AlphaComposite.Clear);
					graphics.fillRoundRect((int) ($ * 0.125), (int) ($ * 0.375), (int) ($ * 0.75), (int) ($ * 0.25), (int) ($ * 0.125), (int) ($ * 0.125));
					graphics.dispose();
				}}
		),
		OFFLINE(OnlineStatus.OFFLINE, $ ->
				new BufferedImage($, $, BufferedImage.TYPE_INT_ARGB) {{
					Graphics2D graphics = createGraphics();
					graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					graphics.setColor(new Color(0x747f8d));
					graphics.fillOval(2, 2, getWidth() - 4, getHeight() - 4);
					graphics.setComposite(AlphaComposite.Clear);
					graphics.fillOval((int) ($ * 0.25), (int) ($ * 0.25), (int) ($ * 0.5), (int) ($ * 0.5));
					graphics.dispose();
				}}
		),
		MOBILE_ONLINE(OnlineStatus.ONLINE, $ ->
				new BufferedImage($, (int) ($ * 1.5), BufferedImage.TYPE_INT_ARGB) {{
					double w = $, h = $ * 1.5;
					Graphics2D graphics = createGraphics();
					graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					graphics.setColor(new Color(0x43b581));
					graphics.fillRoundRect(0, 0, (int) w, (int) h, (int) (w * 0.1875), (int) (h * 0.125));
					graphics.setComposite(AlphaComposite.Clear);
					graphics.fillRect((int) (w * 0.125), (int) (h / 6.0), (int) (w * 0.75), (int) (h * 0.5));
					graphics.fillOval((int) (w * 0.375), (int) (h * 0.75), (int) (w * 0.25), (int) (h / 6.0));
					graphics.dispose();
				}}
		),
		MOBILE_IDLE(OnlineStatus.IDLE, $ ->
				new BufferedImage($, (int) ($ * 1.5), BufferedImage.TYPE_INT_ARGB) {{
					double w = $, h = $ * 1.5;
					Graphics2D graphics = createGraphics();
					graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					graphics.setColor(new Color(0xfaa61a));
					graphics.fillRoundRect(0, 0, (int) w, (int) h, (int) (w * 0.1875), (int) (h * 0.125));
					graphics.setComposite(AlphaComposite.Clear);
					graphics.fillRect((int) (w * 0.125), (int) (h / 6.0), (int) (w * 0.75), (int) (h * 0.5));
					graphics.fillOval((int) (w * 0.375), (int) (h * 0.75), (int) (w * 0.25), (int) (h / 6.0));
					graphics.dispose();
				}}
		),
		MOBILE_DND(OnlineStatus.DO_NOT_DISTURB, $ ->
				new BufferedImage($, (int) ($ * 1.5), BufferedImage.TYPE_INT_ARGB) {{
					double w = $, h = $ * 1.5;
					Graphics2D graphics = createGraphics();
					graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					graphics.setColor(new Color(0xf04747));
					graphics.fillRoundRect(0, 0, (int) w, (int) h, (int) (w * 0.1875), (int) (h * 0.125));
					graphics.setComposite(AlphaComposite.Clear);
					graphics.fillRect((int) (w * 0.125), (int) (h / 6.0), (int) (w * 0.75), (int) (h * 0.5));
					graphics.fillOval((int) (w * 0.375), (int) (h * 0.75), (int) (w * 0.25), (int) (h / 6.0));
					graphics.dispose();
				}}
		),
		MOBILE_OFFLINE(OnlineStatus.OFFLINE, $ ->
				new BufferedImage($, (int) ($ * 1.5), BufferedImage.TYPE_INT_ARGB) {{
					double w = $, h = $ * 1.5;
					Graphics2D graphics = createGraphics();
					graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					graphics.setColor(new Color(0x747f8d));
					graphics.fillRoundRect(0, 0, (int) w, (int) h, (int) (w * 0.1875), (int) (h * 0.125));
					graphics.setComposite(AlphaComposite.Clear);
					graphics.fillRect((int) (w * 0.125), (int) (h / 6.0), (int) (w * 0.75), (int) (h * 0.5));
					graphics.fillOval((int) (w * 0.375), (int) (h * 0.75), (int) (w * 0.25), (int) (h / 6.0));
					graphics.dispose();
				}}
		),
		STREAMING(OnlineStatus.ONLINE, $ ->
				new BufferedImage($, $, BufferedImage.TYPE_INT_ARGB) {{
					Graphics2D graphics = createGraphics();
					graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					graphics.setColor(new Color(0x593695));
					graphics.fillOval(2, 2, getWidth() - 4, getHeight() - 4);
					graphics.setComposite(AlphaComposite.Clear);
					graphics.fillPolygon(
							new int[]{ (int) ($ * 0.35), (int) ($ * 0.78301275), (int) ($ * 0.35) },
							new int[]{ (int) ($ * 0.25), (int) ($ * 0.5), (int) ($ * 0.75) },
							3
					);
					graphics.dispose();
				}}
		),
		MOBILE_STREAMING(OnlineStatus.ONLINE, $ ->
				new BufferedImage($, (int) ($ * 1.5), BufferedImage.TYPE_INT_ARGB) {{
					double w = $, h = $ * 1.5;
					Graphics2D graphics = createGraphics();
					graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					graphics.setColor(new Color(0x593695));
					graphics.fillRoundRect(0, 0, (int) w, (int) h, (int) (w * 0.1875), (int) (h * 0.125));
					graphics.setComposite(AlphaComposite.Clear);
					graphics.fillRect((int) (w * 0.125), (int) (h / 6.0), (int) (w * 0.75), (int) (h * 0.5));
					graphics.fillOval((int) (w * 0.375), (int) (h * 0.75), (int) (w * 0.25), (int) (h / 6.0));
					graphics.dispose();
				}}
		);

		private final OnlineStatus status;
		private final Function<Integer, BufferedImage> mapper;
		StatusImage(OnlineStatus status, Function<Integer, BufferedImage> mapper) {
			this.status = status;
			this.mapper = mapper;
		}

		public BufferedImage getIcon(int size) {
			return mapper.apply(size);
		}

		public static BufferedImage getIcon(Member member, int size) {
			if (
					member.getActiveClients().size() == 1
					&& member.getActiveClients().contains(ClientType.MOBILE)
			) { // When connected with mobile
				if (member.getActivities().get(0).getType() == Activity.ActivityType.STREAMING)
					return MOBILE_STREAMING.getIcon(size);
				switch (member.getOnlineStatus()) {
				case ONLINE: return MOBILE_ONLINE.getIcon(size);
				case IDLE: return MOBILE_IDLE.getIcon(size);
				case DO_NOT_DISTURB: return MOBILE_DND.getIcon(size);
				default: return MOBILE_OFFLINE.getIcon(size);
				}
			} else { // When connected with desktop, web or etc...
				if (member.getActivities().get(0).getType() == Activity.ActivityType.STREAMING)
					return STREAMING.getIcon(size);
				switch (member.getOnlineStatus()) {
				case ONLINE: return ONLINE.getIcon(size);
				case IDLE: return IDLE.getIcon(size);
				case DO_NOT_DISTURB: return DO_NOT_DISTURB.getIcon(size);
				default: return OFFLINE.getIcon(size);
				}
			}
		}

		public OnlineStatus getStatus() { return status; }
	}
}
