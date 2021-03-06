package net.buddat.bungeechat;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.exception.NickAlreadyInUseException;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.MessageEvent;

public class ChatBot extends ListenerAdapter<PircBotX> implements Listener<PircBotX> {
    private static final MessageColourTranslater colouriser = new MessageColourTranslater();

    private final PircBotX bot;
    
    private final BasicLogger logger;
    
    private final String channelName;
    private final Channel channel;
    private final String host;
    
    private final String name;
    private final String nickservPassword;
    private final boolean useNickserv;
    
    private final BungeeChat plugin;
    private final int reconnectTime;

    // lock to cover reconnection task and intentional disconnect
    private final Object lock = new Object();
    // guarded by lock
    private volatile ScheduledTask reconnectionTask;
    // guarded by lock
    private volatile boolean isIntentionalDisconnect = false;
    
    private final String connectionMessage;
    private final String disconnectionMessage;

    public ChatBot(BungeeChat plugin, BungeeChatConfig config) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        
        this.name = config.botname;
        this.channelName = config.channel;
        this.host = config.host;
        this.reconnectTime = config.reconnectTime;
        
        this.useNickserv = config.useNickserv;
        this.nickservPassword = config.nickservPassword;
        
        connectionMessage = "IRC bot \"" + name + "\" connected to " + host + " channel " + channelName + ".";
        disconnectionMessage = "IRC bot disconnected, will attempt reconnect in " + reconnectTime + " seconds";
        
        bot = new PircBotX();
        channel = bot.getChannel(channelName);
        
        bot.getListenerManager().addListener(this);
    }
    
    public void reconnect() {
        synchronized(lock) {
            isIntentionalDisconnect = false;
            if (autoReconnect() && reconnectionTask != null) {
                if (reconnectionTask != null) {
                    plugin.getProxy().getScheduler().cancel(reconnectionTask);
                    reconnectionTask = null;
                }
            }
            logger.debug("Cancelled reconnection task");
        }
    }
    
    private boolean autoReconnect() {
        if (bot.isConnected()) {
            logger.info("IRC Bot is already connected, disconnect first");
            return false;
        }
        try {
            bot.setName(name);
            // TODO: Investigate whether nullpointerexception is bug in this, or in pircbotx
            try {
                bot.connect(host);
            } catch (NullPointerException e) {
                logger.error("Couldn't connect", e);
            }
            
            if (useNickserv)
                bot.sendMessage("NickServ", "identify " + nickservPassword);
            
            bot.joinChannel(channelName);
            logger.info(connectionMessage);
            return true;
        } catch (NickAlreadyInUseException e) {
            logger.error("Nick already in use: " + bot.getName(), e);
            bot.disconnect();
        } catch (IOException e) {
            logger.error("Can't connect, IO error", e);
        } catch (IrcException e) {
            logger.error("Can't connect, IRC error", e);
        }
        return false;
    }
    
    /**
     * Disconnects from the server.
     */
    public void disconnect() {
        synchronized(lock) {
            isIntentionalDisconnect = true;
        }
        bot.disconnect();
    }

    public void sendMessage(String message) {
        bot.sendMessage(channel, colouriser.mcToIrc(message));
    }

    /**
     * Whether or not the chat bot is believed to be connected.
     * 
     * @return True if the bot is believed to be connected, otherwise false.
     */
    public boolean isConnected() {
        return bot.isConnected();
    }

    // TODO: Figure out bungee's concurrency model
    @Override
    public void onMessage(MessageEvent<PircBotX> messageEvent) {
        String message = messageEvent.getMessage();
        if (message.startsWith("!")) {
            processCommand(messageEvent);
            return;
        }
        
        message = colouriser.ircToMc(message);
        String nick = messageEvent.getUser().getNick();
        
        for (ProxiedPlayer player : plugin.getProxy().getPlayers()) {
            player.sendMessage("[IRC] " + nick + ": " + message);
        }
    }
    
    private void processCommand(MessageEvent<PircBotX> messageEvent) {
        String message = messageEvent.getMessage();

        if (message.equalsIgnoreCase("!players")) {
            String playerMsg = "Online (";
            playerMsg += plugin.getProxy().getPlayers().size() + "): ";

            for (ProxiedPlayer player : plugin.getProxy().getPlayers())
                playerMsg += player.getName() + " ";

            bot.sendMessage(channel, playerMsg);
        }
    }
    
    @Override
    public void onDisconnect(DisconnectEvent<PircBotX> event) {
        if (isIntentionalDisconnect) {
            logger.info("IRC bot intentionally disconnected");
        } else if (reconnectionTask == null) {
            synchronized(lock) {
                if (reconnectionTask == null) {
                    reconnectionTask = plugin.getProxy().getScheduler().schedule(
                            plugin, new Reconnection(), reconnectTime, reconnectTime, TimeUnit.SECONDS);
                }
            }
            logger.info(disconnectionMessage);
        }
    }
    
    private class Reconnection implements Runnable {
        @Override
        public void run() {
            synchronized (lock) {
                if (autoReconnect()) {
                    plugin.getProxy().getScheduler().cancel(reconnectionTask);
                    reconnectionTask = null;
                }
            }
        }
    }
}
