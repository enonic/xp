package com.enonic.xp.schema.xdata;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class XDataNames
    extends AbstractImmutableEntityList<XDataName>
{
    private static final XDataNames EMPTY = new XDataNames( ImmutableList.of() );

    private XDataNames( final ImmutableList<XDataName> list )
    {
        super( list );
    }

    public static XDataNames empty()
    {
        return EMPTY;
    }

    public static XDataNames from( final String... xdataNames )
    {
        return from( Arrays.asList( xdataNames ) );
    }

    public static XDataNames from( final Collection<String> xdataNames )
    {
        return xdataNames.stream().map( XDataName::from ).collect( collecting() );
    }

    public static XDataNames from( final XDataName... xdataNames )
    {
        return fromInternal( ImmutableList.copyOf( xdataNames ) );
    }

    public static XDataNames from( final Iterable<XDataName> xdataNames )
    {
        return fromInternal( ImmutableList.copyOf( xdataNames ) );
    }

    public static Collector<XDataName, ?, XDataNames> collecting()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), XDataNames::fromInternal );
    }

    private static XDataNames fromInternal( final ImmutableList<XDataName> xdataNames )
    {
        return xdataNames.isEmpty() ? EMPTY : new XDataNames( xdataNames );
    }
}
