package com.enonic.xp.admin.impl.json.aggregation;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.enonic.xp.aggregation.Bucket;

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

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final BucketJson that = (BucketJson) o;
        return docCount == that.docCount && Objects.equals( key, that.key );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( key, docCount );
    }
}
