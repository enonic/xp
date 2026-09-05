package com.enonic.xp.web.impl.dos;

import java.io.IOException;

import org.eclipse.jetty.ee11.servlets.DoSFilter;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;

import com.enonic.xp.annotation.Order;

@Component(immediate = true, service = Filter.class, configurationPid = "com.enonic.xp.web.dos", property = {"connector=xp",
    "connector=api"})
@Order(-400)
@WebFilter("/*")
public final class DosFilterWrapper
    implements Filter
{
    private final DosFilterConfig config;

    /**
     * The wrapped Jetty filter, or {@code null} when the DoS filter is disabled.
     */
    private final Filter delegate;

    private volatile boolean initialized;

    @Activate
    public DosFilterWrapper( final DosFilterConfig config )
    {
        this( config, config.enabled() ? new DoSFilter() : null );
    }

    DosFilterWrapper( final DosFilterConfig config, final Filter delegate )
    {
        this.config = config;
        this.delegate = delegate;
    }

    @Deactivate
    public void deactivate()
    {
        if ( this.initialized )
        {
            this.delegate.destroy();
        }
    }

    @Override
    public void doFilter( final ServletRequest req, final ServletResponse res, final FilterChain chain )
        throws IOException, ServletException
    {
        if ( this.delegate == null )
        {
            chain.doFilter( req, res );
            return;
        }

        initialize( req.getServletContext() );
        this.delegate.doFilter( req, res, chain );
    }

    /**
     * Initializes the delegate. DoSFilter hands the servlet context to every rate tracker it creates, so it
     * can only be initialized once a request has provided one.
     */
    private void initialize( final ServletContext context )
        throws ServletException
    {
        if ( this.initialized )
        {
            return;
        }

        synchronized ( this )
        {
            if ( this.initialized )
            {
                return;
            }

            final FilterConfigImpl filterConfig = new FilterConfigImpl( DoSFilter.class.getSimpleName(), context );
            filterConfig.populate( this.config );
            this.delegate.init( filterConfig );
            this.initialized = true;
        }
    }

    Filter getDelegate()
    {
        return this.delegate;
    }
}
