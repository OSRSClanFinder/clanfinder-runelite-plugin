package com.clanfinder;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup(ClanFinderConfig.CONFIG_GROUP)
public interface ClanFinderConfig extends Config
{
    String CONFIG_GROUP = "clanfinder";

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
}
