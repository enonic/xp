package com.enonic.xp.server.impl.status;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.nio.charset.StandardCharsets;

import org.osgi.service.component.annotations.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.net.MediaType;

import com.enonic.xp.status.StatusReporter;

@Component(immediate = true, service = StatusReporter.class)
public final class JvmThreadsReporter
    implements StatusReporter
{
    @Override
    public String getName()
    {
        return "jvm.threads";
    }

    @Override
    public MediaType getMediaType()
    {
        return MediaType.JSON_UTF_8;
    }

    @Override
    public void report( final OutputStream outputStream )
        throws IOException
    {
        outputStream.write( getReport().toString().getBytes( StandardCharsets.UTF_8 ) );
    }

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
