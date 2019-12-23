package com.enonic.xp.query.aggregation;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.query.aggregation.metric.MaxAggregationQuery;
import com.enonic.xp.query.aggregation.metric.MinAggregationQuery;
import com.enonic.xp.query.aggregation.metric.StatsAggregationQuery;
import com.enonic.xp.query.aggregation.metric.ValueCountAggregationQuery;
import com.enonic.xp.util.GeoPoint;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BucketAggregationQueryTest
{
    @Test
    public void addSubQueries()
    {
        final GeoDistanceAggregationQuery.Builder builder = GeoDistanceAggregationQuery.create( "geo" ).
            unit( "inch" ).
            origin( GeoPoint.from( "20,30" ) );

        builder.addSubQueries( generateSubQueries() );

        final BucketAggregationQuery query = builder.build();

        assertEquals( 4, query.getSubQueries().getSize() );
    }

    private List<AggregationQuery> generateSubQueries()
    {
        final MaxAggregationQuery query1 = MaxAggregationQuery.create( "query1" ).build();
        final MinAggregationQuery query2 = MinAggregationQuery.create( "query2" ).build();
        final StatsAggregationQuery query3 = StatsAggregationQuery.create( "query3" ).build();
        final ValueCountAggregationQuery query4 = ValueCountAggregationQuery.create( "query4" ).build();
        return Arrays.asList( query1, query2, query3, query4 );
    }
}
