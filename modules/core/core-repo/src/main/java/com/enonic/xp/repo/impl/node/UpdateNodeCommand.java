package com.enonic.xp.repo.impl.node;


import java.time.Instant;

import com.google.common.base.Preconditions;

import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.EditableNode;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.repo.impl.blob.BlobStore;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.util.Exceptions;

import static com.enonic.xp.repo.impl.node.NodePermissionsResolver.requireContextUserPermissionOrAdmin;

public final class UpdateNodeCommand
    extends AbstractNodeCommand
{
    private final UpdateNodeParams params;

    private final BlobStore binaryBlobStore;

    private UpdateNodeCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.binaryBlobStore = builder.binaryBlobStore;
    }

    public Node execute()
    {
        try
        {
            return doExecute();
        }
        catch ( final Exception e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    private Node doExecute()
    {
        final Node persistedNode = doGetById( params.getId() );

        if ( persistedNode == null )
        {
            throw new NodeNotFoundException( "Cannot update node with id '" + params.getId() + "', node not found" );
        }
        requireContextUserPermissionOrAdmin( Permission.MODIFY, persistedNode );

        final EditableNode editableNode = new EditableNode( persistedNode );
        params.getEditor().edit( editableNode );

        if ( editableNode.inheritPermissions != persistedNode.inheritsPermissions() ||
            !persistedNode.getPermissions().equals( editableNode.permissions ) )
        {
            requireContextUserPermissionOrAdmin( Permission.WRITE_PERMISSIONS, persistedNode );
        }

        final AttachedBinaries updatedBinaries = UpdatedAttachedBinariesResolver.create().
            editableNode( editableNode ).
            persistedNode( persistedNode ).
            binaryAttachments( this.params.getBinaryAttachments() ).
            binaryBlobStore( this.binaryBlobStore ).
            build().
            resolve();

        final Node editedNode = editableNode.build();

        if ( editedNode.equals( persistedNode ) )
        {
            return persistedNode;
        }

        final Node updatedNode = createUpdatedNode( Node.create( editedNode ).
            timestamp( Instant.now() ).
            attachedBinaries( updatedBinaries ).
            build() );

        if ( !this.params.isDryRun() )
        {
            StoreNodeCommand.create( this ).
                node( updatedNode ).
                build().
                execute();
        }

        return updatedNode;
    }

    private Node createUpdatedNode( final Node editedNode )
    {
        final NodePath parentPath = editedNode.path().getParentPath();
        final AccessControlList permissions =
            evaluatePermissions( parentPath, editedNode.inheritsPermissions(), editedNode.getPermissions() );

        final Node.Builder updateNodeBuilder = Node.create( editedNode ).
            permissions( permissions );
        return updateNodeBuilder.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }


    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private UpdateNodeParams params;

        private BlobStore binaryBlobStore;

        private Builder()
        {
            super();
        }

        private Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder params( final UpdateNodeParams params )
        {
            this.params = params;
            return this;
        }

        public Builder binaryBlobStore( final BlobStore blobStore )
        {
            this.binaryBlobStore = blobStore;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.params );
            Preconditions.checkNotNull( this.binaryBlobStore );
        }

        public UpdateNodeCommand build()
        {
            this.validate();
            return new UpdateNodeCommand( this );
        }
    }

}
