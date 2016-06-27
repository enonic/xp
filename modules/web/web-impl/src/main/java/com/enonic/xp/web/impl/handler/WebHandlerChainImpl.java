package com.enonic.xp.web.impl.handler;

import java.util.Collection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;

import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

final class WebHandlerChainImpl
    implements WebHandlerChain
{
    private final UnmodifiableIterator<WebHandler> webHandlerIterator;

    public WebHandlerChainImpl( final Collection<WebHandler> handlers )
    {
        this.webHandlerIterator = ImmutableList.copyOf( handlers ).iterator();
    }

    @Override
    public WebResponse handle( final WebRequest webRequest, final WebResponse webResponse )
        throws Exception
    {
        WebResponse result = webResponse;

        if ( webHandlerIterator.hasNext() )
        {
            result = webHandlerIterator.next().handle( webRequest, result, this );
        }
        return result;
    }
}