package com.enonic.wem.api.schema.metadata;

import java.util.Collection;
import java.util.HashSet;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.enonic.wem.api.support.AbstractImmutableEntitySet;

public final class MetadataSchemaNames
    extends AbstractImmutableEntitySet<MetadataSchemaName>
{
    protected MetadataSchemaNames( final ImmutableSet<MetadataSchemaName> set )
    {
        super( set );
    }

    public MetadataSchemaName getFirst()
    {
        return this.set.isEmpty() ? null : this.set.iterator().next();
    }

    public MetadataSchemaNames add( final String... paths )
    {
        return add( parsePaths( paths ) );
    }

    public MetadataSchemaNames add( final MetadataSchemaName... paths )
    {
        return add( ImmutableSet.copyOf( paths ) );
    }

    public MetadataSchemaNames add( final Iterable<MetadataSchemaName> paths )
    {
        return add( ImmutableSet.copyOf( paths ) );
    }

    private MetadataSchemaNames add( final ImmutableSet<MetadataSchemaName> paths )
    {
        final HashSet<MetadataSchemaName> tmp = Sets.newHashSet();
        tmp.addAll( this.set );
        tmp.addAll( paths );
        return new MetadataSchemaNames( ImmutableSet.copyOf( tmp ) );
    }

    public MetadataSchemaNames remove( final String... paths )
    {
        return remove( parsePaths( paths ) );
    }

    public MetadataSchemaNames remove( final MetadataSchemaName... paths )
    {
        return remove( ImmutableSet.copyOf( paths ) );
    }

    public MetadataSchemaNames remove( final Iterable<MetadataSchemaName> paths )
    {
        return remove( ImmutableSet.copyOf( paths ) );
    }

    private MetadataSchemaNames remove( final ImmutableSet<MetadataSchemaName> paths )
    {
        final HashSet<MetadataSchemaName> tmp = Sets.newHashSet();
        tmp.addAll( this.set );
        tmp.removeAll( paths );
        return new MetadataSchemaNames( ImmutableSet.copyOf( tmp ) );
    }

    public static MetadataSchemaNames empty()
    {
        final ImmutableSet<MetadataSchemaName> set = ImmutableSet.of();
        return new MetadataSchemaNames( set );
    }

    public static MetadataSchemaNames from( final String... paths )
    {
        return new MetadataSchemaNames( parsePaths( paths ) );
    }

    public static MetadataSchemaNames from( final MetadataSchemaName... paths )
    {
        return new MetadataSchemaNames( ImmutableSet.copyOf( paths ) );
    }

    public static MetadataSchemaNames from( final Iterable<MetadataSchemaName> paths )
    {
        return new MetadataSchemaNames( ImmutableSet.copyOf( paths ) );
    }

    private static ImmutableSet<MetadataSchemaName> parsePaths( final String... paths )
    {
        final Collection<String> list = Lists.newArrayList( paths );
        final Collection<MetadataSchemaName> pathList = Collections2.transform( list, new ParseFunction() );
        return ImmutableSet.copyOf( pathList );
    }


    private final static class ParseFunction
        implements Function<String, MetadataSchemaName>
    {
        @Override
        public MetadataSchemaName apply( final String value )
        {
            return MetadataSchemaName.from( value );
        }
    }
}
