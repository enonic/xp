package com.enonic.xp.server.impl.status;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

import org.osgi.service.component.annotations.Component;

import com.fasterxml.jackson.databind.JsonNode;
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
