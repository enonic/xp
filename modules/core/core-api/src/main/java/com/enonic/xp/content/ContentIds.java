package com.enonic.xp.content;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class ContentIds
    extends AbstractImmutableEntitySet<ContentId>
{
    private static final ContentIds EMPTY = new ContentIds( ImmutableSet.of() );

    private ContentIds( final ImmutableSet<ContentId> set )
    {
        super( set );
    }

    public static ContentIds empty()
    {
        return EMPTY;
    }

    public static ContentIds from( final ContentId... ids )
    {
        return fromInternal( ImmutableSet.copyOf( ids ) );
    }

    public static ContentIds from( final String... ids )
    {
        return from( Arrays.asList( ids ) );
    }

    public static ContentIds from( final Collection<String> ids )
    {
        return ids.stream().map( ContentId::from ).collect( collecting() );
    }

    public static ContentIds from( final Iterable<ContentId> ids )
    {
        if ( ids instanceof ContentIds )
        {
            return (ContentIds) ids;
        }

        return fromInternal( ImmutableSet.copyOf( ids ) );
    }

    public static Collector<ContentId, ?, ContentIds> collecting()
    {
        return Collectors.collectingAndThen( ImmutableSet.toImmutableSet(), ContentIds::fromInternal );
    }

    private static ContentIds fromInternal( final ImmutableSet<ContentId> set )
    {
        if ( set.isEmpty() )
        {
            return EMPTY;
        }
        else
        {
            return new ContentIds( set );
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableSet.Builder<ContentId> contents = ImmutableSet.builder();

        public Builder add( final ContentId contentId )
        {
            this.contents.add( contentId );
            return this;
        }

        public Builder addAll( final ContentIds contentIds )
        {
            this.contents.addAll( contentIds.getSet() );
            return this;
        }

        public ContentIds build()
        {
            return fromInternal( contents.build() );
        }
    }
}
