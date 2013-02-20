package com.enonic.wem.api.content.schema;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.enonic.wem.api.util.AbstractImmutableEntityList;

public final class Schemas
    extends AbstractImmutableEntityList<Schema>
{
    private Schemas( final ImmutableList<Schema> list )
    {
        super( list );
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

    public static Schemas from( final Iterable<? extends Schema>... baseTypes )
    {
        final List<Schema> all = Lists.newArrayList();
        for ( final Iterable<? extends Schema> iterable : baseTypes )
        {
            for ( Schema schema : iterable )
            {
                all.add( schema );
            }
        }
        return new Schemas( ImmutableList.copyOf( all ) );
    }

    public static Schemas from( final Collection<? extends Schema> baseTypes )
    {
        return new Schemas( ImmutableList.copyOf( baseTypes ) );
    }

}
