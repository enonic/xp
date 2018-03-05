package com.enonic.xp.lib.portal.url;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.IdentityUrlParams;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

public final class LoginUrlHandler
    extends AbstractUrlHandler
{
    private final static Set<String> VALID_URL_PROPERTY_KEYS =
        new HashSet<>( Arrays.asList( "userStore", "redirect", "contextPath", "type", "params" ) );

    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final IdentityUrlParams params = new IdentityUrlParams().
            portalRequest( request ).
            idProviderFunction( "login" ).
            userStoreKey( retrieveUserStoreKey() ).
            setAsMap( map );
        return this.urlService.identityUrl( params );
    }

    @Override
    protected boolean isValidParam( final String param )
    {
        return VALID_URL_PROPERTY_KEYS.contains( param );
    }

    private UserStoreKey retrieveUserStoreKey()
    {
        final VirtualHost virtualHost = VirtualHostHelper.getVirtualHost( request.getRawRequest() );
        if ( virtualHost != null )
        {
            return virtualHost.getUserStoreKey();
        }
        return null;
    }
}
