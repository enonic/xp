package com.enonic.wem.core.search.elastic;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.logging.slf4j.Slf4jESLoggerFactory;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.springframework.beans.factory.FactoryBean;
import javax.inject.Inject;
import org.springframework.stereotype.Component;

@Component
public final class ElasticNodeFactory
    implements FactoryBean<Node>
{
    private Node node;

    private NodeSettingsBuilder nodeSettingsBuilder;

    public ElasticNodeFactory()
    {
        ESLoggerFactory.setDefaultFactory( new Slf4jESLoggerFactory() );
    }

    public Node getObject()
    {
        return this.node;
    }

    public Class<?> getObjectType()
    {
        return Node.class;
    }

    public boolean isSingleton()
    {
        return true;
    }

    @PostConstruct
    public void start()
    {
        final Settings settings = nodeSettingsBuilder.buildNodeSettings();
        this.node = NodeBuilder.nodeBuilder().settings( settings ).build();
        this.node.start();
    }

    @Inject
    public void setNodeSettingsBuilder( final NodeSettingsBuilder nodeSettingsBuilder )
    {
        this.nodeSettingsBuilder = nodeSettingsBuilder;
    }

    @PreDestroy
    public void stop()
    {
        this.node.close();
    }
}
