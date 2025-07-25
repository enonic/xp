package com.enonic.xp.schema.content;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSortedSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class ContentTypeNames
    extends AbstractImmutableEntitySet<ContentTypeName>
{
    private static final ContentTypeNames EMPTY = new ContentTypeNames( ImmutableSortedSet.of() );

    private ContentTypeNames( final ImmutableSortedSet<ContentTypeName> set )
    {
        super( set );
    }

    public static ContentTypeNames empty()
    {
        return EMPTY;
    }

    public static ContentTypeNames from( final String... contentTypeNames )
    {
        return from( Arrays.asList( contentTypeNames ) );
    }

    public static ContentTypeNames from( final Collection<String> contentTypeNames )
    {
        return contentTypeNames.stream().map( ContentTypeName::from ).collect( collector() );
    }

    public static ContentTypeNames from( final ContentTypeName... contentTypeNames )
    {
        return fromInternal( ImmutableSortedSet.copyOf( contentTypeNames ) );
    }

    public static ContentTypeNames from( final Iterable<ContentTypeName> contentTypeNames )
    {
        return fromInternal( ImmutableSortedSet.copyOf( contentTypeNames ) );
    }

    public static Collector<ContentTypeName, ?, ContentTypeNames> collector()
    {
        return Collectors.collectingAndThen( ImmutableSortedSet.toImmutableSortedSet( Comparator.naturalOrder() ),
                                             ContentTypeNames::fromInternal );
    }

    private static ContentTypeNames fromInternal( final ImmutableSortedSet<ContentTypeName> set )
    {
        return set.isEmpty() ? EMPTY : new ContentTypeNames( set );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableSortedSet.Builder<ContentTypeName> set = ImmutableSortedSet.naturalOrder();

        private Builder()
        {
        }

        public Builder add( final ContentTypeName value )
        {
            this.set.add( value );
            return this;
        }

        public Builder addAll( final Iterable<ContentTypeName> iterable )
        {
            for ( final ContentTypeName contentTypeName : iterable )
            {
                this.set.add( contentTypeName );
            }
            return this;
        }

        public ContentTypeNames build()
        {
            return fromInternal( this.set.build() );
        }
    }
}
