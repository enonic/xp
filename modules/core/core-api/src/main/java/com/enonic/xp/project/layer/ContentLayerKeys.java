package com.enonic.xp.project.layer;


import java.util.Collection;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class ContentLayerKeys
    extends AbstractImmutableEntitySet<ContentLayerKey>
{
    private ContentLayerKeys( final Builder builder )
    {
        super( builder.keys.build() );
    }

    public static ContentLayerKeys from( final Collection<ContentLayerKey> contentLayerKeys )
    {
        return ContentLayerKeys.create().addAll( contentLayerKeys ).build();
    }

    public static ContentLayerKeys empty()
    {
        return ContentLayerKeys.create().build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final ImmutableSet.Builder<ContentLayerKey> keys = ImmutableSet.builder();

        public Builder add( final ContentLayerKey key )
        {
            this.keys.add( key );
            return this;
        }

        public Builder addAll( final Collection<ContentLayerKey> keys )
        {
            this.keys.addAll( keys );
            return this;
        }

        public ContentLayerKeys build()
        {
            return new ContentLayerKeys( this );
        }
    }

}
