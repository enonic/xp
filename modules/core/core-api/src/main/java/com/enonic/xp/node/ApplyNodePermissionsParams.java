package com.enonic.xp.node;

import java.util.Objects;

import com.google.common.annotations.Beta;

@Beta
public final class ApplyNodePermissionsParams
{
    private final NodeId nodeId;

    private final boolean overwriteChildPermissions;

    private ApplyNodePermissionsParams( Builder builder )
    {
        nodeId = Objects.requireNonNull( builder.nodeId );
        overwriteChildPermissions = builder.overwriteChildPermissions;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public boolean isOverwriteChildPermissions()
    {
        return overwriteChildPermissions;
    }


    public static final class Builder
    {
        private NodeId nodeId;

        private boolean overwriteChildPermissions;

        private Builder()
        {
        }

        public Builder nodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder overwriteChildPermissions( final boolean overwriteChildPermissions )
        {
            this.overwriteChildPermissions = overwriteChildPermissions;
            return this;
        }

        public ApplyNodePermissionsParams build()
        {
            return new ApplyNodePermissionsParams( this );
        }
    }
}
