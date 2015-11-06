package com.enonic.xp.elasticsearch.impl;

import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true)
public final class ClientActivator
{
    private final static long CHECK_INTERVAL_MS = 1000L;

    private final String CLUSTER_HEALTH_TIMEOUT = "5s";

    private Node node;

    private BundleContext context;

    private final Timer timer;

    protected ServiceRegistration<Client> reg;

    private final static Logger LOG = LoggerFactory.getLogger( ClientActivator.class );

    public ClientActivator()
    {
        this.timer = new Timer();
    }

    @Activate
    public void activate( final BundleContext context )
    {
        this.context = context;
        this.reg = null;

        registerClientIfNotRed();
        this.timer.schedule( new TimerTask()
        {
            @Override
            public void run()
            {
                registerClientIfNotRed();
            }
        }, CHECK_INTERVAL_MS, CHECK_INTERVAL_MS );
    }

    @Deactivate
    public void deactivate()
    {
        this.timer.cancel();
        unregisterClient();
    }

    protected void registerClientIfNotRed()
    {
        if ( isRedState() )
        {
            unregisterClient();
        }
        else
        {
            registerClient();
        }
    }


    private void registerClient()
    {
        if ( this.reg != null )
        {
            return;
        }

        LOG.error( "Cluster operational, register elasticsearch-client" );
        this.reg = this.context.registerService( Client.class, this.node.client(), new Hashtable<>() );
    }

    private void unregisterClient()
    {
        if ( this.reg == null )
        {
            return;
        }

        try
        {
            LOG.error( "Cluster not operational , unregister elasticsearch-client" );
            this.reg.unregister();
        }
        finally
        {
            this.reg = null;
        }
    }

    private boolean isRedState()
    {
        try
        {
            final ClusterHealthResponse response = this.node.client().admin().cluster().health( new ClusterHealthRequest().
                timeout( CLUSTER_HEALTH_TIMEOUT ).
                waitForYellowStatus() ).
                actionGet();

            final boolean isRed = response.getStatus() == ClusterHealthStatus.RED;

            if ( isRed )
            {
                LOG.error( "Cluster health in state 'RED' " );
            }

            return isRed;
        }
        catch ( final Exception e )
        {
            LOG.error( "Cluster health in state 'RED' ", e );
            return true;
        }
    }

    @Reference
    public void setNode( final Node node )
    {
        this.node = node;
    }
}
