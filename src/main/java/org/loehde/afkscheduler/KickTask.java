package org.loehde.afkscheduler;

import com.earth2me.essentials.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;

public class KickTask extends BukkitRunnable {

    private final AfkScheduler afkScheduler;
    private boolean isRunning = false;

    public KickTask(AfkScheduler afkScheduler) {
        this.afkScheduler = afkScheduler;
    }

    @Override
    public void run() {
        Iterable<User> users = afkScheduler.getEssentials().getOnlineUsers();

        if (!isRunning) {
            isRunning = true;
            for (User user: users) {
                if (user.isAfk())
                    setAfkSinceToSysTime(user);
            }
        }

        for (User user: users) {
            long now = System.currentTimeMillis();
            if (user.getAfkSince() + (afkScheduler.getConfigTime() * 1000) <= now && user.isAfk()) {
                Player player = user.getBase();
                if (!player.hasPermission("AfkScheduler.kickBypass")) {
                    player.kickPlayer(afkScheduler.getKickMessage());
                    if (afkScheduler.isPublicKick()) {
                        StringBuilder builder = new StringBuilder();
                        if (AfkScheduler.isChat) {
                            String prefix = AfkScheduler.chat.getGroupPrefix(player.getWorld(), AfkScheduler.chat.getPlayerGroups(player)[0]);
                            String suffix = AfkScheduler.chat.getGroupSuffix(player.getWorld(), AfkScheduler.chat.getPlayerGroups(player)[0]);
                            if (prefix != null) {
                                builder.append(activateColors(prefix));
                            }
                            builder.append(player.getName());
                            if (suffix != null) {
                                builder.append(activateColors(suffix));
                            }
                        } else {
                            builder.append(player.getName());
                        }
                        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(builder.toString() + ChatColor.RED + afkScheduler.getBroadcastMessage()));
                    }
                }
            }
        }
    }

    private String activateColors(String string) {
        StringBuilder builder = new StringBuilder(string);
        for (int i = 0; i < builder.length(); i++) {
            char index = builder.charAt(i);
            if (index == '&') {
                if (i+1 < builder.length()) {
                    char option = builder.charAt(i+1);
                    // check for 0-9
                    if ('0' <= option && option <= '9') {
                        builder.setCharAt(i, '§');
                        continue;
                    }
                    // check for k-o
                    if ('k' <= option && option <= 'o') {
                        builder.setCharAt(i, '§');
                        continue;
                    }
                    // check for r
                    if (option == 'r') {
                        builder.setCharAt(i, '§');
                    }
                }
            }
        }
        return builder.toString();
    }

    // Set the afkSince in User to current timestamp.
    private void setAfkSinceToSysTime(Object instance){
        Field field;
        try {
            field = instance.getClass().getDeclaredField("afkSince");
            field.setAccessible(true);
            field.set(instance, System.currentTimeMillis());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}