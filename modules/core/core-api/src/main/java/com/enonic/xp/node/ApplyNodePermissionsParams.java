package com.enonic.xp.node;

import java.util.Objects;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.security.acl.AccessControlList;

@PublicApi
public final class ApplyNodePermissionsParams
{
    private final NodeId nodeId;

    private final AccessControlList permissions;

    private final AccessControlList addPermissions;

    private final AccessControlList removePermissions;

    private final ApplyPermissionsScope scope;

    private final Attributes versionAttributes;

    private final ApplyNodePermissionsListener listener;

    private final Branches branches;

    private ApplyNodePermissionsParams( Builder builder )
    {
        nodeId = Objects.requireNonNull( builder.nodeId );
        scope = Objects.requireNonNullElse( builder.scope, ApplyPermissionsScope.SINGLE );
        permissions = builder.permissions.build();
        addPermissions = builder.addPermissions.build();
        removePermissions = builder.removePermissions.build();
        versionAttributes = builder.versionAttributes;
        listener = builder.listener;
        branches = Objects.requireNonNullElse( builder.branches, Branches.empty() );

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

    public ApplyPermissionsScope getScope()
    {
        return scope;
    }

    public Attributes getVersionAttributes()
    {
        return versionAttributes;
    }

    public ApplyNodePermissionsListener getListener()
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

        private ApplyPermissionsScope scope;

        private Attributes versionAttributes;

        private ApplyNodePermissionsListener listener;

        private Branches branches;

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

        public Builder scope( final ApplyPermissionsScope scope )
        {
            this.scope = scope;
            return this;
        }

        public Builder applyPermissionsListener( final ApplyNodePermissionsListener listener )
        {
            this.listener = listener;
            return this;
        }

        public Builder branches( final Branches branches )
        {
            this.branches = branches;
            return this;
        }

        public Builder versionAttributes( final Attributes versionAttributes )
        {
            this.versionAttributes = versionAttributes;
            return this;
        }

        public ApplyNodePermissionsParams build()
        {
            return new ApplyNodePermissionsParams( this );
        }
    }
}
