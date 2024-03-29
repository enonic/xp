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

    private final Branches branches;

    private final boolean overwriteChildPermissions;

    private ApplyPermissionsHandler( final Builder builder )
    {
        super( builder );
        this.nodeKey = builder.nodeKey;
        this.permissions = builder.permissions;
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
                                                                                        .overwriteChildPermissions(
                                                                                            overwriteChildPermissions )
                                                                                        .addBranches( branches )
                                                                                        .build() ) );
    }

    public static final class Builder
        extends AbstractNodeHandler.Builder<Builder>
    {
        private NodeKey nodeKey;

        private AccessControlList permissions;

        private Branches branches;

        private boolean overwriteChildPermissions;

        private Builder()
        {
        }

        public Builder permissions( final AccessControlList val )
        {
            permissions = val;
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
