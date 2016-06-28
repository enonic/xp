package com.enonic.xp.lib.portal.url;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.IdentityUrlParams;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

public final class IdProviderUrlHandler
    extends AbstractUrlHandler
{
    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final IdentityUrlParams params = new IdentityUrlParams().
            portalRequest( request ).
            idProviderFunction( null ).
            userStoreKey( retrieveUserStoreKey() ).
            setAsMap( map );
        return this.urlService.identityUrl( params );
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