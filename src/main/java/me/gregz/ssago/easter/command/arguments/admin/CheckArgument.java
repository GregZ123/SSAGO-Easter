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

public class CheckArgument implements SubCommand {

    private final EggManager eggManager;

    public CheckArgument(EggManager eggManager) {
        this.eggManager = eggManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args, int argsStart) {
        if (!sender.hasPermission("ssago.easter.admin.check")) {
            sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.LIGHT_PURPLE + "Treasure Hunt" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + "Insufficient permission");
            return true;
        }

        int eggCount = eggManager.getEggCount();
        List<Location> unsafeEggLocations = eggManager.getUnsafeEggLocations();

        if (eggCount == 0) {
            sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.LIGHT_PURPLE + "Treasure Hunt" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY + "Currently there are no eggs loaded.");
        } else if (unsafeEggLocations.isEmpty()) {
            sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.LIGHT_PURPLE + "Treasure Hunt" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY + (eggCount == 1 ? "Currently there is one loaded egg, it is in a safe location" : "Currently there are " + eggCount + " loaded eggs, they are all in safe locations."));
        } else {
            sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.LIGHT_PURPLE + "Treasure Hunt" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY + (eggCount == 1 ? "Currently there is one loaded egg, it is in an unsafe location." : "Currently there are " + eggCount + " loaded eggs, " + ChatColor.RED + (unsafeEggLocations.size() == 1 ? "1" + ChatColor.GRAY + " of them is in an unsafe location." : unsafeEggLocations.size() + ChatColor.GRAY.toString() + " of them is in an unsafe location.")));
            unsafeEggLocations.forEach(location -> sender.sendMessage(ChatColor.DARK_GRAY + "  - " + ChatColor.GRAY + eggManager.getEggAt(location).getId() + " " + location.getWorld().getName() + " " + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ() + " " + location.getBlock().getType()));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args, int argsStart) {
        return Collections.emptyList();
    }
}
