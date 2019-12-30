package com.enonic.xp.schema.content;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import com.google.common.collect.ImmutableSortedSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class ContentTypeNames
    extends AbstractImmutableEntitySet<ContentTypeName>
{
    private ContentTypeNames( final ImmutableSortedSet<ContentTypeName> set )
    {
        super( set );
    }

    public static ContentTypeNames empty()
    {
        final ImmutableSortedSet<ContentTypeName> set = ImmutableSortedSet.of();
        return new ContentTypeNames( set );
    }

    public static ContentTypeNames from( final String... contentTypeNames )
    {
        return new ContentTypeNames( parseQualifiedNames( contentTypeNames ) );
    }

    public static ContentTypeNames from( final Collection<String> contentTypeNames )
    {
        return from( contentTypeNames.toArray( new String[contentTypeNames.size()] ) );
    }

    public static ContentTypeNames from( final ContentTypeName... contentTypeNames )
    {
        return new ContentTypeNames( ImmutableSortedSet.copyOf( contentTypeNames ) );
    }

    public static ContentTypeNames from( final Iterable<ContentTypeName> contentTypeNames )
    {
        return new ContentTypeNames( ImmutableSortedSet.copyOf( contentTypeNames ) );
    }

    private static ImmutableSortedSet<ContentTypeName> parseQualifiedNames( final String... contentTypeNames )
    {
        return Arrays.stream( contentTypeNames ).map( ContentTypeName::from ).
            collect( ImmutableSortedSet.toImmutableSortedSet( Comparator.naturalOrder() ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ImmutableSortedSet.Builder<ContentTypeName> set = ImmutableSortedSet.naturalOrder();

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
            return new ContentTypeNames( this.set.build() );
        }
    }
}
