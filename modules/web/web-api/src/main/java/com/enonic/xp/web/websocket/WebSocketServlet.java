package com.enonic.xp.web.websocket;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class WebSocketServlet
    extends HttpServlet
{
    private WebSocketHandlerFactory handlerFactory;

    private WebSocketHandler handler;

    @Override
    public void init()
        throws ServletException
    {
        if ( this.handlerFactory == null )
        {
            throw new ServletException( WebSocketHandlerFactory.class.getSimpleName() + " not registered" );
        }

        this.handler = this.handlerFactory.create();

        try
        {
            configure( this.handler );
        }
        catch ( final ServletException e )
        {
            throw e;
        }
        catch ( final Exception e )
        {
            throw new ServletException( "Failed to configure endpoint", e );
        }
    }

    @Override
    public void destroy()
    {
        this.handler.destroy();
    }

    protected abstract void configure( WebSocketHandler handler )
        throws Exception;

    @Override
    protected void service( HttpServletRequest req, HttpServletResponse res )
        throws ServletException, IOException
    {
        if ( !this.handler.isUpgradeRequest( req, res ) )
        {
            super.service( req, res );
            return;
        }

        if ( this.handler.acceptWebSocket( req, res ) )
        {
            return;
        }

        if ( res.isCommitted() )
        {
            return;
        }

        super.service( req, res );
    }

    public void setHandlerFactory( final WebSocketHandlerFactory handlerFactory )
    {
        this.handlerFactory = handlerFactory;
    }
}
