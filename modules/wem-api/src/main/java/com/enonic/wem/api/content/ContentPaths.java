package com.enonic.wem.api.content;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public final class ContentPaths
    implements Iterable<ContentPath>
{
    private final ImmutableSet<ContentPath> set;

    private ContentPaths( final ImmutableSet<ContentPath> set )
    {
        this.set = set;
    }

    public int getSize()
    {
        return this.set.size();
    }

    public boolean isEmpty()
    {
        return this.set.isEmpty();
    }

    public ContentPath getFirst()
    {
        return this.set.isEmpty() ? null : this.set.iterator().next();
    }

    public boolean contains( final ContentPath ref )
    {
        return this.set.contains( ref );
    }

    public Set<ContentPath> getSet()
    {
        return this.set;
    }

    @Override
    public Iterator<ContentPath> iterator()
    {
        return this.set.iterator();
    }

    public ContentPaths add( final String... paths )
    {
        return add( parsePaths( paths ) );
    }

    public ContentPaths add( final ContentPath... paths )
    {
        return add( ImmutableSet.copyOf( paths ) );
    }

    public ContentPaths add( final Iterable<ContentPath> paths )
    {
        return add( ImmutableSet.copyOf( paths ) );
    }

    private ContentPaths add( final ImmutableSet<ContentPath> paths )
    {
        final HashSet<ContentPath> tmp = Sets.newHashSet();
        tmp.addAll( this.set );
        tmp.addAll( paths );
        return new ContentPaths( ImmutableSet.copyOf( tmp ) );
    }

    public ContentPaths remove( final String... paths )
    {
        return remove( parsePaths( paths ) );
    }

    public ContentPaths remove( final ContentPath... paths )
    {
        return remove( ImmutableSet.copyOf( paths ) );
    }

    public ContentPaths remove( final Iterable<ContentPath> paths )
    {
        return remove( ImmutableSet.copyOf( paths ) );
    }

    private ContentPaths remove( final ImmutableSet<ContentPath> paths )
    {
        final HashSet<ContentPath> tmp = Sets.newHashSet();
        tmp.addAll( this.set );
        tmp.removeAll( paths );
        return new ContentPaths( ImmutableSet.copyOf( tmp ) );
    }

    public int hashCode()
    {
        return this.set.hashCode();
    }

    public boolean equals( final Object o )
    {
        return ( o instanceof ContentPaths ) && this.set.equals( ( (ContentPaths) o ).set );
    }

    public String toString()
    {
        return this.set.toString();
    }

    public static ContentPaths empty()
    {
        final ImmutableSet<ContentPath> set = ImmutableSet.of();
        return new ContentPaths( set );
    }

    public static ContentPaths from( final String... paths )
    {
        return new ContentPaths( parsePaths( paths ) );
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
