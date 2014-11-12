package com.enonic.wem.core.entity.dao;

import com.enonic.wem.repo.Node;
import com.enonic.wem.repo.NodeVersionId;
import com.enonic.wem.repo.NodeVersionIds;
import com.enonic.wem.repo.Nodes;

public interface NodeDao
{
    public NodeVersionId store( Node node );

    public Node getByVersionId( final NodeVersionId nodeVersionId );

    public Nodes getByVersionIds( final NodeVersionIds nodeVersionIds );
}
