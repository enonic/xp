package com.enonic.wem.api.content;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;

public final class Contents
    implements Iterable<Content>
{
    private final ImmutableList<Content> list;

    private Contents( final ImmutableList<Content> list )
    {
        this.list = list;
    }

    public int getSize()
    {
        return this.list.size();
    }

    public boolean isEmpty()
    {
        return this.list.isEmpty();
    }

    public Content getFirst()
    {
        return this.list.isEmpty() ? null : this.list.get( 0 );
    }

    public List<Content> getList()
    {
        return this.list;
    }

    public ContentPaths getPaths()
    {
        final Collection<ContentPath> paths = Collections2.transform( this.list, new ToKeyFunction() );
        return ContentPaths.from( paths );
    }

    @Override
    public Iterator<Content> iterator()
    {
        return this.list.iterator();
    }

    public int hashCode()
    {
        return this.list.hashCode();
    }

    public boolean equals( final Object o )
    {
        return ( o instanceof Contents ) && this.list.equals( ( (Contents) o ).list );
    }

    public static Contents empty()
    {
        final ImmutableList<Content> list = ImmutableList.of();
        return new Contents( list );
    }

    public static Contents from( final Content... contents )
    {
        return new Contents( ImmutableList.copyOf( contents ) );
    }

    public static Contents from( final Iterable<? extends Content> contents )
    {
        return new Contents( ImmutableList.copyOf( contents ) );
    }

    public static Contents from( final Collection<? extends Content> contents )
    {
        return new Contents( ImmutableList.copyOf( contents ) );
    }

    private final static class ToKeyFunction
        implements Function<Content, ContentPath>
    {
        @Override
        public ContentPath apply( final Content value )
        {
            return value.getPath();
        }
    }
}
