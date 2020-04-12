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

package me.gregz.ssago.easter.command.arguments;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * The SubCommand interface this provides similar methods to the
 * {@link org.bukkit.command.TabExecutor} interface with the edition or an
 * argument start integer to indicate the sub command depth.
 *
 * @author GregZ_
 * @version 1
 * @since 1.0
 */
public interface SubCommand {

    /**
     * Executes the given sub command, returning its success.
     * <br>
     * If false is returned, then the "usage" plugin.yml entry for this command
     * (if defined) will be sent to the player.
     *
     * @param sender    Source of the command
     * @param command   Command which was executed
     * @param label     Alias of the command which was used
     * @param args      Passed command arguments
     * @param argsStart The index to read arguments from, this allows for easy
     *                  implementation of sub sub commands and further levels.
     * @return true if a valid command, otherwise false
     */
    boolean onCommand(CommandSender sender, Command command, String label, String[] args, int argsStart);

    /**
     * Requests a list of possible completions for a command argument.
     *
     * @param sender    Source of the command.  For players tab-completing a
     *                  command inside of a command block, this will be the
     *                  player, not the command block.
     * @param command   Command which was executed
     * @param alias     The alias used
     * @param args      The arguments passed to the command, including final
     *                  partial argument to be completed and command label
     * @param argsStart The index to read arguments from, this allows for easy
     *                  implementation of sub sub commands and further levels.
     * @return A List of possible completions for the final argument, or null to
     * default to the command executor
     */
    List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args, int argsStart);
}
