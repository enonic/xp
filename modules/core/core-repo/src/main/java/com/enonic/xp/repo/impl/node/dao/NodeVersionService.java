package com.enonic.xp.repo.impl.node.dao;

import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionIds;
import com.enonic.xp.node.NodeVersions;

public interface NodeVersionService
{
    NodeVersionId store( NodeVersion nodeVersion );

    NodeVersion get( final NodeVersionId nodeVersionId );

    NodeVersions get( final NodeVersionIds nodeVersionIds );
}
