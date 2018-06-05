package com.enonic.xp.cluster.impl;

import java.util.Set;

import com.enonic.xp.cluster.Cluster;
import com.enonic.xp.cluster.ClusterNodeId;
import com.enonic.xp.cluster.ClusterValidationWarning;

class NodesMismatchWarning
    implements ClusterValidationWarning
{
    private final Cluster p1;

    private final Cluster p2;

    private final Set<ClusterNodeId> c1;

    private final Set<ClusterNodeId> c2;

    NodesMismatchWarning( final Cluster p1, final Cluster p2, final Set<ClusterNodeId> c1, final Set<ClusterNodeId> c2 )
    {
        this.p1 = p1;
        this.p2 = p2;
        this.c1 = c1;
        this.c2 = c2;
    }

    @Override
    public String getMessage()
    {
        return "ClusterNodes not matching: " + this.p1.getId() + ": " + this.c1 + "; " + this.p2.getId() + ": " + this.c2;
    }
}
