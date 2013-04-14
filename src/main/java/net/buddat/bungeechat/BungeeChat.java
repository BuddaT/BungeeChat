package net.buddat.bungeechat;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeChat extends Plugin {
    public static final String PLUGIN_NAME = "BungeeChat";
    private static final String BUNGEE_SUITE = "BungeeSuite";
    public static final String CHANNEL_OUTGOING_NAME = BUNGEE_SUITE;
    public static final String SUBCHANNEL_NAME = "chat";
    public static final String CHANNEL_INCOMING_NAME = BUNGEE_SUITE + "Out";
    BasicLogger logger;
    ChatBot bot;

    public BasicLogger getLogger() {
    	return logger;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        logger = new BasicLogger(this);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        ProxyServer proxy = getProxy();
        if (!proxy.getChannels().contains(CHANNEL_INCOMING_NAME)) {
            logger.warn("Can't find channel " + CHANNEL_INCOMING_NAME + " in list of channels");
        }
        bot = new ChatBot(this, "BungeeChat");
        proxy.getPluginManager().registerListener(this, new ChatMessageReceiver(this, bot));
        bot.reconnect();
    }

    @Override
    public void onDisable(){
        super.onDisable();

        bot.disconnectAll();
    }
}
