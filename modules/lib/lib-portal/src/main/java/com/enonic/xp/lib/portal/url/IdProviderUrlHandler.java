package com.enonic.xp.lib.portal.url;

import java.util.Set;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.IdentityUrlParams;

public final class IdProviderUrlHandler
    extends AbstractUrlHandler
{
    private static final Set<String> VALID_URL_PROPERTY_KEYS =
        Set.of( "idProvider", "redirect", "contextPath", "type", "params" );

    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final IdentityUrlParams params = new IdentityUrlParams().
            portalRequest( request ).
            idProviderFunction( null ).
            setAsMap( map );
        return this.urlService.identityUrl( params );
    }

    @Override
    protected boolean isValidParam( final String param )
    {
        return VALID_URL_PROPERTY_KEYS.contains( param );
    }
}
