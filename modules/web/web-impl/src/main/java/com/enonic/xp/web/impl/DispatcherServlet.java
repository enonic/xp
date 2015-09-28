package com.enonic.xp.web.impl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.http.HttpService;

import com.enonic.xp.web.handler.WebHandler;

@Component(immediate = true)
public final class DispatcherServlet
    extends HttpServlet
{
    private final WebHandlerRegistry registry;

    private HttpService httpService;

    public DispatcherServlet()
    {
        this.registry = new WebHandlerRegistry();
    }

    @Activate
    public void initialize()
        throws Exception
    {
        this.httpService.registerServlet( "/*", this, null, null );
    }

    @Override
    protected void service( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
        try
        {
            doService( req, res );
        }
        catch ( final ServletException | IOException e )
        {
            throw e;
        }
        catch ( final Exception e )
        {
            throw new ServletException( e );
        }
    }

    private void doService( final HttpServletRequest req, final HttpServletResponse res )
        throws Exception
    {
        final WebHandlerChainImpl chain = new WebHandlerChainImpl( this.registry.getList() );
        chain.handle( req, res );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addHandler( final WebHandler handler )
    {
        this.registry.add( handler );
    }

    public void removeHandler( final WebHandler handler )
    {
        this.registry.remove( handler );
    }

    @Reference
    public void setHttpService( final HttpService httpService )
    {
        this.httpService = httpService;
    }
}
