package com.xadale.playerlogger.commands;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

public class CommandPurgeIp {

  public static int execute(CommandContext<CommandSourceStack> context, String ip, File logFile) {
    CommandSourceStack source = context.getSource();

    if (logFile == null || !logFile.exists()) {
      source.sendFailure(Component.literal("§cLog file not found."));
      return 0;
    }

    try {
      List<String> lines = Files.readAllLines(logFile.toPath(), StandardCharsets.UTF_8);

      List<String> filtered =
          lines.stream()
              .filter(
                  line -> {
                    String[] parts = line.split(";", 2);
                    return parts.length < 2 || !parts[1].trim().equals(ip);
                  })
              .collect(Collectors.toList());

      int removed = lines.size() - filtered.size();

      Files.write(logFile.toPath(), filtered, StandardCharsets.UTF_8);

      source.sendSuccess(
          () ->
              Component.literal(
                  "§bPurged §f"
                      + removed
                      + "§b entr"
                      + (removed == 1 ? "y" : "ies")
                      + " for IP: §f"
                      + ip),
          true);

      return removed;

    } catch (IOException e) {
      source.sendFailure(Component.literal("§cFailed to purge log file: " + e.getMessage()));
      return 0;
    }
  }
}
