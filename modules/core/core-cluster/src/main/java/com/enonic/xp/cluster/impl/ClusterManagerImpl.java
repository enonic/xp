package com.enonic.xp.cluster.impl;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import com.enonic.xp.cluster.ClusterManager;
import com.enonic.xp.cluster.ClusterProvider;
import com.enonic.xp.cluster.ClusterProviderId;
import com.enonic.xp.cluster.ClusterProviders;
import com.enonic.xp.cluster.ClusterState;
import com.enonic.xp.cluster.ClusterValidator;
import com.enonic.xp.cluster.ClusterValidatorResult;

@Component(immediate = true)
public class ClusterManagerImpl
    implements ClusterManager
{
    private final Long checkIntervalMs;

    private final static List<ClusterProviderId> DEFAULT_REQUIRED_PROVIDERS =
        Lists.newArrayList( ClusterProviderId.from( "elasticsearch" ), ClusterProviderId.from( "ignite" ) );

    private final ClusterProviders clusterProviders;

    private final Logger LOG = LoggerFactory.getLogger( ClusterManagerImpl.class );

    private final Timer timer = new Timer();

    private final List<ClusterValidator> validators = Lists.newArrayList( new HealthValidator(), new ClusterMembersValidator() );

    @SuppressWarnings("WeakerAccess")
    public ClusterManagerImpl()
    {
        this.checkIntervalMs = 1000L;
        this.clusterProviders = new ClusterProviders( DEFAULT_REQUIRED_PROVIDERS );
    }

    private ClusterManagerImpl( final Builder builder )
    {
        this.checkIntervalMs = builder.checkIntervalMs;
        this.clusterProviders = builder.clusterProviders;
    }

    @Override
    public ClusterState getHealth()
    {
        return doGetHealth();
    }

    @Override
    public ClusterProviders getProviders()
    {
        return this.clusterProviders;
    }

    private void activate()
    {
        this.clusterProviders.forEach( ClusterProvider::enable );
    }

    private void deactivate()
    {
        LOG.info( "Deactivate all providers" );
        this.clusterProviders.forEach( ClusterProvider::disable );
    }

    private void registerProvider()
    {
        if ( this.clusterProviders.hasRequiredProviders() )
        {
            LOG.info( "Has all required cluster-providers, activate and start polling health" );
            activate();
            startPolling();
        }
        else
        {
            deactivate();
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
                    doGetHealth();
                }
            }, this.checkIntervalMs, this.checkIntervalMs );
        }
    }

    private ClusterState doGetHealth()
    {
        for ( final ClusterValidator validator : this.validators )
        {
            final ClusterValidatorResult result = validator.validate( this.clusterProviders );

            if ( !result.isOk() )
            {
                deactivate();
                return ClusterState.ERROR;
            }
        }

        activate();
        return ClusterState.OK;
    }


    @SuppressWarnings("unused")
    public void removeProvider( final ClusterProvider provider )
    {
        LOG.info( "Removing cluster-provider: " + provider.getId() );
        this.clusterProviders.remove( provider );
        this.registerProvider();
    }

    static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private Long checkIntervalMs = 1000L;

        private ClusterProviders clusterProviders = new ClusterProviders( DEFAULT_REQUIRED_PROVIDERS );

        private Builder()
        {
        }

        Builder checkIntervalMs( final Long val )
        {
            checkIntervalMs = val;
            return this;
        }

        Builder requiredProviders( final ClusterProviders val )
        {
            clusterProviders = val;
            return this;
        }

        ClusterManagerImpl build()
        {
            return new ClusterManagerImpl( this );
        }
    }

    @SuppressWarnings("WeakerAccess")
    @Reference(cardinality = ReferenceCardinality.AT_LEAST_ONE, policy = ReferencePolicy.DYNAMIC)
    public void addProvider( final ClusterProvider provider )
    {
        LOG.info( "Adding cluster-provider: " + provider.getId() );
        this.clusterProviders.add( provider );
        this.registerProvider();
    }
}
