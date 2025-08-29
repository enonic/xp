package com.enonic.xp.repo.impl.node;

import java.time.Instant;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.storage.StoreNodeParams;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static com.enonic.xp.repo.impl.node.NodeConstants.CLOCK;
import static com.enonic.xp.repo.impl.node.NodePermissionsResolver.requireContextUserPermissionOrAdmin;

public class SetRootPermissionsCommand
    extends AbstractNodeCommand
{
    private final AccessControlList permissions;

    private SetRootPermissionsCommand( final Builder builder )
    {
        super( builder );
        this.permissions = builder.permissions;
    }

    public Node execute()
    {
        final Node rootNode = doGetById( Node.ROOT_UUID );

        if ( rootNode == null )
        {
            throw new NodeAccessException( ContextAccessor.current().getAuthInfo().getUser(), NodePath.ROOT, Permission.READ );
        }

        requireContextUserPermissionOrAdmin( Permission.WRITE_PERMISSIONS, rootNode );

        final Node node = Node.create( rootNode )
            .permissions( this.permissions )
            .timestamp( Instant.now( CLOCK ) )
            .build();

        return this.nodeStorageService.store( StoreNodeParams.newVersion( node ), InternalContext.from( ContextAccessor.current() ) )
            .node();
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

        public SetRootPermissionsCommand build()
        {
            return new SetRootPermissionsCommand( this );
        }
    }
}
