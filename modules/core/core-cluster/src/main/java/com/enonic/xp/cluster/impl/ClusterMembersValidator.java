package com.enonic.xp.cluster.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.cluster.Cluster;
import com.enonic.xp.cluster.ClusterNodes;
import com.enonic.xp.cluster.ClusterValidator;
import com.enonic.xp.cluster.ClusterValidatorResult;
import com.enonic.xp.cluster.Clusters;

class ClusterMembersValidator
    implements ClusterValidator
{
    private final static Logger LOG = LoggerFactory.getLogger( ClusterMembersValidator.class );

    @Override
    public ClusterValidatorResult validate( final Clusters clusters )
    {
        ClusterNodes current = null;
        Cluster first = null;

        for ( final Cluster cluster : clusters )
        {
            final ClusterNodes clusterNodes = cluster.getNodes();

            if ( first != null && current != null && !current.equals( clusterNodes ) )
            {
                final NodesMismatchError error = new NodesMismatchError( cluster, first, clusterNodes, current );

                LOG.error( error.getMessage() );

                return ClusterValidatorResult.create().
                    ok( false ).
                    error( error ).
                    build();
            }

            if ( first == null )
            {
                first = cluster;
            }

            if ( current == null )
            {
                current = clusterNodes;
            }
        }

        return ClusterValidatorResult.ok();
    }
}
