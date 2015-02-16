package com.enonic.xp.portal.impl.jslib.url;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.portal.url.ComponentUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;

@Component(immediate = true, service = CommandHandler.class)
public final class ComponentUrlHandler
    extends AbstractUrlHandler
{
    public ComponentUrlHandler()
    {
        super( "componentUrl" );
    }

    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final ComponentUrlParams params = new ComponentUrlParams().context( getContext() ).setAsMap( map );
        return this.urlService.componentUrl( params );
    }

    @Override
    @Reference
    public void setUrlService( final PortalUrlService value )
    {
        super.setUrlService( value );
    }
}
