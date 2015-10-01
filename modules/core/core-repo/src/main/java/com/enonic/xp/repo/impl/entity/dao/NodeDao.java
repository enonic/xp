package com.enonic.xp.repo.impl.entity.dao;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionIds;
import com.enonic.xp.node.Nodes;

public interface NodeDao
{
    NodeVersionId store( Node node );

    Node get( final NodeVersionId nodeVersionId );

    Nodes get( final NodeVersionIds nodeVersionIds );
}
