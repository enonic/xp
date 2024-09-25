package com.enonic.xp.lib.portal.url;

import java.util.Set;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.ApiUrlParams;

public final class ApiUrlHandler
    extends AbstractUrlHandler
{
    private static final Set<String> VALID_URL_PROPERTY_KEYS = Set.of( "application", "api", "type", "params" );

    private Object path;

    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final ApiUrlParams params = new ApiUrlParams().portalRequest( this.request ).setAsMap( map ).path( this.path );
        return this.urlService.apiUrl( params );
    }

    public void setPath( final Object path )
    {
        this.path = path;
    }

    @Override
    protected boolean isValidParam( final String param )
    {
        return VALID_URL_PROPERTY_KEYS.contains( param );
    }
}
