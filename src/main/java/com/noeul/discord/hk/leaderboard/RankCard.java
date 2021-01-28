package com.noeul.discord.hk.leaderboard;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.ClientType;
import net.dv8tion.jda.api.entities.Member;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.AttributedString;
import java.util.EnumSet;
import java.util.Map;
import java.util.function.Function;

public class RankCard {
	private final String tag;
	private BufferedImage avatar;
	private final OnlineStatus status;
	private final boolean isMobile;
	private final boolean isStreaming;
	private final Map<Leaderboard.Key, Object> data;

	public RankCard(Member member) {
		this(
				member.getUser().getAsTag(),
				Utils.downloadImage(member.getUser().getEffectiveAvatarUrl() + "?size=1024"),
				member.getOnlineStatus(),
				EnumSet.of(ClientType.MOBILE).equals(member.getActiveClients()),
				member.getActivities().size() != 0 && member.getActivities().get(0).getType() == Activity.ActivityType.STREAMING,
				Leaderboard.getData(member.getUser().getAsTag())
		);
	}

	public RankCard(String tag, BufferedImage avatar, OnlineStatus status, boolean isMobile, boolean isStreaming, Map<Leaderboard.Key, Object> data) {
		this.tag = tag;
		this.avatar = avatar;
		this.status = status;
		this.isMobile = isMobile;
		this.isStreaming = isStreaming;
		this.data = data;
	}

