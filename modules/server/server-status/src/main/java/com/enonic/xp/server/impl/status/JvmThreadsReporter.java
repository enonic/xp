package com.enonic.xp.server.impl.status;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import org.osgi.service.component.annotations.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.status.StatusReporter;

@Component(immediate = true, service = StatusReporter.class)
public final class JvmThreadsReporter
    extends JsonStatusReporter
{
    @Override
    public String getName()
    {
        return "jvm.threads";
    }

    @Override
    public JsonNode getReport()
    {
        final ThreadMXBean threads = ManagementFactory.getThreadMXBean();
        final ObjectNode json = JsonNodeFactory.instance.objectNode();

        json.put( "count", threads.getThreadCount() );
        json.put( "peak", threads.getPeakThreadCount() );
        json.put( "daemonCount", threads.getDaemonThreadCount() );
        json.put( "totalStarted", threads.getTotalStartedThreadCount() );

        final long[] deadLocks = threads.findDeadlockedThreads();
        json.put( "numDeadLocks", deadLocks != null ? deadLocks.length : 0 );

        return json;
    }
}
