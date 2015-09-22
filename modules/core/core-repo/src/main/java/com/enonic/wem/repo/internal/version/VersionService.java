package com.enonic.wem.repo.internal.version;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.xp.node.NodeVersion;

public interface VersionService
{
    void store( final NodeVersionDocument nodeVersionDocument, final InternalContext context );

    NodeVersion getVersion( final NodeVersionDocumentId versionId, final InternalContext context );
}
