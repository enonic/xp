package com.enonic.xp.node;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.content.ApplyPermissionsListener;
import com.enonic.xp.security.acl.AccessControlList;

@PublicApi
public final class ApplyNodePermissionsParams
{
    private final NodeId nodeId;

    private final AccessControlList permissions;

    private final boolean inheritPermissions;

    private final boolean overwriteChildPermissions;

    private final ApplyPermissionsListener listener;

    private ApplyNodePermissionsParams( Builder builder )
    {
        nodeId = Objects.requireNonNull( builder.nodeId );
        permissions = builder.permissions;
        inheritPermissions = builder.inheritPermissions;
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

    public AccessControlList getPermissions()
    {
        return permissions;
    }

    public boolean isInheritPermissions()
    {
        return inheritPermissions;
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

        private AccessControlList permissions;

        private boolean inheritPermissions;

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

        public Builder permissions( final AccessControlList permissions )
        {
            this.permissions = permissions;
            return this;
        }

        public Builder inheritPermissions( final boolean inheritPermissions )
        {
            this.inheritPermissions = inheritPermissions;
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
