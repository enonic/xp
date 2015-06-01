package com.enonic.xp.portal.impl.jslib.url;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.ProcessHtmlParams;

@Component(immediate = true, service = CommandHandler.class)
public final class ProcessHtmlHandler
    extends AbstractUrlHandler
{
    public ProcessHtmlHandler()
    {
        super( "processHtml" );
    }

    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final ProcessHtmlParams params = new ProcessHtmlParams().portalRequest( getPortalRequest() ).setAsMap( map );
        return this.urlService.processHtml( params );
    }

    @Override
    @Reference
    public void setUrlService( final PortalUrlService value )
    {
        super.setUrlService( value );
    }
}
