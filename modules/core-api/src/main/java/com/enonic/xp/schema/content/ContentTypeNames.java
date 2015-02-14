package com.enonic.xp.schema.content;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Lists;

import com.enonic.xp.support.AbstractImmutableEntitySet;

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
        final Collection<String> list = Lists.newArrayList( contentTypeNames );
        final Collection<ContentTypeName> contentTypeNameList = Collections2.transform( list, new ParseFunction() );
        return ImmutableSortedSet.copyOf( contentTypeNameList );
    }

    private final static class ParseFunction
        implements Function<String, ContentTypeName>
    {
        @Override
        public ContentTypeName apply( final String value )
        {
            return ContentTypeName.from( value );
        }
    }

    public static Builder newContentTypeNames()
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
