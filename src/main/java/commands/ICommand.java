package commands;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public interface ICommand {
	public static String prefix = ".";
	public static String userID = "284763028178599936"; // creator id

	public abstract void handle(GuildMessageReceivedEvent event);

}
