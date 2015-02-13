package com.enonic.wem.api.query.aggregation;

import java.time.Instant;

import org.junit.Assert;
import org.junit.Test;

public class DateRangeTest
{
    @Test
    public void testBuilder()
    {
        final Instant now = Instant.now();
        final DateRange fromRange = DateRange.create().key( "myKey" ).from( now ).build();

        Assert.assertEquals( "myKey", fromRange.getKey() );
        Assert.assertEquals( now, fromRange.getFrom() );
        Assert.assertEquals( null, fromRange.getTo() );
    }
}
