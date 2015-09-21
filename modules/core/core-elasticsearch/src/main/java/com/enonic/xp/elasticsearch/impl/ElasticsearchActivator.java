package com.enonic.xp.elasticsearch.impl;

import java.util.Hashtable;
import java.util.Map;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.logging.slf4j.Slf4jESLoggerFactory;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

@Component(immediate = true, configurationPid = "com.enonic.xp.elasticsearch")
public final class ElasticsearchActivator
{
    private Node node;

    private ServiceRegistration<Client> clientReg;

    public ElasticsearchActivator()
    {
        ESLoggerFactory.setDefaultFactory( new Slf4jESLoggerFactory() );
    }

    @Activate
    public void activate( final BundleContext context, final Map<String, String> map )
    {
        final Settings settings = new NodeSettingsBuilder( context ).
            buildSettings( map );

        this.node = NodeBuilder.nodeBuilder().settings( settings ).build();
        this.node.start();

        this.clientReg = context.registerService( Client.class, this.node.client(), new Hashtable<>() );
    }

    @Deactivate
    public void deactivate()
    {
        this.clientReg.unregister();
        this.node.stop();
    }
}
