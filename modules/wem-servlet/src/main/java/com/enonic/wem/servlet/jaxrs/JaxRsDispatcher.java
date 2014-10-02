package com.enonic.wem.servlet.jaxrs;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import com.enonic.wem.servlet.ServletRequestHolder;
import com.enonic.wem.servlet.internal.DefaultExceptionMapper;
import com.enonic.wem.servlet.internal.JaxRsApplication;
import com.enonic.wem.servlet.internal.JaxRsResourceFactory;

public abstract class JaxRsDispatcher
    extends HttpServlet
{
    private final HttpServletDispatcher dispatcher;

    private JaxRsContributor contributor;

    public JaxRsDispatcher()
    {
        this.dispatcher = new HttpServletDispatcher();
    }

    @Override
    public final void init( final ServletConfig config )
        throws ServletException
    {
        doInit( config );
        addProvider( new DefaultExceptionMapper() );
        refresh();
    }

    private void doInit( final ServletConfig config )
        throws ServletException
    {
        final ClassLoader currentLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader( HttpServletDispatcher.class.getClassLoader() );

        try
        {
            this.dispatcher.init( config );
        }
        finally
        {
            Thread.currentThread().setContextClassLoader( currentLoader );
        }
    }

    public final void setContributor( final JaxRsContributor contributor )
    {
        this.contributor = contributor;
    }

    private void refresh()
    {
        final JaxRsApplication app = new JaxRsApplication();
        this.contributor.getSingletons().forEach( app::addSingleton );

        app.getProviders().forEach( this::addProvider );
        app.getResourceProviders().forEach( this::addResourceProvider );
        app.getResources().forEach( this::addResource );
    }

    private void addResource( final Object resource )
    {
        this.dispatcher.getDispatcher().getRegistry().addSingletonResource( resource );
    }

    private void addResourceProvider( final ResourceProvider provider )
    {
        this.dispatcher.getDispatcher().getRegistry().addResourceFactory( new JaxRsResourceFactory( provider ) );
    }

    private void addProvider( final Object provider )
    {
        this.dispatcher.getDispatcher().getProviderFactory().register( provider );
    }

    @Override
    protected final void service( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
        try
        {
            ServletRequestHolder.setRequest( req );
            this.dispatcher.service( req, res );
        }
        finally
        {
            ServletRequestHolder.setRequest( null );
        }
    }
}
