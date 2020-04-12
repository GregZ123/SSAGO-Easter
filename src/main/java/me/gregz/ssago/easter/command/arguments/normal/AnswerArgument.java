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

package me.gregz.ssago.easter.command.arguments.normal;

import me.gregz.ssago.easter.command.arguments.SubCommand;
import me.gregz.ssago.easter.egg.EasterEgg;
import me.gregz.ssago.easter.egg.EggManager;
import me.gregz.ssago.easter.http.HTTPManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class AnswerArgument implements SubCommand {

    private final EggManager eggManager;
    private final HTTPManager httpManager;

    public AnswerArgument(EggManager eggManager, HTTPManager httpManager) {
        this.eggManager = eggManager;
        this.httpManager = httpManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args, int argsStart) {
        Player p = (Player) sender;
        EasterEgg egg = eggManager.getLastClickedEgg(p.getUniqueId());

        if (egg == null) {
            p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.LIGHT_PURPLE + "Treasure Hunt" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + "You do not have a treasure selected, please find one and click it (selection is cleared when you log out).");
            return true;
        }

        if (args.length - argsStart < 1) {
            p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.LIGHT_PURPLE + "Treasure Hunt" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + "Please supply and answer to the question, usage: /<command> answer LONG ANSWER HERE");
            return true;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = argsStart; i < args.length; i++) {
            sb.append(args[i]).append(' ');
        }

        httpManager.answerQuestion(p, egg, sb.toString());

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args, int argsStart) {
        return Collections.emptyList();
    }
}
