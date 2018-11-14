package com.enonic.xp.repo.impl.node.dao;

import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionIds;
import com.enonic.xp.node.NodeVersions;
import com.enonic.xp.repo.impl.InternalContext;

public interface NodeVersionService
{
    NodeVersionId store( NodeVersion nodeVersion, final InternalContext context );

    NodeVersion get( final NodeVersionId nodeVersionId, final InternalContext context );

    NodeVersions get( final NodeVersionIds nodeVersionIds, final InternalContext context );
}
