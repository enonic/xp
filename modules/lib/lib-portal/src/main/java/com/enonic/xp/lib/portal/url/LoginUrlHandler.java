package com.enonic.xp.lib.portal.url;

import com.enonic.xp.portal.url.IdentityUrlParams;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;
import com.google.common.collect.Multimap;

import java.util.Arrays;
import java.util.List;

public final class LoginUrlHandler
    extends AbstractUrlHandler
{
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
    protected List<String> getValidUrlPropertyKeys() {
        return Arrays.asList("userStore", "redirect", "type", "params");
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
