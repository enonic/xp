package com.enonic.xp.cluster.impl.discovery;

import java.util.UUID;

import com.enonic.xp.cluster.ClusterNodeId;

public class ClusterNodeNameProvider
{
    private final static String ID = UUID.randomUUID().toString();

    public static ClusterNodeId getID()
    {
        return ClusterNodeId.from( ID );
    }
}
