package com.enonic.xp.lib.portal.url;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.ProcessHtmlParams;

final class ProcessHtmlHandler
    extends AbstractUrlHandler
{
    public ProcessHtmlHandler( final PortalUrlService urlService )
    {
        super( urlService );
    }

    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final ProcessHtmlParams params = new ProcessHtmlParams().portalRequest( getPortalRequest() ).setAsMap( map );
        return this.urlService.processHtml( params );
    }

}
