package com.worldeconomy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WorldEconomy extends JavaPlugin implements Listener {

    private final Map<UUID, Double> balances = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("WorldEconomy enabled!");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command,
                             String label, String[] args) {
        String cmd = command.getName().toLowerCase();

        if (cmd.equals("money")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("This command is for players.");
                return true;
            }

            Player player = (Player) sender;
            double balance = balances.getOrDefault(player.getUniqueId(), 1000.0);
            player.sendMessage(ChatColor.GREEN + "Your balance: $" + balance);
            return true;
        }

        if (cmd.equals("shop")) {
            sender.sendMessage(ChatColor.GOLD + "WorldEconomy Shop");
            sender.sendMessage(ChatColor.YELLOW + "Shop system is being built.");
            sender.sendMessage(ChatColor.GRAY + "Items supported: "
                    + Material.values().length);
            return true;
        }

        if (cmd.equals("pay")) {
            if (!(sender instanceof Player) || args.length != 2) {
                sender.sendMessage("Usage: /pay <player> <amount>");
                return true;
            }

            Player player = (Player) sender;
            Player target = Bukkit.getPlayerExact(args[0]);

            if (target == null) {
                player.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }

            double amount;
            try {
                amount = Double.parseDouble(args[1]);
            } catch (NumberFormatException ex) {
                player.sendMessage(ChatColor.RED + "Invalid amount.");
                return true;
            }

            if (amount <= 0 || !Double.isFinite(amount)) {
                player.sendMessage(ChatColor.RED + "Amount must be positive.");
                return true;
            }

            double balance = balances.getOrDefault(player.getUniqueId(), 1000.0);
            double targetBalance = balances.getOrDefault(target.getUniqueId(), 1000.0);

            if (balance < amount) {
                player.sendMessage(ChatColor.RED + "Not enough money.");
                return true;
            }

            balances.put(player.getUniqueId(), balance - amount);
            balances.put(target.getUniqueId(), targetBalance + amount);

            player.sendMessage(ChatColor.GREEN + "Payment sent.");
            target.sendMessage(ChatColor.GREEN + "You received $" + amount);
            return true;
        }

        return false;
    }
}
