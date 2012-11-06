package com.enonic.wem.api.content.type;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public final class ContentTypeNames
    implements Iterable<QualifiedContentTypeName>
{
    private final ImmutableSet<QualifiedContentTypeName> set;

    private ContentTypeNames( final ImmutableSet<QualifiedContentTypeName> set )
    {
        this.set = set;
    }

    public int getSize()
    {
        return this.set.size();
    }

    public boolean isEmpty()
    {
        return this.set.isEmpty();
    }

    public QualifiedContentTypeName getFirst()
    {
        return this.set.isEmpty() ? null : this.set.iterator().next();
    }

    public boolean contains( final QualifiedContentTypeName ref )
    {
        return this.set.contains( ref );
    }

    public Set<QualifiedContentTypeName> getSet()
    {
        return this.set;
    }

    @Override
    public Iterator<QualifiedContentTypeName> iterator()
    {
        return this.set.iterator();
    }

    public ContentTypeNames add( final String... contentTypeNames )
    {
        return add( parseQualifiedNames( contentTypeNames ) );
    }

    public ContentTypeNames add( final QualifiedContentTypeName... contentTypeNames )
    {
        return add( ImmutableSet.copyOf( contentTypeNames ) );
    }

    public ContentTypeNames add( final Iterable<QualifiedContentTypeName> contentTypeNames )
    {
        return add( ImmutableSet.copyOf( contentTypeNames ) );
    }

    private ContentTypeNames add( final ImmutableSet<QualifiedContentTypeName> contentTypeNames )
    {
        final HashSet<QualifiedContentTypeName> tmp = Sets.newHashSet();
        tmp.addAll( this.set );
        tmp.addAll( contentTypeNames );
        return new ContentTypeNames( ImmutableSet.copyOf( tmp ) );
    }

    public ContentTypeNames remove( final String... contentTypeNames )
    {
        return remove( parseQualifiedNames( contentTypeNames ) );
    }

    public ContentTypeNames remove( final QualifiedContentTypeName... contentTypeNames )
    {
        return remove( ImmutableSet.copyOf( contentTypeNames ) );
    }

    public ContentTypeNames remove( final Iterable<QualifiedContentTypeName> contentTypeNames )
    {
        return remove( ImmutableSet.copyOf( contentTypeNames ) );
    }

    private ContentTypeNames remove( final ImmutableSet<QualifiedContentTypeName> contentTypeNames )
    {
        final HashSet<QualifiedContentTypeName> tmp = Sets.newHashSet();
        tmp.addAll( this.set );
        tmp.removeAll( contentTypeNames );
        return new ContentTypeNames( ImmutableSet.copyOf( tmp ) );
    }

    public int hashCode()
    {
        return this.set.hashCode();
    }

    public boolean equals( final Object o )
    {
        return ( o instanceof ContentTypeNames ) && this.set.equals( ( (ContentTypeNames) o ).set );
    }

    public String toString()
    {
        return this.set.toString();
    }

    public static ContentTypeNames empty()
    {
        final ImmutableSet<QualifiedContentTypeName> set = ImmutableSet.of();
        return new ContentTypeNames( set );
    }

    public static ContentTypeNames from( final String... contentTypeNames )
    {
        return new ContentTypeNames( parseQualifiedNames( contentTypeNames ) );
    }

    public static ContentTypeNames from( final QualifiedContentTypeName... contentTypeNames )
    {
        return new ContentTypeNames( ImmutableSet.copyOf( contentTypeNames ) );
    }

    public static ContentTypeNames from( final Iterable<QualifiedContentTypeName> contentTypeNames )
    {
        return new ContentTypeNames( ImmutableSet.copyOf( contentTypeNames ) );
    }

    private static ImmutableSet<QualifiedContentTypeName> parseQualifiedNames( final String... contentTypeNames )
    {
        final Collection<String> list = Lists.newArrayList( contentTypeNames );
        final Collection<QualifiedContentTypeName> contentTypeNameList = Collections2.transform( list, new ParseFunction() );
        return ImmutableSet.copyOf( contentTypeNameList );
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
}
