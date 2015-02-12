package com.enonic.xp.web.jaxrs2;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Deactivate;

import com.enonic.xp.web.handler.BaseWebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;
import com.enonic.xp.web.jaxrs2.impl.JaxRsDispatcher;

public abstract class JaxRsHandler
    extends BaseWebHandler
{
    private final JaxRsDispatcher dispatcher;

    private boolean needsRefresh;

    private String path;

    public JaxRsHandler()
    {
        this.dispatcher = new JaxRsDispatcher();
        this.needsRefresh = true;
    }

    protected final void setPath( final String path )
    {
        this.path = path;
        this.dispatcher.setMappingPrefix( this.path );
    }

    @Override
    protected boolean canHandle( final HttpServletRequest req )
    {
        return req.getRequestURI().startsWith( this.path );
    }

    @Override
    protected void doHandle( final HttpServletRequest req, final HttpServletResponse res, final WebHandlerChain chain )
        throws Exception
    {
        refreshIfNeeded( req.getServletContext() );
        this.dispatcher.service( req.getMethod(), req, res, true );
    }

    @Deactivate
    public void destroy()
    {
        this.dispatcher.destroy();
    }

    private void refreshIfNeeded( final ServletContext context )
        throws Exception
    {
        if ( !this.needsRefresh )
        {
            return;
        }

        refresh( context );
    }

    private synchronized void refresh( final ServletContext context )
        throws Exception
    {
        destroy();
        this.dispatcher.init( context );
        this.needsRefresh = false;
    }

    protected final void addSingleton( final Object instance )
    {
        this.dispatcher.addSingleton( instance );
        this.needsRefresh = true;
    }

    protected final void removeSingleton( final Object instance )
    {
        this.dispatcher.removeSingleton( instance );
        this.needsRefresh = true;
    }
}
