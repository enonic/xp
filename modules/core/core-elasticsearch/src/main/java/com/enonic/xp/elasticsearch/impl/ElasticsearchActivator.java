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
import org.elasticsearch.transport.TransportRequestHandler;
import org.elasticsearch.transport.TransportService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.google.common.collect.Maps;

@Component(immediate = true, configurationPid = "com.enonic.xp.elasticsearch")
public final class ElasticsearchActivator
{
    private static final String ACTION_PROPERTY_KEY = "action";

    private BundleContext context;

    private Node node;

    private ServiceRegistration<Client> clientReg;

    private ServiceRegistration<ClusterService> clusterServiceReg;

    private ServiceRegistration<TransportService> transportServiceReg;

    private TransportService transportService;

    private Map<String, TransportRequestHandler> transportRequestHandlerMap = Maps.newConcurrentMap();


    public ElasticsearchActivator()
    {
        ESLoggerFactory.setDefaultFactory( new Slf4jESLoggerFactory() );
    }

    @Activate
    public void activate( final BundleContext context, final Map<String, String> map )
    {
        this.context = context;

        final Settings settings = new NodeSettingsBuilder( context ).
            buildSettings( map );

        this.node = NodeBuilder.nodeBuilder().settings( settings ).build();
        this.node.start();

        final Injector injector = ( (InternalNode) this.node ).injector();
        final ClusterService clusterService = injector.getInstance( ClusterService.class );
        this.transportService = injector.getInstance( TransportService.class );

        this.clientReg = context.registerService( Client.class, this.node.client(), new Hashtable<>() );
        this.clusterServiceReg = context.registerService( ClusterService.class, clusterService, new Hashtable<>() );
        this.transportServiceReg = context.registerService( TransportService.class, this.transportService, new Hashtable<>() );
    }

    @Modified
    public void modified( final Map<String, String> map )
    {
        deactivate();
        activate( context, map );

        for ( Map.Entry<String, TransportRequestHandler> transportRequestHandlerEntry : transportRequestHandlerMap.entrySet() )
        {
            addTransportRequestHandler( transportRequestHandlerEntry.getValue(), transportRequestHandlerEntry.getKey() );
        }
    }

    @Deactivate
    public void deactivate()
    {
        this.transportServiceReg.unregister();
        this.clusterServiceReg.unregister();
        this.clientReg.unregister();
        this.node.stop();
    }

    @Reference(target = "(" + ACTION_PROPERTY_KEY + "=*)", cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addTransportRequestHandler( final TransportRequestHandler transportRequestHandler, final Map<String, String> map )
    {
        final String actionPropertyValue = map.get( ACTION_PROPERTY_KEY );
        addTransportRequestHandler( transportRequestHandler, actionPropertyValue );
    }

    private void addTransportRequestHandler( final TransportRequestHandler transportRequestHandler, final String action )
    {
        transportRequestHandlerMap.put( action, transportRequestHandler );
        this.transportService.registerHandler( action, transportRequestHandler );
    }

    public void removeTransportRequestHandler( final TransportRequestHandler transportRequestHandler, final Map<String, String> map )
    {
        final String actionPropertyValue = map.get( ACTION_PROPERTY_KEY );
        transportRequestHandlerMap.remove( actionPropertyValue );
        this.transportService.removeHandler( actionPropertyValue );
    }

}
