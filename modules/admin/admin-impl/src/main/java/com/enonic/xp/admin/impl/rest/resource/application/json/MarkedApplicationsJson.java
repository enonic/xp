package com.enonic.xp.admin.impl.rest.resource.application.json;

import java.util.Map;

import com.google.common.collect.Maps;

public class MarkedApplicationsJson
{
    private Map<String, MarkedApplicationJson> hits;

    public MarkedApplicationsJson()
    {
        hits = Maps.newHashMap();
    }

    public Map<String, MarkedApplicationJson> getHits()
    {
        return hits;
    }

    public MarkedApplicationsJson add( final String key, final MarkedApplicationJson markedApplicationJson )
    {
        this.hits.put( key, markedApplicationJson );
        return this;
    }

    @SuppressWarnings("unused")
    public int getTotal()
    {
        return this.hits.keySet().size();
    }
}


