package com.enonic.xp.content;

import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.aggregation.Bucket;
import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.schema.content.ContentTypeName;

@PublicApi
public class ContentDependenciesAggregation
{
    private final ContentTypeName type;

    private final Long count;

    private final Set<ContentId> contentIds;

    @Deprecated
    public ContentDependenciesAggregation( final Bucket bucket )
    {
        this.type = ContentTypeName.from( bucket.getKey() );
        this.count = bucket.getDocCount();
        this.contentIds = Set.of();
    }

    @Deprecated
    public ContentDependenciesAggregation( final ContentTypeName type, final Long count )
    {
        this.type = type;
        this.count = count;
        this.contentIds = Set.of();
    }

    public ContentDependenciesAggregation( final Builder builder )
    {
        this.type = builder.type;
        this.contentIds = builder.contentIds.build();
        this.count = (long)this.contentIds.size();
    }

    public ContentTypeName getType()
    {
        return type;
    }

    public static Builder create() {
        return new Builder();
    }

    @Deprecated
    public long getCount()
    {
        return count;
    }

    public Set<ContentId> getContentIds()
    {
        return contentIds;
    }

    public static final class Builder
    {
        private final ImmutableSet.Builder<ContentId> contentIds = ImmutableSet.builder();

        private ContentTypeName type;

        private Builder()
        {
        }

        public Builder type( final ContentTypeName type )
        {
            this.type = type;
            return this;
        }

        @Deprecated
        public Builder count( final Long count )
        {
            return this;
        }

        public Builder addContentId( final ContentId id )
        {
            this.contentIds.add( id );
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( type, "type must be set" );
        }

        public ContentDependenciesAggregation build()
        {
            validate();
            return new ContentDependenciesAggregation( this );
        }
    }
}
