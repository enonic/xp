package com.enonic.xp.web.impl.handler;

import java.util.Collection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;

import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

final class WebHandlerChainImpl
    implements WebHandlerChain
{
    private final UnmodifiableIterator<WebHandler> webHandlerIterator;

    WebHandlerChainImpl( final Collection<WebHandler> handlers )
    {
        this.webHandlerIterator = ImmutableList.copyOf( handlers ).iterator();
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
