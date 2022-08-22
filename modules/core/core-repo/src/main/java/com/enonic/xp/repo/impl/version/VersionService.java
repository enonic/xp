package com.enonic.xp.repo.impl.version;

import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.repo.impl.InternalContext;

public interface VersionService
{
    void store( NodeVersionMetadata nodeVersionMetadata, InternalContext context );

    void delete( NodeVersionId nodeVersionId, InternalContext context );

    NodeVersionMetadata getVersion( NodeVersionId nodeVersionId, InternalContext context );
}
