package com.enonic.wem.api.content.type;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

public final class ContentTypes
    implements Iterable<ContentType>
{
    private final ImmutableList<ContentType> list;

    private final ImmutableMap<QualifiedContentTypeName, ContentType> map;

    private ContentTypes( final ImmutableList<ContentType> list )
    {
        this.list = list;
        this.map = Maps.uniqueIndex( list, new ToNameFunction() );
    }

    public int getSize()
    {
        return this.list.size();
    }

    public boolean isEmpty()
    {
        return this.list.isEmpty();
    }

    public ContentType getFirst()
    {
        return this.list.isEmpty() ? null : this.list.get( 0 );
    }

    public List<ContentType> getList()
    {
        return this.list;
    }

    public Set<QualifiedContentTypeName> getNames()
    {
        final Collection<QualifiedContentTypeName> names = Collections2.transform( this.list, new ToNameFunction() );
        return ImmutableSet.copyOf( names );
    }

    public ContentType getContentType( final QualifiedContentTypeName contentTypeName )
    {
        return map.get( contentTypeName );
    }

    @Override
    public Iterator<ContentType> iterator()
    {
        return this.list.iterator();
    }

    public int hashCode()
    {
        return this.list.hashCode();
    }

    public boolean equals( final Object o )
    {
        return ( o instanceof ContentTypes ) && this.list.equals( ( (ContentTypes) o ).list );
    }

    public static ContentTypes empty()
    {
        final ImmutableList<ContentType> list = ImmutableList.of();
        return new ContentTypes( list );
    }

    public static ContentTypes from( final ContentType... contentTypes )
    {
        return new ContentTypes( ImmutableList.copyOf( contentTypes ) );
    }

    public static ContentTypes from( final Iterable<? extends ContentType> contentTypes )
    {
        return new ContentTypes( ImmutableList.copyOf( contentTypes ) );
    }

    public static ContentTypes from( final Collection<? extends ContentType> contentTypes )
    {
        return new ContentTypes( ImmutableList.copyOf( contentTypes ) );
    }

    private final static class ToNameFunction
        implements Function<ContentType, QualifiedContentTypeName>
    {
        @Override
        public QualifiedContentTypeName apply( final ContentType value )
        {
            return value.getQualifiedName();
        }
    }
}
