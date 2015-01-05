package com.enonic.wem.servlet.internal.dispatch;

import java.io.IOException;
import java.util.Set;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.google.common.collect.Sets;

import com.enonic.xp.web.WebContext;
import com.enonic.xp.web.WebHandler;
import com.enonic.xp.web.servlet.ServletRequestHolder;

@Component(immediate = true, service = Servlet.class, property = "alias=/*")
public final class DispatcherServlet
    extends HttpServlet
{
    private final Set<WebHandler> handlers;

    public DispatcherServlet()
    {
        this.handlers = Sets.newTreeSet( new WebHandlerComparator() );
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
        synchronized ( this.handlers )
        {
            this.handlers.add( handler );
        }
    }

    public void removeHandler( final WebHandler handler )
    {
        synchronized ( this.handlers )
        {
            this.handlers.remove( handler );
        }
    }
}
