package com.enonic.xp.repo.impl.branch.storage;

import java.util.Collection;

import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeId;

interface BranchEntriesExecutorMethod
{
    void execute( final Collection<NodeId> nodeIds, final NodeBranchEntries.Builder builder );
}
