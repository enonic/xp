package com.enonic.wem.servlet.internal.dispatch;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.google.common.collect.Lists;

import com.enonic.xp.web.WebContext;
import com.enonic.xp.web.WebHandler;
import com.enonic.xp.web.servlet.ServletRequestHolder;

@Component(immediate = true, service = Servlet.class, property = "alias=/*")
public final class DispatcherServlet
    extends HttpServlet
{
    private final List<WebHandler> handlers;

    public DispatcherServlet()
    {
        this.handlers = Lists.newCopyOnWriteArrayList();
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
            throw new IOException( e );
        }
        finally
        {
            ServletRequestHolder.setRequest( null );
        }
    }

    private void doService( final HttpServletRequest req, final HttpServletResponse res )
        throws Exception
    {
        final WebContextImpl context = new WebContextImpl();
        context.setRequest( req );
        context.setResponse( res );

        final boolean result = doHandle( context );
        if ( result )
        {
            return;
        }

        res.sendError( HttpServletResponse.SC_NOT_FOUND );
    }

    private boolean doHandle( final WebContext context )
        throws Exception
    {
        for ( final WebHandler handler : this.handlers )
        {
            final boolean result = handler.handle( context );
            if ( result )
            {
                return true;
            }
        }

        return false;
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addHandler( final WebHandler handler )
    {
        this.handlers.add( handler );
        sortHandlers();
    }

    public void removeHandler( final WebHandler handler )
    {
        this.handlers.remove( handler );
        sortHandlers();
    }

    private void sortHandlers()
    {
        Collections.sort( this.handlers, this::compare );
    }

    private int compare( final WebHandler o1, final WebHandler o2 )
    {
        if ( o1.getOrder() > o2.getOrder() )
        {
            return 1;
        }

        if ( o1.getOrder() < o2.getOrder() )
        {
            return -1;
        }

        return 0;
    }
}
