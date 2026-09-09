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
    public void service( final HttpServletRequest req, final HttpServletResponse res )
        throws IOException, ServletException
    {
        this.resource.service( req, res );
    }
}
