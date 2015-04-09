package com.enonic.xp.web.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.annotations.Beta;

@Beta
public abstract class OncePerRequestHandler
    extends BaseWebHandler
{
    private final String flag;

    public OncePerRequestHandler()
    {
        this.flag = getClass().getName() + ".handled";
    }

    @Override
    public void handle( final HttpServletRequest req, final HttpServletResponse res, final WebHandlerChain chain )
        throws Exception
    {
        if ( isAlreadyHandled( req ) )
        {
            chain.handle( req, res );
            return;
        }

        setAlreadyHandledFlag( req );
        super.handle( req, res, chain );
    }

    private boolean isAlreadyHandled( final HttpServletRequest req )
    {
        return req.getAttribute( this.flag ) == Boolean.TRUE;
    }

    private void setAlreadyHandledFlag( final HttpServletRequest req )
    {
        req.setAttribute( this.flag, Boolean.TRUE );
    }
}
