package com.enonic.xp.repo.impl.version;

import java.util.Collection;

import com.enonic.xp.context.InternalContext;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionMetadata;

public interface VersionService
{
    void store( final NodeVersionMetadata nodeVersionMetadata, final InternalContext context );

    void delete( final Collection<NodeVersionId> nodeVersionIds, final InternalContext context );

    NodeVersionMetadata getVersion( final NodeId nodeId, final NodeVersionId nodeVersionId, final InternalContext context );
}
