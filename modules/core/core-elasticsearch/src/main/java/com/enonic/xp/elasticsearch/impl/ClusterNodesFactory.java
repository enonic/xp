package com.enonic.xp.elasticsearch.impl;

import org.elasticsearch.cluster.node.DiscoveryNodes;

import com.enonic.xp.cluster.ClusterNode;
import com.enonic.xp.cluster.ClusterNodes;

class ClusterNodesFactory
{
    static ClusterNodes create( final DiscoveryNodes discoveryNodes )
    {
        final ClusterNodes.Builder builder = ClusterNodes.create();
        discoveryNodes.forEach( n -> builder.add( ClusterNode.from( n.getName() ) ) );
        return builder.build();
    }

}
