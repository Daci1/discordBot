package com.discord.bot.daci_bot;

import javax.security.auth.login.LoginException;

import commands.ClearCommand;
import commands.CommandListener;
import commands.GreetCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.managers.Presence;

//public class App implements EventListener 
public class App {
	private static JDA bot;

	public static void main(String[] args) throws LoginException, InterruptedException {
		bot = JDABuilder.createDefault(Token.getToken()).build();

		Presence botPresence = bot.getPresence();
		botPresence.setStatus(OnlineStatus.ONLINE);
		bot.addEventListener(CommandListener.getInstance());
	}

}
