# Discord Music Bot with JDA and Spring Boot

This is a Discord Music Bot written with JDA (Java Discord API) and Spring Boot 3. It supports various music commands to
play and manage songs in a voice channel.

## Commands

Here is a list of available commands:

* `/join`: This command will make the bot join the voice channel that you are currently in.
* `/play` <song>: This command will play the specified song in the voice channel. The bot will search for the song on
  YouTube and play the first result.
* `/now-playing`: This command will display information about the song that is currently playing.
* `/queue`: This command will display the current song queue.
* `/repeat`: This command will toggle the repeat mode on or off.
* `/repeat-queue`: This command will toggle the repeat queue mode on or off.
* `/skip`: This command will skip the current song and play the next song in the queue.
* `/stop`: This command will stop the current song and clear the song queue.
* `/leave`: This command will make the bot leave the voice channel.
* `/pause`: This command will pause the current song.

### Important when using commands

Note that all commands should be used as slash commands and not send as messages to a text channel.

## How to use

1. Clone the repository to your local machine.
2. Create a new [Discord Application](https://discord.com/developers/applications) and obtain the application token.
3. Add the bot to your Discord server.
4. Add the following environment variable: `DISCORD_BOT_TOKEN=<YOUR_TOKEN_HERE>`
5. Create the jar: `mvn package`
6. Start from the root directory: `java -jar <JAR_NAME>.jar`

## Contributing or Feedback

I encourage everyone to contribute to the bot by opening pull requests.
If you have any improvements, bug fixes or new features that you would like to add,
please feel free to create a pull request and I will review it as soon as possible.

If you encounter any problems or have any suggestions,
you can also open an issue and I will take a look at it.
I value your feedback and am always looking to improve the bot.

Thank you for your support and I look forward to working together to make this bot better!

If you enjoy using the bot, I would really appreciate it if you could give me a star on GitHub.
Your support helps me to improve the bot and make it even better for everyone to use.
