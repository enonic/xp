package com.enonic.wem.repo.internal.entity.dao;

import com.enonic.xp.core.node.Node;
import com.enonic.xp.core.node.NodeVersionId;
import com.enonic.xp.core.node.NodeVersionIds;
import com.enonic.xp.core.node.Nodes;

public interface NodeDao
{
    public NodeVersionId store( Node node );

    public Node getByVersionId( final NodeVersionId nodeVersionId );

    public Nodes getByVersionIds( final NodeVersionIds nodeVersionIds );
}
