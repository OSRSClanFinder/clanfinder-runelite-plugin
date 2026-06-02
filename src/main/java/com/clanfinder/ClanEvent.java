package com.clanfinder;

final class ClanEvent
{
    private String id = "";
    private String title = "";
    private String category = "";
    private String description = "";
    private String imageUrl = "";
    private String startAt = "";
    private String endAt = "";
    private String timezone = "";
    private String host = "";
    private String requirements = "";
    private String status = "";
    private boolean isPublic;
    private int favoriteCount;

    String getId()
    {
        return safe(id);
    }

    String getTitle()
    {
        return safe(title);
    }

    String getCategory()
    {
        return safe(category);
    }

    String getDescription()
    {
        return safe(description);
    }

    String getImageUrl()
    {
        return safe(imageUrl);
    }

    String getStartAt()
    {
        return safe(startAt);
    }

    String getEndAt()
    {
        return safe(endAt);
    }

    String getTimezone()
    {
        return safe(timezone);
    }

    String getHost()
    {
        return safe(host);
    }

    String getRequirements()
    {
        return safe(requirements);
    }

    String getStatus()
    {
        return safe(status);
    }

    boolean isPublic()
    {
        return isPublic;
    }

    int getFavoriteCount()
    {
        return favoriteCount;
    }

    private static String safe(String value)
    {
        return value == null ? "" : value;
    }
}
