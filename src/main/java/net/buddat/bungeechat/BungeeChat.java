package net.buddat.bungeechat;

import net.craftminecraft.bungee.bungeeyaml.InvalidConfigurationException;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class BungeeChat extends Plugin {
	
    public static final String PLUGIN_NAME = "BungeeChat";
    private static final String BUNGEE_SUITE = "BungeeSuite";
    
    public static final String CHANNEL_OUTGOING_NAME = BUNGEE_SUITE;
    public static final String CHANNEL_INCOMING_NAME = BUNGEE_SUITE + "Out";
    public static final String SUBCHANNEL_NAME = "chat";
    public static final String SUBCHANNEL_LOGINOUT = "login_logout";

    public static final String BASE_PERMISSION = "bungeechat";

    private BasicLogger logger;
    private ChatBot bot;
    private BungeeChatConfig config;

    public BasicLogger getLogger() {
    	return logger;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        logger = new BasicLogger(this);
        config = new BungeeChatConfig(this);
        try {
            config.init();
        } catch (InvalidConfigurationException e) {
            logger.error("Couldn't initialise config");
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        ProxyServer proxy = getProxy();
        if (!proxy.getChannels().contains(CHANNEL_INCOMING_NAME)) {
            logger.warn("Can't find channel " + CHANNEL_INCOMING_NAME + " in list of channels");
        }
        bot = new ChatBot(this, config);
        PluginManager pluginManager = proxy.getPluginManager();
        pluginManager.registerListener(this, new ChatMessageReceiver(this, bot));
        bot.reconnect();
        pluginManager.registerCommand(this, new BungeeChatCommand(this, bot));
    }

    @Override
    public void onDisable(){
        super.onDisable();
        bot.disconnect();
    }
}
