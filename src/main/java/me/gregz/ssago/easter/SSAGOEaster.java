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

package me.gregz.ssago.easter;

import me.gregz.ssago.easter.command.SSAGOEasterAdminCommand;
import me.gregz.ssago.easter.command.SSAGOEasterCommand;
import me.gregz.ssago.easter.egg.EggManager;
import me.gregz.ssago.easter.http.HTTPManager;
import me.gregz.ssago.easter.listener.EggListener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main class of the SSAGO Easter plugin, this holds the plugin
 * initialization code and servers as the main storage for objects related to
 * the plugin.
 *
 * @author GregZ_
 * @version 3
 * @since 1.0
 */
public class SSAGOEaster extends JavaPlugin {

    /**
     * Called when the plugin is enabled by the server, instantiate and store
     * all needed objects.
     */
    @Override
    public void onEnable() {
        EggManager eggManager = new EggManager(this);
        HTTPManager httpManager = new HTTPManager(this, eggManager);

        getServer().getPluginManager().registerEvents(new EggListener(eggManager, httpManager), this);

        getCommand("SSAGOEaster").setExecutor(new SSAGOEasterCommand(eggManager, httpManager));
        getCommand("SSAGOEasterAdmin").setExecutor(new SSAGOEasterAdminCommand(eggManager));
    }
}
