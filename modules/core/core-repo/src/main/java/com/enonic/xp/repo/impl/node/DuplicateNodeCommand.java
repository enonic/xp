package com.enonic.xp.repo.impl.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.DuplicateNodeListener;
import com.enonic.xp.node.DuplicateNodeParams;
import com.enonic.xp.node.DuplicateNodeResult;
import com.enonic.xp.node.InsertManualStrategy;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.OperationNotPermittedException;
import com.enonic.xp.node.PatchNodeParams;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeBranchEntry;
import com.enonic.xp.repo.impl.binary.BinaryService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.util.Reference;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

public final class DuplicateNodeCommand
    extends AbstractNodeCommand
{
    private static final Logger LOG = LoggerFactory.getLogger( DuplicateNodeCommand.class );

    private final DuplicateNodeParams params;

    private final BinaryService binaryService;

    private final DuplicateNodeResult.Builder result;

    private final DuplicateNodeListener listener;

    private DuplicateNodeCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.binaryService = builder.binaryService;
        this.result = DuplicateNodeResult.create();
        this.listener = requireNonNullElse( params.getDuplicateListener(), NoopDuplicateNodeListener.INSTANCE );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public DuplicateNodeResult execute()
    {
        final Node existingNode = getExistingNode();

        listener.resolved( 1 );

        final Node duplicatedNode = doDuplicateNode( existingNode );

        result.node( duplicatedNode );
        listener.nodesDuplicated( 1 );

        final NodeReferenceUpdatesHolder.Builder builder =
            NodeReferenceUpdatesHolder.create().add( existingNode.id(), duplicatedNode.id() );

        final List<Node> createdChildren = new ArrayList<>();

        if ( params.getIncludeChildren() )
        {
            storeChildNodes( existingNode, duplicatedNode, builder, createdChildren );
        }

        final NodeReferenceUpdatesHolder nodesToBeUpdated = builder.build();

        updateNodeReferences( duplicatedNode, nodesToBeUpdated );
        for ( final Node createdChild : createdChildren )
        {
            updateNodeReferences( createdChild, nodesToBeUpdated );
        }

        refresh( params.getRefresh() );
        return result.build();
    }

    private Node getExistingNode()
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

        return existingNode;
    }

    private Node doDuplicateNode( final Node existingNode )
    {
        final CreateNodeParams.Builder paramsBuilder = CreateNodeParams.from( existingNode );
        attachBinaries( existingNode, paramsBuilder );

        if ( params.getName() != null )
        {
            paramsBuilder.name( params.getName() );
        }
        if ( params.getParent() != null )
        {
            paramsBuilder.parent( params.getParent() );
        }

        paramsBuilder.versionAttributesResolver( params.getVersionAttributesResolver() );

        if ( params.getName() != null || params.getParent() != null )
        {
            final CreateNodeParams processedParams = executeProcessors( paramsBuilder.build() );

            return CreateNodeCommand.create( this ).params( processedParams ).binaryService( binaryService ).build().execute();
        }

        return doDuplicateNode( existingNode, paramsBuilder );
    }

    private Node doDuplicateNode( final Node existingNode, final CreateNodeParams.Builder paramsBuilder )
    {
        Node duplicatedNode = null;
        String newNodeName = existingNode.name().toString();
        do
        {
            try
            {
                newNodeName = DuplicateValueResolver.name( newNodeName );
                final CreateNodeParams processedParams = executeProcessors( paramsBuilder.name( newNodeName ).build() );

                duplicatedNode =
                    CreateNodeCommand.create( this ).params( processedParams ).binaryService( binaryService ).build().execute();
            }
            catch ( NodeAlreadyExistAtPathException e )
            {
                // try again with other name
                LOG.debug( "[{}] node with [{}] parent already exist.", newNodeName, existingNode.parentPath(), e );
            }
        }
        while ( duplicatedNode == null );

        return duplicatedNode;
    }

    private CreateNodeParams executeProcessors( final CreateNodeParams originalParams )
    {
        CreateNodeParams updatedParams = originalParams;

        if ( params.getDataProcessor() != null )
        {
            updatedParams = CreateNodeParams.create( originalParams )
                .data( params.getDataProcessor().process( originalParams.getData(), null ) )
                .build();
        }

        return updatedParams;
    }

    private void storeChildNodes( final Node originalParent, final Node newParent, final NodeReferenceUpdatesHolder.Builder builder,
                                  final List<Node> createdChildren )
    {
        final InternalContext internalContext = InternalContext.from( ContextAccessor.current() );

        final NodeIds subTreeIds = FindNodeBranchEntriesByParentCommand.create( this )
            .parentPath( originalParent.path() )
            .build()
            .execute()
            .stream()
            .map( NodeBranchEntry::getNodeId )
            .collect( NodeIds.collector() );

        // nodes not readable by the caller are not returned, and neither they nor their children are duplicated
        final Nodes originalNodes = this.nodeStorageService.get( subTreeIds, internalContext );

        int total = originalNodes.getSize() + 1;
        listener.resolved( total );

        // the storage get answers in the requested order, so the path order of the listing survives into the walk
        // and a node is always duplicated before any of its children
        final Map<NodePath, DuplicatedParent> duplicatedParents = new HashMap<>();
        duplicatedParents.put( originalParent.path(), new DuplicatedParent( originalParent, newParent ) );

        for ( final Node node : originalNodes )
        {
            final DuplicatedParent parent = duplicatedParents.get( node.parentPath() );

            if ( parent == null )
            {
                // each node of a skipped branch is skipped individually, so the walk shrinks by exactly one per pass
                listener.resolved( --total );
                continue;
            }

            final CreateNodeParams.Builder paramsBuilder = CreateNodeParams.from( node ).parent( parent.copy().path() );

            decideInsertStrategy( parent.original(), node, paramsBuilder );

            attachBinaries( node, paramsBuilder );

            paramsBuilder.versionAttributesResolver( params.getVersionAttributesResolver() );

            final CreateNodeParams originalParams = paramsBuilder.build();

            final CreateNodeParams processedParams = executeProcessors( originalParams );

            // the parent copy is already at hand, and every child path below it is created exactly once
            final Node newChildNode = CreateNodeCommand.create( this )
                .params( processedParams )
                .binaryService( this.binaryService )
                .knownParentNode( parent.copy() )
                .skipVerification( true )
                .build()
                .execute();

            builder.add( node.id(), newChildNode.id() );

            result.addChild( newChildNode );
            createdChildren.add( newChildNode );
            listener.nodesDuplicated( 1 );

            duplicatedParents.put( node.path(), new DuplicatedParent( node, newChildNode ) );
        }
    }

    private record DuplicatedParent(Node original, Node copy)
    {
    }

    private void decideInsertStrategy( final Node originalParent, final Node node, final CreateNodeParams.Builder paramsBuilder )
    {
        if ( originalParent.getChildOrder().isManualOrder() )
        {
            paramsBuilder.manualOrderValue( node.getManualOrderValue() ).insertManualStrategy( InsertManualStrategy.MANUAL );
        }
    }

    private void attachBinaries( final Node node, final CreateNodeParams.Builder paramsBuilder )
    {
        final RepositoryId repositoryId = ContextAccessor.current().getRepositoryId();
        for ( final AttachedBinary attachedBinary : node.getAttachedBinaries() )
        {
            paramsBuilder.attachBinary( attachedBinary.getBinaryReference(), this.binaryService.get( repositoryId, attachedBinary ) );
        }
    }

    private void updateNodeReferences( final Node node, final NodeReferenceUpdatesHolder nodeReferenceUpdatesHolder )
    {
        final PropertyTree data = node.data();

        boolean changes = false;

        for ( final Property property : node.data().getProperties( ValueTypes.REFERENCE ) )
        {
            final Reference reference = property.getReference();
            if ( reference != null && nodeReferenceUpdatesHolder.mustUpdate( reference, node.id() ) )
            {
                changes = true;
                data.setReference( property.getPath(), nodeReferenceUpdatesHolder.getNewReference( reference ) );
            }
        }

        if ( changes )
        {
            PatchNodeCommand.create()
                .params( PatchNodeParams.create().id( node.id() ).editor( toBeEdited -> toBeEdited.data = data ).build() )
                .binaryService( this.binaryService )
                .indexServiceInternal( this.indexServiceInternal )
                .storageService( this.nodeStorageService )
                .searchService( this.nodeSearchService )
                .build()
                .execute();
        }

        listener.nodesReferencesUpdated( 1 );
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
            requireNonNull( this.binaryService );
            requireNonNull( params, "params cannot be null" );
        }
    }

    private enum NoopDuplicateNodeListener
        implements DuplicateNodeListener
    {
        INSTANCE;

        @Override
        public void nodesDuplicated( final int count )
        {
        }

        @Override
        public void nodesReferencesUpdated( final int count )
        {
        }
    }
}
