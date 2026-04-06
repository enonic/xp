package com.enonic.xp.cluster.impl;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jspecify.annotations.NonNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.cluster.ClusterService;
import com.enonic.xp.core.internal.Local;
import com.enonic.xp.core.internal.concurrent.DynamicReference;

@Component
@Local
public class ClusterServiceImpl
    implements ClusterService
{
    private static final Logger LOG = LoggerFactory.getLogger( ClusterServiceImpl.class );

    private final DynamicReference<ClusterService> clusteredClusterServiceRef = new DynamicReference<>();

    private volatile ClusterConfig clusterConfig;

    @Override
    public boolean isLeader()
    {
        final ClusterService clusterService = resolveClusterService();
        if ( clusterService == null )
        {
            return true;
        }
        return clusterService.isLeader();
    }

    @Override
    public boolean isLeader( final @NonNull ApplicationKey applicationKey )
    {
        final ClusterService clusterService = resolveClusterService();
        if ( clusterService == null )
        {
            return true;
        }
        return clusterService.isLeader( applicationKey );
    }

    private ClusterService resolveClusterService()
    {
        final ClusterService clusterService = clusteredClusterServiceRef.getNow( null );
        if ( clusterService != null )
        {
            return clusterService;
        }

        if ( clusterConfig != null && clusterConfig.isEnabled() )
        {
            try
            {
                LOG.info( "Cluster service is unavailable, waiting..." );
                return clusteredClusterServiceRef.get( 5, TimeUnit.SECONDS );
            }
            catch ( InterruptedException e )
            {
                Thread.currentThread().interrupt();
                throw new IllegalStateException( "Interrupted while waiting for cluster service", e );
            }
            catch ( TimeoutException e )
            {
                throw new IllegalStateException( "Cannot resolve cluster service", e );
            }
        }

        return null;
    }

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY, target = "(!(local=true))")
    public void setClusteredClusterService( final ClusterService clusterService )
    {
        this.clusteredClusterServiceRef.set( clusterService );
    }

    public void unsetClusteredClusterService( final ClusterService clusterService )
    {
        this.clusteredClusterServiceRef.reset();
    }

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    public void setClusterConfig( final ClusterConfig clusterConfig )
    {
        this.clusterConfig = clusterConfig;
    }

    public void unsetClusterConfig( final ClusterConfig clusterConfig )
    {
        this.clusterConfig = null;
    }
}
