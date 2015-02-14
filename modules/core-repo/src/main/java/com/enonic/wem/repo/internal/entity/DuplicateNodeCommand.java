package com.enonic.wem.repo.internal.entity;

import com.google.common.base.Preconditions;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.util.Reference;
import com.enonic.wem.repo.internal.blob.BlobStore;
import com.enonic.wem.repo.internal.index.query.QueryService;

public class DuplicateNodeCommand
    extends AbstractNodeCommand
{
    private final NodeId nodeId;

    private final BlobStore binaryBlobStore;

    private DuplicateNodeCommand( final Builder builder )
    {
        super( builder );
        this.nodeId = builder.id;
        this.binaryBlobStore = builder.binaryBlobStore;
    }

    public Node execute()
    {
        final Node existingNode = doGetById( nodeId, false );

        final String newNodeName = resolveNewNodeName( existingNode );

        final CreateNodeParams createNodeParams = CreateNodeParams.from( existingNode ).
            name( newNodeName ).
            build();

        final Node duplicatedNode = doCreateNode( createNodeParams, this.binaryBlobStore );

        final NodeReferenceUpdatesHolder.Builder builder = NodeReferenceUpdatesHolder.create().
            add( existingNode.id(), duplicatedNode.id() );

        storeChildNodes( existingNode, duplicatedNode, builder );

        final NodeReferenceUpdatesHolder nodesToBeUpdated = builder.build();

        updateNodeReferences( duplicatedNode, nodesToBeUpdated );
        updateChildReferences( duplicatedNode, nodesToBeUpdated );

        return duplicatedNode;
    }

    private void storeChildNodes( final Node originalParent, final Node newParent, final NodeReferenceUpdatesHolder.Builder builder )
    {
        final FindNodesByParentResult findNodesByParentResult = doFindNodesByParent( FindNodesByParentParams.create().
            parentPath( originalParent.path() ).
            from( 0 ).
            size( QueryService.GET_ALL_SIZE_FLAG ).
            build() );

        for ( final Node node : findNodesByParentResult.getNodes() )
        {
            final CreateNodeParams.Builder paramsBuilder = CreateNodeParams.from( node ).
                parent( newParent.path() );

            attachBinaries( node, paramsBuilder );

            final Node newChildNode = this.doCreateNode( paramsBuilder.build(), this.binaryBlobStore );

            builder.add( node.id(), newChildNode.id() );

            storeChildNodes( node, newChildNode, builder );
        }
    }

    private void attachBinaries( final Node node, final CreateNodeParams.Builder paramsBuilder )
    {
        for ( final AttachedBinary attachedBinary : node.getAttachedBinaries() )
        {
            paramsBuilder.attachBinary( attachedBinary.getBinaryReference(), binaryBlobStore.getByteSource( attachedBinary.getBlobKey() ) );
        }
    }

    private void updateChildReferences( final Node duplicatedParent, final NodeReferenceUpdatesHolder nodeReferenceUpdatesHolder )
    {
        final FindNodesByParentResult findNodesByParentResult = doFindNodesByParent( FindNodesByParentParams.create().
            parentPath( duplicatedParent.path() ).
            from( 0 ).
            size( QueryService.GET_ALL_SIZE_FLAG ).
            build() );

        for ( final Node node : findNodesByParentResult.getNodes() )
        {
            updateNodeReferences( node, nodeReferenceUpdatesHolder );
            updateChildReferences( node, nodeReferenceUpdatesHolder );
        }
    }

    private void updateNodeReferences( final Node node, final NodeReferenceUpdatesHolder nodeReferenceUpdatesHolder )
    {
        final PropertyTree data = node.data();

        boolean changes = false;

        for ( final Property property : node.data().getByValueType( ValueTypes.REFERENCE ) )
        {
            final Reference reference = property.getReference();
            if ( reference != null && nodeReferenceUpdatesHolder.mustUpdate( reference ) )
            {
                changes = true;
                data.setReference( property.getPath(), nodeReferenceUpdatesHolder.getNewReference( reference ) );
            }
        }

        if ( changes )
        {
            doUpdateNode( UpdateNodeParams.create().
                id( node.id() ).
                editor( toBeEdited -> toBeEdited.data = data ).
                build(), this.binaryBlobStore );
        }
    }

    private String resolveNewNodeName( final Node existingNode )
    {
        String newNodeName = DuplicateValueResolver.name( existingNode.name() );

        boolean resolvedUnique = false;

        while ( !resolvedUnique )
        {
            final NodePath checkIfExistsPath = NodePath.newNodePath( existingNode.parentPath(), newNodeName ).build();
            Node foundNode = this.doGetByPath( checkIfExistsPath, false );

            if ( foundNode == null )
            {
                resolvedUnique = true;
            }
            else
            {
                newNodeName = DuplicateValueResolver.name( newNodeName );
            }
        }

        return newNodeName;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeId id;

        private BlobStore binaryBlobStore;

        Builder()
        {
            super();
        }

        public Builder id( final NodeId nodeId )
        {
            this.id = nodeId;
            return this;
        }

        public DuplicateNodeCommand build()
        {
            validate();
            return new DuplicateNodeCommand( this );
        }

        public Builder binaryBlobStore( final BlobStore blobStore )
        {
            this.binaryBlobStore = blobStore;
            return this;
        }

        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.id );
            Preconditions.checkNotNull( this.binaryBlobStore );
        }
    }

}
