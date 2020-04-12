/*
 * The Spigot plugin for the SSAGO(https://ssago.org) Easter event within Minecraft.
 * Copyright (C) 2020  Gregory HS (GregZ_)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.gregz.ssago.easter.command.arguments.admin;

import me.gregz.ssago.easter.command.arguments.SubCommand;
import me.gregz.ssago.easter.egg.EggManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class BreakArgument implements SubCommand {

    private final EggManager eggManager;

    public BreakArgument(EggManager eggManager) {
        this.eggManager = eggManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args, int argsStart) {
        if (!sender.hasPermission("ssago.easter.admin.break")) {
            sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.LIGHT_PURPLE + "Treasure Hunt" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + "Insufficient permission");
            return true;
        }

        if (!eggManager.areEggsPlaced()) {
            sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.LIGHT_PURPLE + "Treasure Hunt" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY + " No eggs are currently placed.");
            return true;
        }

        List<Location> invalidHeads = eggManager.breakAllEggs();

        if (invalidHeads.isEmpty()) {
            sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.LIGHT_PURPLE + "Treasure Hunt" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY + " Successful broke all placed eggs.");
        } else {
            sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.LIGHT_PURPLE + "Treasure Hunt" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + " Some eggs had the incorrect type, unable to break " + invalidHeads.size() + " of " + eggManager.getEggCount() + " eggs.");
            invalidHeads.forEach(location -> sender.sendMessage(ChatColor.DARK_GRAY + "  - " + ChatColor.GRAY + location.getWorld() + " " + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ() + " " + location.getBlock().getType()));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args, int argsStart) {
        return Collections.emptyList();
    }
}
