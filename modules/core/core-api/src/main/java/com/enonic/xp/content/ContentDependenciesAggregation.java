package com.enonic.xp.content;

import com.google.common.annotations.Beta;

import com.enonic.xp.aggregation.Bucket;
import com.enonic.xp.schema.content.ContentTypeName;

@Beta
public class ContentDependenciesAggregation
{
    private ContentTypeName type;

    private Long count;

    public ContentDependenciesAggregation( final Bucket bucket )
    {
        this.type = ContentTypeName.from( bucket.getKey() );
        this.count = bucket.getDocCount();
    }

    public ContentDependenciesAggregation( final ContentTypeName type, final Long count )
    {
        this.type = type;
        this.count = count;
    }

    public ContentTypeName getType()
    {
        return type;
    }

    public long getCount()
    {
        return count;
    }

}
