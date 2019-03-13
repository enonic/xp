package com.enonic.xp.cluster.impl;

import com.enonic.xp.cluster.Cluster;
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
            if ( !provider.getHealth().isHealthy() )
            {
                final ClusterHealthError error = new ClusterHealthError( provider.getId(), provider.getHealth().getErrorMessage() );

                return ClusterValidatorResult.create().
                    error( error ).
                    build();
            }
        }

        return ClusterValidatorResult.ok();
    }
}
