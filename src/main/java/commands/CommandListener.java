package commands;

import java.lang.reflect.Method;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter {

	public static CommandListener instance;

	private CommandListener() {
	}

	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		
		String[] args = event.getMessage().getContentRaw().split(" ");
		if(args[0].startsWith(ICommand.prefix)) {
			String commandClass = "commands." + args[0].substring(1,2).toUpperCase() + args[0].substring(2) + "Command";
			try {
				Class<?> cls = Class.forName(commandClass); //get the class
				Method method = cls.getMethod("getInstance", null); //declare the method for the command instance
				Object obj = method.invoke(null, null); //invoke the get instance method that return the instance
				((ICommand)obj).execute(event); //cast it to the interface and execute the command
			}catch(Exception e) {
				e.printStackTrace();
			}
			
//			System.out.println(commandClass);
		}
	}

	public static CommandListener getInstance() {
		if (instance == null) {
			instance = new CommandListener();
		}
		return instance;
	}
}
