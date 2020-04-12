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

import me.gregz.ssago.easter.SSAGOEaster;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class EggManager {

    private final HashMap<Location, EasterEgg> eggs = new HashMap<>();
    private final HashMap<UUID, EasterEgg> lastClickedEgg = new HashMap<>();
    private final SSAGOEaster plugin;
    private final File eggsConfigFile;
    private YamlConfiguration eggsConfig;
    private boolean placed;

    public EggManager(SSAGOEaster plugin) {

        this.plugin = plugin;
        this.eggsConfigFile = new File(plugin.getDataFolder(), "eggs.yml");

        reloadConfig(plugin);
    }

    public final boolean reloadConfig(SSAGOEaster plugin) {
        boolean errorFound = false;

        String workingPath;

        World workingWorld;
        int workingX;
        int workingY;
        int workingZ;
        Location workingLocation;

        UUID workingUUID;
        EggSkull workingSkull;

        if (!eggsConfigFile.exists()) {
            plugin.saveResource("eggs.yml", false);
        }
        this.eggsConfig = YamlConfiguration.loadConfiguration(eggsConfigFile);

        // Spigot is a pain with comments in YAML files so sod it here is the
        // header manually added.
        eggsConfig.options().header(
            "The Spigot plugin for the SSAGO(https://ssago.org) Easter event within Minecraft.\n" +
                "Copyright (C) 2020  Gregory HS (GregZ_)\n" +
                "\n" +
                "This program is free software: you can redistribute it and/or modify\n" +
                "it under the terms of the GNU Affero General Public License as published\n" +
                "by the Free Software Foundation, either version 3 of the License, or\n" +
                "(at your option) any later version.\n" +
                "\n" +
                "This program is distributed in the hope that it will be useful,\n" +
                "but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
                "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n" +
                "GNU Affero General Public License for more details.\n" +
                "\n" +
                "You should have received a copy of the GNU Affero General Public License\n" +
                "along with this program.  If not, see <https://www.gnu.org/licenses/>.\n" +
                "\n" +
                "##############################################################################\n" +
                "##        This is the eggs storage file for the SSAGO Easter plugin         ##\n" +
                "##    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   ##\n" +
                "##       This file contains all of the egs intended for use in the          ##\n" +
                "##       easter egg hunt allong with their requested textures.              ##\n" +
                "##############################################################################\n" +
                "\n" +
                "##############################################################################\n" +
                "##                                 WARNING                                  ##\n" +
                "##    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   ##\n" +
                "##       THIS FILE IS NEEDED FOR IN GAME LOGIC. IT IS NOT INTENDED TO       ##\n" +
                "##       BE MANUALLY EDITED, ONLY DO SO IF YOU ARE SURE YOU KNOW WHAT       ##\n" +
                "##       YOU ARE DOING.                                                     ##\n" +
                "##############################################################################\n" +
                "\n" +
                "Format:\n" +
                "  EGG_ID_STRING:      ~ The id of the egg, this will be submitted to SSAGO\n" +
                "    Location:\n" +
                "      World:          ~ The world location to place the skull at.\n" +
                "      X:              ~ The integer X location to place the skull at.\n" +
                "      Y:              ~ The integer Y location to place the skull at.\n" +
                "      Z:              ~ The integer Z location to place the skull at.\n" +
                "    Texture:\n" +
                "      UUID:           ~ The skull UUID to use for texture identification.\n" +
                "      Base64:         ~ The base 64 encoded skull texture.\n" +
                "    Question:         ~ The question to ask the player");

        if (!eggsConfig.contains("Placed")) {
            plugin.getLogger().log(Level.SEVERE, "The eggs config is missing the key Placed, defaulting to false.");
            eggsConfig.set("Placed", false);
            this.placed = false;
            errorFound = true;
        } else if (!eggsConfig.isBoolean("Placed")) {
            plugin.getLogger().log(Level.SEVERE, "The Eggs config file Placed key does not contain a boolean value, defaulting to false.");
            eggsConfig.set("Placed", false);
            this.placed = false;
            errorFound = true;
        } else {
            this.placed = eggsConfig.getBoolean("Placed");
        }

        this.eggs.clear();

        if (!eggsConfig.contains("Eggs", false)) {
            plugin.getLogger().log(Level.SEVERE, "The eggs config is missing the key Eggs, assuming no eggs exist.");
            eggsConfig.createSection("Eggs");
            errorFound = true;
        } else {
            for (String eggID : eggsConfig.getConfigurationSection("Eggs").getKeys(false)) {
                workingPath = "Eggs." + eggID + "." + "Location";

                if (!eggsConfig.contains(workingPath)) {
                    plugin.getLogger().log(Level.SEVERE, "The Eggs config file does not contain a Location key for the egg with id " + eggID + ".");
                    errorFound = true;
                    continue;
                }

                workingPath += ".World";
                if (!eggsConfig.contains(workingPath)) {
                    plugin.getLogger().log(Level.SEVERE, "The Eggs config file does not contain a World Location key for the egg with id " + eggID + ".");
                    errorFound = true;
                    continue;
                }
                workingWorld = Bukkit.getWorld(eggsConfig.getString(workingPath));
                if (workingWorld == null) {
                    plugin.getLogger().log(Level.SEVERE, "The Eggs config file value for the World Location of the egg with id " + eggID + " could not be identified as a valid world.");
                    errorFound = true;
                    continue;
                }

                workingPath = "Eggs." + eggID + "." + "Location.X";
                if (!eggsConfig.contains(workingPath)) {
                    plugin.getLogger().log(Level.SEVERE, "The Eggs config file does not contain a X Location key for the egg with id " + eggID + ".");
                    errorFound = true;
                    continue;
                }
                workingX = eggsConfig.getInt(workingPath);

                workingPath = "Eggs." + eggID + "." + "Location.Y";
                if (!eggsConfig.contains(workingPath)) {
                    plugin.getLogger().log(Level.SEVERE, "The Eggs config file does not contain a Y Location key for the egg with id " + eggID + ".");
                    errorFound = true;
                    continue;
                }
                workingY = eggsConfig.getInt(workingPath);

                workingPath = "Eggs." + eggID + "." + "Location.Z";
                if (!eggsConfig.contains(workingPath)) {
                    plugin.getLogger().log(Level.SEVERE, "The Eggs config file does not contain a Z Location key for the egg with id " + eggID + ".");
                    errorFound = true;
                    continue;
                }
                workingZ = eggsConfig.getInt(workingPath);

                workingLocation = new Location(workingWorld, workingX, workingY, workingZ);

                if (eggs.containsKey(workingLocation)) {
                    plugin.getLogger().log(Level.SEVERE, "Multiple eggs found at location " + workingLocation + ", loading only first found egg.");
                    errorFound = true;
                    continue;
                }

                workingPath = "Eggs." + eggID + "." + "Texture";
                if (!eggsConfig.contains(workingPath)) {
                    plugin.getLogger().log(Level.SEVERE, "The Eggs config file does not contain a Texture key for the egg with id " + eggID + ".");
                    errorFound = true;
                    continue;
                }

                workingPath += ".UUID";
                if (!eggsConfig.contains(workingPath)) {
                    plugin.getLogger().log(Level.SEVERE, "The Eggs config file does not contain a Texture UUID key for the egg with id " + eggID + ".");
                    errorFound = true;
                    continue;
                }
                try {
                    workingUUID = UUID.fromString(eggsConfig.getString(workingPath));
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().log(Level.SEVERE, "The Eggs config file value for the Texture UUID of the egg with id " + eggID + " is not a valid UUID.");
                    continue;
                }

                workingPath = "Eggs." + eggID + "." + "Texture.Base64";
                if (!eggsConfig.contains(workingPath)) {
                    plugin.getLogger().log(Level.INFO, "The Eggs config file does not contain a Texture Base64 key for the egg with id " + eggID + ", assuming uuid only texture.");
                }

                workingSkull = new EggSkull(workingUUID, eggsConfig.getString(workingPath));

                workingPath = "Eggs." + eggID + "." + "Question";
                if (!eggsConfig.contains(workingPath)) {
                    plugin.getLogger().log(Level.SEVERE, "The Eggs config file does not contain a Question key for the egg with id " + eggID + ".");
                    errorFound = true;
                    continue;
                }

                eggs.put(workingLocation, new EasterEgg(eggID, workingSkull, eggsConfig.getString(workingPath)));
                System.out.println("Init egg \n\t" + workingLocation + "\n\t" + eggs.get(workingLocation));
            }
        }

        if (!saveConfig()) {
            errorFound = true;
        }

        return errorFound;
    }

    public boolean placeAllEggs(boolean replace) {
        if (!replace && !getUnsafeEggLocations().isEmpty()) {
            return false;
        }

        eggs.forEach((location, easterEgg) -> {
            easterEgg.placeAt(location, replace);
        });

        placed = true;
        eggsConfig.set("Placed", true);
        saveConfig();

        return true;
    }

    public List<Location> getUnsafeEggLocations() {
        List<Location> unsafeLocations = new ArrayList<>();

        for (Location loc : eggs.keySet()) {
            if (loc.getBlock().getType() != Material.AIR && loc.getBlock().getType() != Material.CAVE_AIR) {
                unsafeLocations.add(loc);
            }
        }

        return unsafeLocations;
    }

    public List<Location> breakAllEggs() {
        if (!placed) {
            return Collections.emptyList();
        }

        List<Location> nonEggLocations = new ArrayList<>();
        Block workingBlock;

        for (Location loc : eggs.keySet()) {
            workingBlock = loc.getBlock();
            if (workingBlock.getType() == Material.PLAYER_HEAD) {
                workingBlock.setType(Material.AIR);
                continue;
            }
            nonEggLocations.add(loc);
        }

        placed = false;
        eggsConfig.set("Placed", false);
        saveConfig();

        return nonEggLocations;
    }

    public boolean saveConfig() {
        try {
            eggsConfig.save(eggsConfigFile);
            return true;
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred while saving the eggs data file.", e);
            return false;
        }
    }

    public boolean areEggsPlaced() {
        return placed;
    }

    public boolean isEgg(Location loc) {
        return eggs.containsKey(loc);
    }

    public EasterEgg getEggAt(Location loc) {
        return eggs.get(loc);
    }

    public int getEggCount() {
        return eggs.size();
    }

    public void setLastClickedEgg(UUID uuid, EasterEgg easterEgg) throws IllegalArgumentException {
        if (uuid == null) {
            throw new IllegalArgumentException("The given player UUID must not be null.");
        }
        if (easterEgg == null) {
            throw new IllegalArgumentException("The given last clicked egg must not be null.");
        }

        lastClickedEgg.put(uuid, easterEgg);
    }

    public EasterEgg getLastClickedEgg(UUID uuid) throws IllegalArgumentException {
        if (uuid == null) {
            throw new IllegalArgumentException("The given player UUID must not be null.");
        }

        return lastClickedEgg.get(uuid);
    }

    public void removeLastClickedEgg(UUID uuid) throws IllegalArgumentException {
        if (uuid == null) {
            throw new IllegalArgumentException("The given player UUID must not be null.");
        }

        lastClickedEgg.remove(uuid);
    }
}
