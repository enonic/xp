package com.enonic.wem.core.index.elastic;

import javax.inject.Inject;

import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.logging.slf4j.Slf4jESLoggerFactory;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

import com.google.inject.Provider;

import com.enonic.wem.core.lifecycle.DisposableBean;

public final class ElasticNodeProvider
    implements Provider<Node>, DisposableBean
{
    private final Node node;

    @Inject
    public ElasticNodeProvider( final NodeSettingsBuilder nodeSettingsBuilder )
    {
        ESLoggerFactory.setDefaultFactory( new Slf4jESLoggerFactory() );
        final Settings settings = nodeSettingsBuilder.buildNodeSettings();
        this.node = NodeBuilder.nodeBuilder().settings( settings ).build();

        // TODO: This should be in start() lifecycle method
        this.node.start();
    }

    @Override
    public Node get()
    {
        return this.node;
    }

    @Override
    public void destroy()
        throws Exception
    {
        this.node.close();
    }
}
