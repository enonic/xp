package com.enonic.xp.cluster.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.cluster.Cluster;
import com.enonic.xp.cluster.ClusterValidator;
import com.enonic.xp.cluster.ClusterValidatorResult;
import com.enonic.xp.cluster.Clusters;

class HealthValidator
    implements ClusterValidator
{
    private final static Logger LOG = LoggerFactory.getLogger( ClusterMembersValidator.class );

    @Override
    public ClusterValidatorResult validate( final Clusters clusters )
    {
        for ( final Cluster provider : clusters )
        {
            if ( !provider.getHealth().isHealthy() )
            {
                final ClusterHealthError error = new ClusterHealthError( provider.getId() );

                LOG.error( error.getMessage() );

                return ClusterValidatorResult.create().
                    error( error ).
                    build();
            }
        }

        return ClusterValidatorResult.ok();
    }
}
