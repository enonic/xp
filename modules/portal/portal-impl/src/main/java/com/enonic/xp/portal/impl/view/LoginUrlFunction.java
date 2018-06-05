package com.enonic.xp.portal.impl.view;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.url.IdentityUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.view.ViewFunction;
import com.enonic.xp.portal.view.ViewFunctionParams;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

@Component(immediate = true)
public final class LoginUrlFunction
    implements ViewFunction
{
    private PortalUrlService urlService;

    @Override
    public String getName()
    {
        return "loginUrl";
    }

    @Override
    public Object execute( final ViewFunctionParams params )
    {
        final IdentityUrlParams urlParams = new IdentityUrlParams().
            userStoreKey( retrieveUserStoreKey( params.getPortalRequest() ) ).
            idProviderFunction( "login" ).
            portalRequest( params.getPortalRequest() ).
            setAsMap( params.getArgs() );

        return this.urlService.identityUrl( urlParams );
    }

    private UserStoreKey retrieveUserStoreKey( final PortalRequest portalRequest )
    {
        final VirtualHost virtualHost = VirtualHostHelper.getVirtualHost( portalRequest.getRawRequest() );
        if ( virtualHost != null )
        {
            return virtualHost.getUserStoreKey();
        }
        return null;
    }

    @Reference
    public void setUrlService( final PortalUrlService urlService )
    {
        this.urlService = urlService;
    }
}
