package com.enonic.xp.node;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class PushNodeResult
{
    private final NodeId nodeId;

    private final NodePath nodePath;

    private final NodeVersionId nodeVersionId;

    private final NodePath targetPath;

    private final Reason failureReason;

    private PushNodeResult( final NodeId nodeId, final NodePath nodePath, final NodeVersionId nodeVersionId, final NodePath targetPath,
                           final Reason failureReason )
    {
        this.nodeId = nodeId;
        this.nodeVersionId = nodeVersionId;
        this.nodePath = nodePath;
        this.targetPath = targetPath;
        this.failureReason = failureReason;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public NodeVersionId getNodeVersionId()
    {
        return nodeVersionId;
    }

    public NodePath getNodePath()
    {
        return nodePath;
    }

    public NodePath getTargetPath()
    {
        return targetPath;
    }

    public Reason getFailureReason()
    {
        return failureReason;
    }

    public static PushNodeResult success( final NodeId nodeId, final NodeVersionId nodeVersionId, final NodePath nodePath,
                                          final NodePath targetPath )
    {
        return new PushNodeResult( nodeId, nodePath, nodeVersionId, targetPath, null );
    }

    public static PushNodeResult failure( final NodeId nodeId, final NodePath nodePath, final Reason failureReason )
    {
        return new PushNodeResult( nodeId, nodePath, null, null, Objects.requireNonNull( failureReason ) );
    }

    public enum Reason
    {
        ALREADY_EXIST, PARENT_NOT_FOUND, ACCESS_DENIED
    }
}
