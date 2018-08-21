package com.enonic.xp.repo.impl.node;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.codehaus.jparsec.util.Lists;

import com.google.common.base.Preconditions;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.DuplicateNodeParams;
import com.enonic.xp.node.DuplicateValueResolver;
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
    private final DuplicateNodeParams params;

    private final BinaryService binaryService;

    private DuplicateNodeCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.binaryService = builder.binaryService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Node execute()
    {
        final Node existingNode = doGetById( params.getNodeId() );

        if ( existingNode == null )
        {
            throw new NodeNotFoundException( "Cannot duplicate node with id [" + params.getNodeId() + "]" );
        }

        if ( existingNode.isRoot() )
        {
            throw new OperationNotPermittedException( "Not allowed to duplicate root-node" );
        }

        final String newNodeName = resolveNewNodeName( existingNode );

        final CreateNodeParams.Builder createNodeParams = CreateNodeParams.
            from( existingNode ).
            name( newNodeName );

        if ( params.getParent() != null )
        {
            createNodeParams.parent( params.getParent() );
        }

        attachBinaries( existingNode, createNodeParams );
        final CreateNodeParams originalParams = createNodeParams.build();

        final CreateNodeParams processedParams = executeProcessors( existingNode.id(), originalParams );

        final Node duplicatedNode = CreateNodeCommand.create( this ).
            params( processedParams ).
            binaryService( binaryService ).
            build().
            execute();

        nodeDuplicated( 1 );

        final NodeReferenceUpdatesHolder.Builder builder = NodeReferenceUpdatesHolder.create().
            add( existingNode.id(), duplicatedNode.id() );

        if ( params.getIncludeChildren() )
        {
            storeChildNodes( existingNode, duplicatedNode, builder );
        }

        storeDependentNodes( existingNode, duplicatedNode, builder );

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

    private CreateNodeParams executeProcessors( final NodeId originalNodeId, final CreateNodeParams originalParams )
    {
        if ( params.getProcessor() != null )
        {
            return params.getProcessor().process( originalNodeId, originalParams );
        }

        return originalParams;
    }

    private void storeDependentNodes( final Node originalNode, final Node newNode, final NodeReferenceUpdatesHolder.Builder builder )
    {

        final Nodes internalDependencies = FindDependenciesWithinPathCommand.create().
            searchService( this.nodeSearchService ).
            storageService( this.nodeStorageService ).
            indexServiceInternal( this.indexServiceInternal ).
            nodeIds( Collections.singletonMap( params.getNodeId(), params.getDependenciesToDuplicatePath() ) ).
            skipChildren( params.getIncludeChildren() ).
            build().
            execute();

        final List<Node> internalDependenciesList = Lists.arrayList();

        internalDependenciesList.addAll( internalDependencies.getSet() );
        internalDependenciesList.sort( ( o1, o2 ) -> o1.path().elementCount() - o2.path().elementCount() );

        for ( final Node dependency : internalDependenciesList )
        {

            NodePath oldDependencyParentPath, newDependencyParentPath;
            if ( params.getDependenciesToDuplicatePath() != null )
            {
                final int dependencyPathCount = params.getDependenciesToDuplicatePath().elementCount();

                final Optional<NodePath> optionalNewDependencyParentPath = newNode.path().getParentPaths().stream().filter(
                    parentPath -> parentPath.elementCount() == dependencyPathCount ).findFirst();

                newDependencyParentPath =
                    optionalNewDependencyParentPath.isPresent() ? optionalNewDependencyParentPath.get() : newNode.path();
                oldDependencyParentPath = params.getDependenciesToDuplicatePath();
            }
            else
            {
                newDependencyParentPath = newNode.path();
                oldDependencyParentPath = originalNode.path();
            }

            final NodePath newDependencyPath = NodePath.create(
                dependency.path().toString().replace( oldDependencyParentPath.toString(), newDependencyParentPath.toString() ) ).build();

            final Node alreadyExistedDependency = doGetByPath( newDependencyPath );
            if ( alreadyExistedDependency == null )
            {
                this.storeChildNode( dependency, newDependencyPath.getParentPath(), doGetByPath( dependency.parentPath() ).getChildOrder(),
                                     builder );
            }
            else
            {
                builder.add( dependency.id(), alreadyExistedDependency.id() );
            }
        }

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
            final Node newChildNode = this.storeChildNode( node, newParent.path(), originalParent.getChildOrder(), builder );

            storeChildNodes( node, newChildNode, builder );
        }
    }

    private Node storeChildNode( final Node originalChild, final NodePath newParentPath, final ChildOrder parentChildOrder,
                                 final NodeReferenceUpdatesHolder.Builder builder )
    {
        final CreateNodeParams.Builder paramsBuilder = CreateNodeParams.from( originalChild ).
            parent( newParentPath );

        decideInsertStrategy( originalChild, paramsBuilder, parentChildOrder );

        attachBinaries( originalChild, paramsBuilder );

        final CreateNodeParams originalParams = paramsBuilder.build();

        final CreateNodeParams processedParams = executeProcessors( originalChild.id(), originalParams );

        final Node newChildNode = CreateNodeCommand.create( this ).
            params( processedParams ).
            binaryService( this.binaryService ).
            build().
            execute();

        builder.add( originalChild.id(), newChildNode.id() );

        nodeDuplicated( 1 );

        return newChildNode;
    }

    private void decideInsertStrategy( final Node node, final CreateNodeParams.Builder paramsBuilder, final ChildOrder parentChildOrder )
    {
        if ( parentChildOrder.isManualOrder() )
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
        String newNodeName = existingNode.name().toString();

        boolean resolvedUnique = false;

        while ( !resolvedUnique )
        {
            final NodePath parentPath = params.getParent() != null ? params.getParent() : existingNode.parentPath();
            final NodePath checkIfExistsPath = NodePath.create( parentPath, newNodeName ).build();

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
                final DuplicateValueResolver valueResolver =
                    params.getDuplicateValueResolver() != null ? params.getDuplicateValueResolver() : new DuplicateValueResolver();
                newNodeName = valueResolver.name( newNodeName );
            }
        }

        return newNodeName;
    }

    private void nodeDuplicated( final int count )
    {
        if ( params.getDuplicateListener() != null )
        {
            params.getDuplicateListener().nodesDuplicated( count );
        }
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private BinaryService binaryService;

        private DuplicateNodeParams params;

        Builder()
        {
            super();
        }

        public Builder params( final DuplicateNodeParams params )
        {
            this.params = params;
            return this;
        }

        public Builder binaryService( final BinaryService binaryService )
        {
            this.binaryService = binaryService;
            return this;
        }

        public DuplicateNodeCommand build()
        {
            validate();
            return new DuplicateNodeCommand( this );
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.params.getNodeId() );
            Preconditions.checkNotNull( this.binaryService );
        }
    }

}
