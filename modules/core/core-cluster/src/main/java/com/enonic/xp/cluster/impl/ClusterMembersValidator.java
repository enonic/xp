package com.enonic.xp.cluster.impl;

import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.cluster.Cluster;
import com.enonic.xp.cluster.ClusterNode;
import com.enonic.xp.cluster.ClusterNodeId;
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
        Set<ClusterNodeId> current = null;
        Cluster first = null;

        for ( final Cluster cluster : clusters )
        {
            final Set<ClusterNodeId> clusterNodeIds = cluster.getNodes().
                stream().
                map( ClusterNode::getId ).
                collect( Collectors.toSet() );

            if ( first != null && current != null && !current.equals( clusterNodeIds ) )
            {
                final NodesMismatchError error = new NodesMismatchError( cluster, first, clusterNodeIds, current );

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
                current = clusterNodeIds;
            }
        }

        return ClusterValidatorResult.ok();
    }
}
