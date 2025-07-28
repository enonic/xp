package com.enonic.xp.schema.xdata;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class XDataNames
    extends AbstractImmutableEntitySet<XDataName>
{
    private static final XDataNames EMPTY = new XDataNames( ImmutableSet.of() );

    private XDataNames( final ImmutableSet<XDataName> list )
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
        return xdataNames.stream().map( XDataName::from ).collect( collector() );
    }

    public static XDataNames from( final XDataName... xdataNames )
    {
        return fromInternal( ImmutableSet.copyOf( xdataNames ) );
    }

    public static XDataNames from( final Iterable<XDataName> xdataNames )
    {
        return xdataNames instanceof XDataNames x ? x : fromInternal( ImmutableSet.copyOf( xdataNames ) );
    }

    public static Collector<XDataName, ?, XDataNames> collector()
    {
        return Collectors.collectingAndThen( ImmutableSet.toImmutableSet(), XDataNames::fromInternal );
    }

    private static XDataNames fromInternal( final ImmutableSet<XDataName> xdataNames )
    {
        return xdataNames.isEmpty() ? EMPTY : new XDataNames( xdataNames );
    }
}
