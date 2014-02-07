package com.enonic.wem.api.query.aggregation;

public class Bucket
{
    final String key;

    final long docCount;

    public Bucket( final String key, final long docCount )
    {
        this.key = key;
        this.docCount = docCount;
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
