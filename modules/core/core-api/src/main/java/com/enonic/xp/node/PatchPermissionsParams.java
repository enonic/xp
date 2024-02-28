package com.enonic.xp.node;

import com.enonic.xp.security.acl.AccessControlList;

public final class PatchPermissionsParams
{
    private final NodeId nodeId;

    private final AccessControlList permissions;

    private final boolean overwriteChildrenPermissions;

    private PatchPermissionsParams( final Builder builder )
    {
        nodeId = builder.nodeId;
        permissions = builder.permissions;
        overwriteChildrenPermissions = builder.overwriteChildrenPermissions;
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

    public boolean isOverwriteChildrenPermissions()
    {
        return overwriteChildrenPermissions;
    }

    public static final class Builder
    {
        private NodeId nodeId;

        private AccessControlList permissions;

        private boolean overwriteChildrenPermissions;

        private Builder()
        {
        }

        public Builder nodeId( final NodeId val )
        {
            nodeId = val;
            return this;
        }

        public Builder permissions( final AccessControlList val )
        {
            permissions = val;
            return this;
        }

        public Builder overwriteChildrenPermissions( final boolean val )
        {
            overwriteChildrenPermissions = val;
            return this;
        }

        public PatchPermissionsParams build()
        {
            return new PatchPermissionsParams( this );
        }
    }
}
