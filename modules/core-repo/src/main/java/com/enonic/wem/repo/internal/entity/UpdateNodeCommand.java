package com.enonic.wem.repo.internal.entity;


import java.time.Instant;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.node.AttachedBinaries;
import com.enonic.wem.api.node.EditableNode;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.UpdateNodeParams;
import com.enonic.wem.api.security.acl.AccessControlList;
import com.enonic.wem.api.util.Exceptions;
import com.enonic.wem.repo.internal.blob.BlobStore;

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
        final Node persistedNode = doGetById( params.getId(), false );

        final EditableNode editableNode = new EditableNode( persistedNode );
        params.getEditor().edit( editableNode );

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

        final Node updatedNode = createUpdatedNode( Node.newNode( editedNode ).
            attachedBinaries( updatedBinaries ).
            build() );

        doStoreNode( updatedNode );

        final Node nodeWithHashChildrenSet = NodeHasChildResolver.create().
            queryService( this.queryService ).
            build().
            resolve( updatedNode );
        return nodeWithHashChildrenSet;
    }

    private Node createUpdatedNode( final Node editedNode )
    {
        final Instant now = Instant.now();

        final NodePath parentPath = editedNode.path().getParentPath();
        final AccessControlList permissions =
            evaluatePermissions( parentPath, editedNode.inheritsPermissions(), editedNode.getPermissions() );

        final Node.Builder updateNodeBuilder = Node.newNode( editedNode ).
            modifiedTime( now ).
            modifier( getCurrentPrincipalKey() ).
            permissions( permissions );
        return updateNodeBuilder.build();
    }

    public static Builder create()
    {
        return new Builder();
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
