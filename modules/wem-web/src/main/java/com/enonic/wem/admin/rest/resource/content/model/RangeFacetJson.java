package com.enonic.wem.admin.rest.resource.content.model;


import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.facet.RangeFacet;
import com.enonic.wem.api.facet.RangeFacetEntry;

public class RangeFacetJson
    extends AbstractFacetJson
{
    private String type;

    private List<RangeFacetEntryJson> terms;

    public RangeFacetJson( final RangeFacet facet )
    {
        super( facet );
        this.type = "range";

        ImmutableList.Builder<RangeFacetEntryJson> builder = ImmutableList.builder();
        for ( RangeFacetEntry result : facet.getResultEntries() )
        {
            builder.add( new RangeFacetEntryJson( result ) );
        }
        this.terms = builder.build();
    }

    @JsonProperty(value = "_type")
    public String getType()
    {
        return type;
    }

    public List<RangeFacetEntryJson> getTerms()
    {
        return terms;
    }

    public class RangeFacetEntryJson
        extends AbstractFacetJson.FacetEntryJson
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
