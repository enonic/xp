package com.enonic.xp.aggregation;


import java.time.Instant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DateHistogramBucketTest
{
    @Test
    void builder()
    {
        DateHistogramBucket.Builder builder = DateHistogramBucket.create();

        long time = System.currentTimeMillis();
        Instant instant = Instant.ofEpochMilli( time );
        builder.keyAsInstant( instant );
        DateHistogramBucket dateHistogramBucket = builder.build();

        assertNotNull( dateHistogramBucket.getKeyAsInstant() );
        assertEquals( time, dateHistogramBucket.getKeyAsInstant().toEpochMilli() );
    }
}
