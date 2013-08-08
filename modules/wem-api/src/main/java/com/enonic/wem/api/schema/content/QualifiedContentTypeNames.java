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

public final class QualifiedContentTypeNames
    extends AbstractImmutableEntitySet<QualifiedContentTypeName>
{
    public QualifiedContentTypeNames( final ImmutableSet<QualifiedContentTypeName> set )
    {
        super( set );
    }

    public QualifiedContentTypeName getFirst()
    {
        return this.set.isEmpty() ? null : this.set.iterator().next();
    }

    public QualifiedContentTypeNames add( final String... contentTypeNames )
    {
        return add( parseQualifiedNames( contentTypeNames ) );
    }

    public QualifiedContentTypeNames add( final QualifiedContentTypeName... contentTypeNames )
    {
        return add( ImmutableSet.copyOf( contentTypeNames ) );
    }

    public QualifiedContentTypeNames add( final Iterable<QualifiedContentTypeName> contentTypeNames )
    {
        return add( ImmutableSet.copyOf( contentTypeNames ) );
    }

    private QualifiedContentTypeNames add( final ImmutableSet<QualifiedContentTypeName> contentTypeNames )
    {
        final HashSet<QualifiedContentTypeName> tmp = Sets.newHashSet();
        tmp.addAll( this.set );
        tmp.addAll( contentTypeNames );
        return new QualifiedContentTypeNames( ImmutableSet.copyOf( tmp ) );
    }

    public QualifiedContentTypeNames remove( final String... contentTypeNames )
    {
        return remove( parseQualifiedNames( contentTypeNames ) );
    }

    public QualifiedContentTypeNames remove( final QualifiedContentTypeName... contentTypeNames )
    {
        return remove( ImmutableSet.copyOf( contentTypeNames ) );
    }

    public QualifiedContentTypeNames remove( final Iterable<QualifiedContentTypeName> contentTypeNames )
    {
        return remove( ImmutableSet.copyOf( contentTypeNames ) );
    }

    private QualifiedContentTypeNames remove( final ImmutableSet<QualifiedContentTypeName> contentTypeNames )
    {
        final HashSet<QualifiedContentTypeName> tmp = Sets.newHashSet();
        tmp.addAll( this.set );
        tmp.removeAll( contentTypeNames );
        return new QualifiedContentTypeNames( ImmutableSet.copyOf( tmp ) );
    }

    public static QualifiedContentTypeNames empty()
    {
        final ImmutableSet<QualifiedContentTypeName> set = ImmutableSet.of();
        return new QualifiedContentTypeNames( set );
    }

    public static QualifiedContentTypeNames from( final String... contentTypeNames )
    {
        return new QualifiedContentTypeNames( parseQualifiedNames( contentTypeNames ) );
    }

    public static QualifiedContentTypeNames from( final Collection<String> contentTypeNames )
    {
        return from( contentTypeNames.toArray( new String[contentTypeNames.size()] ) );
    }

    public static QualifiedContentTypeNames from( final QualifiedContentTypeName... contentTypeNames )
    {
        return new QualifiedContentTypeNames( ImmutableSet.copyOf( contentTypeNames ) );
    }

    public static QualifiedContentTypeNames from( final Iterable<QualifiedContentTypeName> contentTypeNames )
    {
        return new QualifiedContentTypeNames( ImmutableSet.copyOf( contentTypeNames ) );
    }

    private static ImmutableSet<QualifiedContentTypeName> parseQualifiedNames( final String... contentTypeNames )
    {
        final Collection<String> list = Lists.newArrayList( contentTypeNames );
        final Collection<QualifiedContentTypeName> contentTypeNameList = Collections2.transform( list, new ParseFunction() );
        return ImmutableSet.copyOf( contentTypeNameList );
    }

    public Set<String> getAsStringSet()
    {
        Set<String> contentTypeNamesAsStrings = Sets.newHashSet();

        for ( QualifiedContentTypeName qualifiedContentTypeName : this.getSet() )
        {
            contentTypeNamesAsStrings.add( qualifiedContentTypeName.toString() );
        }

        return contentTypeNamesAsStrings;
    }

    private final static class ParseFunction
        implements Function<String, QualifiedContentTypeName>
    {
        @Override
        public QualifiedContentTypeName apply( final String value )
        {
            return new QualifiedContentTypeName( value );
        }
    }

    public static Builder newQualifiedContentTypeNames()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ImmutableSet.Builder<QualifiedContentTypeName> set = ImmutableSet.builder();

        public Builder add( final QualifiedContentTypeName value )
        {
            this.set.add( value );
            return this;
        }

        public QualifiedContentTypeNames build()
        {
            return new QualifiedContentTypeNames( this.set.build() );
        }
    }
}
