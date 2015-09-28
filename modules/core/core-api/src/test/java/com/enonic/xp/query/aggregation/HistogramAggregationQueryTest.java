package com.enonic.xp.query.aggregation;

import org.junit.Test;

import static org.junit.Assert.*;

public class HistogramAggregationQueryTest
{
    @Test
    public void testBuilder()
    {
        final HistogramAggregationQuery query = HistogramAggregationQuery.create( "histogram" ).
            extendedBoundMax( 100 ).
            extendedBoundMin( 10 ).
            order( HistogramAggregationQuery.Order.COUNT_ASC ).
            interval( 10L ).
            fieldName( "fieldName" ).
            minDocCount( 10L ).
            build();

        assertEquals( "histogram", query.getName() );
        assertEquals( new Long( 100 ), query.getExtendedBoundMax() );
        assertEquals( new Long( 10 ), query.getExtendedBoundMin() );
        assertEquals( HistogramAggregationQuery.Order.COUNT_ASC, query.getOrder() );
        assertEquals( new Long( 10 ), query.getInterval() );
        assertEquals( "fieldName", query.getFieldName() );
        assertEquals( new Long( 10 ), query.getMinDocCount() );
    }
}
