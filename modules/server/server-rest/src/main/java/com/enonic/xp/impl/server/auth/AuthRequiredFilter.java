package com.enonic.xp.impl.server.auth;

import org.osgi.service.component.annotations.Component;

import com.google.common.net.HttpHeaders;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.annotation.Order;
import com.enonic.xp.web.filter.OncePerRequestFilter;

@Component(immediate = true, service = Filter.class, property = {"connector=api"})
@Order(-20)
@WebFilter("/*")
public class AuthRequiredFilter
    extends OncePerRequestFilter
{
    @Override
    protected void doHandle( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain )
        throws Exception
    {
        if ( req.getUserPrincipal() == null )
        {
            res.addHeader( HttpHeaders.WWW_AUTHENTICATE, "Basic");
            res.addHeader( HttpHeaders.WWW_AUTHENTICATE, "Bearer");
            res.sendError( HttpServletResponse.SC_UNAUTHORIZED );
            return;
        }
        chain.doFilter( req, res );
    }
}
