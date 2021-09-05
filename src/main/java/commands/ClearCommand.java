package commands;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ClearCommand implements ICommand {

	public static ClearCommand instance;

	private ClearCommand() {
	}

	@Override
	public void handle(GuildMessageReceivedEvent event) {

		System.out.println("ceva");

	}

	public static ClearCommand getInstance() {
		if (instance == null) {
			instance = new ClearCommand();
		}

		return instance;
	}
}
