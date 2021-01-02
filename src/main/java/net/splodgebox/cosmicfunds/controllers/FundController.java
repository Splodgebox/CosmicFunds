package net.splodgebox.cosmicfunds.controllers;

import lombok.Getter;
import lombok.Setter;
import net.splodgebox.cosmicfunds.CosmicFunds;
import net.splodgebox.cosmicfunds.data.FundGoal;
import net.splodgebox.cosmicfunds.utils.Chat;
import net.splodgebox.cosmicfunds.utils.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;

public class FundController {

    @Getter private CosmicFunds plugin;

    @Getter private LinkedHashMap<String, Boolean> goals;
    @Getter private LinkedHashMap<String, FundGoal> fundGoals;

    @Getter @Setter private long totalFunds;

    public FundController(CosmicFunds plugin) {
        this.plugin = plugin;
        goals = new LinkedHashMap<>();
        fundGoals = new LinkedHashMap<>();

        load();
    }

    public void load() {
        setTotalFunds(plugin.getData().getConfiguration().getLong("Total"));

        for (String funds : plugin.getConfig().getConfigurationSection("Funds").getKeys(false)) {
            String path = "Funds." + funds + ".";
            fundGoals.put(funds, new FundGoal(
                    funds,
                    plugin.getConfig().getLong(path + "amount"),
                    plugin.getConfig().getStringList(path + "blocked-commands"),
                    plugin.getConfig().getStringList(path + "reward-commands"),
                    plugin.getConfig().getString(path + "message"),
                    plugin.getConfig().getStringList(path + "complete-message")
            ));
        }

        if (plugin.getData().getConfiguration().getConfigurationSection("Goals") == null ||
                plugin.getData().getConfiguration().getConfigurationSection("Goals").getKeys(false).isEmpty()) {
            fundGoals.keySet().forEach(s -> goals.put(s, false));
            return;
        }

        plugin.getData().getConfiguration().getConfigurationSection("Goals").getKeys(false)
                .forEach(key -> goals.put(key, plugin.getData().getConfiguration().getBoolean("Goals." + key)));
    }

    public void reload() {
        fundGoals.clear();
        goals.clear();

        load();
    }

    public void save() {
        plugin.getData().getConfiguration().set("Goals", null);

        plugin.getData().getConfiguration().set("Total", totalFunds);
        goals.forEach((s, aBoolean) ->
                plugin.getData().getConfiguration().set("Goals." + s, aBoolean));

        plugin.getData().save();
    }


    public void addMoney(Player player, long amount){
        setTotalFunds(getTotalFunds() + amount);
        Bukkit.broadcastMessage(Message.FUNDS_BROADCAST.toString()
                .replace("%player%", player.getName())
                .replace("%amount%", new DecimalFormat("#,###").format(amount))
        );
        Chat.msg(player, Message.FUNDS_MESSAGE.toString()
                .replace("%amount%", new DecimalFormat("#,###").format(amount))
        );

        checkCompleted();
    }

    public void checkCompleted() {
        for (String goal : goals.keySet()) {
            if (goals.get(goal)) continue;

            if (getTotalFunds() >= fundGoals.get(goal).getCost()) {
                goals.put(goal, true);

                fundGoals.get(goal).getCompleteMessage().stream().map(Chat::color).forEach(Bukkit::broadcastMessage);
                fundGoals.get(goal).getRewardCommands().forEach(rewardCommand ->
                        Bukkit.getServer().getOnlinePlayers().forEach(onlinePlayer ->
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                        rewardCommand.replace("{PLAYER}", onlinePlayer.getName()))));
            }
        }
    }

}
