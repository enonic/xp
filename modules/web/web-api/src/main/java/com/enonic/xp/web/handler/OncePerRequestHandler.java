package com.enonic.xp.web.handler;


import com.google.common.annotations.Beta;

@Beta
public abstract class OncePerRequestHandler
    extends BaseWebHandler
{
    private final String flag = getClass().getName() + ".handled";

    @Override
    public void handle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
    {
        if ( isAlreadyHandled( webRequest ) )
        {
            webHandlerChain.handle( webRequest, webResponse );
            return;
        }

        setAlreadyHandledFlag( webRequest );
        super.handle( webRequest, webResponse, webHandlerChain );
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
