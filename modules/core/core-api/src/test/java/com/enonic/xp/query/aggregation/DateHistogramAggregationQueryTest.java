package com.enonic.xp.query.aggregation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DateHistogramAggregationQueryTest
{
    @Test
    void testBuilder()
    {
        final DateHistogramAggregationQuery query = DateHistogramAggregationQuery.create( "query" ).
            format( "format" ).
            build();

        assertNotNull( query );
        assertEquals( "format", query.getFormat() );
        assertEquals( "query", query.getName() );
    }
}
