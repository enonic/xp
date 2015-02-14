package com.enonic.xp.node;

import java.util.Objects;

import com.enonic.xp.security.PrincipalKey;

public final class ApplyNodePermissionsParams
{
    private final NodeId nodeId;

    private final boolean overwriteChildPermissions;

    private final PrincipalKey modifier;

    private ApplyNodePermissionsParams( Builder builder )
    {
        nodeId = Objects.requireNonNull( builder.nodeId );
        modifier = Objects.requireNonNull( builder.modifier );
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

    public PrincipalKey getModifier()
    {
        return modifier;
    }

    public static final class Builder
    {
        private NodeId nodeId;

        private boolean overwriteChildPermissions;

        private PrincipalKey modifier;

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

        public Builder modifier( final PrincipalKey modifier )
        {
            this.modifier = modifier;
            return this;
        }

        public ApplyNodePermissionsParams build()
        {
            return new ApplyNodePermissionsParams( this );
        }
    }
}
