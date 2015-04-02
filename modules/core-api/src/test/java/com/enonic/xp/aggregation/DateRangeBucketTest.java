package com.enonic.xp.aggregation;


import java.time.Instant;
import java.util.Date;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;


public class DateRangeBucketTest
{
    @Test
    public void builder()
    {
        DateRangeBucket.Builder builder = DateRangeBucket.create();

        long timeFrom = System.currentTimeMillis();
        Instant instantFrom = new Date( timeFrom ).toInstant();
        builder.from( instantFrom );

        long timeTo = System.currentTimeMillis();
        Instant instantTo = new Date( timeTo ).toInstant();
        builder.to( instantTo );

        DateRangeBucket dateRangeBucket = builder.build();

        assertNotNull( dateRangeBucket.getFrom() );
        assertNotNull( dateRangeBucket.getTo() );

        assertEquals( timeFrom, dateRangeBucket.getFrom().toEpochMilli() );
        assertEquals( timeTo, dateRangeBucket.getTo().toEpochMilli() );
    }
}
