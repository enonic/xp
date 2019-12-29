package com.enonic.xp.node;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class NodeIdExistsException
    extends RuntimeException
{
    public NodeIdExistsException( final NodeId nodeId )
    {
        super( "Node " + nodeId + " already exists" );
    }
}
