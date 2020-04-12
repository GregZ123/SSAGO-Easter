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

import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.Objects;

public class EasterEgg {

    private final String id;
    private final EggSkull eggSkull;
    private final String question;

    public EasterEgg(String id, EggSkull eggSkull, String question) throws IllegalArgumentException {
        if (id == null) {
            throw new IllegalArgumentException("The given egg id string must not be null.");
        }

        this.id = id;
        this.eggSkull = eggSkull;
        this.question = question;
    }

    protected boolean placeAt(Location loc, boolean replace) throws IllegalArgumentException {
        return eggSkull.placeAtLocation(loc, replace);
    }

    public String getId() {
        return id;
    }

    public EggSkull getEggSkull() {
        return eggSkull;
    }

    public String getQuestion() {
        return question;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EasterEgg easterEgg = (EasterEgg) o;
        return id.equals(easterEgg.id) &&
            eggSkull.equals(easterEgg.eggSkull) &&
            question.equals(easterEgg.question);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, eggSkull, question);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("EasterEgg{");
        sb.append("id='").append(id).append('\'');
        sb.append(", eggSkull=").append(eggSkull);
        sb.append(", question='").append(question).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
