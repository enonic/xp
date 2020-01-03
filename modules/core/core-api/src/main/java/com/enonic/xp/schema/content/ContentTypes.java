package com.enonic.xp.schema.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class ContentTypes
    extends AbstractImmutableEntityList<ContentType>
{
    private final ImmutableMap<ContentTypeName, ContentType> map;

    private ContentTypes( final ImmutableList<ContentType> list )
    {
        super( list );
        this.map = list.stream().collect( ImmutableMap.toImmutableMap( ContentType::getName, Function.identity() ) );
    }

    public ContentTypes add( final ContentType... contentTypes )
    {
        return add( ImmutableList.copyOf( contentTypes ) );
    }

    public ContentTypes add( final Iterable<ContentType> contentTypes )
    {
        return add( ImmutableList.copyOf( contentTypes ) );
    }

    private ContentTypes add( final ImmutableList<ContentType> contentTypes )
    {
        final List<ContentType> tmp = new ArrayList<>();
        tmp.addAll( this.list );
        tmp.addAll( contentTypes );

        return new ContentTypes( ImmutableList.copyOf( tmp ) );
    }

    public ImmutableSet<ContentTypeName> getNames()
    {
        return map.keySet();
    }

    public ContentType getContentType( final ContentTypeName contentTypeName )
    {
        return map.get( contentTypeName );
    }

    public ContentTypes filter( final Predicate<ContentType> filter )
    {
        return from( this.map.values().stream().filter( filter ).iterator() );
    }

    public static ContentTypes empty()
    {
        return new ContentTypes( ImmutableList.of() );
    }

    public static ContentTypes from( final ContentType... contentTypes )
    {
        return new ContentTypes( ImmutableList.copyOf( contentTypes ) );
    }

    public static ContentTypes from( final Iterable<? extends ContentType> contentTypes )
    {
        return new ContentTypes( ImmutableList.copyOf( contentTypes ) );
    }

    public static ContentTypes from( final Iterator<? extends ContentType> contentTypes )
    {
        return new ContentTypes( ImmutableList.copyOf( contentTypes ) );
    }

    public static ContentTypes from( final Collection<? extends ContentType> contentTypes )
    {
        return new ContentTypes( ImmutableList.copyOf( contentTypes ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ImmutableList.Builder<ContentType> contentTypes = ImmutableList.builder();

        public Builder add( final ContentType value )
        {
            contentTypes.add( value );
            return this;
        }

        public ContentTypes build()
        {
            return new ContentTypes( contentTypes.build() );
        }
    }
}