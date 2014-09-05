package com.enonic.wem.api.schema;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.enonic.wem.api.support.AbstractImmutableEntityList;

public final class Schemas
    extends AbstractImmutableEntityList<Schema>
{
    private final ImmutableMap<SchemaName, Schema> map;

    private Schemas( final ImmutableList<Schema> list )
    {
        super( list );
        this.map = Maps.uniqueIndex( list, Schema::getName );
    }

    public Schema getSchema( final SchemaName schemaName )
    {
        return map.get( schemaName );
    }

    public static Schemas empty()
    {
        final ImmutableList<Schema> list = ImmutableList.of();
        return new Schemas( list );
    }

    public static Schemas from( final Schema... schemas )
    {
        return new Schemas( ImmutableList.copyOf( schemas ) );
    }

    public static Schemas from( final Iterable<? extends Schema>... schemas )
    {
        final List<Schema> all = Lists.newArrayList();
        for ( final Iterable<? extends Schema> iterable : schemas )
        {
            for ( Schema schema : iterable )
            {
                all.add( schema );
            }
        }
        return new Schemas( ImmutableList.copyOf( all ) );
    }

    public static Schemas from( final Collection<? extends Schema> schemas )
    {
        return new Schemas( ImmutableList.copyOf( schemas ) );
    }
}
