package com.enonic.wem.core.index.elastic.result;

import org.elasticsearch.search.facet.query.QueryFacet;

import com.enonic.wem.api.query.QueryFacetResultSet;

public class QueryFacetResultSetFactory
    extends AbstractFacetResultSetFactory
{

    protected static QueryFacetResultSet create( final String facetName, final QueryFacet facet )
    {
        QueryFacetResultSet queryFacetResultSet = new QueryFacetResultSet( facet.getCount() );
        queryFacetResultSet.setName( facetName );

        return queryFacetResultSet;
    }


}
