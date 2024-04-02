package com.enonic.xp.lib.node;

import com.enonic.xp.lib.node.mapper.NodeMapper;
import com.enonic.xp.node.Node;
import com.enonic.xp.security.acl.AccessControlList;

@Deprecated
public class SetRootPermissionsHandler
    extends AbstractNodeHandler
{
    private final AccessControlList permissions;

    private SetRootPermissionsHandler( final Builder builder )
    {
        super( builder );
        this.permissions = builder.permissions;
    }

    @Override
    public Object execute()
    {
        final Node node = this.nodeService.setRootPermissions( this.permissions );
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

        private Builder()
        {
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
