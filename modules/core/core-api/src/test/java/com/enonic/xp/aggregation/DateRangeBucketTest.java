package com.enonic.xp.aggregation;


import java.time.Instant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class DateRangeBucketTest
{
    @Test
    void builder()
    {
        DateRangeBucket.Builder builder = DateRangeBucket.create();

        final long now = System.currentTimeMillis();
        long timeFrom = now - 1;
        Instant instantFrom = Instant.ofEpochMilli( timeFrom );
        builder.from( instantFrom );

        long timeTo = now + 1;
        Instant instantTo = Instant.ofEpochMilli( timeTo );
        builder.to( instantTo );

        DateRangeBucket dateRangeBucket = builder.build();

        assertNotNull( dateRangeBucket.getFrom() );
        assertNotNull( dateRangeBucket.getTo() );

        assertEquals( timeFrom, dateRangeBucket.getFrom().toEpochMilli() );
        assertEquals( timeTo, dateRangeBucket.getTo().toEpochMilli() );
    }
}
