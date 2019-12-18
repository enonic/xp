package com.enonic.xp.elasticsearch.impl;

import java.io.IOException;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
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

@Component(immediate = true)
public final class ElasticsearchCluster
    implements Cluster
{
    private final ClusterId id = ClusterId.from( "elasticsearch" );

    private static final String CLUSTER_HEALTH_TIMEOUT = "5s";

    private RestHighLevelClient client;

    private BundleContext context;

    protected ServiceRegistration<Client> reg;

    private final static Logger LOG = LoggerFactory.getLogger( ElasticsearchCluster.class );

    @Activate
    @SuppressWarnings("WeakerAccess")
    public void activate( final BundleContext context )
    {
        this.context = context;
        this.reg = null;
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
        return this.reg != null;
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

    private void registerClient()
    {
        if ( this.reg != null )
        {
            return;
        }

        // LOG.info( "Cluster operational, register elasticsearch-client" );
        //this.reg = this.context.registerService( Client.class, this.node.client(), new Hashtable<>() );
    }

    private void unregisterClient()
    {
        if ( this.reg == null )
        {
            return;
        }

        try
        {
            LOG.info( "Cluster not operational, unregister elasticsearch-client" );
            this.reg.unregister();
        }
        finally
        {
            this.reg = null;
        }
    }

    private ClusterHealthResponse doGetHealth()
        throws IOException
    {
        return client.cluster().health( new ClusterHealthRequest().
            timeout( CLUSTER_HEALTH_TIMEOUT ).
            waitForYellowStatus(), RequestOptions.DEFAULT );
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


    @Reference
    public void setClient( final RestHighLevelClient client )
    {
        this.client = client;
    }
}
