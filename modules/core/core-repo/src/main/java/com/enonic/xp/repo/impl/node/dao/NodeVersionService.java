package com.enonic.xp.repo.impl.node.dao;

import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.security.acl.AccessControlList;

public interface NodeVersionService
{
    NodeVersionKey store( NodeStoreVersion nodeVersion, InternalContext context );

    NodeStoreVersion get( NodeVersionKey nodeVersionKey, InternalContext context );

    AccessControlList getPermissions( NodeVersionKey nodeVersionKey, InternalContext context );
}
