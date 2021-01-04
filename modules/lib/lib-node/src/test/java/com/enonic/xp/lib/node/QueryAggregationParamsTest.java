package com.enonic.xp.lib.node;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.enonic.xp.query.aggregation.AggregationQueries;
import com.enonic.xp.query.aggregation.metric.MaxAggregationQuery;
import com.enonic.xp.query.aggregation.metric.MinAggregationQuery;
import com.enonic.xp.query.aggregation.metric.ValueCountAggregationQuery;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QueryAggregationParamsTest
{
    @Test
    public void test()
    {
        final Map<String, Object> aggregations = new HashMap<>();
        aggregations.put( "maxPrice", createMetricAggregationAsMap( "max", "fieldName1" ) );
        aggregations.put( "minPrice", createMetricAggregationAsMap( "min", "fieldName2" ) );
        aggregations.put( "countNodesWithNonEmptyPrice", createMetricAggregationAsMap( "count", "fieldName3" ) );

        final AggregationQueries result = new QueryAggregationParams().getAggregations( aggregations );

        assertEquals( 3, result.getSize() );

        result.forEach( aggregationQuery -> {
            if ( aggregationQuery instanceof MaxAggregationQuery )
            {
                assertEquals( "maxPrice", aggregationQuery.getName() );
                assertEquals( "fieldName1", ( (MaxAggregationQuery) aggregationQuery ).getFieldName() );
            }
            else if ( aggregationQuery instanceof MinAggregationQuery )
            {
                assertEquals( "minPrice", aggregationQuery.getName() );
                assertEquals( "fieldName2", ( (MinAggregationQuery) aggregationQuery ).getFieldName() );
            }
            else
            {
                assertEquals( "countNodesWithNonEmptyPrice", aggregationQuery.getName() );
                assertEquals( "fieldName3", ( (ValueCountAggregationQuery) aggregationQuery ).getFieldName() );
            }
        } );
    }

    private Map<String, Object> createMetricAggregationAsMap( final String name, final String fieldName )
    {
        final Map<String, Object> aggregationAsMap = new HashMap<>();
        aggregationAsMap.put( name, Collections.singletonMap( "field", fieldName ) );
        return aggregationAsMap;
    }
}
