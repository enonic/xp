package com.enonic.xp.cluster.impl;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import com.enonic.xp.cluster.Cluster;
import com.enonic.xp.cluster.ClusterId;
import com.enonic.xp.cluster.ClusterManager;
import com.enonic.xp.cluster.ClusterState;
import com.enonic.xp.cluster.ClusterValidator;
import com.enonic.xp.cluster.ClusterValidatorResult;
import com.enonic.xp.cluster.Clusters;

@Component(immediate = true)
public class ClusterManagerImpl
    implements ClusterManager
{
    private final Long checkIntervalMs;

    private final static List<ClusterId> DEFAULT_REQUIRED_INSTANCES =
        Lists.newArrayList( ClusterId.from( "elasticsearch" ), ClusterId.from( "ignite" ) );

    private final Clusters instances;

    private final Logger LOG = LoggerFactory.getLogger( ClusterManagerImpl.class );

    private final Timer timer = new Timer();

    private final List<ClusterValidator> validators = Lists.newArrayList( new HealthValidator(), new ClusterMembersValidator() );

    private boolean isHealthy;

    @SuppressWarnings("WeakerAccess")
    public ClusterManagerImpl()
    {
        this.checkIntervalMs = 1000L;
        this.instances = new Clusters( DEFAULT_REQUIRED_INSTANCES );
    }

    private ClusterManagerImpl( final Builder builder )
    {
        this.checkIntervalMs = builder.checkIntervalMs;
        this.instances = builder.clusters;
    }

    @Override
    public ClusterState getClusterState()
    {
        return doGetClusterState();
    }

    @Override
    public Clusters getInstances()
    {
        return this.instances;
    }

    private void activateProviders()
    {
        if ( !this.isHealthy )
        {
            LOG.info( "Deactivating all providers" );
        }

        this.instances.forEach( Cluster::enable );
        this.isHealthy = true;
    }

    private void deactivateProviders()
    {
        if ( this.isHealthy )
        {
            LOG.info( "Deactivating all providers" );
        }

        this.instances.forEach( Cluster::disable );
        this.isHealthy = false;
    }

    private void registerProvider()
    {
        if ( this.instances.hasRequiredProviders() )
        {
            LOG.info( "Has all required cluster-providers, activate and start polling health" );
            activateProviders();
            startPolling();
        }
        else
        {
            deactivateProviders();
        }
    }

    private void startPolling()
    {
        if ( checkIntervalMs > 0 )
        {
            this.timer.schedule( new TimerTask()
            {
                @Override
                public void run()
                {
                    doGetClusterState();
                }
            }, this.checkIntervalMs, this.checkIntervalMs );
        }
    }

    private ClusterState doGetClusterState()
    {
        for ( final ClusterValidator validator : this.validators )
        {
            final ClusterValidatorResult result = validator.validate( this.instances );

            if ( !result.isOk() )
            {
                deactivateProviders();
                return ClusterState.ERROR;
            }
        }

        if ( this.instances.hasRequiredProviders() )
        {
            activateProviders();
            return ClusterState.OK;
        }
        else
        {
            return ClusterState.ERROR;
        }
    }


    @SuppressWarnings("unused")
    public void removeProvider( final Cluster provider )
    {
        LOG.info( "Removing cluster-provider: " + provider.getId() );
        this.instances.remove( provider );
        this.registerProvider();
    }

    static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private Long checkIntervalMs = 1000L;

        private Clusters clusters = new Clusters( DEFAULT_REQUIRED_INSTANCES );

        private Builder()
        {
        }

        Builder checkIntervalMs( final Long val )
        {
            checkIntervalMs = val;
            return this;
        }

        Builder requiredInstances( final Clusters val )
        {
            clusters = val;
            return this;
        }

        ClusterManagerImpl build()
        {
            return new ClusterManagerImpl( this );
        }
    }

    @SuppressWarnings("WeakerAccess")
    @Reference(cardinality = ReferenceCardinality.AT_LEAST_ONE, policy = ReferencePolicy.DYNAMIC)
    public void addProvider( final Cluster instance )
    {
        LOG.info( "Adding cluster-provider: " + instance.getId() );
        this.instances.add( instance );
        this.registerProvider();
    }


    @Deactivate
    public void deactivate()
    {
        this.timer.cancel();
    }
}
