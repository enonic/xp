package com.enonic.xp.repo.impl.node.dao;

import com.enonic.xp.blob.NodeVersionKey;
import com.enonic.xp.blob.NodeVersionKeys;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersions;
import com.enonic.xp.repo.impl.InternalContext;

public interface NodeVersionService
{
    NodeVersionKey store( NodeVersion nodeVersion, InternalContext context );

    NodeVersion get( NodeVersionKey nodeVersionKey, InternalContext context );

    NodeVersions get( NodeVersionKeys nodeVersionKeys, InternalContext context );
}
