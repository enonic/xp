package com.enonic.wem.admin.json.aggregation;

public class BucketJson
{
    private final String name;

    private final long docCount;

    public BucketJson( final String name, final long docCount )
    {
        this.name = name;
        this.docCount = docCount;
    }

    public String getName()
    {
        return name;
    }

    public long getDocCount()
    {
        return docCount;
    }
}
