package com.enonic.xp.aggregation;


import java.time.Instant;
import java.util.Date;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;


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
