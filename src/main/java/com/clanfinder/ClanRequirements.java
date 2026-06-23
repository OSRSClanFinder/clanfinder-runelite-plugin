package com.clanfinder;

final class ClanRequirements
{
    private int combatLevel;
    private int totalLevel;
    private int questPoints;
    private int raidKc;
    private boolean discordRequired;
    private boolean applicationRequired;
    private String applicationInstructions = "";
    private boolean microphoneRequired;
    private String notes = "";

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

    String getApplicationInstructions()
    {
        return safe(applicationInstructions);
    }

    boolean isMicrophoneRequired()
    {
        return microphoneRequired;
    }

    String getNotes()
    {
        return safe(notes);
    }

    private static String safe(String value)
    {
        return value == null ? "" : value;
    }
}
