package com.enonic.wem.core.index.query.facet;

import org.elasticsearch.search.facet.query.QueryFacetBuilder;

import com.enonic.wem.api.query.facet.QueryFacetQuery;
import com.enonic.wem.core.index.query.AbstractBuilderFactory;
import com.enonic.wem.core.index.query.FilterBuilderFactory;
import com.enonic.wem.core.index.query.QueryBuilderFactory;

public class QueryFacetBuilderFactory
    extends AbstractBuilderFactory
{
    private FilterBuilderFactory filterBuilderFactory = new FilterBuilderFactory();

    private QueryBuilderFactory queryBuilderFactory = new QueryBuilderFactory();

    public QueryFacetBuilder create( final QueryFacetQuery queryFacetQuery )
    {
        QueryFacetBuilder builder = new QueryFacetBuilder( queryFacetQuery.getName() );
        builder.facetFilter( filterBuilderFactory.create( queryFacetQuery.getQueryFilter() ) );
        builder.query( queryBuilderFactory.create( queryFacetQuery.getQuery(), null ) );

        return builder;
    }

}
