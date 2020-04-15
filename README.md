# DiscordBot (Kotlin) [WIP]
A simple discordbot for playing music using the Discord4j-API, written in Kotlin.

### Requirements
* OS: Windows or Linux with a working Java installation
* Java-version: Compiled for Java 8 (1.8)

### Setup
* Go to [https://discord.com/developers] and register a new application. Additionally you need to create a bot under the "Bot"-section
* Download jar or compile source code
* Create a start script. Example for Windows (start.bat):
```
java -Xmx512M -jar <YOUR-JAR-NAME>.jar -token <YOUR_BOT_TOKEN>
PAUSE
```

If you want to use the youtube search feature using the play command, you need to create a youtube data api-key v3.
You then specify this key in your start script like below:
```
java -Xmx512M -jar <YOUR-JAR-NAME>.jar -token <YOUR_BOT_TOKEN> -ytkey <YOUR_YT_KEY>
PAUSE
```

[https://discord.com/developers]: https://discordapp.com/developers