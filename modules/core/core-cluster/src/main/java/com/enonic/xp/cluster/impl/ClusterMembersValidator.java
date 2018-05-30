package com.enonic.xp.cluster.impl;

import java.util.Set;
import java.util.stream.Collectors;

import com.enonic.xp.cluster.Cluster;
import com.enonic.xp.cluster.ClusterNode;
import com.enonic.xp.cluster.ClusterNodeId;
import com.enonic.xp.cluster.ClusterValidator;
import com.enonic.xp.cluster.ClusterValidatorResult;
import com.enonic.xp.cluster.Clusters;

class ClusterMembersValidator
    implements ClusterValidator
{
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
                final NodesMismatchWarning warning = new NodesMismatchWarning( cluster, first, clusterNodeIds, current );

                return ClusterValidatorResult.create().
                    warning( warning ).
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