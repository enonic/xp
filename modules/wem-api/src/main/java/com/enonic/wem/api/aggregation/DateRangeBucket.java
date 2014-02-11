package com.enonic.wem.api.aggregation;

import org.joda.time.DateTime;

public class DateRangeBucket
    extends Bucket
{
    private DateTime from;

    private DateTime to;


    public DateRangeBucket( final String key, final long docCount )
    {
        super( key, docCount );
    }

    public DateTime getFrom()
    {
        return from;
    }

    public void setFrom( final DateTime from )
    {
        this.from = from;
    }

    public DateTime getTo()
    {
        return to;
    }

    public void setTo( final DateTime to )
    {
        this.to = to;
    }
}
