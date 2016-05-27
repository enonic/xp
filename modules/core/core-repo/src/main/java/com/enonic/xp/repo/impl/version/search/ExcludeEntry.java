package com.enonic.xp.repo.impl.version.search;

import com.enonic.xp.node.NodePath;

public class ExcludeEntry
{
    private final NodePath nodePath;

    private final boolean recursive;

    public ExcludeEntry( final NodePath nodePath, final boolean recursive )
    {
        this.nodePath = nodePath;
        this.recursive = recursive;
    }

    public NodePath getNodePath()
    {
        return nodePath;
    }

    public boolean isRecursive()
    {
        return recursive;
    }
}
