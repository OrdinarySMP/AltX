package com.xadale.playerlogger;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.mojang.logging.LogUtils;

public class WebhookMessageSender {

  private static WebhookClient webhookClient = setupWebhook();
  private static final String WEBHOOK_URL =
      PlayerLogger.getInstance().getConfig().altNotifs.webhookUrl;
  private static final String WEBHOOK_NAME = "AltX Notification";
  private static final String WEBHOOK_AVATAR_URL =
      "https://raw.githubusercontent.com/OrdinarySMP/AltX/main/assets/AltX-avatar.png";

  private static WebhookClient setupWebhook() {
    if (!PlayerLogger.getInstance().getConfig().altNotifs.enableWebhookNotifs) {
      return null;
    }

    WebhookClient client = null;
    try {
      client = WebhookClient.withUrl(WEBHOOK_URL);
    } catch (IllegalArgumentException e) {
      LogUtils.getLogger().warn("Failed to load webhook: " + e.getMessage());
    }
    return client;
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
