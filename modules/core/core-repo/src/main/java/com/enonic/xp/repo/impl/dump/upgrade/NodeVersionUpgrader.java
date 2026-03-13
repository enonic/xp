package com.enonic.xp.repo.impl.dump.upgrade;

import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repository.RepositoryId;

public interface NodeVersionUpgrader
{
    NodeStoreVersion upgradeNodeVersion( RepositoryId repositoryId, NodeStoreVersion nodeVersion );
}
