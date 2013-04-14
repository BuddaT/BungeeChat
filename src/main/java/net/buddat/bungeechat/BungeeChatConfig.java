package net.buddat.bungeechat;

import java.io.File;

import net.craftminecraft.bungee.bungeeyaml.supereasyconfig.Config;

public class BungeeChatConfig extends Config {
    private static final String PLUGIN_SUBDIR = "plugins";
    private static final String CONFIG_FILE_NAME = "config.yml";

    private static final String DEFAULT_CHANNEL_NAME = "#testbungeechat";
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_BOT_NAME = BungeeChat.PLUGIN_NAME;

    public BungeeChatConfig(BungeeChat plugin) {
        CONFIG_FILE = new File(PLUGIN_SUBDIR + File.separator + plugin.getDescription().getName(), CONFIG_FILE_NAME);
        CONFIG_HEADER = "BungeeChat config";
    }
    
    public String channel = DEFAULT_CHANNEL_NAME;
    public String host = DEFAULT_HOST;
    public String botname = DEFAULT_BOT_NAME;
}
