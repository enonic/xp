package com.enonic.xp.query.aggregation;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateRangeTest
{
    @Test
    void testBuilder()
    {
        final Instant now = Instant.now();
        final String from = "2015-04-03T07:16:31.876Z";
        final String to = "2016-04-03T07:16:31.876Z";

        final DateRange fromRange = DateRange.create().key( "myKey" ).from( now ).build();
        final DateRange fromtoRange = DateRange.create().key( "myKey2" ).from( from ).to( to ).build();

        assertEquals( "myKey", fromRange.getKey() );
        assertEquals( now, fromRange.getFrom() );
        assertEquals( null, fromRange.getTo() );

        assertEquals( "myKey2", fromtoRange.getKey() );
        assertEquals( from, fromtoRange.getFrom() );
        assertEquals( to, fromtoRange.getTo() );
    }
}
