package com.enonic.xp.site;

import java.util.Collection;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

@Beta
public class XDataMappings
    extends AbstractImmutableEntityList<XDataMapping>
{
    private XDataMappings( final ImmutableList<XDataMapping> list )
    {
        super( list );
    }

    public static XDataMappings empty()
    {
        return new XDataMappings( ImmutableList.of() );
    }

    public static XDataMappings from( final XDataMapping... xDataMappings )
    {
        return new XDataMappings( ImmutableList.copyOf( xDataMappings ) );
    }

    public static XDataMappings from( final Iterable<? extends XDataMapping> xDataMappings )
    {
        return new XDataMappings( ImmutableList.copyOf( xDataMappings ) );
    }

    public static XDataMappings from( final Collection<? extends XDataMapping> xDataMappings )
    {
        return new XDataMappings( ImmutableList.copyOf( xDataMappings ) );
    }
}
