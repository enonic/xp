package com.enonic.wem.core.elasticsearch;

import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.logging.slf4j.Slf4jESLoggerFactory;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

import com.enonic.wem.core.elasticsearch.resource.NodeSettingsBuilder;

public final class ElasticNodeProvider
{
    private final Node node;

    public ElasticNodeProvider( final NodeSettingsBuilder nodeSettingsBuilder )
    {
        ESLoggerFactory.setDefaultFactory( new Slf4jESLoggerFactory() );
        final Settings settings = nodeSettingsBuilder.buildNodeSettings();
        this.node = NodeBuilder.nodeBuilder().settings( settings ).build();
    }

    public Node get()
    {
        return this.node;
    }

    public void start()
        throws Exception
    {
        this.node.start();
    }

    public void stop()
        throws Exception
    {
        this.node.close();
    }
}
