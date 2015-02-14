package com.enonic.xp.portal.jslib.impl.url;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.ServiceUrlParams;

@Component(immediate = true, service = CommandHandler.class)
public final class ServiceUrlHandler
    extends AbstractUrlHandler
{
    public ServiceUrlHandler()
    {
        super( "serviceUrl" );
    }

    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final ServiceUrlParams params = new ServiceUrlParams().context( getContext() ).setAsMap( map );
        return this.urlService.serviceUrl( params );
    }

    @Override
    @Reference
    public void setUrlService( final PortalUrlService value )
    {
        super.setUrlService( value );
    }
}
