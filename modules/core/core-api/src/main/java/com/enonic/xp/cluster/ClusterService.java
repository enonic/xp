package com.enonic.xp.cluster;

import org.jspecify.annotations.NonNull;

import com.enonic.xp.app.ApplicationKey;

public interface ClusterService
{
    boolean isLeader();

    boolean isLeader( @NonNull ApplicationKey applicationKey );

    /**
     * Whether the application is currently started on any cluster member.
     * In a non-clustered installation always returns false - combine with local knowledge.
     */
    boolean hasApplication( @NonNull ApplicationKey applicationKey );
}
