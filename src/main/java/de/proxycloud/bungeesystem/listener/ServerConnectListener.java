package de.proxycloud.bungeesystem.listener;

import de.proxycloud.bungeesystem.BungeeSystem;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

import java.io.IOException;

/**
 * Developer ProxyCloud
 * Coded on 19.03.2018
 * Coded with IntelliJ
 */
public class ServerConnectListener implements Listener
{

    private Configuration configuration;

    @EventHandler
    public void on(final ServerConnectEvent event)
    {
        final ProxiedPlayer player = event.getPlayer();

        if(BungeeSystem.getInstance().getBanManager().isBanned(player.getUniqueId().toString()))
        {
            long current = System.currentTimeMillis();
            long end = BungeeSystem.getInstance().getBanManager().getEnd(player.getUniqueId().toString());
            if(((current < end ? 1 : 0) | (end == -1L ? 1 : 0)) != 0)
            {
                try
                {
                    this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(BungeeSystem.getInstance().getFile());
                    event.setCancelled(true);
                    player.disconnect(new TextComponent("§fYou §fwere §fbanned §ffrom §fthe §2§lPVPOASE §a§lNETWORK§f!\n\n§fReason §8» §7"
                            + BungeeSystem.getInstance().getBanManager().getReason(player.getUniqueId().toString()) + " \n §fTime §fremaining §8» §7"
                            + BungeeSystem.getInstance().getBanManager().getRemainingTime(player.getUniqueId().toString())));
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }

            }
            else
            {
                BungeeSystem.getInstance().getBanManager().unban(player.getUniqueId().toString());
                for(ProxiedPlayer players : BungeeSystem.getInstance().getProxy().getPlayers())
                {
                    if(player.hasPermission("system.ban"))
                    {
                        if(BungeeSystem.getInstance().getNotifyCache().get(players.getUniqueId().toString()) == 0)
                        {
                            players.sendMessage(new TextComponent(BungeeSystem.getInstance().getPrefix() + " "));
                            players.sendMessage(new TextComponent(BungeeSystem.getInstance().getPrefix() + BungeeSystem.getInstance().getPrefixCache().get(player.getUniqueId().toString()) + player.getName() + " §fwas §funbanned §fby §bCloud§8."));
                            players.sendMessage(new TextComponent(BungeeSystem.getInstance().getPrefix() + " "));
                        }
                    }
                }
            }
        }

        BungeeSystem.getInstance().getProxy().getScheduler().runAsync(BungeeSystem.getInstance(), () ->
        {
            BungeeSystem.getInstance().getPrefixCache().put(player.getUniqueId().toString(), BungeeSystem.getInstance().getPrefixManager().getPrefix(player.getUniqueId().toString()));
            if(player.hasPermission("system.notify"))
            {
                if(!(BungeeSystem.getInstance().getNotifyManager().isSaved(player.getUniqueId().toString())))
                {
                    BungeeSystem.getInstance().getNotifyManager().build(player.getUniqueId().toString());
                    BungeeSystem.getInstance().getNotifyCache().put(player.getUniqueId().toString(), BungeeSystem.getInstance().getNotifyManager().getNotify(player.getUniqueId().toString()));
                    return;
                }
                BungeeSystem.getInstance().getNotifyCache().put(player.getUniqueId().toString(), BungeeSystem.getInstance().getNotifyManager().getNotify(player.getUniqueId().toString()));
            }
            if(player.hasPermission("system.team"))
            {
                if(!(BungeeSystem.getInstance().getTeamManager().isSaved(player.getUniqueId().toString())))
                {
                    BungeeSystem.getInstance().getTeamManager().build(player.getUniqueId().toString());
                }
            }
        });
    }

}
