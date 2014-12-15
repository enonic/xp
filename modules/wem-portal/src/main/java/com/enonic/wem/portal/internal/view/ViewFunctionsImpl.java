package com.enonic.wem.portal.internal.view;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

import com.google.common.collect.Maps;

import com.enonic.wem.portal.view.ViewFunctions;

@Component
public final class ViewFunctionsImpl
    implements ViewFunctions
{
    @Override
    public String url( final String... params )
    {
        return url( toMap( params ) );
    }

    private String url( final Map<String, String> params )
    {
        return null;
    }

    @Override
    public String assetUrl( final String... params )
    {
        return assetUrl( toMap( params ) );
    }

    private String assetUrl( final Map<String, String> params )
    {
        return null;
    }

    @Override
    public String pageUrl( final String... params )
    {
        return pageUrl( toMap( params ) );
    }

    private String pageUrl( final Map<String, String> params )
    {
        return null;
    }

    @Override
    public String imageUrl( final String... params )
    {
        return imageUrl( toMap( params ) );
    }

    private String imageUrl( final Map<String, String> params )
    {
        return null;
    }

    @Override
    public String attachmentUrl( final String... params )
    {
        return attachmentUrl( toMap( params ) );
    }

    private String attachmentUrl( final Map<String, String> params )
    {
        return null;
    }

    @Override
    public String serviceUrl( final String... params )
    {
        return serviceUrl( toMap( params ) );
    }

    private String serviceUrl( final Map<String, String> params )
    {
        return null;
    }

    @Override
    public String componentUrl( final String... params )
    {
        return componentUrl( toMap( params ) );
    }

    private String componentUrl( final Map<String, String> params )
    {
        return null;
    }

    private Map<String, String> toMap( final String... params )
    {
        final Map<String, String> map = Maps.newHashMap();
        for ( final String param : params )
        {
            addParam( map, param );
        }

        return map;
    }

    private void addParam( final Map<String, String> map, final String param )
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
