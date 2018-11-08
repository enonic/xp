package com.enonic.xp.node;

import java.util.Objects;

import com.google.common.annotations.Beta;

import com.enonic.xp.content.ApplyPermissionsListener;

@Beta
public final class ApplyNodePermissionsParams
{
    private final NodeId nodeId;

    private final boolean overwriteChildPermissions;

    private final ApplyPermissionsListener listener;

    private ApplyNodePermissionsParams( Builder builder )
    {
        nodeId = Objects.requireNonNull( builder.nodeId );
        overwriteChildPermissions = builder.overwriteChildPermissions;
        listener = builder.listener;
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

    public ApplyPermissionsListener getListener()
    {
        return listener;
    }

    public static final class Builder
    {
        private NodeId nodeId;

        private boolean overwriteChildPermissions;

        private ApplyPermissionsListener listener;

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

        public Builder applyPermissionsListener( final ApplyPermissionsListener listener )
        {
            this.listener = listener;
            return this;
        }

        public ApplyNodePermissionsParams build()
        {
            return new ApplyNodePermissionsParams( this );
        }
    }
}
