package com.enonic.xp.aggregation;


import java.time.Instant;
import java.util.Date;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class DateHistogramBucketTest
{
    @Test
    public void builder()
    {
        DateHistogramBucket.Builder builder = DateHistogramBucket.create();

        long time = System.currentTimeMillis();
        Instant instant = new Date( time ).toInstant();
        builder.keyAsInstant( instant );
        DateHistogramBucket dateHistogramBucket = builder.build();

        assertNotNull( dateHistogramBucket.getKeyAsInstant() );
        assertEquals( time, dateHistogramBucket.getKeyAsInstant().toEpochMilli() );
    }
}
