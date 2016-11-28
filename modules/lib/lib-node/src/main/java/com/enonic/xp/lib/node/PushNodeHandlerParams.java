package com.enonic.xp.lib.node;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodeIds;

public class PushNodeHandlerParams
{
    private NodeIds ids;

    private Branch targetBranch;

    private boolean resolve = false;

    private boolean includeChildren = true;

    private NodeIds exclude;


    public NodeIds getIds()
    {
        return ids;
    }

    public void setIds( final String[] ids )
    {
        this.ids = NodeIds.from( ids );
    }

    public Branch getTargetBranch()
    {
        return targetBranch;
    }

    public void setTargetBranch( final String targetBranch )
    {
        this.targetBranch = Branch.from( targetBranch );
    }

    public boolean isResolve()
    {
        return resolve;
    }

    public void setResolve( final boolean resolve )
    {
        this.resolve = resolve;
    }

    public boolean isIncludeChildren()
    {
        return includeChildren;
    }

    public void setIncludeChildren( final boolean includeChildren )
    {
        this.includeChildren = includeChildren;
    }

    public NodeIds getExclude()
    {
        return exclude;
    }

    public void setExclude( final String[] exclude )
    {
        this.exclude = NodeIds.from( exclude );
    }
}
