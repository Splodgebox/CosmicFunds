package net.splodgebox.cosmicfunds;

import co.aikar.commands.PaperCommandManager;
import com.google.common.collect.Maps;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import net.splodgebox.cosmicfunds.events.CommandEvents;
import net.splodgebox.cosmicfunds.manager.DataManager;
import net.splodgebox.cosmicfunds.utils.Chat;
import net.splodgebox.cosmicfunds.utils.FileManager;
import net.splodgebox.cosmicfunds.utils.Message;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public final class CosmicFunds extends JavaPlugin {

    @Getter
    private static CosmicFunds instance;
    @Getter
    private static FileManager data;
    public FileManager lang;
    @Getter
    private static Economy econ;

    public long totalFunds;
    public DataManager dataManager;
    public HashMap<String, Boolean> fundsCompleted = Maps.newHashMap();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        data = new FileManager(this, "data", getDataFolder().getAbsolutePath());
        lang = new FileManager(this, "lang", getDataFolder().getAbsolutePath());
        dataManager = new DataManager(this);
        if (!setupEconomy() ) {
            Chat.log(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        PaperCommandManager paperCommandManager = new PaperCommandManager(this);
        paperCommandManager.registerCommand(new CosmicFundsCMD());

        loadMessages();
        getServer().getPluginManager().registerEvents(new CommandEvents(), this);

        new BukkitRunnable() {
            @Override
            public void run() {
                totalFunds = fetchTotal();
                dataManager.checkCompleted();
            }
        }.runTaskLater(this, 20L);
    }

    @Override
    public void onDisable(){
        saveTotal();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private void loadMessages() {
        for (Message message : Message.values()) {
            if (!this.lang.getConfiguration().contains(message.getPath())) {
                this.lang.getConfiguration().set(message.getPath(), message.getDefault());
            }
        }

        lang.save();
        Message.setFile(this.lang.getConfiguration());
    }

    public long fetchTotal(){
        return data.getConfiguration().getLong("Total");
    }


    public void saveTotal(){
        data.getConfiguration().set("Total", totalFunds);
        data.save();
    }
}
