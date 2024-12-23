package com.enonic.xp.impl.server.auth;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

import com.google.common.net.HttpHeaders;

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
