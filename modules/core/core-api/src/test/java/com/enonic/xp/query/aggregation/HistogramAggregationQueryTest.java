package com.enonic.xp.query.aggregation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HistogramAggregationQueryTest
{
    @Test
    void testBuilder()
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
        assertEquals( 100, query.getExtendedBoundMax() );
        assertEquals( 10, query.getExtendedBoundMin() );
        assertEquals( HistogramAggregationQuery.Order.COUNT_ASC, query.getOrder() );
        assertEquals( 10, query.getInterval() );
        assertEquals( "fieldName", query.getFieldName() );
        assertEquals( 10, query.getMinDocCount() );
    }
}
