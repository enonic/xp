package com.enonic.xp.web.impl.handler;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.servlet.ServletRequestHolder;

final class WebDispatcher
{
    private final List<WebHandler> webHandlerList = Lists.newCopyOnWriteArrayList();

    public synchronized void add( final WebHandler webHandler )
    {
        webHandlerList.add( webHandler );
        sortWebHandlerList();
    }

    public synchronized void remove( final WebHandler webHandler )
    {
        webHandlerList.remove( webHandler );
        sortWebHandlerList();
    }

    private void sortWebHandlerList()
    {
        Collections.sort( webHandlerList, this::compare );
    }

    private int compare( final WebHandler webHandler1, final WebHandler webHandler2 )
    {
        return webHandler1.getOrder() - webHandler2.getOrder();
    }

    public WebResponse dispatch( final WebRequest webRequest, final WebResponse webResponse )
        throws Exception
    {
        ServletRequestHolder.setRequest( webRequest.getRawRequest() );
        return new WebHandlerChainImpl( webHandlerList ).
            handle( webRequest, webResponse );
    }
}