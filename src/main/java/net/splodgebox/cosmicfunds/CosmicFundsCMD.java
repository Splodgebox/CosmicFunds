package net.splodgebox.cosmicfunds;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.google.common.base.Strings;
import net.milkbowl.vault.economy.EconomyResponse;
import net.splodgebox.cosmicfunds.manager.DataManager;
import net.splodgebox.cosmicfunds.utils.Chat;
import net.splodgebox.cosmicfunds.utils.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.Collections;

@CommandAlias("fund|funds|cosmicfunds|cfunds")
public class CosmicFundsCMD extends BaseCommand {

    @Default
    @CommandPermission("CosmicFunds.view")
    public void sendDefault(CommandSender commandSender) {
        Chat.msg(commandSender, Message.FUNDS_MAIN.toString().replace("%amount%",
                new DecimalFormat("#,###").format(CosmicFunds.getInstance().totalFunds)));
        for (String s : CosmicFunds.getInstance().getConfig().getConfigurationSection("Funds").getKeys(false)) {
            long amount = CosmicFunds.getInstance().getConfig().getLong("Funds." + s + ".amount");
            long total = CosmicFunds.getInstance().totalFunds;
            float percentage = (((float)total) / amount) *100;
            if (percentage > 100) percentage = 100;
            Chat.msg(commandSender, CosmicFunds.getInstance().getConfig().getString("Funds." + s + ".message").replace("%percent%", new DecimalFormat("###.##").format(percentage)));
            int completedBar = (int) percentage;
            int notCompleted = 100 - completedBar;

            Chat.msg(commandSender, Strings.repeat(CosmicFunds.getInstance().getConfig().getString("Settings.colors.secondary") + ";", completedBar) + Strings.repeat(CosmicFunds.getInstance().getConfig().getString("Settings.colors.primary") + ";", notCompleted));
        }
    }

    @Subcommand("add|deposit")
    @CommandPermission("CosmicFunds.deposit")
    public void addFands(CommandSender commandSender, long amount) {
        Player player = (Player) commandSender;
        if (CosmicFunds.getEcon().has(player, amount)) {
            EconomyResponse economyResponse = CosmicFunds.getEcon().withdrawPlayer(player, amount);
            if (economyResponse.transactionSuccess()) {
                Chat.msg(player, Message.FUNDS_REMOVED.toString().replace("%amount%",
                        new DecimalFormat("#,###").format(amount)));
                new DataManager(CosmicFunds.getInstance()).addMoney(player, amount);
            }
        } else {
            Message.FUNDS_NOT_ENOUGH.msg(player);
        }
    }

    @Subcommand("reload")
    @CommandPermission("CosmicFunds.reload")
    public void reloadFiles(CommandSender commandSender) {
        CosmicFunds.getInstance().lang.reload();
        CosmicFunds.getInstance().reloadConfig();
        new DataManager(CosmicFunds.getInstance()).checkCompleted();
        Message.CONFIGURATION_RELOAD.msg(commandSender);
    }

    @Subcommand("reset")
    @CommandPermission("CosmicFunds.reset")
    public void resetFunds(CommandSender commandSender) {
        CosmicFunds.getInstance().totalFunds = 0;
        CosmicFunds.getInstance().saveTotal();
        Message.FUNDS_RESET.msg(commandSender);
    }
}