	public BufferedImage make(Option option) {
		BufferedImage img = new BufferedImage(option.width, option.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Fill background color
		g.setColor(option.bgColor);
		g.fillRect(0, 0, option.width, option.height);

		// Draw border
		g.setColor(option.tertiaryColor);
		g.setStroke(new BasicStroke(option.borderStroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g.drawRoundRect(
				option.borderMargin, option.borderMargin,
				option.width - option.borderMargin * 2, option.height - option.borderMargin * 2,
				option.borderRadius * 2, option.borderRadius * 2
		);

		int aw = avatar.getWidth(), ah = avatar.getHeight();
		// Crop avatar circularly
		BufferedImage circularImage = new BufferedImage(aw, ah, BufferedImage.TYPE_INT_ARGB);
		Graphics2D cg = circularImage.createGraphics();
		cg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		cg.fillOval(0, 0, aw, ah);
		cg.setComposite(AlphaComposite.SrcIn);
		cg.drawImage(avatar, 0, 0, null);
		cg.dispose();
		avatar = circularImage;

		// Draw avatar with status icon
		Graphics2D ag = avatar.createGraphics();
		ag.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		//// Pad icon
		ag.setComposite(AlphaComposite.Clear);
		if (!isMobile) Utils.fillCircle(ag, aw * 0.85, ah * 0.85, aw * 0.15);
		else Utils.fillRect(ag, aw * 0.7, ah * 0.6, aw * 0.3, ah * 0.4, aw * 0.08, ah * 0.08);

		//// Draw icon
		ag.setComposite(AlphaComposite.SrcOver);
		BufferedImage statusIcon = StatusIcon.getIcon(status, isMobile, isStreaming, (int) (aw / 5.0));
		ag.drawImage(statusIcon, (int) (aw * 3 / 4.0), (int) (ah * (isMobile ? 13 : 15) / 20.0), null);

		//// Resize image
		g.drawImage(
				avatar.getScaledInstance(option.avatarSize, option.avatarSize, Image.SCALE_SMOOTH),
				(int) (option.avatarLocation.x - option.avatarSize / 2.0),
				(int) (option.avatarLocation.y - option.avatarSize / 2.0), null
		);

		// Draw experiENCE point gauge
		long exp = (long) data.get(Leaderboard.Key.EXP),
				upto = (long) data.get(Leaderboard.Key.UP_TO);
		g.setColor(option.tertiaryColor);
		g.setStroke(new BasicStroke(option.expBarStroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g.drawLine(option.expBarStart.x, option.expBarStart.y, option.expBarEnd.x, option.expBarEnd.y);
		g.setColor(option.primaryColor);
		g.drawLine(
				option.expBarStart.x, option.expBarStart.y,
				(int) (option.expBarStart.x + (double) (option.expBarEnd.x - option.expBarStart.x) * exp / upto),
				(int) (option.expBarStart.y + (double) (option.expBarEnd.y - option.expBarStart.y) * exp / upto)
		);

		// Draw experiENCE point and max xp of the level
		Function<Long, String> unitMapper = $ -> {
			double floor = Math.floor(Math.log10($) / 3);
			return String.format(
					floor > 0 ? "%.2f%s" : "%.0f",
					$ != 0 ? $ / Math.pow(1000, floor) : 0,
					new String[]{ "", "K", "M", "G", "T", "P", "E", "Z", "Y" }[(int) floor]
			);
		};
		String expString = String.format("%s / %s XP", unitMapper.apply(exp), unitMapper.apply(upto));
		g.drawString(
				new AttributedString(expString) {{
					addAttribute(TextAttribute.FONT, option.font);
					addAttribute(TextAttribute.FOREGROUND, option.primaryFontColor, 0, expString.indexOf('/'));
					addAttribute(TextAttribute.FOREGROUND, option.secondaryFontColor, expString.indexOf('/'), expString.length());
				}}.getIterator(),
				2200 - g.getFontMetrics(option.font).stringWidth(expString), 500
		);
		g.drawString(
				new AttributedString(data.get(Leaderboard.Key.TOTAL_XP) + " XP") {{
					addAttribute(TextAttribute.FONT, option.font);
					addAttribute(TextAttribute.FOREGROUND, option.primaryFontColor);
				}}.getIterator(),
				750, 500
		);

		// Draw tag
		g.drawString(
				new AttributedString(tag) {{
					int width = g.getFontMetrics(option.boldFont).stringWidth(tag);
					addAttribute(TextAttribute.FONT, option.boldFont.deriveFont(Math.min(option.primaryFontSize, 200000f / width)));
					addAttribute(TextAttribute.FOREGROUND, option.primaryFontColor);
				}}.getIterator(),
				750, 400
		);

		/*// Draw ranking
		String rankString = "RANK " + data.get(Leaderboard.Header.RANK);
		g.drawString(
				new AttributedString(rankString) {{
					addAttribute(TextAttribute.FONT, option.font, 0, rankString.indexOf(' ') + 1);
					addAttribute(TextAttribute.FONT, option.boldFont.deriveFont(option.boldFont.getSize2D() * 0.8f), rankString.indexOf(' ') + 1, rankString.length());
					addAttribute(TextAttribute.FOREGROUND, option.primaryFontColor);
				}}.getIterator(),
				1100 - g.getFontMetrics(option.font).stringWidth(rankString.substring(0, rankString.indexOf(' ') + 1))
						- g.getFontMetrics(option.boldFont.deriveFont(option.boldFont.getSize2D() * 0.8f)).stringWidth(rankString.substring(rankString.indexOf(' ') + 1)), 125
		);

		// Draw Level
		String levelString = data.get(Leaderboard.Header.LEVEL) + " LV.";
		g.drawString(
				new AttributedString(levelString) {{
					addAttribute(TextAttribute.FONT, option.boldFont.deriveFont(option.boldFont.getSize2D() * 0.8f), 0, levelString.indexOf(' '));
					addAttribute(TextAttribute.FONT, option.font, levelString.indexOf(' '), levelString.length());
					addAttribute(TextAttribute.FOREGROUND, option.primaryFontColor);
				}}.getIterator(),
				375, 125
		);*/
		/*String rankString = "RANK #" + data.get(Leaderboard.Header.RANK) + " LEVEL " + data.get(Leaderboard.Header.LEVEL);
		g.drawString(
				new AttributedString(rankString) {{
					addAttribute(TextAttribute.FONT, option.font.deriveFont(Font.BOLD, option.secondaryFontSize), 0, rankString.indexOf('#'));
					addAttribute(TextAttribute.FONT, option.boldFont.deriveFont(Font.BOLD, option.primaryFontSize), rankString.indexOf('#'), rankString.indexOf("LEVEL"));
					addAttribute(TextAttribute.FONT, option.font.deriveFont(Font.BOLD, option.secondaryFontSize), rankString.indexOf("LEVEL"), rankString.lastIndexOf(' ') + 1);
					addAttribute(TextAttribute.FONT, option.boldFont.deriveFont(Font.BOLD, option.primaryFontSize), rankString.lastIndexOf(' ') + 1, rankString.length());
					addAttribute(TextAttribute.FOREGROUND, option.primaryColor, rankString.indexOf("LEVEL"), rankString.length());
				}}.getIterator(),
				1100 - g.getFontMetrics(option.font.deriveFont(Font.BOLD, option.secondaryFontSize)).stringWidth("RANK LEVEL ")
						- g.getFontMetrics(option.boldFont.deriveFont(Font.BOLD, option.primaryFontSize)).stringWidth(rankString.replaceAll("RANK (#\\d+) LEVEL (\\d+)", "$1$2")),
				125
		);*/

		g.dispose();
		return img;
	}

	public static class Option {
		private final int width, height;

		public Option(int width, int height) {
			this.width = width;
			this.height = height;
		}

		public int getWidth() { return width; }
		public int getHeight() { return height; }

		private int borderMargin = 0;
		private int borderStroke = 100;
		private int borderRadius = 0;
		private int avatarSize = 500;
		private Point avatarLocation = new Point(400, 400);
		private int expBarStroke = 100;
		private Point expBarStart = new Point(750, 600);
		private Point expBarEnd = new Point(2200, 600);
		private Font font;
		private Font boldFont;
		private float primaryFontSize = 140f;
		private float secondaryFontSize = 70f;

		{
			try {
				font = Font.createFont(Font.TRUETYPE_FONT, Option.class.getResourceAsStream("/font/HANDotum.ttf")).deriveFont(Font.BOLD, secondaryFontSize);
				boldFont = Font.createFont(Font.TRUETYPE_FONT, Option.class.getResourceAsStream("/font/HANDotumB.ttf")).deriveFont(Font.BOLD, primaryFontSize);
			} catch (FontFormatException | IOException e) {
				e.printStackTrace();
			}
		}

		public Option setBorderMargin(int margin) { this.borderMargin = margin; return this; }
		public Option setBorderStroke(int stroke) { this.borderStroke = stroke; return this; }
		public Option setBorderRadius(int radius) { this.borderRadius = radius; return this; }
		public Option setAvatarSize(int size) { this.avatarSize = size; return this; }
		public Option setAvatarLocation(Point location) { this.avatarLocation = location; return this; }
		public Option setAvatarLocation(int x, int y) { this.avatarLocation = new Point(x, y); return this; }
		public Option setExpBarStroke(int stroke) { this.expBarStroke = stroke; return this; }
		public Option setExpBarStartPos(Point location) { this.expBarStart = location; return this; }
		public Option setExpBarEndPos(Point location) { this.expBarEnd = location; return this; }
		public Option setFont(Font font) { this.font = font; return this; }
		public Option setPrimaryFontSize(int size) { this.primaryFontSize = size; return this; }
		public Option setSecondaryFontSize(int size) { this.secondaryFontSize = size; return this; }

		private Color bgColor = new Color(0x212121);
		private Color primaryColor = new Color(0x2196f3);
		private Color secondaryColor = new Color(0x9e9e9e);
		private Color tertiaryColor = new Color(0x424242);
		private Color primaryFontColor = new Color(0xeeeeee);
		private Color secondaryFontColor = new Color(0x9e9e9e);

		public Option setBackgroundColor(Color color) { this.bgColor = color; return this; }
		public Option setPrimaryColor(Color color) { this.primaryColor = color; return this; }
		public Option setSecondaryColor(Color color) { this.secondaryColor = color; return this; }
		public Option setTertiaryColor(Color color) { this.tertiaryColor = color; return this; }
		public Option setPrimaryFontColor(Color color) { this.primaryFontColor = color; return this; }
		public Option setSecondaryFontColor(Color color) { this.secondaryFontColor = color; return this; }

		public Option setBackgroundColor(int color) { return this.setBackgroundColor(new Color(color)); }
		public Option setPrimaryColor(int color) { return this.setPrimaryColor(new Color(color)); }
		public Option setSecondaryColor(int color) { return this.setSecondaryColor(new Color(color)); }
		public Option setTertiaryColor(int color) { return this.setTertiaryColor(new Color(color)); }
		public Option setPrimaryFontColor(int color) { return this.setPrimaryFontColor(new Color(color)); }
		public Option setSecondaryFontColor(int color) { return this.setSecondaryFontColor(new Color(color)); }
	}

	public enum StatusIcon {
		ONLINE				(OnlineStatus.ONLINE,			false,	false, new Color(0x43b581)),
		IDLE				(OnlineStatus.IDLE,				false,	false, new Color(0xfaa61a)),
		DO_NOT_DISTURB		(OnlineStatus.DO_NOT_DISTURB,	false,	false, new Color(0xf04747)),
		OFFLINE				(OnlineStatus.OFFLINE,			false,	false, new Color(0x747f8d)),
		MOBILE_ONLINE		(OnlineStatus.ONLINE,			true,	false, new Color(0x43B581)),
		MOBILE_IDLE			(OnlineStatus.IDLE,				true,	false, new Color(0xfaa61a)),
		MOBILE_DND			(OnlineStatus.DO_NOT_DISTURB,	true,	false, new Color(0xf04747)),
		MOBILE_OFFLINE		(OnlineStatus.OFFLINE,			true,	false, new Color(0x747f8d)),
		STREAMING			(OnlineStatus.ONLINE,			false,	true, new Color(0x593695)),
		MOBILE_STREAMING	(OnlineStatus.ONLINE,			true,	true, new Color(0x593695));

		private final OnlineStatus status;
		private final boolean isMobile;
		private final boolean isStreaming;
		private final Color color;
		StatusIcon(OnlineStatus status, boolean isMobile, boolean isStreaming, Color color) {
			this.status = status;
			this.isMobile = isMobile;
			this.isStreaming = isStreaming;
			this.color = color;
		}

		public static BufferedImage getIcon(OnlineStatus status, boolean isMobile, boolean isStreaming, int size) {
			if (!isMobile) {
				if (isStreaming) return STREAMING.getIcon(size);
				switch (status) {
				case ONLINE: return ONLINE.getIcon(size);
				case IDLE: return IDLE.getIcon(size);
				case DO_NOT_DISTURB: return DO_NOT_DISTURB.getIcon(size);
				default: return OFFLINE.getIcon(size);
				}
			} else {
				if (isStreaming) return MOBILE_STREAMING.getIcon(size);
				switch (status) {
				case ONLINE: return MOBILE_ONLINE.getIcon(size);
				case IDLE: return MOBILE_IDLE.getIcon(size);
				case DO_NOT_DISTURB: return MOBILE_DND.getIcon(size);
				default: return MOBILE_OFFLINE.getIcon(size);
				}
			}
		}

		@SuppressWarnings("unchecked")
		public BufferedImage getIcon(int size) {
			if (!isMobile) {
				BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = img.createGraphics();
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(color);
				Utils.fillCircle(g, size * 0.5, size * 0.5, size * 0.5);
				g.setComposite(AlphaComposite.Clear);

				if (isStreaming) {
					Utils.fillPolygon(g, new Pair[]{
							Pair.of(size * 0.35, size * 0.25),
							Pair.of(size * 0.78301275, size * 0.5),
							Pair.of(size * 0.35, size * 0.75)
					});
				} else {
					switch (status) {
					case ONLINE: break;
					case IDLE:
						Utils.fillCircle(g, size * 0.25, size * 0.25, size * 0.375);
						break;
					case DO_NOT_DISTURB:
						Utils.fillRect(g, size * 0.125, size * 0.375, size * 0.75, size * 0.25, size * 0.125, size * 0.125);
						break;
					default:
						Utils.fillCircle(g, size * 0.5, size * 0.5, size * 0.25);
						break;
					}
				}

				g.dispose();
				return img;
			} else {
				double w = size, h = size * 1.5;
				BufferedImage img = new BufferedImage((int) w, (int) h, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = img.createGraphics();
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(color);
				Utils.fillRect(g, 0, 0, w, h, w * 0.1875, h * 0.125);
				g.setComposite(AlphaComposite.Clear);
				Utils.fillRect(g, w * 0.125, h/6.0, w * 0.75, h * 0.5);
				Utils.fillEllipse(g, w * 0.5, h * 5/6.0, w * 0.125, h/12.0);
				g.dispose();
				return img;
			}
		}
	}
}
