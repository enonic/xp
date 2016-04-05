package com.enonic.xp.admin.impl.rest.resource.application.json;

import java.util.Map;

import com.google.common.collect.Maps;

public class MarketApplicationsJson
{
    private Map<String, MarketApplicationJson> hits;

    private int total;

    public MarketApplicationsJson()
    {
        hits = Maps.newHashMap();
    }

    public Map<String, MarketApplicationJson> getHits()
    {
        return hits;
    }

    public MarketApplicationsJson add( final String key, final MarketApplicationJson marketApplicationJson )
    {
        this.hits.put( key, marketApplicationJson );
        return this;
    }

    @SuppressWarnings("unused")
    public int getTotal()
    {
        return this.total;
    }
}


