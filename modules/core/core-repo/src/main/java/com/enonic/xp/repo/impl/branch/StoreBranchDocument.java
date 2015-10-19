package com.enonic.xp.repo.impl.branch;

import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.repo.impl.branch.storage.BranchNodeVersion;

public class StoreBranchDocument
{
    private final NodeVersion nodeVersion;

    private final BranchNodeVersion branchNodeVersion;

    public StoreBranchDocument( final NodeVersion nodeVersion, final BranchNodeVersion branchNodeVersion )
    {
        this.nodeVersion = nodeVersion;
        this.branchNodeVersion = branchNodeVersion;
    }

    public NodeVersion getNodeVersion()
    {
        return nodeVersion;
    }

    public BranchNodeVersion getBranchNodeVersion()
    {
        return branchNodeVersion;
    }
}
