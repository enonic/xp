package com.enonic.xp.web.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public abstract class BaseWebFilter
    implements Filter
{
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
}
