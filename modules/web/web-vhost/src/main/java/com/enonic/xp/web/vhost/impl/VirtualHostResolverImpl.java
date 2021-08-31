package com.enonic.xp.web.vhost.impl;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

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

    private final List<VirtualHost> virtualHosts;

    @Activate
    public VirtualHostResolverImpl( @Reference final VirtualHostService virtualHostService )
    {
        this.virtualHosts = List.copyOf( virtualHostService.getVirtualHosts() );
    }

    @Override
    public VirtualHost resolveVirtualHost( final HttpServletRequest req )
    {
        for ( final VirtualHost virtualHost : virtualHosts )
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
        final String serverName = req.getServerName().toLowerCase( Locale.ROOT );
        final String host = virtualHost.getHost();
        return Stream.of( host.split( " " ) ).map( h -> h.toLowerCase( Locale.ROOT ) ).allMatch( h -> {
            if ( h.startsWith( "~" ) )
            {
                return serverName.matches( h.substring( 1 ) );
            }
            else
            {
                return h.equals( serverName );
            }
        } );
    }

    private boolean matchesSource( final VirtualHost virtualHost, final HttpServletRequest req )
    {
        final String actualPath = req.getRequestURI();
        return "/".equals( virtualHost.getSource() ) || actualPath.equals( virtualHost.getSource() ) ||
            actualPath.startsWith( virtualHost.getSource() + "/" );
    }

}
