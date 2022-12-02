package de.proxycloud.bungeesystem.listener;

import de.proxycloud.bungeesystem.BungeeSystem;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Developer ProxyCloud
 * Coded on 19.03.2018
 * Coded with IntelliJ
 */
public class PlayerDisconnectListener implements Listener
{

    @EventHandler
    public void on(final PlayerDisconnectEvent event)
    {
        final ProxiedPlayer player = event.getPlayer();
        BungeeSystem.getInstance().getProxy().getScheduler().runAsync(BungeeSystem.getInstance(), () -> {
            if(BungeeSystem.getInstance().getReportManager().isReported(player.getName()))
            {
                BungeeSystem.getInstance().getReportManager().deleteReport(player.getName());
            }
            if(player.hasPermission("system.notify"))
            {
                BungeeSystem.getInstance().getNotifyManager().setNotify(player.getUniqueId().toString(), BungeeSystem.getInstance().getNotifyCache().get(player.getUniqueId().toString()));
            }
        });
    }

}
