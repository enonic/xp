package com.enonic.xp.query.aggregation;

import org.junit.Test;

import static org.junit.Assert.*;

public class DateHistogramAggregationQueryTest
{
    @Test
    public void testBuilder()
    {
        final DateHistogramAggregationQuery query = DateHistogramAggregationQuery.create( "query" ).
            format( "format" ).
            build();

        assertNotNull( query );
        assertEquals( "format", query.getFormat() );
        assertEquals( "query", query.getName() );
    }
}
