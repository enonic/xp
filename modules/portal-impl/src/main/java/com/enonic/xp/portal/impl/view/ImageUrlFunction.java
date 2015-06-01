package com.enonic.xp.portal.impl.view;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.portal.url.ImageUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.view.ViewFunction;
import com.enonic.xp.portal.view.ViewFunctionParams;

@Component(immediate = true)
public final class ImageUrlFunction
    implements ViewFunction
{
    private PortalUrlService urlService;

    @Override
    public String getName()
    {
        return "imageUrl";
    }

    @Override
    public Object execute( final ViewFunctionParams params )
    {
        final ImageUrlParams urlParams = new ImageUrlParams().setAsMap( params.getArgs() ).portalRequest( params.getPortalRequest() );
        return this.urlService.imageUrl( urlParams );
    }

    @Reference
    public void setUrlService( final PortalUrlService urlService )
    {
        this.urlService = urlService;
    }
}
