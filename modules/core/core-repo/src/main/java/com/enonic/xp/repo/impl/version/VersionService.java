package com.enonic.xp.repo.impl.version;

import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.repo.impl.InternalContext;

public interface VersionService
{
    void store( final NodeVersionDocument nodeVersionDocument, final InternalContext context );

    NodeVersionMetadata getVersion( final NodeVersionDocumentId versionId, final InternalContext context );
}
