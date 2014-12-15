package com.enonic.xp.portal.jslib.impl;

import org.osgi.service.component.annotations.Component;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.PortalUrlBuilder;
import com.enonic.xp.portal.url.PortalUrlBuildersHelper;
import com.enonic.wem.script.command.CommandHandler;

@Component(immediate = true, service = CommandHandler.class)
public final class ServiceUrlHandler
    extends AbstractUrlHandler
{
    public ServiceUrlHandler()
    {
        super( "serviceUrl" );
    }

    @Override
    protected PortalUrlBuilder createBuilder( final Multimap<String, String> map )
    {
        return PortalUrlBuildersHelper.apply( createBuilders().serviceUrl(), map );
    }
}
