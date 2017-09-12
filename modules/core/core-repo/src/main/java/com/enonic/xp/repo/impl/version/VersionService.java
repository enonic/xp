package com.enonic.xp.repo.impl.version;

import java.util.Collection;

import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.repo.impl.InternalContext;

public interface VersionService
{
    void store( final NodeVersionMetadata nodeVersionMetadata, final InternalContext context );

    void delete( final Collection<NodeVersionDocumentId> nodeVersionDocumentIds, final InternalContext context );

    NodeVersionMetadata getVersion( final NodeVersionDocumentId versionId, final InternalContext context );
}
