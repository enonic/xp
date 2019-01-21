package com.enonic.xp.portal.impl.view;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.url.IdentityUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.view.ViewFunction;
import com.enonic.xp.portal.view.ViewFunctionParams;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

@Component(immediate = true)
public final class LogoutUrlFunction
    implements ViewFunction
{
    private PortalUrlService urlService;

    @Override
    public String getName()
    {
        return "logoutUrl";
    }

    @Override
    public Object execute( final ViewFunctionParams params )
    {
        final IdentityUrlParams urlParams = new IdentityUrlParams().
            idProviderKey( retrieveIdProviderKey( params.getPortalRequest() ) ).
            idProviderFunction( "logout" ).
            portalRequest( params.getPortalRequest() ).
            setAsMap( params.getArgs() );

        return this.urlService.identityUrl( urlParams );
    }

    private IdProviderKey retrieveIdProviderKey( final PortalRequest portalRequest )
    {
        final VirtualHost virtualHost = VirtualHostHelper.getVirtualHost( portalRequest.getRawRequest() );
        if ( virtualHost != null )
        {
            return virtualHost.getIdProviderKey();
        }
        return null;
    }

    @Reference
    public void setUrlService( final PortalUrlService urlService )
    {
        this.urlService = urlService;
    }
}
