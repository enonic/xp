package com.enonic.xp.web.jetty.impl;

import org.eclipse.jetty.util.thread.ThreadPool;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.status.JsonStatusReporter;

public class HttpThreadPoolStatusReporter
    extends JsonStatusReporter
{
    private final ThreadPool threadPool;

    HttpThreadPoolStatusReporter( final ThreadPool threadPool )
    {
        this.threadPool = threadPool;
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
