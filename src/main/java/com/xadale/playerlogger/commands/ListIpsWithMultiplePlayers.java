package com.xadale.playerlogger.commands;

import com.mojang.brigadier.context.CommandContext;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;

public class ListIpsWithMultiplePlayers {
  public static int execute(CommandContext<CommandSourceStack> context, File logFile) {
    Map<String, Set<String>> ipToPlayers = new HashMap<>();

    // check if show ips
    final CommandSourceStack source = context.getSource();

    try {
      ListIpsWithMultiplePlayers.readLogFile(logFile, ipToPlayers);
    } catch (IOException e) {
      source.sendFailure(Component.literal("§cFailed to read log file: " + e.getMessage()));
      return 0;
    }

    // Filter and build the result
      MutableComponent response = Component.literal("§bIPs with two or more users:");
    boolean found = false;

    for (Map.Entry<String, Set<String>> entry : ipToPlayers.entrySet()) {
      if (entry.getValue().size() >= 2) {
        found = true;
        response.append("\n");
        if (Permissions.check(source, "altx.viewips", 4)) {
          response.append("§3- (§b").append(getIpText(entry.getKey())).append("§3): §f");
        } else {
          response.append("§3- §f");
        }
        response.append(String.join(", ", entry.getValue()));
      }
    }

    if (!found) {
      response.append("§c No IPs with two or more players found.");
    }

    // Send the response
    source.sendSuccess(() -> response, false);

    return 1;
  }

  private static Component getIpText(String ip) {
    return Component.literal(ip)
        .withStyle(
            style ->
                style
                    .withColor(ChatFormatting.AQUA) // §b
                    .withClickEvent(new ClickEvent.CopyToClipboard(ip))
                    .withHoverEvent(new HoverEvent.ShowText(Component.literal("Copy"))));
  }

  private static void readLogFile(File logFile, Map<String, Set<String>> ipToPlayers)
      throws IOException {
    // Load the IP-to-players map from the log file
    BufferedReader reader = new BufferedReader(new FileReader(logFile));
    String line;
    while ((line = reader.readLine()) != null) {
      String[] parts = line.split(";");
      if (parts.length == 2) {
        String playerName = parts[0].trim();
        String ipAddress = parts[1].trim();

        ipToPlayers.computeIfAbsent(ipAddress, k -> new HashSet<>()).add(playerName);
      }
    }
  }
}
