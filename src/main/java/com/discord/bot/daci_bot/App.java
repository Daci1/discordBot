package com.discord.bot.daci_bot;


import commands.CommandListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.managers.Presence;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

//public class App implements EventListener 
public class App {
	private static JDA bot;

	public static void main(String[] args) {
		System.out.println(System.getenv("DISCORD_BOT_TOKEN"));
		bot = JDABuilder
				.createDefault(System.getenv("DISCORD_BOT_TOKEN"), GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_VOICE_STATES)
				.setMemberCachePolicy(MemberCachePolicy.VOICE).build();

		Presence botPresence = bot.getPresence();
		botPresence.setStatus(OnlineStatus.ONLINE);
		bot.addEventListener(CommandListener.getInstance());
	}
	
	public static JDA getBot() {
		return bot;
	}

}
