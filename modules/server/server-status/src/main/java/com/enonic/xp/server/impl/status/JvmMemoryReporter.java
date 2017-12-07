package com.enonic.xp.server.impl.status;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.status.JsonStatusReporter;
import com.enonic.xp.status.StatusReporter;

@Component(immediate = true, service = StatusReporter.class)
public final class JvmMemoryReporter
    extends JsonStatusReporter
{
    @Override
    public String getName()
    {
        return "jvm.memory";
    }

    @Override
    public JsonNode getReport()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();

        final MemoryMXBean bean = ManagementFactory.getMemoryMXBean();
        json.set( "heap", buildMemoryUsageInfo( bean.getHeapMemoryUsage() ) );
        json.set( "nonHeap", buildMemoryUsageInfo( bean.getNonHeapMemoryUsage() ) );
        json.set( "pools", buildMemoryPools() );

        return json;
    }

    private ArrayNode buildMemoryPools()
    {
        final ArrayNode jsonNode = JsonNodeFactory.instance.arrayNode();

        final List<MemoryPoolMXBean> memoryPools = ManagementFactory.getMemoryPoolMXBeans();

        for ( final MemoryPoolMXBean mp : memoryPools )
        {
            jsonNode.add( buildPoolInfo( mp ) );
        }

        return jsonNode;
    }

    private ObjectNode buildPoolInfo( final MemoryPoolMXBean pool )
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();

        json.put( "name", pool.getName() );
        json.put( "type", pool.getType().toString() );

        if ( pool.getUsage() != null )
        {
            json.set( "usage", buildMemoryUsageInfo( pool.getUsage() ) );
        }

        return json;
    }

    private ObjectNode buildMemoryUsageInfo( final MemoryUsage mem )
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put( "init", mem.getInit() );
        json.put( "max", mem.getMax() );
        json.put( "committed", mem.getCommitted() );
        json.put( "used", mem.getUsed() );
        return json;
    }
}
