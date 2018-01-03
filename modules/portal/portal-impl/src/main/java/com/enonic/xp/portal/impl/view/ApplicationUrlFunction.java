package com.enonic.xp.portal.impl.view;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.portal.url.ApplicationUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.view.ViewFunction;
import com.enonic.xp.portal.view.ViewFunctionParams;

@Component(immediate = true)
public final class ApplicationUrlFunction
    implements ViewFunction
{
    private PortalUrlService urlService;

    @Override
    public String getName()
    {
        return "applicationUrl";
    }

    @Override
    public Object execute( final ViewFunctionParams params )
    {
        final ApplicationUrlParams urlParams =
            new ApplicationUrlParams().setAsMap( params.getArgs() ).portalRequest( params.getPortalRequest() );
        return this.urlService.applicationUrl( urlParams );
    }

    @Reference
    public void setUrlService( final PortalUrlService urlService )
    {
        this.urlService = urlService;
    }
}
