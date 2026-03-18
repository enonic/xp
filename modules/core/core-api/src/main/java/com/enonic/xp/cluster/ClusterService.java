package com.enonic.xp.cluster;

import com.enonic.xp.app.ApplicationKey;

public interface ClusterService
{
    boolean isLeader();

    boolean isLeader( ApplicationKey applicationKey );
}
