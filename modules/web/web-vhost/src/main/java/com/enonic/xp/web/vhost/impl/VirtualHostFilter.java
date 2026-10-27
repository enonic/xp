package com.enonic.xp.web.vhost.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import org.jspecify.annotations.Nullable;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.annotation.Order;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.filter.OncePerRequestFilter;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostIdProvider;
import com.enonic.xp.web.vhost.VirtualHostHelper;
import com.enonic.xp.web.vhost.WellKnownPaths;
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
        final String connector = (String) req.getAttribute( DispatchConstants.CONNECTOR_ATTRIBUTE );

        if ( virtualHostService.isEnabled() )
        {
            final String resourcePath = WellKnownPaths.resourcePath( req.getPathInfo() );
            if ( resourcePath != null && DispatchConstants.WEB_CONNECTOR.equals( connector ) )
            {
                final VirtualHost virtualHost = virtualHostResolver.resolveVirtualHost( new PathInfoRequestWrapper( req, resourcePath ) );
                if ( virtualHost != null )
                {
                    VirtualHostHelper.setVirtualHost( req, virtualHost );
                    VirtualHostHelper.setRequestedPath( req, requestedPath( req ) );
                    chain.doFilter( req, res );
                    return;
                }
            }
            final VirtualHost virtualHost = virtualHostResolver.resolveVirtualHost( req );
            if ( virtualHost != null )
            {
                VirtualHostHelper.setVirtualHost( req, virtualHost );
                VirtualHostHelper.setRequestedPath( req, requestedPath( req ) );
                chain.doFilter( new VirtualHostRequestWrapper( req, virtualHost ), res );
            }
            else if ( DispatchConstants.WEB_CONNECTOR.equals( connector ) || hasMappingsFor( connector ) )
            {
                LOG.warn( "Virtual host mapping could not be resolved for host [{}] and path [{}] on connector [{}]", req.getServerName(),
                          req.getPathInfo(), connector );
                res.setStatus( HttpServletResponse.SC_NOT_FOUND );
            }
            else
            {
                applyDefaultVirtualHost( req, res, chain, connector );
            }
        }
        else
        {
            applyDefaultVirtualHost( req, res, chain, connector );
        }
    }

    /**
     * The request's path as sent, when it is in canonical form: what the routing resolves, dot
     * segments, empty segments and path parameters, would otherwise let it name a resource other
     * than the one the request reaches.
     */
    static @Nullable String requestedPath( final HttpServletRequest req )
    {
        final String uri = req.getRequestURI();
        return uri != null && isCanonical( uri ) ? uri : null;
    }

    static boolean isCanonical( final String path )
    {
        if ( !path.startsWith( "/" ) || path.contains( "//" ) || path.contains( ";" ) )
        {
            return false;
        }
        try
        {
            return path.equals( new URI( path ).normalize().getRawPath() );
        }
        catch ( URISyntaxException e )
        {
            return false;
        }
    }

    /**
     * The same request with another path, for resolving a mapping by it.
     */
    private static final class PathInfoRequestWrapper
        extends HttpServletRequestWrapper
    {
        private final String pathInfo;

        PathInfoRequestWrapper( final HttpServletRequest request, final String pathInfo )
        {
            super( request );
            this.pathInfo = pathInfo;
        }

        @Override
        public String getPathInfo()
        {
            return pathInfo;
        }
    }

    private boolean hasMappingsFor( final String connector )
    {
        return virtualHostService.getVirtualHosts()
            .stream()
            .anyMatch( virtualHost -> Objects.equals( connector, virtualHost.getConnector() ) );
    }

    private static void applyDefaultVirtualHost( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain,
                                                 final String connector )
        throws Exception
    {
        final VirtualHostIdProvidersMapping idProvidersMapping = VirtualHostIdProvidersMapping.create()
            .addIdProviderWithSettings( IdProviderKey.system(), VirtualHostIdProvider.create().allFlows().build() )
            .setDefaultIdProvider( IdProviderKey.system() )
            .build();

        final String serverName = req.getServerName();
        VirtualHostHelper.setVirtualHost( req, new VirtualHostMapping( serverName, serverName, "/", "/", idProvidersMapping,
                                                                       Integer.MAX_VALUE, null, connector ) );
        VirtualHostHelper.setRequestedPath( req, requestedPath( req ) );
        chain.doFilter( req, res );
    }
}
