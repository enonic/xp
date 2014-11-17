package com.enonic.wem.repo.internal.entity.dao;

import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeVersionId;
import com.enonic.wem.api.node.NodeVersionIds;
import com.enonic.wem.api.node.Nodes;

public interface NodeDao
{
    public NodeVersionId store( Node node );

    public Node getByVersionId( final NodeVersionId nodeVersionId );

    public Nodes getByVersionIds( final NodeVersionIds nodeVersionIds );
}
