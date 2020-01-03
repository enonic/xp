package com.enonic.xp.content;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
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
        return addPaths( parsePaths( Arrays.asList( paths ) ) );
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
        return removePaths( parsePaths( Arrays.asList( paths ) ) );
    }

    public ContentPaths remove( final ContentPath... paths )
    {
        return removePaths( ImmutableSet.copyOf( paths ) );
    }

    public ContentPaths remove( final Iterable<?> paths )
    {
        return removePaths( adaptPaths( paths ) );
    }

    @Override
    public int hashCode()
    {
        return super.hashCode();
    }

    @Override
    public boolean equals( final Object o )
    {
        return super.equals( o );
    }

    @Override
    public String toString()
    {
        return super.toString();
    }

    public static ContentPaths empty()
    {
        return EMPTY;
    }

    public static ContentPaths from( final String... paths )
    {
        return new ContentPaths( parsePaths( Arrays.asList( paths ) ) );
    }

    public static ContentPaths from( final Collection<String> contentPaths )
    {
        return new ContentPaths( parsePaths( contentPaths ) );
    }

    public static ContentPaths from( final ContentPath... paths )
    {
        return new ContentPaths( ImmutableSet.copyOf( paths ) );
    }

    public static ContentPaths from( final Iterable<ContentPath> paths )
    {
        return new ContentPaths( ImmutableSet.copyOf( paths ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    private ContentPaths addPaths( final Collection<ContentPath> paths )
    {
        return new ContentPaths( Stream.concat( set.stream(), paths.stream() ).
            collect( ImmutableSet.toImmutableSet() ) );
    }

    private ContentPaths removePaths( final Set<ContentPath> paths )
    {
        return new ContentPaths( set.stream().filter( Predicate.not( paths::contains ) ).
            collect( ImmutableSet.toImmutableSet() ) );
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

    private static ImmutableSet<ContentPath> parsePaths( final Collection<String> paths )
    {
        return paths.stream().
            map( ContentPath::from ).
            collect( ImmutableSet.toImmutableSet() );
    }

    public static class Builder
    {
        private ImmutableSet.Builder<ContentPath> paths = ImmutableSet.builder();

        public Builder add( final ContentPath contentPath )
        {
            this.paths.add( contentPath );
            return this;
        }

        public Builder addAll( final ContentPaths contentPaths )
        {
            this.paths.addAll( contentPaths.getSet() );
            return this;
        }

        public ContentPaths build()
        {
            return new ContentPaths( paths.build() );
        }
    }
}
