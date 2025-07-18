package com.xadale.playerlogger;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.mojang.logging.LogUtils;

public class WebhookMessageSender {

  private static WebhookClient webhookClient = null;
  private static final String WEBHOOK_NAME = "AltX Notification";
  private static final String WEBHOOK_AVATAR_URL =
      "https://raw.githubusercontent.com/OrdinarySMP/AltX/main/assets/AltX-avatar.png";

  public static void reload() {
    if (!PlayerLogger.getInstance().getConfig().altNotifs.enableWebhookNotifs) {
      webhookClient = null;
      return;
    }

    String webhookUrl = PlayerLogger.getInstance().getConfig().altNotifs.webhookUrl;
    try {
      webhookClient = WebhookClient.withUrl(webhookUrl);
    } catch (IllegalArgumentException e) {
      webhookClient = null;
      LogUtils.getLogger().warn("Failed to load webhook: " + e.getMessage());
    }
  }

  public static void sendMessage(String messageString) {
    if (webhookClient == null) {
      return;
    }
    WebhookMessage message =
        new WebhookMessageBuilder()
            .setContent(messageString)
            .setUsername(WEBHOOK_NAME)
            .setAvatarUrl(WEBHOOK_AVATAR_URL)
            .build();

    webhookClient.send(message);
  }
}
