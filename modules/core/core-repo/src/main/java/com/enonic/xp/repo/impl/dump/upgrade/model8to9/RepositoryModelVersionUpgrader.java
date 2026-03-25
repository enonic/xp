package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import com.enonic.xp.repo.impl.Model;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.dump.upgrade.NodeVersionUpgrader;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.SystemConstants;

public class RepositoryModelVersionUpgrader
    implements NodeVersionUpgrader
{
    private static final String MODEL_VERSION_KEY = "modelVersion";

    @Override
    public NodeStoreVersion upgradeNodeVersion( final RepositoryId repositoryId, final NodeStoreVersion nodeVersion )
    {
        if ( !SystemConstants.SYSTEM_REPO_ID.equals( repositoryId ) )
        {
            return null;
        }

        if ( nodeVersion.data().hasProperty( "data" ) )
        {
            nodeVersion.data().setString( MODEL_VERSION_KEY, Model.MODEL_VERSION.toString() );
            return nodeVersion;
        }

        return null;
    }
}
