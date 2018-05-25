package com.enonic.xp.cluster.impl;

import java.util.List;

import org.osgi.service.component.annotations.Component;
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
    private final static List<ClusterId> DEFAULT_REQUIRED_INSTANCES =
        Lists.newArrayList( ClusterId.from( "elasticsearch" ), ClusterId.from( "ignite" ) );

    private final Clusters instances;

    private final Logger LOG = LoggerFactory.getLogger( ClusterManagerImpl.class );

    private final List<ClusterValidator> validators = Lists.newArrayList( new HealthValidator(), new ClusterMembersValidator() );

    private boolean isHealthy;

    @SuppressWarnings("WeakerAccess")
    public ClusterManagerImpl()
    {
        this( new Clusters( DEFAULT_REQUIRED_INSTANCES ) );
    }

    ClusterManagerImpl( final Clusters clusterInstances )
    {
        this.instances = clusterInstances;
    }

    @Override
    public ClusterState getClusterState()
    {
        for ( final ClusterValidator validator : this.validators )
        {
            final ClusterValidatorResult result = validator.validate( this.instances );

            if ( !result.isOk() )
            {
                return ClusterState.ERROR;
            }
        }

        if ( this.instances.hasRequiredProviders() )
        {
            return ClusterState.OK;
        }
        else
        {
            return ClusterState.ERROR;
        }
    }

    @Override
    public Clusters getClusters()
    {
        return this.instances;
    }

    private void activateProviders()
    {
        if ( !this.isHealthy )
        {
            LOG.info( "Activating cluster providers" );
        }

        this.instances.forEach( Cluster::enable );
        this.isHealthy = true;
    }

    private void deactivateProviders()
    {
        if ( this.isHealthy )
        {
            LOG.info( "Deactivating cluster providers" );
        }

        this.instances.forEach( Cluster::disable );
        this.isHealthy = false;
    }

    private void registerProvider()
    {
        if ( this.instances.hasRequiredProviders() )
        {
            activateProviders();
        }
        else
        {
            deactivateProviders();
        }
    }

    @SuppressWarnings("unused")
    public void removeProvider( final Cluster provider )
    {
        LOG.info( "Removing cluster-provider: " + provider.getId() );
        this.instances.remove( provider );
        this.registerProvider();
    }

    @SuppressWarnings("WeakerAccess")
    @Reference(cardinality = ReferenceCardinality.AT_LEAST_ONE, policy = ReferencePolicy.DYNAMIC)
    public void addProvider( final Cluster instance )
    {
        LOG.info( "Adding cluster-provider: " + instance.getId() );
        this.instances.add( instance );
        this.registerProvider();
    }
}
