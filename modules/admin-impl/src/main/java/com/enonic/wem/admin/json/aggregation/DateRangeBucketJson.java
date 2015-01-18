package com.enonic.wem.admin.json.aggregation;

import java.time.Instant;

import com.enonic.wem.api.aggregation.DateRangeBucket;

public class DateRangeBucketJson
    extends BucketJson
{
    private Instant from;

    private Instant to;

    public DateRangeBucketJson( final DateRangeBucket dateRangeBucket )
    {
        super( dateRangeBucket );
        this.from = dateRangeBucket.getFrom();
        this.to = dateRangeBucket.getTo();
    }

    public Instant getFrom()
    {
        return from;
    }

    public Instant getTo()
    {
        return to;
    }
}
