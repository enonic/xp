package com.enonic.xp.web.impl;

import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import com.enonic.xp.web.handler.WebHandler;

final class WebHandlerRegistry
{
    private final Set<WebHandler> handlers;

    private ImmutableList<WebHandler> list;

    public WebHandlerRegistry()
    {
        this.handlers = Sets.newTreeSet( this::compare );
        updateList();
    }

    public ImmutableList<WebHandler> getList()
    {
        return this.list;
    }

    private void updateList()
    {
        this.list = ImmutableList.copyOf( this.handlers );
    }

    public synchronized void add( final WebHandler handler )
    {
        this.handlers.add( handler );
        updateList();
    }

    public synchronized void remove( final WebHandler handler )
    {
        this.handlers.remove( handler );
        updateList();
    }

    private int compare( final WebHandler o1, final WebHandler o2 )
    {
        if ( o1.getOrder() > o2.getOrder() )
        {
            return 1;
        }

        if ( o1.getOrder() < o2.getOrder() )
        {
            return -1;
        }

        return 0;
    }
}
