package commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface ICommand {
	static String prefix = ".";
	static String userID = "284763028178599936"; // creator id

	abstract void handle(MessageReceivedEvent event);

}
