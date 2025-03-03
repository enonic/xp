package com.enonic.xp.web.impl.handler;

import java.util.Iterator;
import java.util.List;

import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

final class WebHandlerChainImpl
    implements WebHandlerChain
{
    private final Iterator<WebHandler> webHandlerIterator;

    WebHandlerChainImpl( final List<WebHandler> handlers )
    {
        this.webHandlerIterator = handlers.iterator();
    }

    @Override
    public WebResponse handle( final WebRequest webRequest, final WebResponse webResponse )
        throws Exception
    {
        if ( webHandlerIterator.hasNext() )
        {
            return webHandlerIterator.next().handle( webRequest, webResponse, this );
        }
        throw WebException.notFound( "Handler not found" );
    }
}
