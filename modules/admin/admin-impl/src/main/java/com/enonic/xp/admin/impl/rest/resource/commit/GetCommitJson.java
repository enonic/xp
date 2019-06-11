package com.enonic.xp.admin.impl.rest.resource.commit;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.node.NodeId;

public final class GetCommitJson
{
    private final NodeId nodeId;

    private final NodeCommitId nodeCommitId;

    public GetCommitJson( @JsonProperty("nodeId") final String nodeId, @JsonProperty("nodeCommitId") final String nodeCommitId )
    {

        this.nodeId = nodeId != null ? NodeId.from( nodeId ) : null;
        this.nodeCommitId = nodeCommitId != null ? NodeCommitId.from( nodeCommitId ) : null;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public NodeCommitId getNodeCommitId()
    {
        return nodeCommitId;
    }
}
