package com.enonic.xp.repo.impl.node;

import java.time.Instant;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static com.enonic.xp.repo.impl.node.NodeConstants.CLOCK;
import static com.enonic.xp.repo.impl.node.NodePermissionsResolver.requireContextUserPermissionOrAdmin;

public class SetRootPermissionsCommand
    extends AbstractNodeCommand
{
    private final AccessControlList permissions;

    private final boolean inheritPermissions;

    private SetRootPermissionsCommand( final Builder builder )
    {
        super( builder );
        this.permissions = builder.permissions;
        this.inheritPermissions = builder.inheritPermissions;
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
                permissions( this.permissions ).
                inheritPermissions( this.inheritPermissions ).
                timestamp( Instant.now( CLOCK ) ).
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

        private boolean inheritPermissions = true;

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

        public Builder inheritPermissions( final boolean inheritPermissions )
        {
            this.inheritPermissions = inheritPermissions;
            return this;
        }

        public SetRootPermissionsCommand build()
        {
            return new SetRootPermissionsCommand( this );
        }
    }
}
