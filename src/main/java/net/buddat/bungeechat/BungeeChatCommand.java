package net.buddat.bungeechat;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class BungeeChatCommand extends Command {

    public static final String RECONNECT = "reconnect";
    public static final String DISCONNECT = "disconnect";
    public static final String PERMISSION_RECONNECT = BungeeChat.BASE_PERMISSION + "." + RECONNECT;
    public static final String PERMISSION_DISCONNECT = BungeeChat.BASE_PERMISSION + "." + DISCONNECT;
    private final ChatBot bot;
    public BungeeChatCommand(BungeeChat plugin, ChatBot bot) {
        super("bungeechat", BungeeChat.BASE_PERMISSION, new String[] {"bcr"});
        this.bot = bot;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(BungeeChat.BASE_PERMISSION)) {
            return;
        } else if (args.length < 1) {
            sender.sendMessage("Requires a subcommand - disconnect/reconnect");
        }
        String command = args[0];
        if (RECONNECT.equalsIgnoreCase(command) && sender.hasPermission(PERMISSION_RECONNECT)) {
            sender.sendMessage("Attempting reconnect.");
            bot.reconnect();
        } else if (DISCONNECT.equalsIgnoreCase(command) && sender.hasPermission(PERMISSION_DISCONNECT)) {
            sender.sendMessage("Disconnecting.");
            bot.disconnect();
        }
    }
}
