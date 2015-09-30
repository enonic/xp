package com.enonic.wem.repo.internal.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AggregationBuilder;

import com.enonic.wem.repo.internal.elasticsearch.query.translator.QueryFieldNameResolver;
import com.enonic.wem.repo.internal.elasticsearch.query.translator.builder.AbstractBuilderFactory;
import com.enonic.xp.query.aggregation.AbstractRangeAggregationQuery;
import com.enonic.xp.query.aggregation.DateRangeAggregationQuery;
import com.enonic.xp.query.aggregation.GeoDistanceAggregationQuery;
import com.enonic.xp.query.aggregation.NumericRangeAggregationQuery;

class RangeAggregationQueryBuilderFactory
    extends AbstractBuilderFactory
{
    public RangeAggregationQueryBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    AggregationBuilder create( final AbstractRangeAggregationQuery aggregationQuery )
    {
        if ( aggregationQuery instanceof DateRangeAggregationQuery )
        {
            return new DateRangeAggregationQueryBuilderFactory( fieldNameResolver ).create( (DateRangeAggregationQuery) aggregationQuery );
        }
        else if ( aggregationQuery instanceof NumericRangeAggregationQuery )
        {
            return new NumericRangeAggregationQueryFactory( fieldNameResolver ).create( (NumericRangeAggregationQuery) aggregationQuery );
        }
        else if ( aggregationQuery instanceof GeoDistanceAggregationQuery )
        {
            return new GeoDistanceAggregationQueryBuilderFactory( fieldNameResolver ).create(
                (GeoDistanceAggregationQuery) aggregationQuery );
        }
        else
        {
            throw new UnsupportedOperationException(
                "Range aggreagations of type  " + aggregationQuery.getClass().getName() + " not implemented" );
        }
    }


}
