package net.buddat.bungeechat;

import com.google.common.eventbus.Subscribe;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Map;
import java.util.logging.Logger;

public class BungeeChat extends Plugin {
    private static final String CHANNEL_NAME = "BungeeChat";
    Logger logger;

    @Override
    public void onLoad() {
        super.onLoad();
        logger = getProxy().getLogger();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        ProxyServer proxy = getProxy();
        proxy.registerChannel(CHANNEL_NAME);
        proxy.getPluginManager().registerListener(this, new ChatEventListener(this));
    }

    @Override
    public void onDisable(){
        super.onDisable();
        ProxyServer proxy = getProxy();

        // deregister listener? can't find a command

        proxy.unregisterChannel(CHANNEL_NAME);
    }
}
