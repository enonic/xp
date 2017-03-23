package com.enonic.xp.web.impl.dispatch.mapping;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enonic.xp.web.dispatch.ServletMapping;

final class ServletDefinitionImpl
    extends ResourceDefinitionImpl<Servlet>
    implements ServletDefinition
{
    ServletDefinitionImpl( final ServletMapping mapping )
    {
        super( mapping );
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
}
