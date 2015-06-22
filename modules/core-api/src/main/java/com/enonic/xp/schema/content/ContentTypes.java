package com.enonic.xp.schema.content;

import java.util.Collection;
import java.util.List;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.enonic.xp.support.AbstractImmutableEntityList;

@Beta
public final class ContentTypes
    extends AbstractImmutableEntityList<ContentType>
{
    private final ImmutableMap<ContentTypeName, ContentType> map;

    private ContentTypes( final ImmutableList<ContentType> list )
    {
        super( list );
        this.map = Maps.uniqueIndex( list, new ToNameFunction() );
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
        final List<ContentType> tmp = Lists.newArrayList();
        tmp.addAll( this.list );
        tmp.addAll( contentTypes );

        return new ContentTypes( ImmutableList.copyOf( tmp ) );
    }

    public ImmutableSet<ContentTypeName> getNames()
    {
        final Collection<ContentTypeName> names = Collections2.transform( this.list, new ToNameFunction() );
        return ImmutableSet.copyOf( names );
    }

    public ContentType getContentType( final ContentTypeName contentTypeName )
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
        implements Function<ContentType, ContentTypeName>
    {
        @Override
        public ContentTypeName apply( final ContentType value )
        {
            return value.getName();
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