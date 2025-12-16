package com.enonic.xp.web.impl.dos;

import java.io.IOException;

import org.eclipse.jetty.ee11.servlets.DoSFilter;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
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
    protected Filter delegate;

    private DosFilterConfig config;

    @Activate
    public void activate( final DosFilterConfig config )
    {
        this.config = config;

        if ( this.config.enabled() )
        {
            this.delegate = new DoSFilter();
        }
    }

    @Override
    public void init( final FilterConfig config )
        throws ServletException
    {
        if ( this.delegate == null )
        {
            return;
        }

        final FilterConfigImpl wrapped = new FilterConfigImpl( config );
        wrapped.populate( this.config );
        this.delegate.init( wrapped );
    }

    @Override
    public void doFilter( final ServletRequest req, final ServletResponse res, final FilterChain chain )
        throws IOException, ServletException
    {
        if ( this.delegate != null )
        {
            this.delegate.doFilter( req, res, chain );
            return;
        }

        chain.doFilter( req, res );
    }

    @Override
    public void destroy()
    {
        if ( this.delegate == null )
        {
            return;
        }

        try
        {
            this.delegate.destroy();
        }
        finally
        {
            this.delegate = null;
        }
    }
}
