package com.enonic.xp.lib.node;

import com.enonic.xp.branch.Branch;

public class DiffBranchesHandlerParams
{
    private NodeKey key;

    private Branch targetBranch;

    private boolean includeChildren = true;

    public void setKey( final String key )
    {
        this.key = NodeKey.from( key );
    }

    public void setTargetBranch( final String targetBranch )
    {
        this.targetBranch = Branch.from( targetBranch );
    }

    public void setIncludeChildren( final boolean includeChildren )
    {
        this.includeChildren = includeChildren;
    }

    public NodeKey getKey()
    {
        return key;
    }

    public Branch getTargetBranch()
    {
        return targetBranch;
    }

    public boolean isIncludeChildren()
    {
        return includeChildren;
    }
}
