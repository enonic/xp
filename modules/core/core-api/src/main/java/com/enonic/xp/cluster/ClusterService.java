package com.enonic.xp.cluster;

import org.jspecify.annotations.NonNull;

import com.enonic.xp.app.ApplicationKey;

public interface ClusterService
{
    boolean isLeader();

    boolean isLeader( @NonNull ApplicationKey applicationKey );

    /**
     * Whether the application is started on some member of the cluster.
     * A non-clustered installation has no cluster, so the answer there is always false -
     * callers pair this with local knowledge.
     */
    boolean inCluster( @NonNull ApplicationKey applicationKey );
}
