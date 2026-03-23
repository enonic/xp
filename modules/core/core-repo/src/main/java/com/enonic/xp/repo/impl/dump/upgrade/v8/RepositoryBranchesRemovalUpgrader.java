package com.enonic.xp.repo.impl.dump.upgrade.v8;

import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.dump.upgrade.NodeVersionUpgrader;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.SystemConstants;

public class RepositoryBranchesRemovalUpgrader
    implements NodeVersionUpgrader
{
    private static final String BRANCHES_KEY = "branches";

    @Override
    public NodeStoreVersion upgradeNodeVersion( final RepositoryId repositoryId, final NodeStoreVersion nodeVersion )
    {
        if ( !SystemConstants.SYSTEM_REPO_ID.equals( repositoryId ) )
        {
            return null;
        }

        if ( !nodeVersion.data().hasProperty( BRANCHES_KEY ) )
        {
            return null;
        }

        nodeVersion.data().removeProperties( BRANCHES_KEY );
        return nodeVersion;
    }
}
