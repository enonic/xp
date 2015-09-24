package com.enonic.xp.repo.impl.entity.dao;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionIds;
import com.enonic.xp.node.Nodes;

public interface NodeDao
{
    public NodeVersionId store( Node node );

    public Node getByVersionId( final NodeVersionId nodeVersionId );

    public Nodes getByVersionIds( final NodeVersionIds nodeVersionIds );
}
