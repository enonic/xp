package com.enonic.xp.cluster;

import org.jspecify.annotations.NonNull;

import com.enonic.xp.app.ApplicationKey;

public interface ClusterService
{
    boolean isLeader();

    boolean isLeader( @NonNull ApplicationKey applicationKey );
}
