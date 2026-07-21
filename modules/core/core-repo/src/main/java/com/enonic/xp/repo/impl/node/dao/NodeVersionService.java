package com.enonic.xp.repo.impl.node.dao;

import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.security.acl.AccessControlList;

public interface NodeVersionService
{
    /**
     * Serializes the three payload segments (node-data/index-config/ACL) and computes their
     * content-addressed keys. Pure — no I/O, no persistence (Phase 3 Gate B,
     * nodb/BUILD-PHASE-3.md: persisting the bytes is now the storage SPI's job, via
     * {@code NodeStore#storeVersion}/{@code #storeNode}, so callers pass the result of this
     * method on to {@code VersionService#store}/{@code BranchService#storeWithVersion}).
     */
    SerializedNodeVersion serialize( NodeStoreVersion nodeVersion );

    NodeStoreVersion get( NodeVersionKey nodeVersionKey, InternalContext context );

    AccessControlList getPermissions( NodeVersionKey nodeVersionKey, InternalContext context );
}
