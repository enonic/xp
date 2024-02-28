package com.enonic.xp.content;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.security.acl.AccessControlList;

public final class PatchContentPermissionsParams
{
    private final NodeId nodeId;

    private final AccessControlList permissions;

    private PatchContentPermissionsParams( final Builder builder )
    {
        nodeId = builder.nodeId;
        permissions = builder.permissions;
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

    public static final class Builder
    {
        private NodeId nodeId;

        private AccessControlList permissions;

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

        public PatchContentPermissionsParams build()
        {
            return new PatchContentPermissionsParams( this );
        }
    }
}
