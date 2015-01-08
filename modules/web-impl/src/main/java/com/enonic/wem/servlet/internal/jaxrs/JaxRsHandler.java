package com.enonic.wem.servlet.internal.jaxrs;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.servlet.ServletBootstrap;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.enonic.wem.servlet.internal.exception.ExceptionFeature;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;
import com.enonic.xp.web.jaxrs.JaxRsComponent;
import com.enonic.xp.web.handler.BaseWebHandler;

@Component(immediate = true, service = WebHandler.class)
public final class JaxRsHandler
    extends BaseWebHandler
{
    private final JaxRsDispatcher dispatcher;

    private final JaxRsApplication app;

    private boolean needsRefresh;

    public JaxRsHandler()
    {
        setOrder( MAX_ORDER );
        this.dispatcher = new JaxRsDispatcher();
        this.app = new JaxRsApplication();
        this.app.addComponent( new ExceptionFeature() );
        this.app.addComponent( new JaxRsSecurityFeature() );
        this.needsRefresh = true;
    }

    @Override
    protected boolean canHandle( final HttpServletRequest req )
    {
        return true;
    }

    @Override
    protected void doHandle( final HttpServletRequest req, final HttpServletResponse res, final WebHandlerChain chain )
        throws Exception
    {
        refreshIfNeeded( req.getServletContext() );
        this.dispatcher.service( req.getMethod(), req, res, true );
    }

    private void initDispatcher( final ServletContext context )
        throws Exception
    {
        final ServletConfigImpl config = new ServletConfigImpl( "jaxrs", context );

        final ServletBootstrap bootstrap = new ServletBootstrap( config );
        final RequestFactoryImpl requestFactory = new RequestFactoryImpl( context );
        final ResponseFactoryImpl responseFactory = new ResponseFactoryImpl();

        this.dispatcher.init( context, bootstrap, requestFactory, responseFactory );

        final SynchronousDispatcher synchronousDispatcher = (SynchronousDispatcher) this.dispatcher.getDispatcher();
        requestFactory.setDispatcher( synchronousDispatcher );
        responseFactory.setDispatcher( synchronousDispatcher );
        synchronousDispatcher.getDefaultContextObjects().put( ServletConfig.class, config );

        this.dispatcher.apply( this.app );
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
        initDispatcher( context );
        this.needsRefresh = false;
    }

    @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MULTIPLE)
    public void addComponent( final JaxRsComponent resource )
    {
        this.app.addComponent( resource );
        this.needsRefresh = true;
    }

    public void removeComponent( final JaxRsComponent resource )
    {
        this.app.removeComponent( resource );
        this.needsRefresh = true;
    }
}
