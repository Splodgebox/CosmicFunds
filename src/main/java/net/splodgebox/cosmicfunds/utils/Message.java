package net.splodgebox.cosmicfunds.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;

public enum Message {
    FUNDS_MAIN("&6&l<&e&l*&6&l> &6&lCosmic World Fund &6&l<&e&l*&6&l>" + "\n" +
            "&6&l      : $&e&n%amount%&6&l :" + "\n" +
            "&7Add $ to the /fund and unlock new content for your entire planet with &n/fund deposit <amount>"),
    FUNDS_BROADCAST("&6&l(!) &e%player% &6has just contributed $%amount% to /fund"),
    FUNDS_MESSAGE("&a&l(!) &aYou have added $%amount% to /fund"),
    FUNDS_COMMANDS("&c&l(!) &cYou cannot execute that command until it is unlocked in /fund"),
    FUNDS_REMOVED("&c&l- $%amount%"),
    FUNDS_NOT_ENOUGH("&c&l(!) &cYou do not have enough to do that!"),
    FUNDS_RESET("&c&L(!) &cYou have reset all the funds on this server!"),
    DEPOSIT_LOW("&C&L(!) &cYou cannot a fund deposit lower than $10,000!"),
    CONFIGURATION_RELOAD("&6&l(!) &eConfiguration Files have been reloaded!"),;

    private String path;
    private String msg;
    private static FileConfiguration LANG;
    public static SimpleDateFormat sdf;

    Message(String path, String start) {
        this.path = path;
        this.msg = start;
    }

    Message(String string) {
        this.path = this.name();
        this.msg = string;
    }

    public static void setFile(FileConfiguration configuration) {
        LANG = configuration;
    }

    @Override
    public String toString() {
        return ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, msg));
    }

    public String getDefault() {
        return this.msg;
    }

    public String getPath() {
        return this.path;
    }

    public void msg(CommandSender p, Object... args) {
        String s = toString();

        if (s.contains("\n")) {
            String[] split = s.split("\n");

            for (String inner : split) {
                sendMessage(p, inner, args);
            }

        } else {
            sendMessage(p, s, args);
        }
    }

    public void broadcast(Object... args) {
        String s = toString();

        if (s.contains("\n")) {
            String[] split = s.split("\n");

            for (String inner : split) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    sendMessage(player, inner, args);
                }
            }
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                sendMessage(player, s, args);
            }
        }
    }

    private String getFinalized(String string, Object... order) {
        int current = 0;

        for (Object object : order) {
            String placeholder = "{" + current + "}";

            if (string.contains(placeholder)) {
                if (object instanceof CommandSender) {
                    string = string.replace(placeholder, ((CommandSender) object).getName());
                } else if (object instanceof OfflinePlayer) {
                    string = string.replace(placeholder, ((OfflinePlayer) object).getName());
                } else if (object instanceof Location) {
                    Location location = (Location) object;
                    String repl = location.getWorld().getName() + ", " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ();

                    string = string.replace(placeholder, repl);
                } else if (object instanceof Double) {
                    string = string.replace(placeholder, "" + object);
                } else if (object instanceof Integer) {
                    string = string.replace(placeholder, "" + object);
                }
            }

            current++;
        }

        return string;
    }

    private void sendMessage(CommandSender target, String string, Object... order) {
        string = getFinalized(string, order);

        target.sendMessage(string);
    }

}
