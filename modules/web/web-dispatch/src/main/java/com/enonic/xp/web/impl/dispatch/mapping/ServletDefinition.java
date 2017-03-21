package com.enonic.xp.web.impl.dispatch.mapping;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class ServletDefinition
    extends ResourceDefinition<Servlet>
{
    private ServletDefinition( final Servlet servlet )
    {
        super( servlet );
    }

    @Override
    void doInit( final ResourceConfig config )
        throws ServletException
    {
        this.resource.init( config );
    }

    @Override
    void doDestroy()
    {
        this.resource.destroy();
    }

    public boolean service( final HttpServletRequest req, final HttpServletResponse res )
        throws IOException, ServletException
    {
        if ( matches( req.getRequestURI() ) )
        {
            this.resource.service( req, res );
            return true;
        }

        return false;
    }

    @Override
    public void configure( final Map<String, Object> serviceProps )
    {
        super.configure( serviceProps );
        setName( serviceProps, "osgi.http.whiteboard.servlet.name" );
        setInitParams( serviceProps, "osgi.http.whiteboard.servlet.init" );
        setUrlPatterns( serviceProps, "osgi.http.whiteboard.servlet.pattern" );
    }

    public static ServletDefinition create( final Servlet servlet )
    {
        return new ServletDefinition( servlet );
    }
}
