package com.enonic.wem.portal;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enonic.wem.portal.dispatch.PortalDispatcher;

@Singleton
public final class PortalServlet
    extends HttpServlet
{
    private final PortalDispatcher dispatcher;

    @Inject
    public PortalServlet( final PortalDispatcher dispatcher )
    {
        this.dispatcher = dispatcher;
    }

    @Override
    protected void service( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
        this.dispatcher.dispatch( req, res );
    }
}
