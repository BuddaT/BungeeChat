package net.buddat.bungeechat;

import java.io.IOException;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.exception.NickAlreadyInUseException;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import sun.security.krb5.Config;

public class ChatBot extends ListenerAdapter<PircBotX> implements Listener<PircBotX> {
    private static final MessageColourTranslater colouriser = new MessageColourTranslater();

    private final PircBotX bot;
    private volatile boolean isConnected = false;

    private final BasicLogger logger;
    
    private final String channelName;
    private final Channel channel;
    private final String host;
    
    private final String name;
    private final String nickservPassword;
    private final boolean useNickserv;
    
    private final BungeeChat plugin;


    public ChatBot(BungeeChat plugin, BungeeChatConfig config) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        
        this.name = config.botname;
        this.channelName = config.channel;
        this.host = config.host;
        
        this.useNickserv = config.useNickserv;
        this.nickservPassword = config.nickservPassword;
        
        System.out.println(name + ":" + channelName + ":" + host);
        bot = new PircBotX();
        channel = bot.getChannel(channelName);
        
        bot.getListenerManager().addListener(this);
    }
    
    public void reconnect() {
        try {
            bot.setName(name);
            // TODO: Investigate whether nullpointerexception is bug in this, or in pircbotx
            try {
                bot.connect(host);
            } catch (NullPointerException e) {
                logger.error("Couldn't connect", e);
                setIsConnected(false);
            }
            
            if (useNickserv)
            	bot.sendMessage("NickServ", "identify " + nickservPassword);
            
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
        bot.sendMessage(channel, colouriser.mcToIrc(message));
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
        if (messageEvent.getMessage().startsWith("!")) {
        	processCommand(messageEvent);
        	return;
        }
        
        String message = colouriser.ircToMc(messageEvent.getMessage());
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
}
