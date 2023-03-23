package commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class GreetCommand implements ICommand {

	public static GreetCommand instance;

	private GreetCommand() {
	}

	public void handle(MessageReceivedEvent event) {
		event.getChannel().sendMessage("Hi " + event.getAuthor().getAsMention()).queue();
	}

	public static GreetCommand getInstance() {
		if (instance == null) {
			instance = new GreetCommand();
		}
		return instance;
	}

}
