package com.enonic.xp.cluster.impl;

import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.cluster.Cluster;
import com.enonic.xp.cluster.ClusterId;
import com.enonic.xp.cluster.ClusterManager;
import com.enonic.xp.cluster.ClusterState;
import com.enonic.xp.cluster.ClusterValidationStatus;
import com.enonic.xp.cluster.ClusterValidator;
import com.enonic.xp.cluster.ClusterValidatorResult;
import com.enonic.xp.cluster.Clusters;

@Component(immediate = true)
public class ClusterManagerImpl
    implements ClusterManager
{
    private static final Logger LOG = LoggerFactory.getLogger( ClusterManagerImpl.class );

    private static final List<ClusterId> DEFAULT_REQUIRED_INSTANCES = List.of( ClusterId.from( "elasticsearch" ) );

    private final Clusters instances;

    private final List<ClusterValidator> validators = List.of( new HealthValidator(), new ClusterMembersValidator() );

    @Activate
    public ClusterManagerImpl()
    {
        this( new Clusters( DEFAULT_REQUIRED_INSTANCES ) );
    }

    ClusterManagerImpl( final Clusters instances )
    {
        this.instances = instances;
    }

    @Override
    public ClusterState getClusterState()
    {
        return doGetClusterState();
    }

    @Override
    public Clusters getClusters()
    {
        return this.instances;
    }

    @Override
    public boolean isHealthy()
    {
        return doGetClusterState() == ClusterState.OK;
    }

    private ClusterState doGetClusterState()
    {
        if ( !this.instances.hasRequiredProviders() )
        {
            return ClusterState.ERROR;
        }

        for ( final ClusterValidator validator : validators )
        {
            final ClusterValidatorResult result = validator.validate( this.instances );
            result.getErrors().
                forEach( error -> LOG.error( error.getMessage() ) );
            result.getWarnings().
                forEach( warning -> LOG.warn( warning.getMessage() ) );
            if ( result.getStatus() == ClusterValidationStatus.RED )
            {
                return ClusterState.ERROR;
            }
        }

        return ClusterState.OK;
    }

    @Reference(cardinality = ReferenceCardinality.AT_LEAST_ONE, policy = ReferencePolicy.DYNAMIC)
    public void addProvider( final Cluster instance )
    {
        LOG.info( "Adding cluster-provider: {}", instance.getId() );
        this.instances.add( instance );
    }

    public void removeProvider( final Cluster provider )
    {
        LOG.info( "Removing cluster-provider: {}", provider.getId() );
        this.instances.remove( provider );
    }
}