package com.xadale.playerlogger;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.xadale.playerlogger.commands.HandleAltsCommand;
import com.xadale.playerlogger.commands.ListIpsWithMultiplePlayers;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class Commands {

  private final PlayerLogger altx;

  public Commands(PlayerLogger altx) {
    this.altx = altx;
  }

  public void register() {
    // Register the command
    CommandRegistrationCallback.EVENT.register(
        (dispatcher, registryAccess, environment) -> {
          // Root command AltX
          dispatcher.register(
              net.minecraft.commands.Commands.literal("altx")
                  .requires(Permissions.require("altx.command", 4))
                  .executes(
                      context -> {
                        StringBuilder help = new StringBuilder();
                        help.append("§bAltX by MrCookiePrincess");
                        help.append("\n§3Commands:");
                        help.append("\n§b/altx §fShows a list of available AltX commands");

                        final CommandSourceStack source = context.getSource();

                        if (Permissions.check(source, "altx.list", 4)) {
                          help.append(
                              "\n§b/altx list §fShows a list of players using the same IP address");
                        }

                        if (Permissions.check(source, "altx.trace", 4)) {
                          help.append(
                              "\n§b/altx trace <player> §fShows all players on given players IP address");

                          if (Permissions.check(source, "altx.viewips", 4)) {
                            help.append(
                                "\n§b/altx trace <ip> §fShows all players on given IP address");
                          }
                        }
                        help.append("\n§3Special thanks to the Ordinary SMP team!");

                        source
                            .sendSuccess(() -> Component.literal(help.toString()), false);

                        return 1; // Return success
                      })
                  // Command Trace
                  .then(
                          net.minecraft.commands.Commands.literal("trace")
                          .requires(Permissions.require("altx.trace", 4))
                          .then(
                                  net.minecraft.commands.Commands.argument("query", StringArgumentType.string())
                                  .suggests(
                                      (context, builder) -> {
                                        String partialQuery =
                                            builder.getRemaining(); // Get the current typed string
                                        for (ServerPlayer player :
                                            context
                                                .getSource()
                                                .getServer()
                                                .getPlayerList()
                                                .getPlayers()) {
                                          String playerName = player.getName().getString();
                                          if (playerName
                                              .toLowerCase()
                                              .startsWith(partialQuery.toLowerCase())) {
                                            builder.suggest(playerName);
                                          }
                                        }
                                        return builder.buildFuture();
                                      })
                                  .executes(
                                      (context) ->
                                          HandleAltsCommand.execute(
                                              context, this.altx.getLogFile()))))

                  // Command list
                  .then(
                          net.minecraft.commands.Commands.literal("list")
                          .requires(Permissions.require("altx.list", 4))
                          .executes(
                              (context) ->
                                  ListIpsWithMultiplePlayers.execute(
                                      context, this.altx.getLogFile()))));
        });
  }
}
