package com.enonic.xp.elasticsearch.impl;

import java.io.IOException;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.Client;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.cluster.Cluster;
import com.enonic.xp.cluster.ClusterHealth;
import com.enonic.xp.cluster.ClusterHealthStatus;
import com.enonic.xp.cluster.ClusterId;
import com.enonic.xp.cluster.ClusterNode;
import com.enonic.xp.cluster.ClusterNodes;
import com.enonic.xp.elasticsearch.client.impl.EsClient;

@Component(immediate = true)
public final class ElasticsearchCluster
    implements Cluster
{
    private static final Logger LOG = LoggerFactory.getLogger( ElasticsearchCluster.class );

    private static final String CLUSTER_HEALTH_TIMEOUT = "5s";

    private EsClient client;

    private final ClusterId id = ClusterId.from( "elasticsearch" );

    private final BundleContext bundleContext;

    private volatile ServiceRegistration<Client> clientServiceRegistration;

    @Activate
    public ElasticsearchCluster( final BundleContext bundleContext, @Reference final EsClient client )
    {
        this.bundleContext = bundleContext;
        this.client = client;
    }

    @Deactivate
    @SuppressWarnings("unused")
    public void deactivate()
    {
        unregisterClient();
    }

    @Override
    public ClusterId getId()
    {
        return id;
    }

    @Override
    public boolean isEnabled()
    {
        return this.clientServiceRegistration != null;
    }

    @Override
    public ClusterHealth getHealth()
    {
        try
        {
            final ClusterHealthResponse healthResponse = doGetHealth();
            if ( healthResponse.getStatus() != org.elasticsearch.cluster.health.ClusterHealthStatus.RED )
            {
                if ( !checkAllIndicesOpened() )
                {
                    return ClusterHealth.create().
                        status( ClusterHealthStatus.RED ).
                        errorMessage( "Closed indices" ).
                        build();
                }
            }
            return toClusterHealth( healthResponse );
        }
        catch ( Exception e )
        {
            return ClusterHealth.create().
                status( ClusterHealthStatus.RED ).
                errorMessage( e.getClass().getSimpleName() + "[" + e.getMessage() + "]" ).
                build();
        }
    }

    private boolean checkAllIndicesOpened()
    {
        return true;
    }

    @Override
    public ClusterNodes getNodes()
    {
        try
        {
            return ClusterNodes.create().add( ClusterNode.from( "local" ) ).build();
        }
        catch ( Exception e )
        {
            return ClusterNodes.create().build();
        }
    }

    @Override
    public void enable()
    {
        registerClient();
    }

    @Override
    public void disable()
    {
        unregisterClient();
    }

    private synchronized void registerClient()
    {
        if ( this.clientServiceRegistration != null )
        {
            return;
        }

        // LOG.info( "Cluster operational, register elasticsearch-client" );
        //this.clientServiceRegistration = this.bundleContext.registerService( Client.class, this.node.client(), null );
    }

    private synchronized void unregisterClient()
    {
        if ( this.clientServiceRegistration == null )
        {
            return;
        }

        try
        {
            LOG.info( "Cluster not operational, unregister elasticsearch-client" );
            this.clientServiceRegistration.unregister();
        }
        finally
        {
            this.clientServiceRegistration = null;
        }
    }

    private ClusterHealthResponse doGetHealth()
        throws IOException
    {
        return client.clusterHealth( new ClusterHealthRequest().
            timeout( CLUSTER_HEALTH_TIMEOUT ).
            waitForYellowStatus() );
    }

    private ClusterHealth toClusterHealth( final ClusterHealthResponse healthResponse )
    {
        if ( healthResponse.getStatus() == org.elasticsearch.cluster.health.ClusterHealthStatus.RED )
        {
            return ClusterHealth.create().
                status( ClusterHealthStatus.RED ).
                errorMessage( healthResponse.toString() ).
                build();
        }

        if ( healthResponse.getStatus() == org.elasticsearch.cluster.health.ClusterHealthStatus.YELLOW )
        {
            return ClusterHealth.yellow();
        }

        return ClusterHealth.green();
    }
}