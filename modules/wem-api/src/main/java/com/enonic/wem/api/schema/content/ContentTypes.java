package com.enonic.wem.api.schema.content;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import com.enonic.wem.api.support.AbstractImmutableEntityList;

public final class ContentTypes
    extends AbstractImmutableEntityList<ContentType>
{
    private final ImmutableMap<QualifiedContentTypeName, ContentType> map;

    private ContentTypes( final ImmutableList<ContentType> list )
    {
        super( list );
        this.map = Maps.uniqueIndex( list, new ToNameFunction() );
    }

    public ImmutableSet<QualifiedContentTypeName> getNames()
    {
        final Collection<QualifiedContentTypeName> names = Collections2.transform( this.list, new ToNameFunction() );
        return ImmutableSet.copyOf( names );
    }

    public ContentType getContentType( final QualifiedContentTypeName contentTypeName )
    {
        return map.get( contentTypeName );
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

    public static Builder newContentTypes()
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
