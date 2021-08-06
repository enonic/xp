package com.enonic.xp.lib.node;

import com.enonic.xp.lib.node.mapper.NodeMapper;
import com.enonic.xp.node.Node;
import com.enonic.xp.security.acl.AccessControlList;

public class SetRootPermissionsHandler
    extends AbstractNodeHandler
{
    private final AccessControlList permissions;

    private final boolean inheritPermissions;

    private SetRootPermissionsHandler( final Builder builder )
    {
        super( builder );
        this.permissions = builder.permissions;
        this.inheritPermissions = builder.inheritPermissions;
    }

    @Override
    public Object execute()
    {
        final Node node = this.nodeService.setRootPermissions( this.permissions, this.inheritPermissions );
        return new NodeMapper( node );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractNodeHandler.Builder<Builder>
    {
        private AccessControlList permissions;

        private boolean inheritPermissions = true;

        private Builder()
        {
        }

        public Builder inheritPermissions( final boolean inheritPermissions )
        {
            this.inheritPermissions = inheritPermissions;
            return this;
        }

        public Builder permissions( final AccessControlList val )
        {
            permissions = val;
            return this;
        }

        public SetRootPermissionsHandler build()
        {
            return new SetRootPermissionsHandler( this );
        }
    }
}
