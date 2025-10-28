package com.enonic.xp.query.aggregation;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AggregationQueriesTest
{
    @Test
    void testBuilder()
    {
        final NumericRange numericRange1 = NumericRange.create().key( "key" ).from( 0.0 ).to( 1.0 ).build();
        final NumericRange numericRange2 = NumericRange.create().key( "key2" ).from( 20.0 ).to( 31.0 ).build();

        final NumericRangeAggregationQuery query = NumericRangeAggregationQuery.create( "query" ).
            setRanges( Arrays.asList( numericRange1, numericRange2 ) ).
            build();

        final AggregationQueries queries = AggregationQueries.create().
            add( query ).
            build();

        assertNotNull( queries );
        assertFalse( queries.isEmpty() );
        assertTrue( queries.contains( query ) );
    }

    @Test
    void testEmpty()
    {
        final AggregationQueries queries = AggregationQueries.empty();

        assertTrue( queries.isEmpty() );
    }
}
