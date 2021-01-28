package com.noeul.discord.hk.leaderboard.command;

import com.noeul.discord.hk.leaderboard.RankCard;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.NoSuchElementException;

public class MyRankCommand extends Command {

	public MyRankCommand(Guild guild, MessageChannel room, User sender, String[] args) {
		super(guild, room, sender, args);
	}

	@Override
	public void run() {
		run(sender.getId());
	}

	public void run(String identity) {
		if (guild.getMember(sender) != null) {
			try {
				if (args.length == 0) {
					BufferedImage cardImage = new RankCard(guild.getMember(sender)).make(new RankCard.Option(2400, 800));
					try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
						ImageIO.write(cardImage, "PNG", outputStream);
						room.sendFile(outputStream.toByteArray(), "RankCard.png").queue();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					if (identity.matches("<@!?\\d+>|\\d+|.+#\\d{4}")) {
						System.out.println(identity);
						BufferedImage cardImage = new RankCard(
								identity.matches(".+#\\d{4}$") ? guild.getMemberByTag(identity) :
								guild.getMemberById(identity.replaceFirst("(?:<@!?)?(\\d+)>?", "$1"))
						).make(new RankCard.Option(2400, 800));
						try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
							ImageIO.write(cardImage, "PNG", outputStream);
							room.sendFile(outputStream.toByteArray(), "RankCard.png").queue();
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {
						room.sendMessage('`' + identity + "` (으)로 신원확인을 할 수 없습니다.").queue();
					}
				}
			} catch (NoSuchElementException | NullPointerException e) {
				room.sendMessage("해당 유저를 찾을 수 없습니다.").queue();
			}
		}
	}
}
