package com.enonic.cms.web.rest.account;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

public final class SearchFacetModel
{
    private String name;

    @JsonProperty("terms")
    private Map<String, Integer> entries;

    public SearchFacetModel( String name )
    {
        this.name = name;
        entries = new HashMap<String, Integer>();
    }

    public void setEntryCount( String entryName, int count )
    {
        entries.put( entryName, count );
    }

    public String getName()
    {
        return name;
    }
}
