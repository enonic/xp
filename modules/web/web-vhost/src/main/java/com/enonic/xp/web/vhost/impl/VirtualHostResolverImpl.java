package com.enonic.xp.web.vhost.impl;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostResolver;
import com.enonic.xp.web.vhost.VirtualHostService;

@Component(immediate = true)
public class VirtualHostResolverImpl
    implements VirtualHostResolver
{

    private final VirtualHostService virtualHostService;

    @Activate
    public VirtualHostResolverImpl( @Reference final VirtualHostService virtualHostService )
    {
        this.virtualHostService = virtualHostService;
    }

    @Override
    public VirtualHost resolveVirtualHost( final HttpServletRequest req )
    {
        for ( final VirtualHost virtualHost : virtualHostService.getVirtualHosts() )
        {
            if ( matchesHost( virtualHost, req ) && matchesSource( virtualHost, req ) )
            {
                return virtualHost;
            }
        }

        return null;
    }

    private boolean matchesHost( final VirtualHost virtualHost, final HttpServletRequest req )
    {
        final String serverName = req.getServerName();
        return virtualHost.getHost().equalsIgnoreCase( serverName );
    }

    private boolean matchesSource( final VirtualHost virtualHost, final HttpServletRequest req )
    {
        final String actualPath = req.getRequestURI();
        return "/".equals( virtualHost.getSource() ) || actualPath.equals( virtualHost.getSource() ) ||
            actualPath.startsWith( virtualHost.getSource() + "/" );
    }

}
