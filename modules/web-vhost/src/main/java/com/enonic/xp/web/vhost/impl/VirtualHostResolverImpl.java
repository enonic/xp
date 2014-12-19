package com.enonic.xp.web.vhost.impl;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

@Component
public final class VirtualHostResolverImpl
    implements VirtualHostResolver
{
    @Override
    public boolean requireVirtualHost()
    {
        return false;
    }

    @Override
    public VirtualHost resolve( final HttpServletRequest req )
    {
        return null;
    }
}
