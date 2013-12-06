package com.enonic.wem.core.index.query.facet;

import java.util.Collection;
import java.util.Set;

import org.elasticsearch.search.facet.FacetBuilder;

import com.google.common.collect.Sets;

import com.enonic.wem.query.facet.FacetQuery;
import com.enonic.wem.query.facet.QueryFacetQuery;
import com.enonic.wem.query.facet.TermsFacetQuery;

public class FacetBuilderFactory
{

    private TermsFacetBuilderFactory termsFacetBuilderFactory = new TermsFacetBuilderFactory();

    private QueryFacetBuilderFactory queryFacetBuilderFactory = new QueryFacetBuilderFactory();

    public Set<FacetBuilder> create( final Collection<FacetQuery> facetQueries )
    {
        final Set<FacetBuilder> facetBuilders = Sets.newHashSet();

        for ( final FacetQuery facetQuery : facetQueries )
        {
            if ( facetQuery instanceof TermsFacetQuery )
            {
                facetBuilders.add( termsFacetBuilderFactory.create( (TermsFacetQuery) facetQuery ) );
            }
            else if ( facetQuery instanceof QueryFacetQuery )
            {
                facetBuilders.add( queryFacetBuilderFactory.create( (QueryFacetQuery) facetQuery ) );
            }
        }

        return facetBuilders;
    }

}
