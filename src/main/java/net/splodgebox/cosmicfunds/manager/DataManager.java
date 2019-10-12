package net.splodgebox.cosmicfunds.manager;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.splodgebox.cosmicfunds.CosmicFunds;
import net.splodgebox.cosmicfunds.utils.Chat;
import net.splodgebox.cosmicfunds.utils.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

@RequiredArgsConstructor
public class DataManager {
    @Getter
    private final CosmicFunds plugin;

    public void addMoney(Player player, long amount){
        plugin.totalFunds = plugin.totalFunds + amount;
        checkCompleted();
        Bukkit.broadcastMessage(Message.FUNDS_BROADCAST.toString()
                .replace("%player%", player.getName())
                .replace("%amount%", new DecimalFormat("#,###").format(amount))
        );
        Chat.msg(player, Message.FUNDS_MESSAGE.toString()
                .replace("%amount%", new DecimalFormat("#,###").format(amount))
        );
    }

    public void checkCompleted() {
        for (String string : plugin.getConfig().getConfigurationSection("Funds").getKeys(false)){
            boolean completed = false;
            if (plugin.totalFunds >= plugin.getConfig().getLong("Funds." + string + ".amount")){
                completed = true;
            }
            plugin.fundsCompleted.put(string, completed);
        }
    }
}
