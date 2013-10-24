package com.enonic.wem.core.item.dao;


import com.enonic.wem.api.item.ItemId;
import com.enonic.wem.api.item.NoItemFoundException;
import com.enonic.wem.api.item.NoItemWithIdFound;
import com.enonic.wem.api.item.Node;
import com.enonic.wem.api.item.NodePath;

public interface NodeDao
{
    public Node getNodeById( ItemId id )
        throws NoItemWithIdFound;

    public Node getNodeByPath( NodePath path )
        throws NoItemFoundException;

    public Node createNode( CreateNodeArgs createNodeArgs );

    public Node updateNode( final UpdateNodeArgs updateNodeArgs );
}
