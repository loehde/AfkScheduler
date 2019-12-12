package org.loehde.afkscheduler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

class KickEvent implements Listener {

    private final AfkScheduler plugin;
    private BukkitTask task = null;
    private boolean isTaskRunning = false;

    public KickEvent(AfkScheduler plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onJoin(PlayerJoinEvent event) {
        if (isTaskRunning) {
            return;
        }

        int playerCount = plugin.getEssentials().getOnlinePlayers().size();

        if (playerCount < plugin.getPlayerThreshold()) {
            return;
        }

        isTaskRunning = true;
        plugin.getLogger().info(plugin.getDescription().getName() + " started kickTask");
        this.task = new KickTask(plugin).runTaskTimer(plugin, 0, 20 * plugin.getRefreshThreshold());

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDisconnect(PlayerQuitEvent event) {
        if (!isTaskRunning) {
            return;
        }

        int playerCount = plugin.getEssentials().getOnlinePlayers().size();

        if (playerCount > plugin.getPlayerThreshold()) {
            return;
        }

        if (task.isCancelled()) {
            return;
        }

        task.cancel();
        isTaskRunning = false;
        plugin.getLogger().info(plugin.getDescription().getName() + " stopped kickTask");
    }

}