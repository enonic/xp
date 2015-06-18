package com.enonic.xp.lib.portal.url;

import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.url.PortalUrlService;

abstract class AbstractUrlHandler
{
    protected final PortalUrlService urlService;

    public AbstractUrlHandler( final PortalUrlService urlService )
    {
        this.urlService = urlService;
    }

    protected final PortalRequest getPortalRequest()
    {
        return PortalRequestAccessor.get();
    }

    protected abstract String buildUrl( final Multimap<String, String> map );

    public final String createUrl( final Map<String, Object> params )
    {
        final Multimap<String, String> map = toMap( params );
        return buildUrl( map );
    }

    private Multimap<String, String> toMap( final Map<String, Object> params )
    {
        final Multimap<String, String> map = HashMultimap.create();
        for ( final Map.Entry<String, Object> param : params.entrySet() )
        {
            final String key = param.getKey();
            if ( key.equals( "params" ) )
            {
                applyParams( map, param.getValue() );
            }
            else
            {
                applyParam( map, "_" + key, param.getValue() );
            }
        }

        return map;
    }

    private void applyParams( final Multimap<String, String> params, final Object value )
    {
        if ( value instanceof Map )
        {
            applyParams( params, (Map) value );
        }
    }

    private void applyParams( final Multimap<String, String> params, final Map<?, ?> value )
    {
        for ( final Map.Entry<?, ?> entry : value.entrySet() )
        {
            final String key = entry.getKey().toString();
            applyParam( params, key, entry.getValue() );
        }
    }

    private void applyParam( final Multimap<String, String> params, final String key, final Object value )
    {
        if ( value instanceof Iterable )
        {
            applyParam( params, key, (Iterable) value );
        }
        else
        {
            params.put( key, value.toString() );
        }
    }

    private void applyParam( final Multimap<String, String> params, final String key, final Iterable values )
    {
        for ( final Object value : values )
        {
            params.put( key, value.toString() );
        }
    }

}
