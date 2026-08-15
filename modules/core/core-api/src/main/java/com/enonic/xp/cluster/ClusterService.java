package com.enonic.xp.cluster;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.app.ApplicationKey;

@NullMarked
public interface ClusterService
{
    /**
     * Whether this member leads the cluster. The leader is the member that joined first, so
     * leadership passes on to the next in join order once the leader leaves; the single node of a
     * non-clustered installation always leads.
     * <p>
     * Leadership follows cluster membership rather than a held lease, so it is not a mutual
     * exclusion guarantee - it can change at any moment, and either side of a network partition
     * has a leader of its own.
     */
    boolean isLeader();

    /**
     * Whether this member leads the members that run the application - of those advertising it, the
     * one that joined first. An application no member runs has no leader anywhere. In a
     * non-clustered installation the single node always leads, whether or not the application is
     * installed on it, so callers pair this with local knowledge.
     */
    boolean isLeader( ApplicationKey applicationKey );

    /**
     * Whether the application is started on some member of the cluster.
     * A non-clustered installation has no cluster, so the answer there is always false -
     * callers pair this with local knowledge.
     */
    boolean inCluster( ApplicationKey applicationKey );
}
