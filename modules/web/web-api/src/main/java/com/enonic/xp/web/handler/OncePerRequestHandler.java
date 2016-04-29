package com.enonic.xp.web.handler;


import com.google.common.annotations.Beta;

@Beta
public abstract class OncePerRequestHandler
    extends BaseWebHandler
{
    private final String flag = getClass().getName() + ".handled";

    @Override
    public WebResponse handle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
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
        return webRequest.getAttribute( this.flag ) == Boolean.TRUE;
    }

    private void setAlreadyHandledFlag( final WebRequest webRequest )
    {
        webRequest.setAttribute( this.flag, Boolean.TRUE );
    }
}
