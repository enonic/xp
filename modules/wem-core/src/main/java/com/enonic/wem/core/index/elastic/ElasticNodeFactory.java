package com.enonic.wem.core.index.elastic;

import javax.inject.Inject;

import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.logging.slf4j.Slf4jESLoggerFactory;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.springframework.stereotype.Component;

import com.enonic.wem.core.lifecycle.DisposableBean;
import com.enonic.wem.core.lifecycle.InitializingBean;
import com.enonic.wem.core.lifecycle.ProviderFactory;

@Component
public final class ElasticNodeFactory
    extends ProviderFactory<Node>
    implements InitializingBean, DisposableBean
{
    private Node node;

    private NodeSettingsBuilder nodeSettingsBuilder;

    public ElasticNodeFactory()
    {
        super(Node.class);
        ESLoggerFactory.setDefaultFactory( new Slf4jESLoggerFactory() );
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
