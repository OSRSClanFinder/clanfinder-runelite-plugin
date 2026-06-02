package com.clanfinder;

final class ClanLinks
{
    private String discord = "";
    private String website = "";
    private String application = "";

    String getDiscord()
    {
        return safe(discord);
    }

    String getWebsite()
    {
        return safe(website);
    }

    String getApplication()
    {
        return safe(application);
    }

    private static String safe(String value)
    {
        return value == null ? "" : value;
    }
}
