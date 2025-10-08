package com.enonic.xp.content;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class ContentVersions
    extends AbstractImmutableEntityList<ContentVersion>
{
    private ContentVersions( Builder builder )
    {
        super( ImmutableList.sortedCopyOf( ContentVersionDateComparator.INSTANCE, builder.contentVersions ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final List<ContentVersion> contentVersions = new ArrayList<>();

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
            return new ContentVersions( this );
        }
    }
}
