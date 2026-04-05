package com.enonic.xp.web.impl.handler;

import java.util.List;

import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

final class WebHandlerChainImpl
    implements WebHandlerChain
{
    private final List<WebHandler> handlers;

    private int index;

    WebHandlerChainImpl( final List<WebHandler> handlers )
    {
        this.handlers = handlers;
    }

    @Override
    public WebResponse handle( final WebRequest webRequest, final WebResponse webResponse )
        throws Exception
    {
        if ( index < handlers.size() )
        {
            return handlers.get( index++ ).handle( webRequest, webResponse, this );
        }
        throw WebException.notFound( "Handler not found" );
    }
}
