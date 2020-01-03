package com.enonic.xp.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public abstract class BaseWebFilter
    implements Filter
{
    @Override
    public void init( final FilterConfig config )
        throws ServletException
    {
        // Do nothing
    }

    @Override
    public final void doFilter( final ServletRequest req, final ServletResponse res, final FilterChain chain )
        throws IOException, ServletException
    {
        try
        {
            doFilter( (HttpServletRequest) req, (HttpServletResponse) res, chain );
        }
        catch ( final Exception e )
        {
            if ( e instanceof ServletException )
            {
                throw (ServletException) e;
            }

            if ( e instanceof IOException )
            {
                throw (IOException) e;
            }

            throw new ServletException( e );
        }
    }

    protected abstract void doFilter( HttpServletRequest req, HttpServletResponse res, FilterChain chain )
        throws Exception;

    @Override
    public void destroy()
    {
        // Do nothing
    }
}
