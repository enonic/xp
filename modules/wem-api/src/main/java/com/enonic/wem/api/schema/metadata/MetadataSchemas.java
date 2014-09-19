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

public class MetadataSchemas
    extends AbstractImmutableEntityList<MetadataSchema>
{
    private final ImmutableMap<MetadataSchemaName, MetadataSchema> map;

    private MetadataSchemas( final ImmutableList<MetadataSchema> list )
    {
        super( list );
        this.map = Maps.uniqueIndex( list, new ToNameFunction() );
    }

    public Set<MetadataSchemaName> getNames()
    {
        final Collection<MetadataSchemaName> names = Collections2.transform( this.list, new ToNameFunction() );
        return ImmutableSet.copyOf( names );
    }

    public MetadataSchema getMetadata( final MetadataSchemaName metadataSchemaName )
    {
        return map.get( metadataSchemaName );
    }

    public static MetadataSchemas empty()
    {
        final ImmutableList<MetadataSchema> list = ImmutableList.of();
        return new MetadataSchemas( list );
    }

    public static MetadataSchemas from( final MetadataSchema... metadataSchemas )
    {
        return new MetadataSchemas( ImmutableList.copyOf( metadataSchemas ) );
    }

    public static MetadataSchemas from( final Iterable<? extends MetadataSchema> metadatas )
    {
        return new MetadataSchemas( ImmutableList.copyOf( metadatas ) );
    }

    public static MetadataSchemas from( final Collection<? extends MetadataSchema> metadatas )
    {
        return new MetadataSchemas( ImmutableList.copyOf( metadatas ) );
    }

    private final static class ToNameFunction
        implements Function<MetadataSchema, MetadataSchemaName>
    {
        @Override
        public MetadataSchemaName apply( final MetadataSchema value )
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
        private ImmutableList.Builder<MetadataSchema> builder = ImmutableList.builder();

        public Builder add( MetadataSchema node )
        {
            builder.add( node );
            return this;
        }

        public MetadataSchemas build()
        {
            return new MetadataSchemas( builder.build() );
        }
    }
}
