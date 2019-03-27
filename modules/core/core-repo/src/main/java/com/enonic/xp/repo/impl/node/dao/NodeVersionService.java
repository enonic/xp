package com.enonic.xp.repo.impl.node.dao;

import com.enonic.xp.blob.NodeVersionKey;
import com.enonic.xp.blob.NodeVersionKeys;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersions;
import com.enonic.xp.repo.impl.InternalContext;

public interface NodeVersionService
{
    NodeVersionKey store( NodeVersion nodeVersion, final InternalContext context );

    NodeVersion get( final NodeVersionKey nodeVersionKey, final InternalContext context );

    NodeVersions get( final NodeVersionKeys nodeVersionKeys, final InternalContext context );
}
