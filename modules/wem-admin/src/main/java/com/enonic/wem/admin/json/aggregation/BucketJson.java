package com.enonic.wem.admin.json.aggregation;

import com.enonic.wem.api.query.aggregation.Bucket;

public class BucketJson
{
    private final String key;

    private final long docCount;

    public BucketJson( final Bucket bucket )
    {
        this.key = bucket.getKey();
        this.docCount = bucket.getDocCount();
    }

    public String getKey()
    {
        return key;
    }

    public long getDocCount()
    {
        return docCount;
    }
}
