package com.enonic.wem.core.index.elastic;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.logging.slf4j.Slf4jESLoggerFactory;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

import com.google.inject.Provider;

import com.enonic.wem.core.lifecycle.LifecycleBean;
import com.enonic.wem.core.lifecycle.RunLevel;

@Singleton
public final class ElasticNodeProvider
    extends LifecycleBean
    implements Provider<Node>
{
    private final Node node;

    @Inject
    public ElasticNodeProvider( final NodeSettingsBuilder nodeSettingsBuilder )
    {
        super( RunLevel.L1 );

        ESLoggerFactory.setDefaultFactory( new Slf4jESLoggerFactory() );
        final Settings settings = nodeSettingsBuilder.buildNodeSettings();
        this.node = NodeBuilder.nodeBuilder().settings( settings ).build();
    }

    @Override
    public Node get()
    {
        return this.node;
    }

    @Override
    protected void doStart()
        throws Exception
    {
        this.node.start();
    }

    @Override
    protected void doStop()
        throws Exception
    {
        this.node.close();
    }
}
