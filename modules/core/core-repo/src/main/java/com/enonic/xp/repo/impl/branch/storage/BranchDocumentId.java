package com.enonic.xp.repo.impl.branch.storage;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.storage.RoutableId;

public final class BranchDocumentId
{
    private static final String SEPARATOR = "_";

    public static String asString( final NodeId nodeId, final Branch branch )
    {
        return nodeId + SEPARATOR + branch;
    }

    public static RoutableId asRoutableId( final NodeId nodeId, final Branch branch )
    {
        return new RoutableId( asString( nodeId, branch ), nodeId.toString() );
    }
}
