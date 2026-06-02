package com.clanfinder;

import java.util.Collections;
import java.util.List;

final class ClanListing
{
    private String slug = "";
    private String name = "";
    private String clanChatName = "";
    private String headline = "";
    private String description = "";
    private String logoUrl = "";
    private String bannerUrl = "";
    private String bannerThumbnailUrl = "";
    private List<String> types = Collections.emptyList();
    private List<String> tags = Collections.emptyList();
    private List<ClanEvent> events = Collections.emptyList();
    private String region = "";
    private String timezone = "";
    private String language = "";
    private int memberCount;
    private ClanRequirements requirements = new ClanRequirements();
    private ClanLinks links = new ClanLinks();
    private boolean verified;
    private boolean sponsored;
    private boolean recruiting;
    private int publicEventCount;

    String getSlug()
    {
        return safe(slug);
    }

    String getName()
    {
        return safe(name);
    }

    String getClanChatName()
    {
        return safe(clanChatName);
    }

    String getHeadline()
    {
        return safe(headline);
    }

    String getDescription()
    {
        return safe(description);
    }

    String getLogoUrl()
    {
        return safe(logoUrl);
    }

    String getBannerUrl()
    {
        return safe(bannerUrl);
    }

    String getBannerThumbnailUrl()
    {
        return safe(bannerThumbnailUrl);
    }

    List<String> getTypes()
    {
        return types == null ? Collections.emptyList() : types;
    }

    List<String> getTags()
    {
        return tags == null ? Collections.emptyList() : tags;
    }

    List<ClanEvent> getEvents()
    {
        return events == null ? Collections.emptyList() : events;
    }

    String getRegion()
    {
        return safe(region);
    }

    String getTimezone()
    {
        return safe(timezone);
    }

    String getLanguage()
    {
        return safe(language);
    }

    int getMemberCount()
    {
        return memberCount;
    }

    ClanRequirements getRequirements()
    {
        return requirements == null ? new ClanRequirements() : requirements;
    }

    ClanLinks getLinks()
    {
        return links == null ? new ClanLinks() : links;
    }

    boolean isVerified()
    {
        return verified;
    }

    boolean isSponsored()
    {
        return sponsored;
    }

    boolean isRecruiting()
    {
        return recruiting;
    }

    int getPublicEventCount()
    {
        return publicEventCount;
    }

    private static String safe(String value)
    {
        return value == null ? "" : value;
    }
}
