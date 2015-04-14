package com.enonic.xp.node;

import com.google.common.annotations.Beta;

@Beta
public class NodeIdExistsException
    extends RuntimeException
{
    public NodeIdExistsException( final NodeId nodeId )
    {
        super( "Node already exist, id: " + nodeId );
    }
}
