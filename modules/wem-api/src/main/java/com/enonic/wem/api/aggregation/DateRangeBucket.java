package com.enonic.wem.api.aggregation;

import java.time.Instant;

public class DateRangeBucket
    extends Bucket
{
    private Instant from;

    private Instant to;


    public DateRangeBucket( final String key, final long docCount )
    {
        super( key, docCount );
    }

    public Instant getFrom()
    {
        return from;
    }

    public void setFrom( final Instant from )
    {
        this.from = from;
    }

    public Instant getTo()
    {
        return to;
    }

    public void setTo( final Instant to )
    {
        this.to = to;
    }
}
