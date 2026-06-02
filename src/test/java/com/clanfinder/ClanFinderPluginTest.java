package com.clanfinder;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ClanFinderPluginTest
{
    public static void main(String[] args) throws Exception
    {
        ExternalPluginManager.loadBuiltin(ClanFinderPlugin.class);
        RuneLite.main(args);
    }
}
