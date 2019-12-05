package com.enonic.xp.cluster.impl;

import com.enonic.xp.cluster.Cluster;
import com.enonic.xp.cluster.ClusterHealth;
import com.enonic.xp.cluster.ClusterValidator;
import com.enonic.xp.cluster.ClusterValidatorResult;
import com.enonic.xp.cluster.Clusters;

class HealthValidator
    implements ClusterValidator
{
    @Override
    public ClusterValidatorResult validate( final Clusters clusters )
    {
        for ( final Cluster provider : clusters )
        {
            final ClusterHealth clusterHealth = provider.getHealth();
            if ( !clusterHealth.isHealthy() )
            {
                final ClusterHealthError error = new ClusterHealthError( provider.getId(), clusterHealth.getErrorMessage() );

                return ClusterValidatorResult.create().
                    error( error ).
                    build();
            }
        }

        return ClusterValidatorResult.ok();
    }
}
