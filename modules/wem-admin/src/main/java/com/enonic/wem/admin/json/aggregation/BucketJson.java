package com.enonic.wem.admin.json.aggregation;

import com.enonic.wem.api.query.aggregation.Bucket;

public class BucketJson
{
    private final String name;

    private final long count;

    public BucketJson( final Bucket bucket )
    {
        this.name = bucket.getName();
        this.count = bucket.getDocCount();
    }

    public String getName()
    {
        return name;
    }

    public long getCount()
    {
        return count;
    }
}
