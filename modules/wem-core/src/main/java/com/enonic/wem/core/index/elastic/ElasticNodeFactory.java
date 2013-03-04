package com.enonic.wem.core.index.elastic;

import javax.inject.Inject;

import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.logging.slf4j.Slf4jESLoggerFactory;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

import com.enonic.wem.core.lifecycle.DisposableBean;
import com.enonic.wem.core.lifecycle.InitializingBean;

@Component
public final class ElasticNodeFactory
    implements FactoryBean<Node>, InitializingBean, DisposableBean
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

    @Override
    public void destroy()
        throws Exception
    {
        this.node.close();
    }

    @Override
    public void afterPropertiesSet()
        throws Exception
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
}
