package com.enonic.xp.aggregation;


import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;


public class NumericRangeBucketTest
{
    @Test
    public void builder()
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
