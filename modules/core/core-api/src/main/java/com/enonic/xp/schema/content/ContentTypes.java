package com.enonic.xp.schema.content;

import java.util.Collection;
import java.util.Iterator;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class ContentTypes
    extends AbstractImmutableEntityList<ContentType>
{
    private ContentTypes( final ImmutableList<ContentType> list )
    {
        super( list );
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
        private final ImmutableList.Builder<ContentType> contentTypes = ImmutableList.builder();

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
