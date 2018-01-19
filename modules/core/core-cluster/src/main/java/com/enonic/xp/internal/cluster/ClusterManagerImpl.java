package com.enonic.xp.internal.cluster;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import com.enonic.xp.cluster.ClusterHealth;
import com.enonic.xp.cluster.ClusterManager;
import com.enonic.xp.cluster.ClusterNodes;
import com.enonic.xp.cluster.ClusterProvider;
import com.enonic.xp.cluster.ClusterProviderId;

@Component(immediate = true)
public class ClusterManagerImpl
    implements ClusterManager
{
    private final Long checkIntervalMs;

    private final List<ClusterProvider> providers = Lists.newArrayList();

    private final Logger LOG = LoggerFactory.getLogger( ClusterManagerImpl.class );

    private final Timer timer = new Timer();

    private final List<ClusterProviderId> requiredProviders;

    private final static List<ClusterProviderId> DEFAULT_REQUIRED_PROVIDERS =
        Lists.newArrayList( ClusterProviderId.from( "elasticsearch" ) );

    public ClusterManagerImpl()
    {
        this.checkIntervalMs = 1000L;
        this.requiredProviders = DEFAULT_REQUIRED_PROVIDERS;
    }

    private ClusterManagerImpl( final Builder builder )
    {
        checkIntervalMs = builder.checkIntervalMs;
        requiredProviders = builder.requiredProviders;
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

    @Override
    public ClusterHealth getHealth()
    {
        return doGetHealth();
    }

    private void activate()
    {
        this.providers.forEach( ClusterProvider::enable );
    }

    private void deactivate()
    {
        LOG.info( "Deactivate all providers" );
        this.providers.forEach( ClusterProvider::disable );
    }

    private void registerProvider()
    {
        if ( hasRequiredProviders() )
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

    private boolean hasRequiredProviders()
    {
        return this.providers.stream().map( ClusterProvider::getId ).collect( Collectors.toList() ).containsAll( requiredProviders );
    }

    private ClusterHealth doGetHealth()
    {
        if ( !allHealthy() || !hasSameNodes() )
        {
            deactivate();
            return ClusterHealth.ERROR;
        }

        activate();
        return ClusterHealth.OK;
    }

    private boolean allHealthy()
    {
        for ( final ClusterProvider provider : providers )
        {
            if ( !provider.getHealth().isHealthy() )
            {
                LOG.error( "Provider " + provider.getId() + "" );
                return false;
            }
        }

        return true;
    }

    private boolean hasSameNodes()
    {
        ClusterNodes current = null;

        // This probably need some grace period to handle nodes added and disappearing from cluster
        for ( final ClusterProvider provider : providers )
        {
            if ( current != null && !current.equals( provider.getNodes() ) )
            {
                return false;
            }

            current = provider.getNodes();
        }

        return true;
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addProvider( final ClusterProvider provider )
    {
        LOG.info( "Adding cluster-provider: " + provider.getId() );
        this.providers.add( provider );
        this.registerProvider();
    }

    @SuppressWarnings("unused")
    public void removeProvider( final ClusterProvider provider )
    {
        LOG.info( "Removing cluster-provider: " + provider.getId() );
        this.providers.remove( provider );
        this.registerProvider();
    }

    static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private Long checkIntervalMs = 1000L;

        private List<ClusterProviderId> requiredProviders = DEFAULT_REQUIRED_PROVIDERS;

        private Builder()
        {
        }

        Builder checkIntervalMs( final Long val )
        {
            checkIntervalMs = val;
            return this;
        }

        public Builder requiredProviders( final List<ClusterProviderId> val )
        {
            requiredProviders = val;
            return this;
        }

        ClusterManagerImpl build()
        {
            return new ClusterManagerImpl( this );
        }
    }
}
