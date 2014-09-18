package com.enonic.wem.api.schema.metadata;

import java.util.Collection;
import java.util.HashSet;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.enonic.wem.api.support.AbstractImmutableEntitySet;

public final class MetadataNames
    extends AbstractImmutableEntitySet<MetadataName>
{
    protected MetadataNames( final ImmutableSet<MetadataName> set )
    {
        super( set );
    }

    public MetadataName getFirst()
    {
        return this.set.isEmpty() ? null : this.set.iterator().next();
    }

    public MetadataNames add( final String... paths )
    {
        return add( parsePaths( paths ) );
    }

    public MetadataNames add( final MetadataName... paths )
    {
        return add( ImmutableSet.copyOf( paths ) );
    }

    public MetadataNames add( final Iterable<MetadataName> paths )
    {
        return add( ImmutableSet.copyOf( paths ) );
    }

    private MetadataNames add( final ImmutableSet<MetadataName> paths )
    {
        final HashSet<MetadataName> tmp = Sets.newHashSet();
        tmp.addAll( this.set );
        tmp.addAll( paths );
        return new MetadataNames( ImmutableSet.copyOf( tmp ) );
    }

    public MetadataNames remove( final String... paths )
    {
        return remove( parsePaths( paths ) );
    }

    public MetadataNames remove( final MetadataName... paths )
    {
        return remove( ImmutableSet.copyOf( paths ) );
    }

    public MetadataNames remove( final Iterable<MetadataName> paths )
    {
        return remove( ImmutableSet.copyOf( paths ) );
    }

    private MetadataNames remove( final ImmutableSet<MetadataName> paths )
    {
        final HashSet<MetadataName> tmp = Sets.newHashSet();
        tmp.addAll( this.set );
        tmp.removeAll( paths );
        return new MetadataNames( ImmutableSet.copyOf( tmp ) );
    }

    public static MetadataNames empty()
    {
        final ImmutableSet<MetadataName> set = ImmutableSet.of();
        return new MetadataNames( set );
    }

    public static MetadataNames from( final String... paths )
    {
        return new MetadataNames( parsePaths( paths ) );
    }

    public static MetadataNames from( final MetadataName... paths )
    {
        return new MetadataNames( ImmutableSet.copyOf( paths ) );
    }

    public static MetadataNames from( final Iterable<MetadataName> paths )
    {
        return new MetadataNames( ImmutableSet.copyOf( paths ) );
    }

    private static ImmutableSet<MetadataName> parsePaths( final String... paths )
    {
        final Collection<String> list = Lists.newArrayList( paths );
        final Collection<MetadataName> pathList = Collections2.transform( list, new ParseFunction() );
        return ImmutableSet.copyOf( pathList );
    }


    private final static class ParseFunction
        implements Function<String, MetadataName>
    {
        @Override
        public MetadataName apply( final String value )
        {
            return MetadataName.from( value );
        }
    }
}
