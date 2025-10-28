package com.enonic.xp.query.aggregation;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NumericRangeAggregationQueryTest
{
    @Test
    void testBuilder()
    {
        final NumericRange numericRange1 = NumericRange.create().key( "key" ).from( 0.0 ).to( 1.0 ).build();
        final NumericRange numericRange2 = NumericRange.create().key( "key2" ).from( 20.0 ).to( 31.0 ).build();

        final NumericRangeAggregationQuery query = NumericRangeAggregationQuery.create( "query" ).
            setRanges( Arrays.asList( numericRange1, numericRange2 ) ).
            build();

        assertEquals( "query", query.getName() );
        assertEquals( 2, query.getRanges().size() );
    }

}
