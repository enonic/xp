package com.enonic.xp.admin.impl.json.aggregation;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.enonic.wem.api.aggregation.Bucket;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({@JsonSubTypes.Type(value = DateRangeBucketJson.class, name = "DateRangeBucket"),})
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
