package com.enonic.xp.repo.impl.vacuum.version;

import com.enonic.xp.node.NodeId;

interface NodeInUseDetector
{
    boolean execute( final NodeId nodeId );
}
