package com.enonic.xp.repo.impl.node.dao;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobKeys;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionIds;
import com.enonic.xp.node.NodeVersions;
import com.enonic.xp.repo.impl.InternalContext;

public interface NodeVersionService
{
    BlobKey store( NodeVersion nodeVersion, final InternalContext context );

    NodeVersion get( final BlobKey blobKey, final InternalContext context );

    NodeVersions get( final BlobKeys blobKeys, final InternalContext context );
}
