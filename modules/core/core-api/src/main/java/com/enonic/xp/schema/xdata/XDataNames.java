package com.enonic.xp.schema.xdata;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

@Beta
public final class XDataNames
    extends AbstractImmutableEntityList<XDataName>
{
    private XDataNames( final ImmutableList<XDataName> list )
    {
        super( list );
    }

    public static XDataNames empty()
    {
        final ImmutableList<XDataName> list = ImmutableList.of();
        return new XDataNames( list );
    }

    public static XDataNames from( final String... xdataNames )
    {
        return new XDataNames( parseQualifiedNames( xdataNames ) );
    }

    public static XDataNames from( final Collection<String> xdataNames )
    {
        return from( xdataNames.toArray( new String[0] ) );
    }

    public static XDataNames from( final XDataName... xdataNames )
    {
        return new XDataNames( ImmutableList.copyOf( xdataNames ) );
    }

    public static XDataNames from( final Iterable<XDataName> xdataNames )
    {
        return new XDataNames( ImmutableList.copyOf( xdataNames ) );
    }

    private static ImmutableList<XDataName> parseQualifiedNames( final String... xdataNames )
    {
        return ImmutableList.copyOf( Arrays.stream( xdataNames ).map( XDataName::from ).iterator() );
    }

}
