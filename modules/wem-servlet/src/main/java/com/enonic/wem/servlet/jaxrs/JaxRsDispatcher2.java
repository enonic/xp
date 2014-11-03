package com.enonic.wem.servlet.jaxrs;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import com.enonic.wem.servlet.ServletRequestHolder;
import com.enonic.wem.servlet.internal.JaxRsApplication2;
import com.enonic.wem.servlet.internal.exception.ExceptionFeature;

public abstract class JaxRsDispatcher2
    extends HttpServlet
{
    private HttpServletDispatcher dispatcher;

    private final JaxRsApplication2 app;

    public JaxRsDispatcher2()
    {
        this.app = new JaxRsApplication2();
    }

    @Override
    public final void init( final ServletConfig config )
        throws ServletException
    {
        addComponent( new ExceptionFeature() );
        super.init( config );
        initDispatcher();
        refresh();
    }

    private void initDispatcher()
        throws ServletException
    {
        this.dispatcher = new HttpServletDispatcher();
        final ClassLoader currentLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader( HttpServletDispatcher.class.getClassLoader() );

        try
        {
            this.dispatcher.init( getServletConfig() );
        }
        finally
        {
            Thread.currentThread().setContextClassLoader( currentLoader );
        }
    }

    public final void setContributor( final JaxRsContributor contributor )
    {
        contributor.getSingletons().forEach( this::addComponent );
    }

    private synchronized void refresh()
        throws ServletException
    {
        initDispatcher();
        this.app.apply( this.dispatcher.getDispatcher() );
    }

    @Override
    protected final void service( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
        refreshIfNeeded();

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

    public void addComponent( final Object component )
    {
        this.app.addComponent( component );
    }

    public void removeComponent( final Object component )
    {
        this.app.removeComponent( component );
    }

    private void refreshIfNeeded()
        throws ServletException
    {
        if ( !this.app.isModified() )
        {
            return;
        }

        refresh();
    }
}
