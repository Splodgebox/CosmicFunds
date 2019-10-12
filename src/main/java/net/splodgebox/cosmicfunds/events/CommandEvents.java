package net.splodgebox.cosmicfunds.events;

import net.splodgebox.cosmicfunds.CosmicFunds;
import net.splodgebox.cosmicfunds.utils.Chat;
import net.splodgebox.cosmicfunds.utils.Message;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class CommandEvents implements Listener {
    @EventHandler
    public void onCommandProcess(PlayerCommandPreprocessEvent event) {
        for (String string : CosmicFunds.getInstance().fundsCompleted.keySet()) {
            if (!CosmicFunds.getInstance().fundsCompleted.get(string)){
                List<String> commands = CosmicFunds.getInstance().getConfig().getStringList("Funds." + string + ".commands");
                if (commands.contains(event.getMessage())) {
                    Message.FUNDS_COMMANDS.msg(event.getPlayer());
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
}
