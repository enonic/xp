package com.enonic.xp.admin.impl.rest.resource.application.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

public class MarkedApplicationsJson
{
    private final List<MarkedApplicationJson> hits;

    public MarkedApplicationsJson( final @JsonProperty("hits") List<MarkedApplicationJson> hits )
    {
        this.hits = hits;
    }

    public MarkedApplicationsJson()
    {
        this.hits = Lists.newArrayList();
    }

    public void add( final MarkedApplicationJson markedApplicationJson )
    {
        this.hits.add( markedApplicationJson );
    }

    @SuppressWarnings("unused")
    public List<MarkedApplicationJson> getHits()
    {
        return hits;
    }

    @SuppressWarnings("unused")
    public int getTotal()
    {
        return this.hits.size();
    }
}


