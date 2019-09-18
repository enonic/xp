package com.enonic.xp.content;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import com.enonic.xp.support.AbstractImmutableEntitySet;

@Beta
public final class ContentPaths
    extends AbstractImmutableEntitySet<ContentPath>
    implements Iterable<ContentPath>
{
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
        return removePaths( Arrays.asList( paths ) );
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
        return new ContentPaths( ImmutableSet.of() );
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
        return new ContentPaths( ImmutableSet.copyOf( Iterables.concat( set, paths ) ) );
    }

    private ContentPaths removePaths( final Collection<ContentPath> paths )
    {
        return new ContentPaths( ImmutableSet.copyOf( Sets.difference( set, ImmutableSet.copyOf( paths ) ) ) );
    }

    private static List<ContentPath> adaptPaths( final Iterable<?> paths )
    {
        return StreamSupport.stream( paths.spliterator(), false ).
            map( ContentPaths::adapt ).
            collect( Collectors.toList() );
    }

    private static ContentPath adapt( Object item )
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

    private static ImmutableSet<ContentPath> parsePaths( final Collection<String> paths )
    {
        return paths.stream().map( ContentPath::from ).collect( ImmutableSet.toImmutableSet() );
    }

    public static class Builder
    {
        private ImmutableList.Builder<ContentPath> paths = ImmutableList.builder();

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
            return ContentPaths.from( paths.build() );
        }
    }
}
