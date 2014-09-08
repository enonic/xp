package com.enonic.wem.core.elasticsearch;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.logging.slf4j.Slf4jESLoggerFactory;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

import com.google.inject.Provider;

import com.enonic.wem.core.elasticsearch.resource.NodeSettingsBuilder;

@Singleton
public final class ElasticNodeProvider
    implements Provider<Node>
{
    private final Node node;

    @Inject
    public ElasticNodeProvider( final NodeSettingsBuilder nodeSettingsBuilder )
    {
        ESLoggerFactory.setDefaultFactory( new Slf4jESLoggerFactory() );
        final Settings settings = nodeSettingsBuilder.buildNodeSettings();
        this.node = NodeBuilder.nodeBuilder().settings( settings ).build();
    }

    @Override
    public Node get()
    {
        return this.node;
    }

    @PostConstruct
    public void start()
        throws Exception
    {
        this.node.start();
    }

    @PreDestroy
    public void stop()
        throws Exception
    {
        this.node.close();
    }
}
