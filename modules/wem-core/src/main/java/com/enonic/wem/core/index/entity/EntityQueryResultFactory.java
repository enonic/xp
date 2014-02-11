package com.enonic.wem.core.index.entity;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHits;

import com.enonic.wem.core.index.aggregation.AggregationsFactory;
import com.enonic.wem.core.index.facet.FacetsFactory;

public class EntityQueryResultFactory
{
    private FacetsFactory facetsFactory = new FacetsFactory();

    private AggregationsFactory aggregationsFactory = new AggregationsFactory();

    public EntityQueryResult create( final SearchResponse searchResponse )
    {
        final SearchHits hits = searchResponse.getHits();

        return EntityQueryResult.newResult().
            hits( hits.getHits() != null ? hits.getHits().length : 0 ).
            totalHits( hits.totalHits() ).
            maxScore( hits.maxScore() ).
            addEntries( hits.getHits() ).
            facets( facetsFactory.create( searchResponse.getFacets() ) ).
            aggregations( aggregationsFactory.create( searchResponse.getAggregations() ) ).
            build();
    }
}
