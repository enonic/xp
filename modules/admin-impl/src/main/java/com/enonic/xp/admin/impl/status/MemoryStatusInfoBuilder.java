package com.enonic.xp.admin.impl.status;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

import com.fasterxml.jackson.databind.node.ObjectNode;

public final class MemoryStatusInfoBuilder
    extends StatusInfoBuilder
{
    public MemoryStatusInfoBuilder()
    {
        super( "memory" );
    }

    @Override
    public void build( final ObjectNode json )
    {
        final MemoryMXBean bean = ManagementFactory.getMemoryMXBean();
        build( json.putObject( "heap" ), bean.getHeapMemoryUsage() );
        build( json.putObject( "nonHeap" ), bean.getNonHeapMemoryUsage() );
    }

    private void build( final ObjectNode json, final MemoryUsage mem )
    {
        json.put( "init", mem.getInit() );
        json.put( "max", mem.getMax() );
        json.put( "committed", mem.getCommitted() );
        json.put( "used", mem.getUsed() );
    }
}
