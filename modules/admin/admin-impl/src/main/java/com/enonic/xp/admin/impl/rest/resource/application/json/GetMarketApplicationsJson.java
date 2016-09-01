package com.enonic.xp.admin.impl.rest.resource.application.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GetMarketApplicationsJson
{
    private String version;

    private Integer start;

    private Integer count;

    private List<String> ids;

    @JsonCreator
    public GetMarketApplicationsJson( @JsonProperty("version") final String version, @JsonProperty("start") final String start,
                                      @JsonProperty("count") final String count, @JsonProperty("ids") final List<String> ids )
    {
        this.version = version;
        this.start = parseInt( start, 0 );
        this.count = parseInt( count, 10 );
        this.ids = ids;
    }

    @JsonIgnore
    public String getVersion()
    {
        return version;
    }

    @JsonIgnore
    public Integer getStart()
    {
        return start;
    }

    @JsonIgnore
    public Integer getCount()
    {
        return count;
    }

    @JsonIgnore
    public List<String> getIds()
    {
        return ids;
    }

    private Integer parseInt( final String value, final int defaultValue )
    {
        try
        {
            return Integer.parseInt( value );
        }
        catch ( NumberFormatException e )
        {
            return defaultValue;
        }
    }
}
