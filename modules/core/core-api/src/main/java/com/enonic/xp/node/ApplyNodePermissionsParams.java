package com.enonic.xp.node;

import java.util.Objects;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ApplyPermissionsListener;
import com.enonic.xp.security.acl.AccessControlList;

@PublicApi
public final class ApplyNodePermissionsParams
{
    private final NodeId nodeId;

    private final AccessControlList permissions;

    private final boolean overwriteChildPermissions;

    private final ApplyPermissionsListener listener;

    private final Branches branches;

    private ApplyNodePermissionsParams( Builder builder )
    {
        nodeId = Objects.requireNonNull( builder.nodeId );
        permissions = builder.permissions;
        overwriteChildPermissions = builder.overwriteChildPermissions;
        listener = builder.listener;
        branches = Branches.from( builder.branches.build() );
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

    public boolean isOverwriteChildPermissions()
    {
        return overwriteChildPermissions;
    }

    public ApplyPermissionsListener getListener()
    {
        return listener;
    }

    public Branches getBranches()
    {
        return branches;
    }

    public static final class Builder
    {
        private NodeId nodeId;

        private AccessControlList permissions;

        private boolean overwriteChildPermissions;

        private ApplyPermissionsListener listener;

        private final ImmutableSet.Builder<Branch> branches = ImmutableSet.builder();

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

        public Builder addBranches( final Branches branches )
        {
            this.branches.addAll( branches );
            return this;
        }

        public ApplyNodePermissionsParams build()
        {
            return new ApplyNodePermissionsParams( this );
        }
    }
}
