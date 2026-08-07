package com.enonic.xp.elasticsearch.impl;

import java.util.Collections;
import java.util.Map;

import org.elasticsearch.Version;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.common.inject.Injector;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.logging.slf4j.Slf4jESLoggerFactory;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.internal.InternalSettingsPreparer;
import org.elasticsearch.plugin.analysis.icu.AnalysisICUPlugin;
import org.elasticsearch.transport.TransportService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.core.internal.Condition;

@Component(immediate = true, configurationPid = {"com.enonic.xp.elasticsearch", "com.enonic.xp.storage.nodb"})
public final class ElasticsearchActivator
{
    private static final Logger LOG = LoggerFactory.getLogger( ElasticsearchActivator.class );

    private static final String NODB_BACKEND_VALUE = "nodb";

    private static final String NODB_CONFIG_PID = "com.enonic.xp.storage.nodb";

    private Node node;

    private ServiceRegistration<Node> nodeReg;

    private ServiceRegistration<Client> clientServiceRegistration;

    private ServiceRegistration<AdminClient> adminClientReg;

    private ServiceRegistration<ClusterAdminClient> clusterAdminClientReg;

    private ServiceRegistration<ClusterService> clusterServiceReg;

    private ServiceRegistration<TransportService> transportServiceReg;

    private final ClusterConfig clusterConfig;

    private final ConfigurationAdmin configurationAdmin;

    @Reference(target = "(" + Condition.CONDITION_ID + "=HazelcastActivatorActivated)")
    @SuppressWarnings("unused")
    private Condition condition;

    @Activate
    public ElasticsearchActivator( @Reference final ClusterConfig clusterConfig, @Reference final ConfigurationAdmin configurationAdmin )
    {
        ESLoggerFactory.setDefaultFactory( new Slf4jESLoggerFactory() );
        this.clusterConfig = clusterConfig;
        this.configurationAdmin = configurationAdmin;
    }

    @Activate
    @SuppressWarnings("WeakerAccess")
    public void activate( final BundleContext context, final Map<String, String> map )
    {
        if ( nodbBackendConfigured( map ) )
        {
            LOG.info( "storage backend is nodb (com.enonic.xp.storage.nodb backend=nodb): embedded Elasticsearch node not started" );
            return;
        }

        final Settings settings = new NodeSettingsBuilder( context, this.clusterConfig ).
            buildSettings( map );

        final Environment environment = InternalSettingsPreparer.prepareEnvironment( settings, null );
        this.node = new Node( environment, Version.CURRENT, Collections.singletonList( AnalysisICUPlugin.class ) )
        {
        };
        this.node.start();

        final Injector injector = this.node.injector();
        final ClusterService clusterService = injector.getInstance( ClusterService.class );
        final TransportService transportService = injector.getInstance( TransportService.class );

        this.nodeReg = context.registerService( Node.class, this.node, null );
        this.clusterServiceReg = context.registerService( ClusterService.class, clusterService, null );
        this.transportServiceReg = context.registerService( TransportService.class, transportService, null );
        this.clientServiceRegistration = context.registerService( Client.class, this.node.client(), null );
        this.adminClientReg = context.registerService( AdminClient.class, this.node.client().admin(), null );
        this.clusterAdminClientReg = context.registerService( ClusterAdminClient.class, this.node.client().admin().cluster(), null );
    }

    private boolean nodbBackendConfigured( final Map<String, String> map )
    {
        if ( NODB_BACKEND_VALUE.equals( map.get( "backend" ) ) )
        {
            return true;
        }
        try
        {
            final Configuration[] configurations =
                this.configurationAdmin.listConfigurations( "(service.pid=" + NODB_CONFIG_PID + ")" );
            if ( configurations != null )
            {
                for ( final Configuration configuration : configurations )
                {
                    final java.util.Dictionary<String, Object> properties = configuration.getProperties();
                    if ( properties != null && NODB_BACKEND_VALUE.equals( properties.get( "backend" ) ) )
                    {
                        return true;
                    }
                }
            }
        }
        catch ( final Exception e )
        {
            LOG.warn( "Could not inspect [{}] configuration; assuming embedded Elasticsearch is wanted", NODB_CONFIG_PID, e );
        }
        return false;
    }

    @Deactivate
    @SuppressWarnings("WeakerAccess")
    public void deactivate()
    {
        if ( this.node == null )
        {
            return;
        }
        this.nodeReg.unregister();
        this.transportServiceReg.unregister();
        this.clusterServiceReg.unregister();
        this.adminClientReg.unregister();
        this.clusterAdminClientReg.unregister();
        this.clientServiceRegistration.unregister();
        this.node.close();
    }
}

