package com.clanfinder;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup(ClanFinderConfig.CONFIG_GROUP)
public interface ClanFinderConfig extends Config
{
    String CONFIG_GROUP = "clanfinder";

    @ConfigItem(
        keyName = "apiBaseUrl",
        name = "API base URL",
        description = "ClanFinder website origin used for public API requests."
    )
    default String apiBaseUrl()
    {
        return ClanFinderApiClient.DEFAULT_BASE_URL;
    }

    @ConfigItem(
        keyName = "websiteBaseUrl",
        name = "Website base URL",
        description = "ClanFinder website origin used when opening clan detail pages."
    )
    default String websiteBaseUrl()
    {
        return ClanFinderApiClient.DEFAULT_BASE_URL;
    }

    @Range(
        min = 5,
        max = 50
    )
    @ConfigItem(
        keyName = "resultsLimit",
        name = "Results limit",
        description = "Maximum approved clan listings to show per refresh."
    )
    default int resultsLimit()
    {
        return 25;
    }

    @ConfigItem(
        keyName = "sendAnonymousActivityPing",
        name = "Anonymous usage count",
        description = "Send an anonymous plugin-session heartbeat to ClanFinder. This does not report clan or member online status."
    )
    default boolean sendAnonymousActivityPing()
    {
        return false;
    }
}
