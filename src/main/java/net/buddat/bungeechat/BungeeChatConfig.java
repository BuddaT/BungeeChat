package net.buddat.bungeechat;

import java.io.File;

import net.craftminecraft.bungee.bungeeyaml.supereasyconfig.Config;

public class BungeeChatConfig extends Config {
    private static final String PLUGIN_SUBDIR = "plugins";
    private static final String CONFIG_FILE_NAME = "config.yml";

    private static final String DEFAULT_CHANNEL_NAME = "#testbungeechat";
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_BOT_NAME = BungeeChat.PLUGIN_NAME;
    
    private static final boolean DEFAULT_USE_NICKSERV = false;
    private static final String DEFAULT_NICKSERV_PASSWORD = "password";

    private static final int DEFAULT_RECONNECT_TIME = 20;
    
    public BungeeChatConfig(BungeeChat plugin) {
        CONFIG_FILE = new File(PLUGIN_SUBDIR + File.separator + plugin.getDescription().getName(), CONFIG_FILE_NAME);
        CONFIG_HEADER = "BungeeChat config";
    }
    
    public String channel = DEFAULT_CHANNEL_NAME;
    public String host = DEFAULT_HOST;
    public String botname = DEFAULT_BOT_NAME;
    public boolean useNickserv = DEFAULT_USE_NICKSERV;
    public String nickservPassword = DEFAULT_NICKSERV_PASSWORD;
    public int reconnectTime = DEFAULT_RECONNECT_TIME;
}
