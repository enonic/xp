package com.enonic.xp.portal.impl;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.portal.PortalException;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.handler.PortalHandler;

final class PortalHandlerRegistry
{
    private final List<PortalHandler> list;

    public PortalHandlerRegistry()
    {
        this.list = Lists.newCopyOnWriteArrayList();
    }

    private void sortList()
    {
        Collections.sort( this.list, this::compare );
    }

    public synchronized void add( final PortalHandler handler )
    {
        this.list.add( handler );
        sortList();
    }

    public synchronized void remove( final PortalHandler handler )
    {
        this.list.remove( handler );
        sortList();
    }

    private int compare( final PortalHandler o1, final PortalHandler o2 )
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

    public PortalHandler find( final PortalRequest req )
    {
        for ( final PortalHandler handler : this.list )
        {
            if ( handler.canHandle( req ) )
            {
                return handler;
            }
        }

        throw PortalException.notFound( "Handler not found" );
    }
}
