package com.clanfinder;

import java.util.Collections;
import java.util.List;

final class ClanSearchResponse
{
    private List<ClanListing> data = Collections.emptyList();
    private ClanSearchPagination pagination = new ClanSearchPagination();

    List<ClanListing> getData()
    {
        return data == null ? Collections.emptyList() : data;
    }

    ClanSearchPagination getPagination()
    {
        return pagination == null ? new ClanSearchPagination() : pagination;
    }
}
