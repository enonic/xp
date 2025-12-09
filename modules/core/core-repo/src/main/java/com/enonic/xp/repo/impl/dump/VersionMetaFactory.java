package com.enonic.xp.repo.impl.dump;

import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.repo.impl.dump.model.VersionMeta;

class VersionMetaFactory
{
    public static VersionMeta create( final NodeVersionMetadata metaData )
    {
        return new VersionMeta( metaData.getNodeVersionId(), metaData.getNodeVersionKey(), metaData.getNodePath(), metaData.getTimestamp(),
                                metaData.getNodeCommitId(), metaData.getAttributes() );
    }
}
