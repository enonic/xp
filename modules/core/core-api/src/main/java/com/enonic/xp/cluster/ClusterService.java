package com.enonic.xp.cluster;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.app.ApplicationKey;

@NullMarked
public interface ClusterService
{
    boolean isLeader();

    boolean isLeader( ApplicationKey applicationKey );

    /**
     * Whether the application is started on some member of the cluster.
     * A non-clustered installation has no cluster, so the answer there is always false -
     * callers pair this with local knowledge.
     */
    boolean inCluster( ApplicationKey applicationKey );
}
