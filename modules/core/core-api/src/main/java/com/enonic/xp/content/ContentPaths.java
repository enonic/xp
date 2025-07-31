package com.enonic.xp.content;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

    public ContentPaths add( final String... paths )
    {
        return add( from( paths ) );
    }

    public ContentPaths add( final ContentPath... paths )
    {
        return addPaths( Arrays.asList( paths ) );
    }

    public ContentPaths add( final Iterable<?> paths )
    {
        return addPaths( adaptPaths( paths ) );
    }

    public ContentPaths remove( final String... paths )
    {
        return remove( from( paths ) );
    }

    public ContentPaths remove( final ContentPath... paths )
    {
        return removePaths( ImmutableSet.copyOf( paths ) );
    }

    public ContentPaths remove( final Iterable<?> paths )
    {
        return removePaths( adaptPaths( paths ) );
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

    private ContentPaths addPaths( final Collection<ContentPath> paths )
    {
        return Stream.concat( stream(), paths.stream() ).collect( collector() );
    }

    private ContentPaths removePaths( final Set<ContentPath> paths )
    {
        return stream().filter( Predicate.not( paths::contains ) ).collect( collector() );
    }

    private static ContentPath adaptPath( Object item )
    {
        if ( item instanceof String )
        {
            return ContentPath.from( (String) item );
        }
        else
        {
            return (ContentPath) item;
        }
    }

    private static ImmutableSet<ContentPath> adaptPaths( final Iterable<?> paths )
    {
        return StreamSupport.stream( paths.spliterator(), false ).
            map( ContentPaths::adaptPath ).
            collect( ImmutableSet.toImmutableSet() );
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
