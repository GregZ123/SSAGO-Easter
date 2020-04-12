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

package me.gregz.ssago.easter.http;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import me.gregz.ssago.easter.SSAGOEaster;
import me.gregz.ssago.easter.egg.EasterEgg;
import me.gregz.ssago.easter.egg.EggManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class HTTPManager {

    private static final long COOLDOWN = 5000;

    private final SSAGOEaster plugin;
    private final EggManager eggManager;

    private final HashMap<UUID, Long> lastClaimTime = new HashMap<>();
    private final HashMap<UUID, Long> lastAnswerTime = new HashMap<>();

    private String urlBase;
    private String key;

    public HTTPManager(SSAGOEaster plugin, EggManager eggManager) {
        this.plugin = plugin;
        this.eggManager = eggManager;

        reloadConfig();
    }

    public boolean reloadConfig() {
        boolean errorFound = false;
        plugin.saveDefaultConfig();

        if (!plugin.getConfig().contains("Key")) {
            plugin.getLogger().log(Level.SEVERE, "The plugin config is missing the api key, defaulting to UNKNOWN");
            this.key = "UNKNOWN";
            errorFound = true;
        } else if (!plugin.getConfig().isString("Key")) {
            plugin.getLogger().log(Level.SEVERE, "The plugin config api key key does not contain a string value, defaulting to UNKNOWN");
            this.key = "UNKNOWN";
            errorFound = true;
        } else {
            this.key = plugin.getConfig().getString("Key");
        }

        if (!plugin.getConfig().contains("UrlBase")) {
            plugin.getLogger().log(Level.SEVERE, "The plugin config is missing the key UrlBase, defaulting to https://virtual.ssago.org/api?");
            this.urlBase = "https://virtual.ssago.org/api?";
            errorFound = true;
        } else if (!plugin.getConfig().isString("UrlBase")) {
            plugin.getLogger().log(Level.SEVERE, "The Eggs config file Placed key does not contain a boolean value, defaulting to false.");
            this.urlBase = "https://virtual.ssago.org/api?";
            errorFound = true;
        } else {
            this.urlBase = plugin.getConfig().getString("UrlBase");
        }

        return errorFound;
    }

    public void handleQuit(UUID playerUUID) {
        lastClaimTime.remove(playerUUID);
        lastAnswerTime.remove(playerUUID);
    }

    public void claimEgg(Player p, EasterEgg egg) {
        if (lastClaimTime.containsKey(p.getUniqueId()) && System.currentTimeMillis() - lastClaimTime.get(p.getUniqueId()) < COOLDOWN) {
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.LIGHT_PURPLE + "Treasure Hunt" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + "You have been rate limited, please do not spam claim treasure!");
            return;
        }

        lastClaimTime.put(p.getUniqueId(), System.currentTimeMillis());

        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            final UUID playerUUID = p.getUniqueId();
            final String playerName = p.getName();
            final EasterEgg easterEgg = egg;

            @Override
            public void run() {
                try {
                    StringBuilder sb = new StringBuilder(urlBase);
                    URL url;
                    HttpsURLConnection conn;

                    sb.append("key=").append(URLEncoder.encode(key, "UTF-8")).append('&')
                        .append("egg=").append(URLEncoder.encode(easterEgg.getId(), "UTF-8")).append('&')
                        .append("who=").append(URLEncoder.encode(playerName, "UTF-8"));

                    url = new URL(sb.toString());

                    conn = (HttpsURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);

                    if (conn.getResponseCode() != 200) {
                        if (conn.getResponseCode() == 403) {
                            plugin.getLogger().log(Level.SEVERE, "Server responded with 403, api key is incorrect,  url: " + sb.toString());
                            Bukkit.getScheduler().runTask(plugin, () -> {
                                Player p = Bukkit.getPlayer(playerUUID);
                                if (p == null) {
                                    return;
                                }
                                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                                p.sendMessage(ChatColor.RED + "An error occurred when registering your latest treasure find (key), if this continues please contact an Admin.");
                            });
                            return;
                        }
                        plugin.getLogger().log(Level.SEVERE, "Server responded with non 200 code, code: " + conn.getResponseCode() + " url: " + sb.toString());
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            Player p = Bukkit.getPlayer(playerUUID);
                            if (p == null) {
                                return;
                            }
                            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                            p.sendMessage(ChatColor.RED + "An error occurred when registering your latest treasure find (response code), if this continues please contact an Admin.");
                        });
                        return;
                    }

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();

                    in.lines().forEach(response::append);
                    in.close();

                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonResponse = (JsonObject) jsonParser.parse(response.toString());

                    conn.disconnect();

                    plugin.getLogger().log(Level.INFO, "Successfully registered player treasure collection, player: " + playerName + " egg: " + easterEgg);
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        Player p = Bukkit.getPlayer(playerUUID);
                        if (p == null) {
                            return;
                        }
                        eggManager.setLastClickedEgg(playerUUID, easterEgg);
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                        //TODO add some kind of cool effect to the treasure here
                        p.sendMessage(ChatColor.GREEN + "Your treasure collection has been registered with the server, correctly answer the following question with " + ChatColor.GOLD + "/ssagoeaster answer" + ChatColor.GREEN + " for a bonus point, if you log out you will have to click this treasure again or use the web system to provide an answer.");
                        p.sendMessage(ChatColor.LIGHT_PURPLE + easterEgg.getQuestion());
                    });
                } catch (UnsupportedEncodingException e) {
                    plugin.getLogger().log(Level.SEVERE, "UTF-8 encoding not supported, THIS SHOULD NEVER HAPPEN.", e);
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        Player p = Bukkit.getPlayer(playerUUID);
                        if (p == null) {
                            return;
                        }
                        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                        p.sendMessage(ChatColor.RED + "An error occurred when registering your latest treasure find (unsupported encoding), if this continues please contact an Admin.");
                    });
                } catch (MalformedURLException e) {
                    plugin.getLogger().log(Level.SEVERE, "Error when forming claim treasure url.", e);
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        Player p = Bukkit.getPlayer(playerUUID);
                        if (p == null) {
                            return;
                        }
                        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                        p.sendMessage(ChatColor.RED + "An error occurred when registering your latest treasure find (malformed url), if this continues please contact an Admin.");
                    });
                } catch (SocketTimeoutException e) {
                    plugin.getLogger().log(Level.SEVERE, "Connection to endpoint timed out.", e);
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        Player p = Bukkit.getPlayer(playerUUID);
                        if (p == null) {
                            return;
                        }
                        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                        p.sendMessage(ChatColor.RED + "An error occurred when registering your latest treasure find (timeout), if this continues please contact an Admin.");
                    });
                } catch (ProtocolException e) {
                    plugin.getLogger().log(Level.SEVERE, "Protocol error, set with GET.", e);
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        Player p = Bukkit.getPlayer(playerUUID);
                        if (p == null) {
                            return;
                        }
                        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                        p.sendMessage(ChatColor.RED + "An error occurred when registering your latest treasure find (protocol error), if this continues please contact an Admin.");
                    });
                } catch (JsonParseException e) {
                    plugin.getLogger().log(Level.SEVERE, "Json response was not.", e);
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        Player p = Bukkit.getPlayer(playerUUID);
                        if (p == null) {
                            return;
                        }
                        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                        p.sendMessage(ChatColor.RED + "An error occurred when registering your latest treasure find (invalid JSON), if this continues please contact an Admin.");
                    });
                } catch (IOException e) {
                    plugin.getLogger().log(Level.SEVERE, "IO exception occurred when communicating player treasure gain to the server.", e);
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        Player p = Bukkit.getPlayer(playerUUID);
                        if (p == null) {
                            return;
                        }
                        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                        p.sendMessage(ChatColor.RED + "An error occurred when registering your latest treasure find (IO), if this continues please contact an Admin.");
                    });
                }
            }
        });
    }

    public void answerQuestion(Player p, EasterEgg egg, String answer) {
        if (lastAnswerTime.containsKey(p.getUniqueId()) && System.currentTimeMillis() - lastAnswerTime.get(p.getUniqueId()) < COOLDOWN) {
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.LIGHT_PURPLE + "Treasure Hunt" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + "You have been rate limited, please do not spam answers!");
            return;
        }

        lastAnswerTime.put(p.getUniqueId(), System.currentTimeMillis());
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            final UUID playerUUID = p.getUniqueId();
            final String playerName = p.getName();
            final EasterEgg easterEgg = egg;

            @Override
            public void run() {
                try {
                    StringBuilder sb = new StringBuilder(urlBase);
                    URL url;
                    HttpsURLConnection conn;

                    sb.append("key=").append(URLEncoder.encode(key, "UTF-8")).append('&')
                        .append("egg=").append(URLEncoder.encode(easterEgg.getId(), "UTF-8")).append('&')
                        .append("who=").append(URLEncoder.encode(playerName, "UTF-8")).append('&')
                        .append("answer=").append(URLEncoder.encode(answer, "UTF-8"));

                    url = new URL(sb.toString());

                    conn = (HttpsURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);

                    if (conn.getResponseCode() != 200) {
                        if (conn.getResponseCode() == 403) {
                            plugin.getLogger().log(Level.SEVERE, "Server responded with 403, api key is incorrect,  url: " + sb.toString());
                            Bukkit.getScheduler().runTask(plugin, () -> {
                                Player p = Bukkit.getPlayer(playerUUID);
                                if (p == null) {
                                    return;
                                }
                                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                                p.sendMessage(ChatColor.RED + "An error occurred when registering your latest treasure find (key), if this continues please contact an Admin.");
                            });
                            return;
                        }
                        plugin.getLogger().log(Level.SEVERE, "Server responded with non 200 code, code: " + conn.getResponseCode() + " url: " + sb.toString());
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            Player p = Bukkit.getPlayer(playerUUID);
                            if (p == null) {
                                return;
                            }
                            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                            p.sendMessage(ChatColor.RED + "An error occurred when registering your latest treasure find (response code), if this continues please contact an Admin.");
                        });
                        return;
                    }

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();

                    in.lines().forEach(response::append);
                    in.close();

                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonResponse = (JsonObject) jsonParser.parse(response.toString());

                    conn.disconnect();

                    plugin.getLogger().log(Level.INFO, "Successfully registered player treasure question answer, player: " + playerName + " egg: " + easterEgg + " answer: " + answer);
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        Player p = Bukkit.getPlayer(playerUUID);
                        if (p == null) {
                            return;
                        }
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                        p.sendMessage(ChatColor.GREEN + "Your answer to the question \"" + ChatColor.LIGHT_PURPLE + easterEgg.getQuestion() + "\"" + ChatColor.GREEN + " has been submitted to the SSAGO answer server, you may view / edit your answers online.");
                    });
                } catch (UnsupportedEncodingException e) {
                    plugin.getLogger().log(Level.SEVERE, "UTF-8 encoding not supported, THIS SHOULD NEVER HAPPEN.", e);
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        Player p = Bukkit.getPlayer(playerUUID);
                        if (p == null) {
                            return;
                        }
                        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                        p.sendMessage(ChatColor.RED + "An error occurred when registering your latest treasure find (unsupported encoding), if this continues please contact an Admin.");
                    });
                } catch (MalformedURLException e) {
                    plugin.getLogger().log(Level.SEVERE, "Error when forming claim treasure url.", e);
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        Player p = Bukkit.getPlayer(playerUUID);
                        if (p == null) {
                            return;
                        }
                        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                        p.sendMessage(ChatColor.RED + "An error occurred when registering your latest treasure find (malformed url), if this continues please contact an Admin.");
                    });
                } catch (SocketTimeoutException e) {
                    plugin.getLogger().log(Level.SEVERE, "Connection to endpoint timed out.", e);
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        Player p = Bukkit.getPlayer(playerUUID);
                        if (p == null) {
                            return;
                        }
                        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                        p.sendMessage(ChatColor.RED + "An error occurred when registering your latest treasure find (timeout), if this continues please contact an Admin.");
                    });
                } catch (ProtocolException e) {
                    plugin.getLogger().log(Level.SEVERE, "Protocol error, set with GET.", e);
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        Player p = Bukkit.getPlayer(playerUUID);
                        if (p == null) {
                            return;
                        }
                        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                        p.sendMessage(ChatColor.RED + "An error occurred when registering your latest treasure find (protocol error), if this continues please contact an Admin.");
                    });
                } catch (JsonParseException e) {
                    plugin.getLogger().log(Level.SEVERE, "Json response was not.", e);
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        Player p = Bukkit.getPlayer(playerUUID);
                        if (p == null) {
                            return;
                        }
                        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                        p.sendMessage(ChatColor.RED + "An error occurred when registering your latest treasure find (invalid JSON), if this continues please contact an Admin.");
                    });
                } catch (IOException e) {
                    plugin.getLogger().log(Level.SEVERE, "IO exception occurred when communicating player treasure gain to the server.", e);
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        Player p = Bukkit.getPlayer(playerUUID);
                        if (p == null) {
                            return;
                        }
                        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                        p.sendMessage(ChatColor.RED + "An error occurred when registering your latest treasure find (IO), if this continues please contact an Admin.");
                    });
                }
            }
        });
    }
}
