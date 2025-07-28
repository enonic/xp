package com.enonic.xp.schema.content;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class ContentTypes
    extends AbstractImmutableEntityList<ContentType>
{
    private static final ContentTypes EMPTY = new ContentTypes( ImmutableList.of() );

    private ContentTypes( final ImmutableList<ContentType> list )
    {
        super( list );
    }

    public static ContentTypes empty()
    {
        return EMPTY;
    }

    public static ContentTypes from( final ContentType... contentTypes )
    {
        return fromInternal( ImmutableList.copyOf( contentTypes ) );
    }

    public static ContentTypes from( final Iterable<? extends ContentType> contentTypes )
    {
        return contentTypes instanceof ContentTypes c ? c : fromInternal( ImmutableList.copyOf( contentTypes ) );
    }

    public static Collector<ContentType, ?, ContentTypes> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), ContentTypes::fromInternal );
    }

    private static ContentTypes fromInternal( final ImmutableList<ContentType> contentTypes )
    {
        return contentTypes.isEmpty() ? EMPTY : new ContentTypes( contentTypes );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<ContentType> contentTypes = ImmutableList.builder();

        public Builder add( final ContentType value )
        {
            contentTypes.add( value );
            return this;
        }

        public ContentTypes build()
        {
            return fromInternal( contentTypes.build() );
        }
    }
}
