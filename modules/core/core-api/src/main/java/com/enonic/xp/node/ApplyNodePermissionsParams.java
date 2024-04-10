package com.enonic.xp.node;

import java.util.Objects;

import com.google.common.base.Preconditions;
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

    private final AccessControlList addPermissions;

    private final AccessControlList removePermissions;

    private final ApplyPermissionsMode mode;

    private final ApplyPermissionsListener listener;

    private final Branches branches;

    private ApplyNodePermissionsParams( Builder builder )
    {
        nodeId = Objects.requireNonNull( builder.nodeId );
        mode = Objects.requireNonNullElse( builder.mode, ApplyPermissionsMode.SINGLE );
        permissions = builder.permissions.build();
        addPermissions = builder.addPermissions.build();
        removePermissions = builder.removePermissions.build();
        listener = builder.listener;
        branches = Branches.from( builder.branches.build() );

        Preconditions.checkArgument( permissions.isEmpty() || ( addPermissions.isEmpty() && removePermissions.isEmpty() ),
                                     "Permissions cannot be set together with addPermissions or removePermissions" );
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

    public AccessControlList getAddPermissions()
    {
        return addPermissions;
    }

    public AccessControlList getRemovePermissions()
    {
        return removePermissions;
    }

    @Deprecated
    public boolean isOverwriteChildPermissions()
    {
        return false;
    }

    public ApplyPermissionsMode getMode()
    {
        return mode;
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

        private final AccessControlList.Builder permissions = AccessControlList.create();

        private final AccessControlList.Builder addPermissions = AccessControlList.create();

        private final AccessControlList.Builder removePermissions = AccessControlList.create();

        private ApplyPermissionsMode mode;

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
            if ( permissions != null )
            {
                this.permissions.addAll( permissions.getEntries() );
            }
            return this;
        }

        public Builder addPermissions( final AccessControlList permissions )
        {
            if ( permissions != null )
            {
                this.addPermissions.addAll( permissions.getEntries() );
            }
            return this;
        }

        public Builder removePermissions( final AccessControlList permissions )
        {
            if ( permissions != null )
            {
                this.removePermissions.addAll( permissions.getEntries() );
            }
            return this;
        }

        @Deprecated
        public Builder overwriteChildPermissions( final boolean overwriteChildPermissions )
        {
            return this;
        }

        public Builder mode( final ApplyPermissionsMode mode )
        {
            this.mode = mode;
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
