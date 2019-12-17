package com.enonic.xp.repo.impl.version;

import java.util.Collection;

import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.repo.impl.InternalContext;

public interface VersionService
{
    void store( final NodeVersionMetadata nodeVersionMetadata, final InternalContext context );

    void delete( final Collection<NodeVersionId> nodeVersionIds, final InternalContext context );

    NodeVersionMetadata getVersion( final NodeId nodeId, final NodeVersionId nodeVersionId, final InternalContext context );

    void deleteBranches( final NodeBranchEntries branchEntries, final InternalContext context, final Boolean all );

}
