package com.enonic.wem.api.schema.metadata;

import java.util.Collection;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import com.enonic.wem.api.support.AbstractImmutableEntityList;

public class Metadatas extends AbstractImmutableEntityList<Metadata>
{
    private final ImmutableMap<MetadataName, Metadata> map;

    private Metadatas( final ImmutableList<Metadata> list )
    {
        super( list );
        this.map = Maps.uniqueIndex( list, new ToNameFunction() );
    }

    public Set<MetadataName> getNames()
    {
        final Collection<MetadataName> names = Collections2.transform( this.list, new ToNameFunction() );
        return ImmutableSet.copyOf( names );
    }

    public Metadata getMetadata( final MetadataName metadataName )
    {
        return map.get( metadataName );
    }

    public static Metadatas empty()
    {
        final ImmutableList<Metadata> list = ImmutableList.of();
        return new Metadatas( list );
    }

    public static Metadatas from( final Metadata... metadatas )
    {
        return new Metadatas( ImmutableList.copyOf( metadatas ) );
    }

    public static Metadatas from( final Iterable<? extends Metadata> metadatas )
    {
        return new Metadatas( ImmutableList.copyOf( metadatas ) );
    }

    public static Metadatas from( final Collection<? extends Metadata> metadatas )
    {
        return new Metadatas( ImmutableList.copyOf( metadatas ) );
    }

    private final static class ToNameFunction
        implements Function<Metadata, MetadataName>
    {
        @Override
        public MetadataName apply( final Metadata value )
        {
            return value.getName();
        }
    }

    public static Builder newMetadatas()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ImmutableList.Builder<Metadata> builder = ImmutableList.builder();

        public Builder add( Metadata node )
        {
            builder.add( node );
            return this;
        }

        public Metadatas build()
        {
            return new Metadatas( builder.build() );
        }
    }
}
