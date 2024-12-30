package com.enonic.xp.repo.impl.node.dao;

import com.enonic.xp.blob.NodeVersionKey;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.security.acl.AccessControlList;

public interface NodeVersionService
{
    NodeVersionKey store( NodeVersion nodeVersion, InternalContext context );

    NodeVersion get( NodeVersionKey nodeVersionKey, InternalContext context );

    AccessControlList getPermissions( NodeVersionKey nodeVersionKey, InternalContext context );
}
