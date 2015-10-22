package com.enonic.xp.repo.impl.node.dao;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionIds;
import com.enonic.xp.node.NodeVersions;

public interface NodeVersionDao
{
    NodeVersionId store( Node node );

    NodeVersion get( final NodeVersionId nodeVersionId );

    NodeVersions get( final NodeVersionIds nodeVersionIds );
}
