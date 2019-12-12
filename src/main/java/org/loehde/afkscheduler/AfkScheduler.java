package org.loehde.afkscheduler;

import com.earth2me.essentials.Essentials;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Logger;

public final class AfkScheduler extends JavaPlugin {

    private Essentials essentials;
    private final Logger logger = Bukkit.getLogger();
    private String kickMessage;
    private int playerThreshold;
    private long configTime = 600;
    private boolean publicKick;
    private int refreshThreshold;
    public static Chat chat = null;
    public static boolean isChat;
    private String broadcastMessage;

    public void onEnable() {
        this.essentials = (Essentials)getServer().getPluginManager().getPlugin("Essentials");

        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            this.logger.info(this.getDescription().getName() + " [*] missing Vault: no chat colors for you!");
        } else {
            isChat = setupChat();
        }

        if (this.essentials == null) {
            this.logger.info(this.getDescription().getName() + " [-] Error: This plugin requires Essentials");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.saveDefaultConfig();
        this.readConfig();
        this.logger.info(this.getDescription().getName() + " enabled");
        new KickEvent(this);
    }

    public void onDisable() {
        this.logger.info(this.getDescription().getName() + " disabled");
    }

    private void readConfig() {
        this.publicKick = this.getConfig().getBoolean("public-kick", false);
        this.kickMessage = this.getConfig().getString("kick-message", "Kicked for being afk");
        this.broadcastMessage = this.getConfig().getString("broadcast-message", " has been kicked for being afk");
        this.playerThreshold = this.getConfig().getInt("player-threshold", 10);
        this.configTime = this.getConfig().getInt("time-threshold", 600);
        this.refreshThreshold = this.getConfig().getInt("refresh-threshold", 30);
    }

    private boolean setupChat()
    {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }

        return (chat != null);
    }

    public Essentials getEssentials() {
        return essentials;
    }

    public String getKickMessage() {
        return kickMessage;
    }

    public int getPlayerThreshold() {
        return playerThreshold;
    }

    public boolean isPublicKick() {
        return publicKick;
    }

    public long getConfigTime() {
        return configTime;
    }

    public int getRefreshThreshold() {
        return refreshThreshold;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    public String getBroadcastMessage() {
        return broadcastMessage;
    }
}