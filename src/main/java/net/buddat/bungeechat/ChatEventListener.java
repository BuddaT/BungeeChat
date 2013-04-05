package net.buddat.bungeechat;

import com.google.common.eventbus.Subscribe;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * Listens for chat events and re-broadcasts them to the other servers.
 */
public class ChatEventListener implements Listener {
    private final BungeeChat plugin;

    public ChatEventListener(BungeeChat plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onChatEvent(ChatEvent event) {
        if (event.isCommand() || event.isCancelled() || !(event.getSender() instanceof ProxiedPlayer)) {
            return;
        }

        InetSocketAddress recipientAddress = event.getReceiver().getAddress();
        Map<String, ServerInfo> servers = plugin.getProxy().getServers();
        // send chat to each server? or to players? can't see docs
        // Sends to each player - servers keep list of players connected to it
        for (String serverName : servers.keySet()) {
            ServerInfo server = servers.get(serverName);
            if (!server.getAddress().equals(recipientAddress)) {
            	for (ProxiedPlayer plr : server.getPlayers()) {
            		if (plr == event.getSender())
            			plr.sendMessage(">> " + event.getMessage());
            		else
            			plr.sendMessage("<< " + event.getMessage());
            	}
            }
        }
    }
}
