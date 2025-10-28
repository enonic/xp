package com.enonic.xp.aggregation;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class NumericRangeBucketTest
{
    @Test
    void builder()
    {
        NumericRangeBucket.Builder builder = NumericRangeBucket.create();

        builder.from( -777 );
        builder.to( 1000.9 );

        NumericRangeBucket numericRangeBucket = builder.build();

        assertNotNull( numericRangeBucket.getFrom() );
        assertNotNull( numericRangeBucket.getTo() );

        assertEquals( -777, numericRangeBucket.getFrom() );
        assertEquals( 1000.9, numericRangeBucket.getTo() );
    }
}
