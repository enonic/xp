package com.enonic.xp.repo.impl.version;

import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.repo.impl.InternalContext;

public interface VersionService
{
    void store( NodeVersion nodeVersion, InternalContext context );

    void delete( NodeVersionId nodeVersionId, InternalContext context );

    NodeVersion getVersion( NodeVersionId nodeVersionId, InternalContext context );
}
