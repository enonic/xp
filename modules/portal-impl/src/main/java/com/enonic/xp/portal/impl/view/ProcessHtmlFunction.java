package com.enonic.xp.portal.impl.view;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.ProcessHtmlParams;
import com.enonic.xp.portal.view.ViewFunction;
import com.enonic.xp.portal.view.ViewFunctionParams;

@Component(immediate = true)
public final class ProcessHtmlFunction
    implements ViewFunction
{
    private PortalUrlService urlService;

    @Override
    public String getName()
    {
        return "processHtml";
    }

    @Override
    public Object execute( final ViewFunctionParams params )
    {
        final ProcessHtmlParams processHtmlParams =
            new ProcessHtmlParams().setAsMap( params.getArgs() ).portalRequest( params.getPortalRequest() );
        return this.urlService.processHtml( processHtmlParams );
    }

    @Reference
    public void setUrlService( final PortalUrlService urlService )
    {
        this.urlService = urlService;
    }
}
