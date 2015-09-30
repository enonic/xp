package com.enonic.xp.repo.impl.version;

import com.google.common.base.Preconditions;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeVersionId;

public class NodeVersionDocumentId
{
    private static final String SEPARATOR = "_";

    private final String value;

    public NodeVersionDocumentId( final NodeId nodeId, final NodeVersionId nodeVersionId )
    {
        Preconditions.checkNotNull( nodeId );
        Preconditions.checkNotNull( nodeVersionId );

        this.value = nodeId + SEPARATOR + nodeVersionId.toString();
    }

    @Override
    public String toString()
    {
        return value;
    }

}
