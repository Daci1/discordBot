package commands;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GreetCommand implements ICommand {

	public static GreetCommand instance;

	private GreetCommand() {
	};

	public void execute(GuildMessageReceivedEvent event) {
		event.getChannel().sendMessage("Hi " + event.getAuthor().getName()).queue();
	}

	public static GreetCommand getInstance() {
		if (instance == null) {
			instance = new GreetCommand();
		}
		return instance;
	}

}
