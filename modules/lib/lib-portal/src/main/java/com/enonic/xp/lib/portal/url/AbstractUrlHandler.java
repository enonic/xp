package com.enonic.xp.lib.portal.url;

import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public abstract class AbstractUrlHandler
    implements ScriptBean
{
    protected PortalRequest request;

    protected PortalUrlService urlService;

    protected abstract String buildUrl( final Multimap<String, String> map );

    public final String createUrl( final ScriptValue params )
    {
        return createUrl( params.getMap() );
    }

    private String createUrl( final Map<String, Object> params )
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

    @Override
    public void initialize( final BeanContext context )
    {
        this.request = PortalRequestAccessor.get();
        this.urlService = context.getService( PortalUrlService.class ).get();
    }
}
