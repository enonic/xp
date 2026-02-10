package com.enonic.xp.repo.impl.dump;

import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.repo.impl.dump.model.VersionMeta;

class VersionMetaFactory
{
    public static VersionMeta create( final NodeVersion nodeVersion )
    {
        return new VersionMeta( nodeVersion.getNodeVersionId(), nodeVersion.getNodeVersionKey(), nodeVersion.getNodePath(),
                                nodeVersion.getTimestamp(), nodeVersion.getNodeCommitId(), nodeVersion.getAttributes() );
    }
}
