package com.enonic.wem.admin.rest.resource.content.model;


import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.facet.DateHistogramFacet;
import com.enonic.wem.api.facet.DateHistogramFacetEntry;


public class DateHistogramFacetJson
    extends AbstractFacetJson
{
    private final String type = "dateHistogram";

    private final List<DateHistogramFacetEntryJson> entries;

    public DateHistogramFacetJson( final DateHistogramFacet facet )
    {
        super( facet );

        ImmutableList.Builder<DateHistogramFacetEntryJson> builder = ImmutableList.builder();
        for ( DateHistogramFacetEntry result : facet.getResultEntries() )
        {
            builder.add( new DateHistogramFacetEntryJson( result ) );
        }
        this.entries = builder.build();
    }

    @JsonProperty(value = "_type")
    public String getType()
    {
        return type;
    }

    public List<DateHistogramFacetEntryJson> getEntries()
    {
        return entries;
    }

    public class DateHistogramFacetEntryJson
        extends AbstractFacetJson.FacetEntryJson
    {

        private Long time;

        public DateHistogramFacetEntryJson( final DateHistogramFacetEntry term )
        {
            super( term.getCount() );
            time = term.getTime();
        }

        public Long getTime()
        {
            return time;
        }
    }

}
