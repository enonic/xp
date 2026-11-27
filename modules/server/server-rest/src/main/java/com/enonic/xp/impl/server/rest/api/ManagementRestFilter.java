package com.enonic.xp.impl.server.rest.api;

import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.annotation.Order;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.security.auth.AccessTokenInfo;
import com.enonic.xp.security.token.XpScopes;
import com.enonic.xp.web.filter.OncePerRequestFilter;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;
import com.enonic.xp.web.vhost.WellKnownPaths;

/**
 * Guards the legacy JAX-RS resources ({@code /repo/...}, {@code /system/...}, ...) of the management endpoint, which
 * are everything there but the {@code /<application>:<api>} management APIs and the well-known documents. Universal
 * API paths are recognised the same way {@code SlashApiFilter} does.
 * <p>
 * A management vhost switches the resources off with {@code mapping.<name>.context.api.rest.enabled = false}; they
 * then answer 404. Default is enabled.
 * <p>
 * The resources know nothing of scopes, so of the access tokens only one for everything reaches them: a token
 * without XP's {@code admin} scope is answered 403 naming it (RFC 6750, section 3.1).
 * <p>
 * The filter runs after authentication. A request without credentials is therefore challenged the same on every
 * path, whether the resources are on or off, and the challenge is where a client learns where to get a token.
 */
@Component(immediate = true, service = Filter.class, property = {"connector=api"})
@Order(-10)
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
        final String pathInfo = req.getPathInfo();
        if ( pathInfo != null && ( API_PATTERN.matcher( pathInfo ).find() || WellKnownPaths.documentOf( pathInfo ) != null ) )
        {
            chain.doFilter( req, res );
            return;
        }
        final VirtualHost virtualHost = VirtualHostHelper.getVirtualHost( req );
        if ( virtualHost != null && "false".equalsIgnoreCase( virtualHost.getContext().get( REST_ENABLED ) ) )
        {
            res.sendError( HttpServletResponse.SC_NOT_FOUND );
            return;
        }
        final AccessTokenInfo accessToken = ContextAccessor.current().getAuthInfo().getAccessToken();
        if ( accessToken != null && !accessToken.getScope().contains( XpScopes.ADMIN ) )
        {
            res.setHeader( "WWW-Authenticate", "Bearer error=\"insufficient_scope\", scope=\"" + XpScopes.ADMIN + "\"" );
            res.sendError( HttpServletResponse.SC_FORBIDDEN );
            return;
        }
        chain.doFilter( req, res );
    }
}
