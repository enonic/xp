package com.enonic.wem.admin.json.aggregation;

import org.joda.time.DateTime;

import com.enonic.wem.api.aggregation.DateRangeBucket;

public class DateRangeBucketJson
    extends BucketJson
{
    private DateTime from;

    private DateTime to;

    public DateRangeBucketJson( final DateRangeBucket dateRangeBucket )
    {
        super( dateRangeBucket );
        this.from = dateRangeBucket.getFrom();
        this.to = dateRangeBucket.getTo();
    }

    public DateTime getFrom()
    {
        return from;
    }

    public DateTime getTo()
    {
        return to;
    }
}
