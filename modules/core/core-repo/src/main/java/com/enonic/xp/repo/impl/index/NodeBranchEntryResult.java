package com.enonic.xp.repo.impl.index;

import java.util.List;

import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.repo.impl.node.executor.ExecutorCommandResult;

class NodeBranchEntryResult
    implements ExecutorCommandResult<List<NodeBranchEntry>>
{
    private final List<NodeBranchEntry> nodeBranchEntries;

    public NodeBranchEntryResult( final List<NodeBranchEntry> nodeBranchEntries )
    {
        this.nodeBranchEntries = nodeBranchEntries;
    }

    @Override
    public boolean isEmpty()
    {
        return nodeBranchEntries.size() == 0;
    }

    @Override
    public List<NodeBranchEntry> get()
    {
        return this.nodeBranchEntries;
    }
}
