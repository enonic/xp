package com.enonic.wem.admin.rest.resource.content.model;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.facet.DateHistogramFacet;
import com.enonic.wem.api.facet.Facet;
import com.enonic.wem.api.facet.Facets;
import com.enonic.wem.api.facet.QueryFacet;
import com.enonic.wem.api.facet.RangeFacet;
import com.enonic.wem.api.facet.TermsFacet;

public class FacetedContentSummaryListJson
    extends ContentSummaryListJson
{
    private List<AbstractFacetJson> facets;

    public FacetedContentSummaryListJson( Content content, Facets facets )
    {
        this( Contents.from( content ), facets );
    }

    public FacetedContentSummaryListJson( final Contents contents, final Facets facets )
    {
        super( contents );

        this.facets = buildFacets( facets );
    }

    public List<AbstractFacetJson> getFacets()
    {
        return facets;
    }

    private List<AbstractFacetJson> buildFacets( Facets facets )
    {
        final ImmutableList.Builder<AbstractFacetJson> builder = ImmutableList.builder();
        if ( facets != null )
        {
            for ( final Facet facet : facets )
            {
                if ( facet instanceof TermsFacet )
                {
                    builder.add( new TermsFacetJson( (TermsFacet) facet ) );
                }
                else if ( facet instanceof DateHistogramFacet )
                {
                    builder.add( new DateHistogramFacetJson( (DateHistogramFacet) facet ) );
                }
                else if ( facet instanceof RangeFacet )
                {
                    builder.add( new RangeFacetJson( (RangeFacet) facet ) );
                }
                else if ( facet instanceof QueryFacet )
                {
                    builder.add( new QueryFacetJson( (QueryFacet) facet ) );
                }
            }
        }
        return builder.build();
    }
}
