package com.enonic.xp.elasticsearch.impl;

import java.util.Map;

import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.cluster.ClusterInfoService;
import org.elasticsearch.node.Node;
import org.elasticsearch.transport.TransportService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.elasticsearch.client.impl.EsClient;

@Component(immediate = true, configurationPid = "com.enonic.xp.elasticsearch")
public final class ElasticsearchActivator
{
    private Node node;

    private EsClient client;

    private ServiceRegistration<Node> nodeReg;

    private ServiceRegistration<EsClient> clientReg;

    private ServiceRegistration<AdminClient> adminClientReg;

    private ServiceRegistration<ClusterAdminClient> clusterAdminClientReg;

    private ServiceRegistration<ClusterInfoService> clusterServiceReg;

    private ServiceRegistration<TransportService> transportServiceReg;

    private ClusterConfig clusterConfig;

    @SuppressWarnings("WeakerAccess")
    public ElasticsearchActivator()
    {
    }

    @Activate
    @SuppressWarnings("WeakerAccess")
    public void activate( final BundleContext context, final Map<String, String> map )
    {
        client = new EsClient( "localhost", 9200 );
        clientReg = context.registerService( EsClient.class, client, null );
    /*final Settings settings = new NodeSettingsBuilder( context, this.clusterConfig ).
            buildSettings( map );

        this.node = NodeBuilder.nodeBuilder().settings( settings ).build();
        this.node.start();

        final Injector injector = this.node.injector();
        final ClusterInfoService clusterService = injector.getInstance( ClusterInfoService.class );
        final TransportService transportService = injector.getInstance( TransportService.class );

        this.nodeReg = context.registerService( Node.class, this.node, new Hashtable<>() );
        this.clusterServiceReg = context.registerService( ClusterInfoService.class, clusterService, new Hashtable<>() );
        this.transportServiceReg = context.registerService( TransportService.class, transportService, new Hashtable<>() );
        this.adminClientReg = context.registerService( AdminClient.class, this.node.client().admin(), new Hashtable<>() );
        this.clusterAdminClientReg =
            context.registerService( ClusterAdminClient.class, this.node.client().admin().cluster(), new Hashtable<>() );*/
    }

    @Deactivate
    @SuppressWarnings("WeakerAccess")
    public void deactivate()
    {
        clientReg.unregister();
       /* this.nodeReg.unregister();
        this.transportServiceReg.unregister();
        this.clusterServiceReg.unregister();
        this.adminClientReg.unregister();
        this.clusterAdminClientReg.unregister();*/
    }

    @Reference
    @SuppressWarnings("WeakerAccess")
    public void setClusterConfig( final ClusterConfig clusterConfig )
    {
        this.clusterConfig = clusterConfig;
    }
}

