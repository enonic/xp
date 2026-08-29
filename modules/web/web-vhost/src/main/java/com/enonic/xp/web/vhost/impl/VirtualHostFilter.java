package com.enonic.xp.web.vhost.impl;

import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.annotation.Order;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.filter.OncePerRequestFilter;
import com.enonic.xp.web.vhost.IdProviderFlow;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;
import com.enonic.xp.web.vhost.VirtualHostResolver;
import com.enonic.xp.web.vhost.VirtualHostService;
import com.enonic.xp.web.vhost.impl.mapping.VirtualHostIdProvidersMapping;
import com.enonic.xp.web.vhost.impl.mapping.VirtualHostMapping;

@Component(immediate = true, service = Filter.class, property = {"connector=xp", "connector=api", "connector=status"})
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
        final Object connectorAttribute = req.getAttribute( DispatchConstants.CONNECTOR_ATTRIBUTE );
        final String connector = connectorAttribute != null ? connectorAttribute.toString() : DispatchConstants.XP_CONNECTOR;

        if ( virtualHostService.isEnabled() )
        {
            final VirtualHost virtualHost = virtualHostResolver.resolveVirtualHost( req );
            if ( virtualHost != null )
            {
                VirtualHostHelper.setVirtualHost( req, virtualHost );
                chain.doFilter( new VirtualHostRequestWrapper( req, virtualHost ), res );
            }
            else if ( DispatchConstants.XP_CONNECTOR.equals( connector ) )
            {
                LOG.warn( "Virtual host mapping could not be resolved for host [{}] and path [{}]", req.getServerName(),
                          req.getPathInfo() );
                res.setStatus( HttpServletResponse.SC_NOT_FOUND );
            }
            else
            {
                // Management and statistics ports stay reachable when no vhost mapping matches.
                applyDefaultVirtualHost( req, res, chain, connector );
            }
        }
        else
        {
            applyDefaultVirtualHost( req, res, chain, connector );
        }
    }

    private static void applyDefaultVirtualHost( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain,
                                                 final String connector )
        throws Exception
    {
        final VirtualHostMapping virtualHost = generateDefaultVirtualHostMapping( req, connector );
        VirtualHostHelper.setVirtualHost( req, virtualHost );
        chain.doFilter( req, res );
    }

    private static VirtualHostMapping generateDefaultVirtualHostMapping( final HttpServletRequest req, final String connector )
    {
        final String serverName = req.getServerName();

        final VirtualHostIdProvidersMapping.Builder idProvidersMapping = VirtualHostIdProvidersMapping.create();
        if ( !DispatchConstants.XP_CONNECTOR.equals( connector ) )
        {
            // Only non-interactive authentication out of the box on the management and statistics ports.
            idProvidersMapping.addIdProvider( IdProviderKey.system(), Set.of( IdProviderFlow.AUTOLOGIN ) );
        }
        idProvidersMapping.setDefaultIdProvider( IdProviderKey.system() );

        return new VirtualHostMapping( serverName, serverName, "/", "/", idProvidersMapping.build(), Integer.MAX_VALUE, null, connector );
    }
}
