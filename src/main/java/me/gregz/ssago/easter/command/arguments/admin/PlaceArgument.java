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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class PlaceArgument implements SubCommand {

    private final EggManager eggManager;

    public PlaceArgument(EggManager eggManager) {
        this.eggManager = eggManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args, int argsStart) {
        if (!sender.hasPermission("ssago.easter.admin.place")) {
            sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.LIGHT_PURPLE + "Treasure Hunt" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + "Insufficient permission");
            return true;
        }

        if (eggManager.areEggsPlaced()) {
            sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.LIGHT_PURPLE + "Treasure Hunt" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY + "Eggs are already placed.");
            return true;
        }

        if (!eggManager.placeAllEggs(args.length - argsStart > 0 && args[argsStart].toLowerCase().equals("force"))) {
            sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.LIGHT_PURPLE + "Treasure Hunt" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + "Unable to place eggs, some locations are unsafe.");
            return true;
        }

        sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.LIGHT_PURPLE + "Treasure Hunt" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY + "Successfully placed all eggs.");


        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args, int argsStart) {
        ArrayList<String> response = new ArrayList<>();

        if (args.length - argsStart == 1) {
            if (!args[argsStart].equals("")) {
                if (sender.hasPermission("ssago.easter.admin.place.force") && "force".startsWith(args[argsStart].toLowerCase())) {
                    response.add("force");
                }
            } else {
                if (sender.hasPermission("ssago.easter.admin.place.force")) {
                    response.add("force");
                }
            }
        }

        return response;
    }
}
