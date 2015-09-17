package com.enonic.xp.web.jaxrs.impl;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.resteasy.spi.UnhandledException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.enonic.xp.web.handler.BaseWebHandler;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;
import com.enonic.xp.web.jaxrs.JaxRsResource;

@Component(immediate = true, service = WebHandler.class)
public class JaxRsHandler
    extends BaseWebHandler
{
    private final JaxRsDispatcher dispatcher;

    private boolean needsRefresh;

    private String path;

    public JaxRsHandler()
    {
        this.dispatcher = new JaxRsDispatcher();
        this.needsRefresh = true;
        setOrder( MAX_ORDER - 20 );
        setPath( "/" );
        addSingleton( new CommonJaxRsFeature() );
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
        try
        {
            refreshIfNeeded( req.getServletContext() );
            this.dispatcher.service( req.getMethod(), req, res, true );
        }
        catch ( final UnhandledException e )
        {
            if ( !res.isCommitted() )
            {
                throw e;
            }
        }
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

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addResource( final JaxRsResource resource )
    {
        addSingleton( resource );
    }

    public void removeResource( final JaxRsResource resource )
    {
        removeSingleton( resource );
    }
}
