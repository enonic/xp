package com.enonic.xp.repo.impl.branch.storage;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodeId;

public final class BranchDocumentId
{
    private static final String SEPARATOR = "_";

    public static String asString( final NodeId nodeId, final Branch branch )
    {
        return nodeId + SEPARATOR + branch;
    }
}
