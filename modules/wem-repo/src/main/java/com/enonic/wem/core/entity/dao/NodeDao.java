package com.enonic.wem.core.entity.dao;

import com.enonic.wem.core.entity.Node;
import com.enonic.wem.core.entity.NodeVersionId;
import com.enonic.wem.core.entity.NodeVersionIds;
import com.enonic.wem.core.entity.Nodes;

public interface NodeDao
{
    public NodeVersionId store( Node node );

    public Node getByVersionId( final NodeVersionId nodeVersionId );

    public Nodes getByVersionIds( final NodeVersionIds nodeVersionIds );
}
