package com.clanfinder;

final class ClanRequirements
{
    private int combatLevel;
    private int totalLevel;
    private int questPoints;
    private int raidKc;
    private boolean discordRequired;
    private boolean applicationRequired;
    private boolean microphoneRequired;

    int getCombatLevel()
    {
        return combatLevel;
    }

    int getTotalLevel()
    {
        return totalLevel;
    }

    int getQuestPoints()
    {
        return questPoints;
    }

    int getRaidKc()
    {
        return raidKc;
    }

    boolean isDiscordRequired()
    {
        return discordRequired;
    }

    boolean isApplicationRequired()
    {
        return applicationRequired;
    }

    boolean isMicrophoneRequired()
    {
        return microphoneRequired;
    }
}
