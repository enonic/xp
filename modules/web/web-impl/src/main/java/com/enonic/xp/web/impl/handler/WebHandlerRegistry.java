package com.enonic.xp.web.impl.handler;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.web.handler.WebHandler;

final class WebHandlerRegistry
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
}
