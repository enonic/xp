package com.enonic.wem.core.index.facet;

import com.enonic.wem.api.facet.QueryFacet;

public class QueryFacetFactory
{

    public QueryFacet create( final org.elasticsearch.search.facet.query.QueryFacet searchResponseFacet )
    {
        return new QueryFacet( searchResponseFacet.getName(), searchResponseFacet.getCount() );
    }

}


