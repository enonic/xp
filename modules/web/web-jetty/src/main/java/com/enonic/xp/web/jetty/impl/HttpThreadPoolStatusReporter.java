package com.enonic.xp.web.jetty.impl;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.status.JsonStatusReporter;

@Component(immediate = true)
public class HttpThreadPoolStatusReporter
    extends JsonStatusReporter
{
    private final ThreadPool threadPool;

    @Activate
    public HttpThreadPoolStatusReporter( @Reference final Server server )
    {
        this.threadPool = server.getThreadPool();
    }

    @Override
    public String getName()
    {
        return "http.threadpool";
    }

    @Override
    public JsonNode getReport()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put( "threads", this.threadPool.getThreads() );
        json.put( "idleThreads", this.threadPool.getIdleThreads() );
        json.put( "isLowOnThreads", this.threadPool.isLowOnThreads() );
        return json;
    }
}
