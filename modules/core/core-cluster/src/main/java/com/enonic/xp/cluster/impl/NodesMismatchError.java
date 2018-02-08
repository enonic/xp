package com.enonic.xp.cluster.impl;

import com.enonic.xp.cluster.Cluster;
import com.enonic.xp.cluster.ClusterNodes;
import com.enonic.xp.cluster.ClusterValidationError;

class NodesMismatchError
    implements ClusterValidationError
{
    private final Cluster p1;

    private final Cluster p2;

    private final ClusterNodes c1;

    private final ClusterNodes c2;

    NodesMismatchError( final Cluster p1, final Cluster p2, final ClusterNodes c1, final ClusterNodes c2 )
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
