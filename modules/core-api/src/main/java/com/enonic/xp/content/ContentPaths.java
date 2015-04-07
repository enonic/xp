package com.enonic.xp.content;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
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
        return add( parsePaths( paths ) );
    }

    public ContentPaths add( final ContentPath... paths )
    {
        return add( ImmutableSet.copyOf( paths ) );
    }

    public ContentPaths add( final Iterable paths )
    {
        return add( ImmutableSet.copyOf( paths ) );
    }

    private ContentPaths add( final ImmutableSet paths )
    {
        final HashSet<ContentPath> tmp = Sets.newHashSet();
        tmp.addAll( this.set );
        tmp.addAll(
            (Set) paths.stream().map( ( item -> item instanceof ContentPath ? item : ContentPath.from( item.toString() ) ) ).collect(
                Collectors.toSet() ) );
        return new ContentPaths( ImmutableSet.copyOf( tmp ) );
    }

    public boolean hasParentOf( final ContentPath contentPath )
    {
        for ( ContentPath possibleParentPath : this.set )
        {
            if ( contentPath.isChildOf( possibleParentPath ) )
            {
                return true;
            }
        }

        return false;
    }

    public ContentPaths filterChildrenIfParentPresents()
    {
        ContentPaths filteredContentPaths = ContentPaths.empty();

        for ( ContentPath contentPath : this.set )
        {
            if ( !this.hasParentOf( contentPath ) )
            {
                filteredContentPaths = filteredContentPaths.add( contentPath );
            }
        }

        return filteredContentPaths;
    }

    public ContentPaths remove( final String... paths )
    {
        return remove( parsePaths( paths ) );
    }

    public ContentPaths remove( final ContentPath... paths )
    {
        return remove( ImmutableSet.copyOf( paths ) );
    }

    public ContentPaths remove( final Iterable paths )
    {
        return remove( ImmutableSet.copyOf( paths ) );
    }

    private ContentPaths remove( final ImmutableSet paths )
    {
        final HashSet<ContentPath> tmp = Sets.newHashSet();
        tmp.addAll( this.set );
        tmp.removeAll(
            (Set) paths.stream().map( ( item -> item instanceof ContentPath ? item : ContentPath.from( item.toString() ) ) ).collect(
                Collectors.toSet() ) );
        return new ContentPaths( ImmutableSet.copyOf( tmp ) );
    }

    @Override
    public int hashCode()
    {
        return this.set.hashCode();
    }

    @Override
    public boolean equals( final Object o )
    {
        return ( o instanceof ContentPaths ) && this.set.equals( ( (ContentPaths) o ).set );
    }

    @Override
    public String toString()
    {
        return this.set.toString();
    }

    public static ContentPaths empty()
    {
        final ImmutableSet<ContentPath> list = ImmutableSet.of();
        return new ContentPaths( list );
    }

    public static ContentPaths from( final String... paths )
    {
        return new ContentPaths( parsePaths( paths ) );
    }

    public static ContentPaths from( final Collection<String> contentPaths )
    {
        return from( contentPaths.toArray( new String[contentPaths.size()] ) );
    }

    public static ContentPaths from( final ContentPath... paths )
    {
        return new ContentPaths( ImmutableSet.copyOf( paths ) );
    }

    public static ContentPaths from( final Iterable<ContentPath> paths )
    {
        return new ContentPaths( ImmutableSet.copyOf( paths ) );
    }

    private static ImmutableSet<ContentPath> parsePaths( final String... paths )
    {
        final Collection<String> list = Lists.newArrayList( paths );
        final Collection<ContentPath> pathList = Collections2.transform( list, new ParseFunction() );
        return ImmutableSet.copyOf( pathList );
    }

    private final static class ParseFunction
        implements Function<String, ContentPath>
    {
        @Override
        public ContentPath apply( final String value )
        {
            return ContentPath.from( value );
        }
    }
}
