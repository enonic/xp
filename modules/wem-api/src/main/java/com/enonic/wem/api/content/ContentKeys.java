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

public final class ContentKeys
    implements Iterable<ContentKey>
{
    private final ImmutableSet<ContentKey> set;

    private ContentKeys( final ImmutableSet<ContentKey> set )
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

    public ContentKey getFirst()
    {
        return this.set.isEmpty() ? null : this.set.iterator().next();
    }

    public boolean contains( final ContentKey ref )
    {
        return this.set.contains( ref );
    }

    public Set<ContentKey> getSet()
    {
        return this.set;
    }

    @Override
    public Iterator<ContentKey> iterator()
    {
        return this.set.iterator();
    }

    public ContentKeys add( final String... keys )
    {
        return add( parseKeys( keys ) );
    }

    public ContentKeys add( final ContentKey... keys )
    {
        return add( ImmutableSet.copyOf( keys ) );
    }

    public ContentKeys add( final Iterable<ContentKey> keys )
    {
        return add( ImmutableSet.copyOf( keys ) );
    }

    private ContentKeys add( final ImmutableSet<ContentKey> keys )
    {
        final HashSet<ContentKey> tmp = Sets.newHashSet();
        tmp.addAll( this.set );
        tmp.addAll( keys );
        return new ContentKeys( ImmutableSet.copyOf( tmp ) );
    }

    public ContentKeys remove( final String... keys )
    {
        return remove( parseKeys( keys ) );
    }

    public ContentKeys remove( final ContentKey... keys )
    {
        return remove( ImmutableSet.copyOf( keys ) );
    }

    public ContentKeys remove( final Iterable<ContentKey> keys )
    {
        return remove( ImmutableSet.copyOf( keys ) );
    }

    private ContentKeys remove( final ImmutableSet<ContentKey> keys )
    {
        final HashSet<ContentKey> tmp = Sets.newHashSet();
        tmp.addAll( this.set );
        tmp.removeAll( keys );
        return new ContentKeys( ImmutableSet.copyOf( tmp ) );
    }

    public int hashCode()
    {
        return this.set.hashCode();
    }

    public boolean equals( final Object o )
    {
        return ( o instanceof ContentKeys ) && this.set.equals( ( (ContentKeys) o ).set );
    }

    public String toString()
    {
        return this.set.toString();
    }

    public static ContentKeys empty()
    {
        final ImmutableSet<ContentKey> set = ImmutableSet.of();
        return new ContentKeys( set );
    }

    public static ContentKeys from( final String... keys )
    {
        return new ContentKeys( parseKeys( keys ) );
    }

    public static ContentKeys from( final ContentKey... keys )
    {
        return new ContentKeys( ImmutableSet.copyOf( keys ) );
    }

    public static ContentKeys from( final Iterable<ContentKey> keys )
    {
        return new ContentKeys( ImmutableSet.copyOf( keys ) );
    }

    private static ImmutableSet<ContentKey> parseKeys( final String... keys )
    {
        final Collection<String> list = Lists.newArrayList( keys );
        final Collection<ContentKey> keyList = Collections2.transform( list, new ParseFunction() );
        return ImmutableSet.copyOf( keyList );
    }


    private final static class ParseFunction
        implements Function<String, ContentKey>
    {
        @Override
        public ContentKey apply( final String value )
        {
            return ContentKey.from( value );
        }
    }
}
