package com.enonic.xp.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public abstract class OncePerRequestFilter
    extends BaseWebFilter
{
    private final String flag;

    public OncePerRequestFilter()
    {
        this.flag = getClass().getName() + ".handled";
    }

    @Override
    public final void doFilter( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain )
        throws Exception
    {
        if ( isAlreadyHandled( req ) )
        {
            chain.doFilter( req, res );
            return;
        }

        setAlreadyHandledFlag( req );
        doHandle( req, res, chain );
    }

    protected abstract void doHandle( HttpServletRequest req, HttpServletResponse res, FilterChain chain )
        throws Exception;

    private boolean isAlreadyHandled( final HttpServletRequest req )
    {
        return Boolean.TRUE.equals( req.getAttribute( this.flag ) );
    }

    private void setAlreadyHandledFlag( final HttpServletRequest req )
    {
        req.setAttribute( this.flag, Boolean.TRUE );
    }
}
