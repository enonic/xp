package com.enonic.xp.repo.impl.branch;

import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.repo.impl.branch.storage.NodeBranchMetadata;

public class StoreBranchDocument
{
    private final NodeVersion nodeVersion;

    private final NodeBranchMetadata nodeBranchMetadata;

    public StoreBranchDocument( final NodeVersion nodeVersion, final NodeBranchMetadata nodeBranchMetadata )
    {
        this.nodeVersion = nodeVersion;
        this.nodeBranchMetadata = nodeBranchMetadata;
    }

    public NodeVersion getNodeVersion()
    {
        return nodeVersion;
    }

    public NodeBranchMetadata getNodeBranchMetadata()
    {
        return nodeBranchMetadata;
    }
}
