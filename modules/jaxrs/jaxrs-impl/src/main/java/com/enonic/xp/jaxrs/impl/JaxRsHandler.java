package com.enonic.xp.jaxrs.impl;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.resteasy.spi.UnhandledException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.jaxrs.JaxRsService;
import com.enonic.xp.web.handler.BaseWebHandler;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

@Component(immediate = true, service = {WebHandler.class, JaxRsService.class})
public final class JaxRsHandler
    extends BaseWebHandler
    implements JaxRsService
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

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addResource( final JaxRsComponent resource )
    {
        this.dispatcher.app.addSingleton( resource );
        this.needsRefresh = true;
    }

    public void removeResource( final JaxRsComponent resource )
    {
        this.dispatcher.app.removeSingleton( resource );
        this.needsRefresh = true;
    }

    @Override
    public List<JaxRsComponent> getComponents()
    {
        return ImmutableList.copyOf( this.dispatcher.app.getComponents() );
    }
}
