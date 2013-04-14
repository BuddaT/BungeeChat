package net.buddat.bungeechat;

import java.io.IOException;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.exception.NickAlreadyInUseException;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

public class ChatBot extends ListenerAdapter<PircBotX> implements Listener<PircBotX> {
    private final PircBotX bot;
    private volatile boolean isConnected = false;

    private final BasicLogger logger;
    private final String channelName = "#testbungeechat";
    private final Channel channel;
    private final String name;
    private final BungeeChat plugin;

    public ChatBot(BungeeChat plugin, String name) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.name = name;
        bot = new PircBotX();
        channel = bot.getChannel(channelName);
        bot.getListenerManager().addListener(this);
    }
    
    public void reconnect() {
        try {
            bot.setName(name);
            bot.connect("irc.rizon.net");
            bot.joinChannel(channelName);
            setIsConnected(true);
        } catch (NickAlreadyInUseException e) {
            logger.error("Nick already in use: " + bot.getName(), e);
        } catch (IOException e) {
            logger.error("Can't connect, IO error", e);
        } catch (IrcException e) {
            logger.error("Can't connect, IRC error", e);
        }
    }
    
    public void disconnectAll() {
        bot.disconnect();
    }

    public void sendMessage(String message) {
        bot.sendMessage(channel, message);
    }

    public boolean isConnected() {
        return isConnected;
    }

    private synchronized void setIsConnected(boolean newConnected) {
        isConnected = newConnected;
    }

    // TODO: Figure out bungee's concurrency model
    @Override
    public void onMessage(MessageEvent<PircBotX> messageEvent) {
        System.out.println("Message received");
        String message = messageEvent.getMessage();
        String nick = messageEvent.getUser().getNick();
        for (ProxiedPlayer player : plugin.getProxy().getPlayers()) {
            player.sendMessage("[IRC] " + nick + ": " + message);
        }
    }
}
