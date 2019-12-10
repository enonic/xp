package com.enonic.xp.admin.impl.rest.resource.application.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.google.common.base.Strings.isNullOrEmpty;

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
        this.version = !isNullOrEmpty( version ) ? version : "1.0.0";
        this.start = parseInt( start, 0 );
        this.count = parseInt( count, 10 );
        this.ids = ids;
    }

    public String getVersion()
    {
        return version;
    }

    public Integer getStart()
    {
        return start;
    }

    public Integer getCount()
    {
        return count;
    }

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
