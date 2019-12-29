package com.enonic.xp.portal.view;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.convert.Converters;
import com.enonic.xp.portal.PortalRequest;

@PublicApi
public final class ViewFunctionParams
{
    private String name;

    private PortalRequest portalRequest;

    private Multimap<String, String> args;

    public String getName()
    {
        return this.name;
    }

    public PortalRequest getPortalRequest()
    {
        return this.portalRequest;
    }

    public Multimap<String, String> getArgs()
    {
        return this.args;
    }

    public ViewFunctionParams name( final String name )
    {
        this.name = name;
        return this;
    }

    public ViewFunctionParams portalRequest( final PortalRequest portalRequest )
    {
        this.portalRequest = portalRequest;
        return this;
    }

    public ViewFunctionParams args( final String... args )
    {
        return args( Arrays.asList( args ) );
    }

    public ViewFunctionParams args( final List<String> args )
    {
        return args( toMap( args ) );
    }

    public ViewFunctionParams args( final Multimap<String, String> args )
    {
        this.args = args;
        return this;
    }

    public <T> T getValue( final String name, final Class<T> type )
    {
        final String value = singleValue( this.args, name );
        if ( value == null )
        {
            return null;
        }

        return Converters.convert( value, type );
    }

    public <T> T getRequiredValue( final String name, final Class<T> type )
    {
        final T value = getValue( name, type, null );
        if ( value != null )
        {
            return value;
        }

        throw new IllegalArgumentException( "Parameter [" + name + "] is required for view function [" + this.name + "]" );
    }

    public <T> T getValue( final String name, final Class<T> source, final T defValue )
    {
        final T value = getValue( name, source );
        return value != null ? value : defValue;
    }

    private static String singleValue( final Multimap<String, String> map, final String name )
    {
        final Collection<String> values = map.removeAll( name );
        if ( values == null )
        {
            return null;
        }

        if ( values.isEmpty() )
        {
            return null;
        }

        return values.iterator().next();
    }

    private static Multimap<String, String> toMap( final List<String> params )
    {
        final Multimap<String, String> map = HashMultimap.create();
        for ( final String param : params )
        {
            addParam( map, param );
        }

        return map;
    }

    private static void addParam( final Multimap<String, String> map, final String param )
    {
        final int pos = param.indexOf( '=' );
        if ( ( pos <= 0 ) || ( pos >= param.length() ) )
        {
            return;
        }

        final String key = param.substring( 0, pos ).trim();
        final String value = param.substring( pos + 1 ).trim();
        map.put( key, value );
    }
}
