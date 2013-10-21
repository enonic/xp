package com.enonic.wem.api.content.page;


import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import com.enonic.wem.api.support.AbstractImmutableEntityList;

public final class ControllerParams
    extends AbstractImmutableEntityList<ControllerParam>
{
    private final ImmutableMap<String, ControllerParam> map;

    private ControllerParams( final ImmutableList<ControllerParam> list )
    {
        super( list );
        this.map = Maps.uniqueIndex( list, new ToNameFunction() );
    }

    public ImmutableSet<String> getNames()
    {
        return map.keySet();
    }

    public ControllerParam getParam( final String name )
    {
        return map.get( name );
    }

    @Override
    public String toString()
    {
        return this.list.toString();
    }

    public static ControllerParams empty()
    {
        final ImmutableList<ControllerParam> list = ImmutableList.of();
        return new ControllerParams( list );
    }

    public static ControllerParams from( final ControllerParam... params )
    {
        return new ControllerParams( ImmutableList.copyOf( params ) );
    }

    public static ControllerParams from( final Iterable<? extends ControllerParam> params )
    {
        return new ControllerParams( ImmutableList.copyOf( params ) );
    }

    public static ControllerParams from( final Collection<? extends ControllerParam> params )
    {
        return new ControllerParams( ImmutableList.copyOf( params ) );
    }

    private final static class ToNameFunction
        implements Function<ControllerParam, String>
    {

        @Override
        public String apply( final ControllerParam value )
        {
            return value.getName();
        }
    }


}
