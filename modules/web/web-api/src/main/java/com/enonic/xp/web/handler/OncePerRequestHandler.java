package com.enonic.xp.web.handler;


import javax.servlet.http.HttpServletRequest;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

@PublicApi
public abstract class OncePerRequestHandler
    extends BaseWebHandler
{
    private final String flag = getClass().getName() + ".handled";

    @Override
    public WebResponse handle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
        throws Exception
    {
        if ( isAlreadyHandled( webRequest ) )
        {
            return webHandlerChain.handle( webRequest, webResponse );
        }

        setAlreadyHandledFlag( webRequest );
        return super.handle( webRequest, webResponse, webHandlerChain );
    }

    private boolean isAlreadyHandled( final WebRequest webRequest )
    {
        final HttpServletRequest rawRequest = webRequest.getRawRequest();
        return rawRequest.getAttribute( this.flag ) == Boolean.TRUE;
    }

    private void setAlreadyHandledFlag( final WebRequest webRequest )
    {
        final HttpServletRequest rawRequest = webRequest.getRawRequest();
        rawRequest.setAttribute( this.flag, Boolean.TRUE );
    }
}