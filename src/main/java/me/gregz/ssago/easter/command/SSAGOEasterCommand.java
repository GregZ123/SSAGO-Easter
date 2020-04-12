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

package me.gregz.ssago.easter.command;

import me.gregz.ssago.easter.command.arguments.normal.AnswerArgument;
import me.gregz.ssago.easter.command.arguments.normal.QuestionArgument;
import me.gregz.ssago.easter.egg.EggManager;
import me.gregz.ssago.easter.http.HTTPManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SSAGOEasterCommand implements TabExecutor {

    private final EggManager eggManager;

    private final QuestionArgument questionArgument;
    private final AnswerArgument answerArgument;

    public SSAGOEasterCommand(EggManager eggManager, HTTPManager httpManager) {
        this.eggManager = eggManager;

        this.questionArgument = new QuestionArgument(eggManager);
        this.answerArgument = new AnswerArgument(eggManager, httpManager);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.LIGHT_PURPLE + "Treasure Hunt" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + "This command may only be executed by a player.");
            return true;
        }

        Player p = (Player) sender;

        if (!eggManager.areEggsPlaced()) {
            p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.LIGHT_PURPLE + "Treasure Hunt" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY + "There is currently no active hunt.");
            return true;
        }

        if (args.length == 0) {
            p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.LIGHT_PURPLE + "Treasure Hunt" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY + "The game is afoot, good luck!");
            return true;
        }

        switch (args[0]) {
            case "question":
            case "q":
                return questionArgument.onCommand(sender, command, label, args, 1);
            case "answer":
            case "a":
                return answerArgument.onCommand(sender, command, label, args, 1);
            default:
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> response = new ArrayList<>();

        if (args.length == 1) {
            if (!args[0].equals("")) {
                if ("answer".startsWith(args[0].toLowerCase())) {
                    response.add("answer");
                }
                if ("question".startsWith(args[0].toLowerCase())) {
                    response.add("question");
                }
            } else {
                response.add("answer");
                response.add("question");
            }
        }

        return response;
    }
}
