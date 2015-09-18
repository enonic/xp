package com.enonic.xp.repo.impl.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AggregationBuilder;

import com.enonic.xp.query.aggregation.AbstractRangeAggregationQuery;
import com.enonic.xp.query.aggregation.DateRangeAggregationQuery;
import com.enonic.xp.query.aggregation.GeoDistanceAggregationQuery;
import com.enonic.xp.query.aggregation.NumericRangeAggregationQuery;

class RangeAggregationQueryBuilderFactory
{
    static AggregationBuilder create( final AbstractRangeAggregationQuery aggregationQuery )
    {
        if ( aggregationQuery instanceof DateRangeAggregationQuery )
        {
            return DateRangeAggregationQueryBuilderFactory.create( (DateRangeAggregationQuery) aggregationQuery );
        }
        else if ( aggregationQuery instanceof NumericRangeAggregationQuery )
        {
            return NumericRangeAggregationQueryFactory.create( (NumericRangeAggregationQuery) aggregationQuery );
        }
        else if ( aggregationQuery instanceof GeoDistanceAggregationQuery )
        {
            return GeoDistanceAggregationQueryBuilderFactory.create( (GeoDistanceAggregationQuery) aggregationQuery );
        }
        else
        {
            throw new UnsupportedOperationException(
                "Range aggreagations of type  " + aggregationQuery.getClass().getName() + " not implemented" );
        }
    }


}
