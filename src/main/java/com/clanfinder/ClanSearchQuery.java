package com.clanfinder;

final class ClanSearchQuery
{
    private final String search;
    private final String type;
    private final String region;
    private final int page;
    private final int limit;

    ClanSearchQuery(String search, String type, String region, int limit)
    {
        this(search, type, region, 1, limit);
    }

    ClanSearchQuery(String search, String type, String region, int page, int limit)
    {
        this.search = clean(search);
        this.type = cleanChoice(type);
        this.region = cleanChoice(region);
        this.page = Math.max(1, page);
        this.limit = Math.max(5, Math.min(limit, 50));
    }

    String getSearch()
    {
        return search;
    }

    String getType()
    {
        return type;
    }

    String getRegion()
    {
        return region;
    }

    int getPage()
    {
        return page;
    }

    int getLimit()
    {
        return limit;
    }

    ClanSearchQuery withLimit(int nextLimit)
    {
        return new ClanSearchQuery(search, type, region, page, nextLimit);
    }

    ClanSearchQuery withPage(int nextPage)
    {
        return new ClanSearchQuery(search, type, region, nextPage, limit);
    }

    private static String clean(String value)
    {
        return value == null ? "" : value.trim();
    }

    private static String cleanChoice(String value)
    {
        String cleaned = clean(value);
        return cleaned.startsWith("All ") ? "" : cleaned;
    }
}
