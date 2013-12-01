package com.enonic.wem.api.schema.content;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.enonic.wem.api.support.AbstractImmutableEntitySet;

public final class ContentTypeNames
    extends AbstractImmutableEntitySet<ContentTypeName>
{
    private ContentTypeNames( final ImmutableSet<ContentTypeName> set )
    {
        super( set );
    }

    public ContentTypeName getFirst()
    {
        return this.set.isEmpty() ? null : this.set.iterator().next();
    }

    public ContentTypeNames add( final String... contentTypeNames )
    {
        return add( parseQualifiedNames( contentTypeNames ) );
    }

    public ContentTypeNames add( final ContentTypeName... contentTypeNames )
    {
        return add( ImmutableSet.copyOf( contentTypeNames ) );
    }

    public ContentTypeNames add( final Iterable<ContentTypeName> contentTypeNames )
    {
        return add( ImmutableSet.copyOf( contentTypeNames ) );
    }

    private ContentTypeNames add( final ImmutableSet<ContentTypeName> contentTypeNames )
    {
        final HashSet<ContentTypeName> tmp = Sets.newHashSet();
        tmp.addAll( this.set );
        tmp.addAll( contentTypeNames );
        return new ContentTypeNames( ImmutableSet.copyOf( tmp ) );
    }

    public ContentTypeNames remove( final String... contentTypeNames )
    {
        return remove( parseQualifiedNames( contentTypeNames ) );
    }

    public ContentTypeNames remove( final ContentTypeName... contentTypeNames )
    {
        return remove( ImmutableSet.copyOf( contentTypeNames ) );
    }

    public ContentTypeNames remove( final Iterable<ContentTypeName> contentTypeNames )
    {
        return remove( ImmutableSet.copyOf( contentTypeNames ) );
    }

    private ContentTypeNames remove( final ImmutableSet<ContentTypeName> contentTypeNames )
    {
        final HashSet<ContentTypeName> tmp = Sets.newHashSet();
        tmp.addAll( this.set );
        tmp.removeAll( contentTypeNames );
        return new ContentTypeNames( ImmutableSet.copyOf( tmp ) );
    }

    public static ContentTypeNames empty()
    {
        final ImmutableSet<ContentTypeName> set = ImmutableSet.of();
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
        return new ContentTypeNames( ImmutableSet.copyOf( contentTypeNames ) );
    }

    public static ContentTypeNames from( final Iterable<ContentTypeName> contentTypeNames )
    {
        return new ContentTypeNames( ImmutableSet.copyOf( contentTypeNames ) );
    }

    private static ImmutableSet<ContentTypeName> parseQualifiedNames( final String... contentTypeNames )
    {
        final Collection<String> list = Lists.newArrayList( contentTypeNames );
        final Collection<ContentTypeName> contentTypeNameList = Collections2.transform( list, new ParseFunction() );
        return ImmutableSet.copyOf( contentTypeNameList );
    }

    public Set<String> getAsStringSet()
    {
        Set<String> contentTypeNamesAsStrings = Sets.newHashSet();

        for ( ContentTypeName contentTypeName : this.getSet() )
        {
            contentTypeNamesAsStrings.add( contentTypeName.toString() );
        }

        return contentTypeNamesAsStrings;
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
        private ImmutableSet.Builder<ContentTypeName> set = ImmutableSet.builder();

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
