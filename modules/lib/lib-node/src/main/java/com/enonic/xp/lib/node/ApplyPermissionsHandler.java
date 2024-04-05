package com.enonic.xp.lib.node;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.lib.node.mapper.ApplyPermissionsResultMapper;
import com.enonic.xp.node.ApplyNodePermissionsParams;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.security.acl.AccessControlList;

public class ApplyPermissionsHandler
    extends AbstractNodeHandler
{
    private final NodeKey nodeKey;

    private final AccessControlList permissions;

    private final AccessControlList addPermissions;

    private final AccessControlList removePermissions;

    private final Branches branches;

    private final boolean overwriteChildPermissions;

    private ApplyPermissionsHandler( final Builder builder )
    {
        super( builder );
        this.nodeKey = builder.nodeKey;
        this.permissions = builder.permissions.build();
        this.addPermissions = builder.addPermissions.build();
        this.removePermissions = builder.removePermissions.build();
        this.branches = builder.branches;
        this.overwriteChildPermissions = builder.overwriteChildPermissions;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public ApplyPermissionsResultMapper execute()
    {
        final NodeId nodeId = getNodeId( this.nodeKey );

        return new ApplyPermissionsResultMapper( this.nodeService.applyPermissions( ApplyNodePermissionsParams.create()
                                                                                        .nodeId( nodeId )
                                                                                        .permissions( permissions )
                                                                                        .addPermissions( addPermissions )
                                                                                        .removePermissions( removePermissions )
                                                                                        .overwriteChildPermissions(
                                                                                            overwriteChildPermissions )
                                                                                        .addBranches( branches )
                                                                                        .build() ) );
    }

    public static final class Builder
        extends AbstractNodeHandler.Builder<Builder>
    {
        private final AccessControlList.Builder permissions = AccessControlList.create();

        private final AccessControlList.Builder addPermissions = AccessControlList.create();

        private final AccessControlList.Builder removePermissions = AccessControlList.create();

        private NodeKey nodeKey;

        private Branches branches;

        private boolean overwriteChildPermissions;

        private Builder()
        {
        }

        public Builder permissions( final AccessControlList val )
        {
            if ( val != null )
            {
                permissions.addAll( val );
            }
            return this;
        }

        public Builder addPermissions( final AccessControlList val )
        {
            if ( val != null )
            {
                addPermissions.addAll( val );
            }
            return this;
        }

        public Builder removePermissions( final AccessControlList val )
        {
            if ( val != null )
            {
                removePermissions.addAll( val );
            }
            return this;
        }

        public Builder nodeKey( final NodeKey val )
        {
            nodeKey = val;
            return this;
        }

        public Builder branches( final Branches val )
        {
            branches = val;
            return this;
        }

        public Builder overwriteChildPermissions( final boolean val )
        {
            overwriteChildPermissions = val;
            return this;
        }

        public ApplyPermissionsHandler build()
        {
            return new ApplyPermissionsHandler( this );
        }
    }
}
