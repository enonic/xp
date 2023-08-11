package com.enonic.xp.web.vhost.impl;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.annotation.Order;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.filter.OncePerRequestFilter;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;
import com.enonic.xp.web.vhost.VirtualHostResolver;
import com.enonic.xp.web.vhost.VirtualHostService;
import com.enonic.xp.web.vhost.impl.mapping.VirtualHostIdProvidersMapping;
import com.enonic.xp.web.vhost.impl.mapping.VirtualHostMapping;

@Component(immediate = true, service = Filter.class, property = {"connector=xp", "connector=api"})
@Order(-200)
@WebFilter("/*")
public final class VirtualHostFilter
    extends OncePerRequestFilter
{
    private static final Logger LOG = LoggerFactory.getLogger( VirtualHostFilter.class );

    private final VirtualHostService virtualHostService;

    private final VirtualHostResolver virtualHostResolver;

    @Activate
    public VirtualHostFilter( @Reference final VirtualHostService virtualHostService,
                              @Reference final VirtualHostResolver virtualHostResolver )
    {
        this.virtualHostService = virtualHostService;
        this.virtualHostResolver = virtualHostResolver;
    }

    @Override
    protected void doHandle( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain )
        throws Exception
    {
        if ( virtualHostService.isEnabled() &&
            DispatchConstants.XP_CONNECTOR.equals( req.getAttribute( DispatchConstants.CONNECTOR_ATTRIBUTE ) ) )
        {
            final VirtualHost virtualHost = virtualHostResolver.resolveVirtualHost( req );
            if ( virtualHost == null )
            {
                LOG.warn(
                    "Virtual host mapping could not be resolved for host [" + req.getServerName() + "] and path [" + req.getRequestURI() +
                        "]" );
                res.setStatus( HttpServletResponse.SC_NOT_FOUND );
            }
            else
            {
                VirtualHostHelper.setVirtualHost( req, virtualHost );
                final String targetPath = VirtualHostInternalHelper.getFullTargetPath( virtualHost, req );

                final RequestDispatcher dispatcher = req.getRequestDispatcher( targetPath );
                dispatcher.forward( req, res );
            }
        }
        else
        {
            final VirtualHostMapping defaultVirtualHostMapping = generateDefaultVirtualHostMapping( req );
            VirtualHostHelper.setVirtualHost( req, defaultVirtualHostMapping );
            chain.doFilter( req, res );
        }
    }

    private VirtualHostMapping generateDefaultVirtualHostMapping( final HttpServletRequest req )
    {
        final String serverName = req.getServerName();

        return new VirtualHostMapping( serverName, serverName, "/", "/", VirtualHostIdProvidersMapping.create().
            setDefaultIdProvider( IdProviderKey.system() ).
            build(), Integer.MAX_VALUE );
    }

}
