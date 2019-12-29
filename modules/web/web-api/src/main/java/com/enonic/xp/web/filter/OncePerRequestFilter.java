package com.enonic.xp.web.filter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
        return req.getAttribute( this.flag ) == Boolean.TRUE;
    }

    private void setAlreadyHandledFlag( final HttpServletRequest req )
    {
        req.setAttribute( this.flag, Boolean.TRUE );
    }
}
