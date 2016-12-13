package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.DuplicateNodeProcessor;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.InsertManualStrategy;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.OperationNotPermittedException;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.repo.impl.binary.BinaryService;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.util.Reference;

public final class DuplicateNodeCommand
    extends AbstractNodeCommand
{
    private final NodeId nodeId;

    private final BinaryService binaryService;

    private final DuplicateNodeProcessor processor;

    private DuplicateNodeCommand( final Builder builder )
    {
        super( builder );
        this.nodeId = builder.id;
        this.binaryService = builder.binaryService;
        this.processor = builder.processor;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Node execute()
    {
        final Node existingNode = doGetById( nodeId );

        if ( existingNode == null )
        {
            throw new NodeNotFoundException( "cannot duplicate node with id [" + nodeId + "]" );
        }

        if ( existingNode.isRoot() )
        {
            throw new OperationNotPermittedException( "Not allowed to duplicate root-node" );
        }

        final String newNodeName = resolveNewNodeName( existingNode );

        final CreateNodeParams.Builder createNodeParams = CreateNodeParams.from( existingNode ).
            name( newNodeName );
        attachBinaries( existingNode, createNodeParams );
        final CreateNodeParams originalParams = createNodeParams.build();

        final CreateNodeParams processedParams = executeProcessors( originalParams );

        final Node duplicatedNode = CreateNodeCommand.create( this ).
            params( processedParams ).
            binaryService( binaryService ).
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

    private CreateNodeParams executeProcessors( final CreateNodeParams originalParams )
    {
        if ( this.processor != null )
        {
            return processor.process( originalParams );
        }

        return originalParams;
    }

    private void storeChildNodes( final Node originalParent, final Node newParent, final NodeReferenceUpdatesHolder.Builder builder )
    {
        final FindNodesByParentResult findNodesByParentResult = doFindNodesByParent( FindNodesByParentParams.create().
            parentPath( originalParent.path() ).
            from( 0 ).
            size( NodeSearchService.GET_ALL_SIZE_FLAG ).
            build() );

        final Nodes children = GetNodesByIdsCommand.create( this ).
            ids( findNodesByParentResult.getNodeIds() ).
            build().
            execute();

        for ( final Node node : children )
        {
            final CreateNodeParams.Builder paramsBuilder = CreateNodeParams.from( node ).
                parent( newParent.path() );

            decideInsertStrategy( originalParent, node, paramsBuilder );

            attachBinaries( node, paramsBuilder );

            final CreateNodeParams originalParams = paramsBuilder.build();

            final CreateNodeParams processedParams = executeProcessors( originalParams );

            final Node newChildNode = CreateNodeCommand.create( this ).
                params( processedParams ).
                binaryService( this.binaryService ).
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
            paramsBuilder.attachBinary( attachedBinary.getBinaryReference(), this.binaryService.get( attachedBinary ) );
        }
    }

    private void updateChildReferences( final Node duplicatedParent, final NodeReferenceUpdatesHolder nodeReferenceUpdatesHolder )
    {
        final FindNodesByParentResult findNodesByParentResult = doFindNodesByParent( FindNodesByParentParams.create().
            parentPath( duplicatedParent.path() ).
            from( 0 ).
            size( NodeSearchService.GET_ALL_SIZE_FLAG ).
            build() );

        final Nodes children = GetNodesByIdsCommand.create( this ).
            ids( findNodesByParentResult.getNodeIds() ).
            build().
            execute();

        for ( final Node node : children )
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
                binaryService( this.binaryService ).
                build().
                execute();
        }
    }

    private String resolveNewNodeName( final Node existingNode )
    {
        // Process as file name as it is so for images
        String newNodeName = DuplicateValueResolver.fileName( existingNode.name().toString() );

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

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeId id;

        private BinaryService binaryService;

        private DuplicateNodeProcessor processor;

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

        public Builder binaryService( final BinaryService binaryService )
        {
            this.binaryService = binaryService;
            return this;
        }

        public Builder processor( final DuplicateNodeProcessor processor )
        {
            this.processor = processor;
            return this;
        }


        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.id );
            Preconditions.checkNotNull( this.binaryService );
        }
    }

}
