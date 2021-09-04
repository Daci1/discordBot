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
		bot = JDABuilder.createDefault("ODE2MDE1MDE2OTc2NTgwNjQ5.YD0zNA.kvOweNPW4dKk4-UVlUWmPCKUM5w").build();

		Presence botPresence = bot.getPresence();
		botPresence.setStatus(OnlineStatus.ONLINE);
//        botPresence.setActivity(Activity.playing("cu niste oameni foarte respectabili"));
		bot.addEventListener(CommandListener.getInstance());
	}
//    @Override
//    public void onEvent(GenericEvent event)
//    {
//        if (event instanceof ReadyEvent) {
//        	bot.getGuilds().forEach(server -> server.getMembers().forEach(member -> System.out.println(member)));
//        }
//            
//    }

}
