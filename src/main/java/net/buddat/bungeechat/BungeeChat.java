package net.buddat.bungeechat;

import com.google.common.eventbus.Subscribe;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Map;
import java.util.logging.Logger;

public class BungeeChat extends Plugin implements Listener {
    Logger logger;

    @Override
    public void onLoad() {
        super.onLoad();
        logger = getProxy().getLogger();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable(){
        super.onDisable();
    }

    @Subscribe
    public void onChatEvent(ChatEvent event) {
        Map<String, ServerInfo> servers = getProxy().getServers();
        getProxy().getPluginManager().registerListener(this, this);
        for (String serverName : servers.keySet()) {
            ServerInfo server = servers.get(serverName);
            // no-op
        }
    }
}
