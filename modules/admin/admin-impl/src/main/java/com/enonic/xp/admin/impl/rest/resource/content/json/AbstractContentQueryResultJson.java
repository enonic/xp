package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.admin.impl.json.aggregation.AggregationJson;
import com.enonic.xp.admin.impl.json.content.ContentIdJson;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentListMetaData;

public abstract class AbstractContentQueryResultJson<T extends ContentIdJson>
{
    private final ImmutableSet<AggregationJson> aggregations;

    private final ContentListMetaData metadata;

    protected ImmutableSet<T> contents;

    protected AbstractContentQueryResultJson( final Builder builder )
    {
        this.aggregations = ImmutableSet.copyOf( builder.aggregations );
        this.metadata = builder.metadata;
    }

    public abstract static class Builder<T extends Builder>
    {
        private final Set<AggregationJson> aggregations = new HashSet<>();

        private ContentListMetaData metadata;

        public T addAggregation( final AggregationJson aggregation )
        {
            this.aggregations.add( aggregation );
            return (T) this;
        }

        public abstract T addContent( Content content );

        public T setMetadata( final ContentListMetaData metadata )
        {
            this.metadata = metadata;
            return (T) this;
        }

        public abstract AbstractContentQueryResultJson build();

    }

    public ImmutableSet<AggregationJson> getAggregations()
    {
        return aggregations;
    }

    public ImmutableSet<T> getContents()
    {
        return contents;
    }

    public ContentListMetaData getMetadata()
    {
        return metadata;
    }
}
