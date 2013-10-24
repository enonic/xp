package com.enonic.wem.core.item.dao;


import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.NoItemFoundException;
import com.enonic.wem.api.entity.NoItemWithIdFound;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;

public interface NodeDao
{
    public Node getNodeById( EntityId id )
        throws NoItemWithIdFound;

    public Node getNodeByPath( NodePath path )
        throws NoItemFoundException;

    public Node createNode( CreateNodeArguments createNodeArguments );

    public Node updateNode( final UpdateNodeArgs updateNodeArgs );
}
