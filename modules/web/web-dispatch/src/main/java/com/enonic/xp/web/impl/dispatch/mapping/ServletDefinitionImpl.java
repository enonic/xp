package com.enonic.xp.web.impl.dispatch.mapping;

import java.io.IOException;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

    @Override
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
