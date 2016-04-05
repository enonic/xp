package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.InsertManualStrategy;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.repo.impl.blob.BlobKey;
import com.enonic.xp.repo.impl.blob.BlobStore;
import com.enonic.xp.repo.impl.search.SearchService;
import com.enonic.xp.util.Reference;

public final class DuplicateNodeCommand
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
        final Node existingNode = doGetById( nodeId );

        final String newNodeName = resolveNewNodeName( existingNode );

        final CreateNodeParams.Builder createNodeParams = CreateNodeParams.from( existingNode ).
            name( newNodeName );
        attachBinaries( existingNode, createNodeParams );

        final Node duplicatedNode = CreateNodeCommand.create( this ).
            params( createNodeParams.build() ).
            binaryBlobStore( binaryBlobStore ).
            build().
            execute();

        final NodeReferenceUpdatesHolder.Builder builder = NodeReferenceUpdatesHolder.create().
            add( existingNode.id(), duplicatedNode.id() );

        storeChildNodes( existingNode, duplicatedNode, builder );

        final NodeReferenceUpdatesHolder nodesToBeUpdated = builder.build();

        RefreshCommand.create().
            refreshMode( RefreshMode.SEARCH ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();

        updateNodeReferences( duplicatedNode, nodesToBeUpdated );
        updateChildReferences( duplicatedNode, nodesToBeUpdated );

        return duplicatedNode;
    }

    private void storeChildNodes( final Node originalParent, final Node newParent, final NodeReferenceUpdatesHolder.Builder builder )
    {
        final FindNodesByParentResult findNodesByParentResult = doFindNodesByParent( FindNodesByParentParams.create().
            parentPath( originalParent.path() ).
            from( 0 ).
            size( SearchService.GET_ALL_SIZE_FLAG ).
            build() );

        for ( final Node node : findNodesByParentResult.getNodes() )
        {
            final CreateNodeParams.Builder paramsBuilder = CreateNodeParams.from( node ).
                parent( newParent.path() );

            decideInsertStrategy( originalParent, node, paramsBuilder );

            attachBinaries( node, paramsBuilder );

            final Node newChildNode = CreateNodeCommand.create( this ).
                params( paramsBuilder.build() ).
                binaryBlobStore( binaryBlobStore ).
                build().
                execute();

            builder.add( node.id(), newChildNode.id() );

            storeChildNodes( node, newChildNode, builder );
        }
    }

    private void decideInsertStrategy( final Node originalParent, final Node node, final CreateNodeParams.Builder paramsBuilder )
    {
        if ( originalParent.getChildOrder().isManualOrder() )
        {
            paramsBuilder.manualOrderValue( node.getManualOrderValue() ).
                insertManualStrategy( InsertManualStrategy.MANUAL );
        }
    }

    private void attachBinaries( final Node node, final CreateNodeParams.Builder paramsBuilder )
    {
        for ( final AttachedBinary attachedBinary : node.getAttachedBinaries() )
        {
            paramsBuilder.attachBinary( attachedBinary.getBinaryReference(),
                                        binaryBlobStore.getRecord( new BlobKey( attachedBinary.getBlobKey() ) ).getBytes() );
        }
    }

    private void updateChildReferences( final Node duplicatedParent, final NodeReferenceUpdatesHolder nodeReferenceUpdatesHolder )
    {
        final FindNodesByParentResult findNodesByParentResult = doFindNodesByParent( FindNodesByParentParams.create().
            parentPath( duplicatedParent.path() ).
            from( 0 ).
            size( SearchService.GET_ALL_SIZE_FLAG ).
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

        for ( final Property property : node.data().getProperties( ValueTypes.REFERENCE ) )
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
            UpdateNodeCommand.create( this ).
                params( UpdateNodeParams.create().
                    id( node.id() ).
                    editor( toBeEdited -> toBeEdited.data = data ).
                    build() ).
                binaryBlobStore( binaryBlobStore ).
                build().
                execute();
        }
    }

    private String resolveNewNodeName( final Node existingNode )
    {
        String newNodeName = DuplicateValueResolver.name( existingNode.name() );

        boolean resolvedUnique = false;

        while ( !resolvedUnique )
        {
            final NodePath checkIfExistsPath = NodePath.create( existingNode.parentPath(), newNodeName ).build();

            final boolean exists = CheckNodeExistsCommand.create( this ).
                nodePath( checkIfExistsPath ).
                build().
                execute();

            if ( !exists )
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

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.id );
            Preconditions.checkNotNull( this.binaryBlobStore );
        }
    }

}
