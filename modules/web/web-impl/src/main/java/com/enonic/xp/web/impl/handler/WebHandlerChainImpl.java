package com.enonic.xp.web.impl.handler;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;
import com.enonic.xp.web.handler.WebRequest;
import com.enonic.xp.web.handler.WebResponse;

final class WebHandlerChainImpl
    implements WebHandlerChain
{
    private final ImmutableList<WebHandler> webHandlerList;

    public WebHandlerChainImpl( final Collection<WebHandler> handlers )
    {
        this.webHandlerList = ImmutableList.copyOf( handlers );
    }

    @Override
    public void handle( final WebRequest webRequest, final WebResponse webResponse )
    {
        webHandlerList.forEach( webHandler -> {
            webHandler.handle( webRequest, webResponse, this );
        } );
    }
}
