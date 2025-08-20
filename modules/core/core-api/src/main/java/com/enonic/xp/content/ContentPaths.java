package com.enonic.xp.content;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class ContentPaths
    extends AbstractImmutableEntitySet<ContentPath>
{
    private static final ContentPaths EMPTY = new ContentPaths( ImmutableSet.of() );

    private ContentPaths( final ImmutableSet<ContentPath> set )
    {
        super( set );
    }

    public static ContentPaths empty()
    {
        return EMPTY;
    }

    public static ContentPaths from( final String... paths )
    {
        return from( Arrays.asList( paths ) );
    }

    public static ContentPaths from( final Collection<String> contentPaths )
    {
        return contentPaths.stream().map( ContentPath::from ).collect( collector() );
    }

    public static ContentPaths from( final ContentPath... paths )
    {
        return fromInternal( ImmutableSet.copyOf( paths ) );
    }

    public static ContentPaths from( final Iterable<ContentPath> paths )
    {
        return paths instanceof ContentPaths p ? p : fromInternal( ImmutableSet.copyOf( paths ) );
    }

    public static Collector<ContentPath, ?, ContentPaths> collector()
    {
        return Collectors.collectingAndThen( ImmutableSet.toImmutableSet(), ContentPaths::fromInternal );
    }

    private static ContentPaths fromInternal( final ImmutableSet<ContentPath> set )
    {
        return set.isEmpty() ? EMPTY : new ContentPaths( set );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableSet.Builder<ContentPath> paths = ImmutableSet.builder();

        private Builder()
        {
        }

        public Builder add( final ContentPath contentPath )
        {
            this.paths.add( contentPath );
            return this;
        }

        public Builder addAll( final Iterable<ContentPath> contentPaths )
        {
            this.paths.addAll( contentPaths );
            return this;
        }

        public ContentPaths build()
        {
            return fromInternal( paths.build() );
        }
    }
}
