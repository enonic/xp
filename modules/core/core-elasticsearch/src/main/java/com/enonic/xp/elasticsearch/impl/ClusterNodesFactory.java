package com.enonic.xp.elasticsearch.impl;

import java.util.List;

import com.enonic.xp.cluster.ClusterNode;
import com.enonic.xp.cluster.ClusterNodes;
import com.enonic.xp.elasticsearch.client.impl.nodes.Node;

class ClusterNodesFactory
{
    static ClusterNodes create( final List<Node> nodes )
    {
        final ClusterNodes.Builder builder = ClusterNodes.create();
        nodes.forEach( n -> builder.add( ClusterNode.from( n.getName() ) ) );
        return builder.build();
    }

}
