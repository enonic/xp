package com.enonic.xp.content;

import com.google.common.annotations.Beta;

import com.enonic.xp.aggregation.Bucket;

@Beta
public class ResolveDependenciesAggregationResult
{
    private String type;

    private Long count;

    public ResolveDependenciesAggregationResult( final Bucket bucket )
    {
        this.type = bucket.getKey();
        this.count = bucket.getDocCount();
    }

    public ResolveDependenciesAggregationResult( final String type, final Long count )
    {
        this.type = type;
        this.count = count;
    }

    public void increaseCount()
    {
        count = count != null ? ++count : 1;
    }

    public String getType()
    {
        return type;
    }

    public long getCount()
    {
        return count;
    }

}
