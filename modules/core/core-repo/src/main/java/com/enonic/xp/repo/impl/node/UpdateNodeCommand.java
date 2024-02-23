package com.enonic.xp.repo.impl.node;


import java.time.Instant;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.EditableNode;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.binary.BinaryService;
import com.enonic.xp.security.acl.Permission;

import static com.enonic.xp.repo.impl.node.NodeConstants.CLOCK;
import static com.enonic.xp.repo.impl.node.NodePermissionsResolver.requireContextUserPermissionOrAdmin;

public final class UpdateNodeCommand
    extends AbstractNodeCommand
{
    private final UpdateNodeParams params;

    private final BinaryService binaryService;

    private UpdateNodeCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.binaryService = builder.binaryService;
    }

    public Node execute()
    {
        final Node persistedNode = getPersistedNode();

        requireContextUserPermissionOrAdmin( Permission.MODIFY, persistedNode );

        final EditableNode editableNode = new EditableNode( persistedNode );
        params.getEditor().edit( editableNode );

        if ( !persistedNode.getPermissions().equals( editableNode.permissions ) )
        {
            requireContextUserPermissionOrAdmin( Permission.WRITE_PERMISSIONS, persistedNode );
        }

        final AttachedBinaries updatedBinaries = UpdatedAttachedBinariesResolver.create().
            editableNode( editableNode ).
            persistedNode( persistedNode ).
            binaryAttachments( this.params.getBinaryAttachments() ).
            binaryService( this.binaryService ).
            build().
            resolve();

        final Node editedNode = editableNode.build();

        if ( editedNode.equals( persistedNode ) && updatedBinaries.equals( persistedNode.getAttachedBinaries() ) )
        {
            return persistedNode;
        }

        final Node.Builder builder = Node.create( editedNode ).timestamp( Instant.now( CLOCK ) ).attachedBinaries( updatedBinaries );

        final Node updatedNode = builder.build();
        if ( !this.params.isDryRun() )
        {
            final Node storedNode = this.nodeStorageService.store( updatedNode, InternalContext.from( ContextAccessor.current() ) );
            refresh( params.getRefresh() );
            return storedNode;
        }
        else
        {
            return updatedNode;
        }
    }

    private Node getPersistedNode()
    {
        final Node persistedNode;
        if ( params.getId() != null )
        {
            persistedNode = doGetById( params.getId() );
            if ( persistedNode == null )
            {
                throw new NodeNotFoundException( "Cannot update node with id '" + params.getId() + "', node not found" );
            }
        }
        else
        {
            persistedNode = doGetByPath( params.getPath() );
            if ( persistedNode == null )
            {
                throw new NodeNotFoundException( "Cannot update node with path '" + params.getPath() + "', node not found" );
            }
        }
        return persistedNode;
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

        private BinaryService binaryService;

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

        public Builder binaryService( final BinaryService binaryService )
        {
            this.binaryService = binaryService;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.params );
            Preconditions.checkNotNull( this.binaryService );
        }

        public UpdateNodeCommand build()
        {
            this.validate();
            return new UpdateNodeCommand( this );
        }
    }

}
