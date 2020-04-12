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

package me.gregz.ssago.easter.listener;

import me.gregz.ssago.easter.egg.EggManager;
import me.gregz.ssago.easter.http.HTTPManager;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EggListener implements Listener {

    private final EggManager eggManager;
    private final HTTPManager httpManager;

    public EggListener(EggManager eggManager, HTTPManager httpManager) {
        this.eggManager = eggManager;
        this.httpManager = httpManager;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerLogout(PlayerQuitEvent event) {
        eggManager.removeLastClickedEgg(event.getPlayer().getUniqueId());
        httpManager.handleQuit(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEggInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null || !eggManager.areEggsPlaced()) {
            return;
        }

        Location loc = event.getClickedBlock().getLocation();

        if (!eggManager.isEgg(loc)) {
            return;
        }

        event.setCancelled(true);
        httpManager.claimEgg(event.getPlayer(), eggManager.getEggAt(loc));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEggBreak(BlockBreakEvent event) {
        if (eggManager.areEggsPlaced() && eggManager.isEgg(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEggDamage(BlockDamageEvent event) {
        if (eggManager.areEggsPlaced() && eggManager.isEgg(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEggBurn(BlockBurnEvent event) {
        if (eggManager.areEggsPlaced() && eggManager.isEgg(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEggFade(BlockFadeEvent event) {
        if (eggManager.areEggsPlaced() && eggManager.isEgg(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEggExplode(BlockExplodeEvent event) {
        if (eggManager.areEggsPlaced()) {
            event.blockList().removeIf(b -> eggManager.isEgg(b.getLocation()));
        }
    }
}
