package com.enonic.xp.repo.impl.node;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.enonic.xp.node.OperationNotPermittedException;
import com.enonic.xp.node.PatchNodeParams;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeBranchEntries;
import com.enonic.xp.repo.impl.NodeBranchEntry;
import com.enonic.xp.repo.impl.binary.BinaryService;
import com.enonic.xp.repo.impl.branch.storage.NodeFactory;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
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
        else if ( params.isIncludeReferences() )
        {
            storeReferredNodes( existingNode, duplicatedNode, builder, createdChildren );
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

        final NodeBranchEntries entries =
            FindNodeBranchEntriesByParentCommand.create( this ).parentPath( originalParent.path() ).build().execute();

        final List<NodeBranchEntry> toDuplicate = filterToDuplicate( entries, internalContext );

        listener.resolved( toDuplicate.size() + 1 );

        final Map<NodePath, DuplicatedParent> duplicatedParents = new HashMap<>();
        duplicatedParents.put( originalParent.path(), new DuplicatedParent( originalParent, newParent ) );

        for ( final NodeBranchEntry entry : toDuplicate )
        {
            final DuplicatedParent parent = requireNonNull( duplicatedParents.get( entry.getNodePath().getParentPath() ),
                                                            () -> "Parent of [" + entry.getNodePath() + "] was not duplicated yet" );

            final Node node =
                NodeFactory.create( this.nodeStorageService.getNodeVersion( entry.getNodeVersionKey(), internalContext ), entry );

            final Node newChildNode = storeNode( node, parent, builder, createdChildren );

            duplicatedParents.put( node.path(), new DuplicatedParent( node, newChildNode ) );
        }
    }

    /**
     * Duplicates the nodes the duplicated node refers to. Only nodes inside the duplicated node's own tree are duplicated, since
     * references to nodes outside of it are equally valid for the copy. The nodes required to hold them are duplicated as well, so
     * that the referred nodes keep their position relative to the duplicated node.
     */
    private void storeReferredNodes( final Node originalNode, final Node newNode, final NodeReferenceUpdatesHolder.Builder builder,
                                     final List<Node> createdChildren )
    {
        final List<Node> toDuplicate = resolveReferredNodes( originalNode );

        if ( toDuplicate.isEmpty() )
        {
            return;
        }

        listener.resolved( toDuplicate.size() + 1 );

        final Map<NodePath, DuplicatedParent> duplicatedParents = new HashMap<>();
        duplicatedParents.put( originalNode.path(), new DuplicatedParent( originalNode, newNode ) );

        for ( final Node node : toDuplicate )
        {
            final DuplicatedParent parent = requireNonNull( duplicatedParents.get( node.parentPath() ),
                                                            () -> "Parent of [" + node.path() + "] was not duplicated yet" );

            final Node newReferredNode = storeNode( node, parent, builder, createdChildren );

            duplicatedParents.put( node.path(), new DuplicatedParent( node, newReferredNode ) );
        }
    }

    /**
     * @return the nodes inside the duplicated node's tree that are referred to by it, directly or through another referred node,
     * together with the nodes holding them, ordered so that a node always comes after the nodes it is stored in.
     */
    private List<Node> resolveReferredNodes( final Node originalNode )
    {
        final InternalContext internalContext = InternalContext.from( ContextAccessor.current() );

        final Set<NodeId> resolved = new HashSet<>();
        resolved.add( originalNode.id() );

        final Map<NodePath, Node> referred = new HashMap<>();

        NodeIds toResolve = referredNodeIds( originalNode, resolved );

        while ( !toResolve.isEmpty() )
        {
            final NodeIds.Builder nextToResolve = NodeIds.create();

            // nodes the current principals are not allowed to read are not returned, and are therefore never duplicated
            for ( final Node node : this.nodeStorageService.get( toResolve, internalContext ) )
            {
                if ( node.path().isChildOf( originalNode.path() ) )
                {
                    referred.put( node.path(), node );

                    // nodes outside of the duplicated tree are not duplicated, so what they refer to is of no interest either
                    nextToResolve.addAll( referredNodeIds( node, resolved ) );
                }
            }

            toResolve = nextToResolve.build();
        }

        final Map<NodePath, Node> toDuplicate = new HashMap<>();

        for ( final Node node : referred.values() )
        {
            addWithHoldingNodes( node, originalNode, referred, toDuplicate );
        }

        return toDuplicate.values().stream().sorted( Comparator.comparing( Node::path ) ).toList();
    }

    private NodeIds referredNodeIds( final Node node, final Set<NodeId> resolved )
    {
        final NodeIds.Builder referredNodeIds = NodeIds.create();

        for ( final Property property : node.data().getProperties( ValueTypes.REFERENCE ) )
        {
            final Reference reference = property.getReference();
            if ( reference != null && resolved.add( reference.getNodeId() ) )
            {
                referredNodeIds.add( reference.getNodeId() );
            }
        }

        return referredNodeIds.build();
    }

    private void addWithHoldingNodes( final Node node, final Node originalNode, final Map<NodePath, Node> referred,
                                      final Map<NodePath, Node> toDuplicate )
    {
        final List<Node> holdingNodes = new ArrayList<>();

        NodePath path = node.parentPath();
        while ( path.isChildOf( originalNode.path() ) && !toDuplicate.containsKey( path ) )
        {
            final Node holdingNode = referred.containsKey( path ) ? referred.get( path ) : doGetByPath( path );
            if ( holdingNode == null )
            {
                // the referred node cannot be placed in the copy without the nodes it is stored in
                return;
            }

            holdingNodes.add( holdingNode );
            path = path.getParentPath();
        }

        toDuplicate.put( node.path(), node );
        holdingNodes.forEach( holdingNode -> toDuplicate.put( holdingNode.path(), holdingNode ) );
    }

    private Node storeNode( final Node node, final DuplicatedParent parent, final NodeReferenceUpdatesHolder.Builder builder,
                            final List<Node> createdChildren )
    {
        final CreateNodeParams.Builder paramsBuilder = CreateNodeParams.from( node ).parent( parent.copy().path() );

        decideInsertStrategy( parent.original(), node, paramsBuilder );

        attachBinaries( node, paramsBuilder );

        paramsBuilder.versionAttributesResolver( params.getVersionAttributesResolver() );

        final CreateNodeParams originalParams = paramsBuilder.build();

        final CreateNodeParams processedParams = executeProcessors( originalParams );

        final Node newNode = CreateNodeCommand.create( this )
            .params( processedParams )
            .binaryService( this.binaryService )
            .knownParentNode( parent.copy() )
            .skipVerification( true )
            .build()
            .execute();

        builder.add( node.id(), newNode.id() );

        result.addChild( newNode );
        createdChildren.add( newNode );
        listener.nodesDuplicated( 1 );

        return newNode;
    }

    private record DuplicatedParent(Node original, Node copy)
    {
    }

    private List<NodeBranchEntry> filterToDuplicate( final NodeBranchEntries entries, final InternalContext context )
    {
        if ( context.getPrincipalKeys().contains( RoleKeys.ADMIN ) )
        {
            return entries.stream().toList();
        }

        final List<NodeBranchEntry> toDuplicate = new ArrayList<>( entries.getSize() );
        String prohibitedRoot = null;
        for ( final NodeBranchEntry entry : entries )
        {
            final String path = entry.getNodePath().toString();
            if ( prohibitedRoot != null && path.startsWith( prohibitedRoot ) )
            {
                continue;
            }

            final AccessControlList permissions = this.nodeStorageService.getNodePermissions( entry.getNodeVersionKey(), context );
            if ( NodePermissionsResolver.hasPermission( context.getPrincipalKeys(), Permission.READ, permissions ) )
            {
                toDuplicate.add( entry );
            }
            else
            {
                prohibitedRoot = path + "/";
            }
        }
        return toDuplicate;
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
