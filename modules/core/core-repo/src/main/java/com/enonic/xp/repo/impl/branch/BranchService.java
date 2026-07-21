package com.enonic.xp.repo.impl.branch;

import java.util.Collection;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeBranchEntries;
import com.enonic.xp.repo.impl.NodeBranchEntry;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.storage.spi.NodeSegments;

public interface BranchService
{
    void store( NodeBranchEntry nodeBranchEntry, InternalContext context );

    /**
     * Combined store of a NEW/updated node's version, payload segments and branch entry —
     * the save path {@code NodeStorageServiceImpl#store} uses (Phase 3 Gate B,
     * nodb/BUILD-PHASE-3.md's "ONE WriteBatch per save"): preserves this class's cache/
     * duplicate-path-guard semantics exactly like {@link #store}, but issues ONE combined
     * {@code NodeStore#storeNode} call instead of a separate version write followed by a
     * separate branch-entry write.
     */
    void storeWithVersion( NodeBranchEntry nodeBranchEntry, NodeVersion nodeVersion, NodeSegments segments, InternalContext context );

    void delete( Collection<NodeBranchEntry> nodeBranchEntries, InternalContext context );

    void push( NodeBranchEntry nodeBranchEntry, InternalContext context );

    NodeBranchEntry get( NodeId nodeId, InternalContext context );

    boolean exists( NodeId nodeId, InternalContext context );

    NodeBranchEntries get( Iterable<NodeId> nodeIds, InternalContext context );

    NodeBranchEntry get( NodePath nodePath, InternalContext context );

    void evictPath( NodePath nodePath, InternalContext context );

    void evictAllPaths();

    Branches getBranches( NodeId nodeId, RepositoryId repositoryId );
}
