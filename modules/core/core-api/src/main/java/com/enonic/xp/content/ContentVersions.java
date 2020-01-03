package com.enonic.xp.content;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSortedSet;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class ContentVersions
    implements Iterable<ContentVersion>
{
    private final ImmutableSortedSet<ContentVersion> contentVersions;

    private final ContentId contentId;

    private ContentVersions( Builder builder )
    {
        contentVersions = ImmutableSortedSet.copyOf( builder.contentVersions );
        contentId = builder.contentId;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public Iterator<ContentVersion> iterator()
    {
        return this.contentVersions.iterator();
    }


    public static final class Builder
    {
        private SortedSet<ContentVersion> contentVersions = new TreeSet<>();

        private ContentId contentId;

        private Builder()
        {
        }

        public Builder add( final ContentVersion contentVersion )
        {
            this.contentVersions.add( contentVersion );
            return this;
        }

        public Builder contentId( ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public ContentVersions build()
        {
            return new ContentVersions( this );
        }
    }
}
