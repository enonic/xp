package com.enonic.wem.repo.internal.storage;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodePath;

public class BranchPathCacheKey
    implements CacheKey
{
    private Branch branch;

    private NodePath nodePath;

    public BranchPathCacheKey( final Branch branch, final NodePath nodePath )
    {
        this.branch = branch;
        this.nodePath = nodePath;
    }
}
