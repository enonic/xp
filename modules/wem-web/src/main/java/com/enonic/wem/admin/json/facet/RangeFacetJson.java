package com.enonic.wem.admin.json.facet;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.facet.RangeFacet;
import com.enonic.wem.api.facet.RangeFacetEntry;

public class RangeFacetJson
    extends FacetJson
{
    private final String type = "range";

    private final List<RangeFacetEntryJson> entries;

    public RangeFacetJson( final RangeFacet facet )
    {
        super( facet );

        ImmutableList.Builder<RangeFacetEntryJson> builder = ImmutableList.builder();
        for ( RangeFacetEntry result : facet.getResultEntries() )
        {
            builder.add( new RangeFacetEntryJson( result ) );
        }
        this.entries = builder.build();
    }

    @JsonProperty(value = "_type")
    public String getType()
    {
        return type;
    }

    public List<RangeFacetEntryJson> getEntries()
    {
        return entries;
    }

    public class RangeFacetEntryJson
        extends FacetJson.FacetEntryJson
    {

        private String from;

        private String to;

        public RangeFacetEntryJson( final RangeFacetEntry term )
        {
            super( term.getCount() );
            from = term.getFrom();
            to = term.getTo();
        }

        public String getFrom()
        {
            return from;
        }

        public String getTo()
        {
            return to;
        }
    }

}
