package com.enonic.xp.portal.impl;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.portal.PortalRequest;

final class PortalHandlerRegistry
{
    private final List<PortalHandler2> list;

    public PortalHandlerRegistry()
    {
        this.list = Lists.newCopyOnWriteArrayList();
    }

    private void sortList()
    {
        Collections.sort( this.list, this::compare );
    }

    public synchronized void add( final PortalHandler2 handler )
    {
        this.list.add( handler );
        sortList();
    }

    public synchronized void remove( final PortalHandler2 handler )
    {
        this.list.remove( handler );
        sortList();
    }

    private int compare( final PortalHandler2 o1, final PortalHandler2 o2 )
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

    public PortalHandler2 find( final PortalRequest req )
    {
        for ( final PortalHandler2 handler : this.list )
        {
            if ( handler.canHandle( req ) )
            {
                return handler;
            }
        }

        throw PortalException.notFound( "Handler not found" );
    }
}
