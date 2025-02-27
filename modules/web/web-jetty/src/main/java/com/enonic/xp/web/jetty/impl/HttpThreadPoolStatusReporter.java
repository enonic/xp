package com.enonic.xp.web.jetty.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.net.MediaType;

import com.enonic.xp.status.StatusReporter;

@Component(immediate = true, service = StatusReporter.class)
public class HttpThreadPoolStatusReporter
    implements StatusReporter
{
    private final ThreadPool threadPool;

    @Activate
    public HttpThreadPoolStatusReporter( @Reference final Server server )
    {
        this.threadPool = server.getThreadPool();
    }

    @Override
    public final MediaType getMediaType()
    {
        return MediaType.JSON_UTF_8;
    }

    @Override
    public final void report( final OutputStream outputStream )
        throws IOException
    {
        outputStream.write( getReport().toString().getBytes( StandardCharsets.UTF_8 ) );
    }

    @Override
    public String getName()
    {
        return "http.threadpool";
    }

    private JsonNode getReport()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put( "threads", this.threadPool.getThreads() );
        json.put( "idleThreads", this.threadPool.getIdleThreads() );
        json.put( "isLowOnThreads", this.threadPool.isLowOnThreads() );
        return json;
    }
}
