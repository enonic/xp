package com.enonic.xp.impl.server.rest.api;

import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.annotation.Order;
import com.enonic.xp.web.filter.OncePerRequestFilter;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

/**
 * Lets a management vhost switch off the legacy JAX-RS resources ({@code /repo/...}, {@code /system/...}, ...) with
 * {@code mapping.<name>.context.api.rest.enabled = false}, leaving only the {@code /<application>:<api>} management
 * APIs reachable. Universal API paths are recognised the same way {@code SlashApiFilter} does. Default is enabled.
 */
@Component(immediate = true, service = Filter.class, property = {"connector=api"})
@Order(-100)
@WebFilter("/*")
public final class ManagementRestFilter
    extends OncePerRequestFilter
{
    public static final String REST_ENABLED = ManagementApiPolicy.PREFIX + "rest.enabled";

    private static final Pattern API_PATTERN = Pattern.compile( "^/[^/]+:[^/?]+" );

    @Override
    protected void doHandle( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain )
        throws Exception
    {
        final VirtualHost virtualHost = VirtualHostHelper.getVirtualHost( req );
        if ( virtualHost != null && "false".equalsIgnoreCase( virtualHost.getContext().get( REST_ENABLED ) ) )
        {
            final String pathInfo = req.getPathInfo();
            if ( pathInfo == null || !API_PATTERN.matcher( pathInfo ).find() )
            {
                res.sendError( HttpServletResponse.SC_NOT_FOUND );
                return;
            }
        }
        chain.doFilter( req, res );
    }
}
