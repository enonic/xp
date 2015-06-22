package com.enonic.xp.portal.impl.jslib.url;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.portal.url.PageUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;

@Component(immediate = true, service = CommandHandler.class)
public final class PageUrlHandler
    extends AbstractUrlHandler
{
    public PageUrlHandler()
    {
        super( "pageUrl" );
    }

    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final PageUrlParams params = new PageUrlParams().portalRequest( getPortalRequest() ).setAsMap( map );
        return this.urlService.pageUrl( params );
    }

    @Override
    @Reference
    public void setUrlService( final PortalUrlService value )
    {
        super.setUrlService( value );
    }
}
