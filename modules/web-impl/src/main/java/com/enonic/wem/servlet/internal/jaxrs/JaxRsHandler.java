package com.enonic.wem.servlet.internal.jaxrs;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.servlet.ServletBootstrap;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.enonic.wem.servlet.internal.exception.ExceptionFeature;
import com.enonic.xp.web.WebContext;
import com.enonic.xp.web.WebHandler;
import com.enonic.xp.web.jaxrs.JaxRsComponent;

@Component(immediate = true)
public final class JaxRsHandler
    implements WebHandler
{
    private final JaxRsDispatcher dispatcher;

    private final JaxRsApplication app;

    private boolean needsRefresh;

    public JaxRsHandler()
    {
        this.dispatcher = new JaxRsDispatcher();
        this.app = new JaxRsApplication();
        this.app.addComponent( new ExceptionFeature() );
        this.app.addComponent( new JaxRsSecurityFeature() );
        this.needsRefresh = true;
    }

    @Override
    public int getOrder()
    {
        return MAX_ORDER;
    }

    @Override
    public boolean handle( final WebContext context )
        throws Exception
    {
        refreshIfNeeded( context.getServletContext() );
        this.dispatcher.service( context.getMethod(), context.getRequest(), context.getResponse(), true );
        return true;
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
