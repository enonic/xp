package com.enonic.xp.content;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class ContentVersions
    extends AbstractImmutableEntityList<ContentVersion>
{
    private ContentVersions( final ImmutableList list )
    {
        super( list );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<ContentVersion> contentVersions = ImmutableList.builder();

        private Builder()
        {
        }

        public Builder add( final ContentVersion contentVersion )
        {
            this.contentVersions.add( contentVersion );
            return this;
        }

        public ContentVersions build()
        {
            return new ContentVersions( this.contentVersions.build() );
        }
    }
}
