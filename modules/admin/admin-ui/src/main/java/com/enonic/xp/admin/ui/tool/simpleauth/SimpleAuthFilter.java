package com.enonic.xp.admin.ui.tool.simpleauth;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.web.filter.OncePerRequestFilter;

@Component(service = Filter.class,
    property = {"osgi.http.whiteboard.filter.pattern=/admin", "service.ranking:Integer=50",
        "osgi.http.whiteboard.filter.dispatcher=FORWARD", "osgi.http.whiteboard.filter.dispatcher=REQUEST"})
public class SimpleAuthFilter
    extends OncePerRequestFilter
{
    @Override
    protected void doHandle( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain )
        throws Exception
    {
        //Executes the rest of the filters with a wrapped response
        if ( req.getRequestURI().startsWith( "/admin/rest" ) )
        {
            chain.doFilter( req, res );
        }
        else
        {
            final SimpleAuthResponseWrapper auth0ResponseWrapper = new SimpleAuthResponseWrapper( req, res );
            chain.doFilter( req, auth0ResponseWrapper );
        }
    }
}
