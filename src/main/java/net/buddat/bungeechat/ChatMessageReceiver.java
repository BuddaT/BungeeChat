package net.buddat.bungeechat;

import com.google.common.eventbus.Subscribe;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Listens for BungeeSuite-filtered chat messages and sends them to the IRC bot.
 */
public class ChatMessageReceiver implements Listener {
    private final BasicLogger logger;
    private final ChatBot bot;

    public ChatMessageReceiver(BungeeChat plugin, ChatBot bot) {
        this.logger = plugin.getLogger();
        this.bot = bot;
    }

    @Subscribe
    public void onPluginMessageEvent(PluginMessageEvent event) {
        if (event.isCancelled() || !event.getTag().equalsIgnoreCase(BungeeChat.CHANNEL_INCOMING_NAME)) {
            return;
        }
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(event.getData()));
        String message;
        try {
        	String subChan = inputStream.readUTF();
            if (!subChan.equalsIgnoreCase(BungeeChat.SUBCHANNEL_NAME) &&
            		!subChan.equalsIgnoreCase(BungeeChat.SUBCHANNEL_LOGINOUT)) {
                return;
            }
            message = inputStream.readUTF();
            bot.sendMessage(message);
        } catch (IOException e) {
            logger.warn("Couldn't read event", e);
            return;
        }
    }
}
