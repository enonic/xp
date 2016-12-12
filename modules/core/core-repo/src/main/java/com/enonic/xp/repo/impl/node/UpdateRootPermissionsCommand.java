package com.enonic.xp.repo.impl.node;

import java.time.Instant;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static com.enonic.xp.repo.impl.node.NodePermissionsResolver.requireContextUserPermissionOrAdmin;

public class UpdateRootPermissionsCommand
    extends AbstractNodeCommand
{
    private final AccessControlList permissions;

    private UpdateRootPermissionsCommand( final Builder builder )
    {
        super( builder );
        permissions = builder.permissions;
    }

    public Node execute()
    {
        final Node rootNode = doGetById( Node.ROOT_UUID );

        if ( rootNode == null )
        {
            throw new NodeAccessException( ContextAccessor.current().getAuthInfo().getUser(), NodePath.ROOT, Permission.READ );
        }

        requireContextUserPermissionOrAdmin( Permission.WRITE_PERMISSIONS, rootNode );

        return StoreNodeCommand.create( this ).
            node( Node.create( rootNode ).
                permissions( permissions ).
                timestamp( Instant.now() ).
                build() ).
            build().
            execute();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private AccessControlList permissions;

        private Builder()
        {
        }

        private Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder permissions( final AccessControlList val )
        {
            permissions = val;
            return this;
        }

        public UpdateRootPermissionsCommand build()
        {
            return new UpdateRootPermissionsCommand( this );
        }
    }
}
