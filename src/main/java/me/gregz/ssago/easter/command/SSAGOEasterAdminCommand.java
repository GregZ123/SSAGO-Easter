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

import me.gregz.ssago.easter.command.arguments.admin.AddArgument;
import me.gregz.ssago.easter.command.arguments.admin.BreakArgument;
import me.gregz.ssago.easter.command.arguments.admin.CheckArgument;
import me.gregz.ssago.easter.command.arguments.admin.PlaceArgument;
import me.gregz.ssago.easter.command.arguments.admin.RemoveArgument;
import me.gregz.ssago.easter.command.arguments.admin.StatusArgument;
import me.gregz.ssago.easter.command.arguments.admin.ViewArgument;
import me.gregz.ssago.easter.egg.EggManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public class SSAGOEasterAdminCommand implements TabExecutor {

    private final EggManager eggManager;

    private final AddArgument addArgument; //TODO
    private final BreakArgument breakArgument;
    private final CheckArgument checkArgument;
    private final PlaceArgument placeArgument;
    private final RemoveArgument removeArgument; //TODO
    private final StatusArgument statusArgument;
    private final ViewArgument viewArgument; //TODO

    public SSAGOEasterAdminCommand(EggManager eggManager) {
        this.eggManager = eggManager;

        this.addArgument = new AddArgument();
        this.breakArgument = new BreakArgument(eggManager);
        this.checkArgument = new CheckArgument(eggManager);
        this.placeArgument = new PlaceArgument(eggManager);
        this.removeArgument = new RemoveArgument();
        this.statusArgument = new StatusArgument(eggManager);
        this.viewArgument = new ViewArgument();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return false;
        }

        switch (args[0]) {
            case "add":
            case "a":
                return addArgument.onCommand(sender, command, label, args, 1);
            case "break":
            case "b":
                return breakArgument.onCommand(sender, command, label, args, 1);
            case "check":
            case "c":
                return checkArgument.onCommand(sender, command, label, args, 1);
            case "place":
            case "p":
                return placeArgument.onCommand(sender, command, label, args, 1);
            case "remove":
            case "r":
                return removeArgument.onCommand(sender, command, label, args, 1);
            case "status":
            case "s":
                return statusArgument.onCommand(sender, command, label, args, 1);
            case "view":
            case "v":
                return viewArgument.onCommand(sender, command, label, args, 1);
            default:
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> response = new ArrayList<>();

        if (args.length == 1) {
            if (!args[0].equals("")) {
                if (sender.hasPermission("ssago.easter.admin.add") && "add".startsWith(args[0].toLowerCase())) {
                    response.add("add");
                }
                if (sender.hasPermission("ssago.easter.admin.break") && "break".startsWith(args[0].toLowerCase())) {
                    response.add("break");
                }
                if (sender.hasPermission("ssago.easter.admin.check") && "check".startsWith(args[0].toLowerCase())) {
                    response.add("check");
                }
                if (sender.hasPermission("ssago.easter.admin.place") && "place".startsWith(args[0].toLowerCase())) {
                    response.add("place");
                }
                if (sender.hasPermission("ssago.easter.admin.remove") && "remove".startsWith(args[0].toLowerCase())) {
                    response.add("remove");
                }
                if (sender.hasPermission("ssago.easter.admin.status") && "status".startsWith(args[0].toLowerCase())) {
                    response.add("status");
                }
                if (sender.hasPermission("ssago.easter.admin.view") && "view".startsWith(args[0].toLowerCase())) {
                    response.add("view");
                }
            } else {
                if (sender.hasPermission("ssago.easter.admin.add")) {
                    response.add("add");
                }
                if (sender.hasPermission("ssago.easter.admin.break")) {
                    response.add("break");
                }
                if (sender.hasPermission("ssago.easter.admin.check")) {
                    response.add("check");
                }
                if (sender.hasPermission("ssago.easter.admin.place")) {
                    response.add("place");
                }
                if (sender.hasPermission("ssago.easter.admin.remove")) {
                    response.add("remove");
                }
                if (sender.hasPermission("ssago.easter.admin.status")) {
                    response.add("status");
                }
                if (sender.hasPermission("ssago.easter.admin.view")) {
                    response.add("view");
                }
            }
        } else if (args[0].equals("remove")) {
            return removeArgument.onTabComplete(sender, command, alias, args, 1);
        } else if (args[0].equals("place")) {
            return placeArgument.onTabComplete(sender, command, alias, args, 1);
        }

        return response;
    }
}
