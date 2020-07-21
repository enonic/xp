package com.enonic.xp.cluster.impl.discovery;

import java.util.UUID;

import com.enonic.xp.cluster.ClusterNodeId;

public class ClusterNodeNameProvider
{
    private static final String ID = UUID.randomUUID().toString();

    public static ClusterNodeId getID()
    {
        return ClusterNodeId.from( ID );
    }
}
