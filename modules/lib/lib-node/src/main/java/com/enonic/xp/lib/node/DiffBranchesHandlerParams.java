package com.enonic.xp.lib.node;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodeId;

public class DiffBranchesHandlerParams
{
    private NodeId nodeId;

    private Branch targetBranch;

    private boolean includeChildren = true;

    public void setNodeId( final String nodeId )
    {
        this.nodeId = NodeId.from( nodeId );
    }

    public void setTargetBranch( final String targetBranch )
    {
        this.targetBranch = Branch.from( targetBranch );
    }

    public void setIncludeChildren( final boolean includeChildren )
    {
        this.includeChildren = includeChildren;
    }

    public NodeId getNodeId()
    {
        return nodeId;
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
