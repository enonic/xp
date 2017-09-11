package com.enonic.xp.repo.impl.vacuum.version;

import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.node.NodeId;

interface NodeIdResolver
{
    NodeId resolve( final BlobRecord record );
}
