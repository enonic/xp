package com.enonic.xp.content;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class ContentVersions
    extends AbstractImmutableEntityList<ContentVersion>
{
    private static final ContentVersions EMPTY = new ContentVersions( ImmutableList.of() );

    private ContentVersions( final ImmutableList list )
    {
        super( list );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Collector<ContentVersion, ?, ContentVersions> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), ContentVersions::fromInternal );
    }

    public static ContentVersions from( final ContentVersion... items )
    {
        return fromInternal( ImmutableList.copyOf( items ) );
    }

    private static ContentVersions fromInternal( final ImmutableList<ContentVersion> list )
    {
        return list.isEmpty() ? EMPTY : new ContentVersions( list );
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<ContentVersion> builder = ImmutableList.builder();

        private Builder()
        {
        }

        public Builder add( final ContentVersion contentVersion )
        {
            this.builder.add( contentVersion );
            return this;
        }

        public ContentVersions build()
        {
            return fromInternal( builder.build() );
        }
    }
}
