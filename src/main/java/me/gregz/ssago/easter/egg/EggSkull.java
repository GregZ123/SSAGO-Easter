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

package me.gregz.ssago.easter.egg;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Objects;
import java.util.UUID;

class EggSkull {

    private final PlayerProfile playerProfile;

    EggSkull(UUID skullID, String texture) throws IllegalArgumentException {
        if (skullID == null) {
            throw new IllegalArgumentException("The given skin uuid must not be null.");
        }

        PlayerProfile playerProfile = Bukkit.createProfile(skullID);

        if (texture != null && !texture.trim().isEmpty()) {
            System.out.println("DID THE THING");
            playerProfile.setProperty(new ProfileProperty("textures", texture));
        }

        this.playerProfile = playerProfile;
    }

    final boolean placeAtLocation(Location loc, boolean replace) throws IllegalArgumentException {
        if (loc == null) {
            throw new IllegalArgumentException("The given egg skull placement location must not be null.");
        }

        Block b = loc.getBlock();

        if (b.getType() != Material.AIR && loc.getBlock().getType() != Material.CAVE_AIR && loc.getBlock().getType() != Material.WATER && !replace) {
            return false;
        }

        b.setType(Material.PLAYER_HEAD);

        Skull skull = ((Skull) b.getState());
        skull.setPlayerProfile(playerProfile);
        skull.update();

        return true;
    }

    final ItemStack getAsItem() {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta itemMeta = (SkullMeta) item.getItemMeta();

        itemMeta.setPlayerProfile(playerProfile);
        item.setItemMeta(itemMeta);

        return item;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EggSkull eggSkull = (EggSkull) o;
        return Objects.equals(playerProfile, eggSkull.playerProfile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerProfile);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("EggSkull{");
        sb.append("playerProfile=").append(playerProfile);
        sb.append('}');
        return sb.toString();
    }
}
