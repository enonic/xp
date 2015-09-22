package com.enonic.xp.elasticsearch.impl;

import java.util.Hashtable;
import java.util.Map;

import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.common.inject.Injector;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.logging.slf4j.Slf4jESLoggerFactory;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.node.internal.InternalNode;
import org.elasticsearch.transport.TransportService;
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

    private ServiceRegistration<ClusterService> clusterServiceReg;

    private ServiceRegistration<TransportService> transportServiceReg;

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

        final Injector injector = ( (InternalNode) this.node ).injector();
        final ClusterService clusterService = injector.getInstance( ClusterService.class );
        final TransportService transportService = injector.getInstance( TransportService.class );

        this.clientReg = context.registerService( Client.class, this.node.client(), new Hashtable<>() );
        this.clusterServiceReg = context.registerService( ClusterService.class, clusterService, new Hashtable<>() );
        this.transportServiceReg = context.registerService( TransportService.class, transportService, new Hashtable<>() );


    }

    @Deactivate
    public void deactivate()
    {
        this.transportServiceReg.unregister();
        this.clusterServiceReg.unregister();
        this.clientReg.unregister();
        this.node.stop();
    }
}
