package com.enonic.xp.repo.impl.node;

import java.util.Map;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.io.ByteSource;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.ApplyNodePermissionsParams;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.CreateRootNodeParams;
import com.enonic.xp.node.DuplicateNodeParams;
import com.enonic.xp.node.FindNodePathsByQueryResult;
import com.enonic.xp.node.FindNodesByMultiRepoQueryResult;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.GetActiveNodeVersionsParams;
import com.enonic.xp.node.GetActiveNodeVersionsResult;
import com.enonic.xp.node.GetNodeVersionsParams;
import com.enonic.xp.node.ImportNodeParams;
import com.enonic.xp.node.ImportNodeResult;
import com.enonic.xp.node.ImportNodeVersionParams;
import com.enonic.xp.node.LoadNodeParams;
import com.enonic.xp.node.LoadNodeResult;
import com.enonic.xp.node.MoveNodeListener;
import com.enonic.xp.node.MoveNodeResult;
import com.enonic.xp.node.MultiRepoNodeQuery;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeComparisons;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionQuery;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.NodesHasChildrenResult;
import com.enonic.xp.node.PushNodesListener;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.RenameNodeParams;
import com.enonic.xp.node.ReorderChildNodesParams;
import com.enonic.xp.node.ReorderChildNodesResult;
import com.enonic.xp.node.ResolveSyncWorkResult;
import com.enonic.xp.node.SetNodeChildOrderParams;
import com.enonic.xp.node.SetNodeStateParams;
import com.enonic.xp.node.SetNodeStateResult;
import com.enonic.xp.node.SyncWorkResolverParams;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.repo.impl.NodeEvents;
import com.enonic.xp.repo.impl.binary.BinaryService;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.repo.impl.version.VersionIndexPath;
import com.enonic.xp.repository.BranchNotFoundException;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryNotFoundException;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.util.BinaryReference;

@Component(immediate = true)
public class NodeServiceImpl
    implements NodeService
{
    private IndexServiceInternal indexServiceInternal;

    private NodeStorageService nodeStorageService;

    private NodeSearchService nodeSearchService;

    private EventPublisher eventPublisher;

    private BinaryService binaryService;

    private RepositoryService repositoryService;

    @SuppressWarnings("unused")
    @Activate
    public void initialize()
    {
    }

    @Override
    public Node getById( final NodeId id )
    {
        final Trace trace = Tracer.newTrace( "node.getById" );
        if ( trace == null )
        {
            return executeGetById( id );
        }

        return Tracer.trace( trace, () -> {
            trace.put( "id", id );
            final Node node = executeGetById( id );
            trace.put( "path", node.path() );
            return node;
        } );
    }

    private Node executeGetById( final NodeId id )
    {
        verifyContext();
        final Node node = doGetById( id );

        if ( node == null )
        {
            throw new NodeNotFoundException(
                "Node with id " + id + " not found in branch " + ContextAccessor.current().getBranch().getValue() );
        }

        return node;
    }

    private Node doGetById( final NodeId id )
    {
        return GetNodeByIdCommand.create().
            id( id ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            build().
            execute();
    }

    @Override
    public Node getByPath( final NodePath path )
    {
        final Trace trace = Tracer.newTrace( "node.getByPath" );
        if ( trace == null )
        {
            return executeGetByPath( path );
        }

        return Tracer.trace( trace, () -> {
            trace.put( "path", path );
            final Node node = executeGetByPath( path );
            if ( node != null )
            {
                trace.put( "id", node.id() );
            }
            return node;
        } );
    }

    private Node executeGetByPath( final NodePath path )
    {
        verifyContext();
        return doGetByPath( path );
    }

    private Node doGetByPath( final NodePath path )
    {
        return GetNodeByPathCommand.create().
            nodePath( path ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            build().
            execute();
    }

    @Override
    public Nodes getByIds( final NodeIds ids )
    {
        final Trace trace = Tracer.newTrace( "node.getByIds" );
        if ( trace == null )
        {
            return executeGetByIds( ids );
        }

        return Tracer.trace( trace, () -> {
            trace.put( "id", ids );
            return executeGetByIds( ids );
        } );
    }

    private Nodes executeGetByIds( final NodeIds ids )
    {
        verifyContext();
        return GetNodesByIdsCommand.create().
            ids( ids ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            build().
            execute();
    }

    @Override
    public Nodes getByPaths( final NodePaths paths )
    {
        final Trace trace = Tracer.newTrace( "node.getByPaths" );
        if ( trace == null )
        {
            return executeGetByPaths( paths );
        }

        return Tracer.trace( trace, () -> {
            trace.put( "path", paths );
            return executeGetByPaths( paths );
        } );
    }

    private Nodes executeGetByPaths( final NodePaths paths )
    {
        verifyContext();
        return GetNodesByPathsCommand.create().
            paths( paths ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            build().
            execute();
    }

    @Override
    public FindNodesByParentResult findByParent( final FindNodesByParentParams params )
    {
        final Trace trace = Tracer.newTrace( "node.findByParent" );
        if ( trace == null )
        {
            return executeFindByParent( params );
        }

        return Tracer.trace( trace, () -> {
            trace.put( "parent", params.getParentPath() != null ? params.getParentPath() : params.getParentId() );
            trace.put( "from", params.getFrom() );
            trace.put( "size", params.getSize() );
            final FindNodesByParentResult result = executeFindByParent( params );
            trace.put( "hits", result.getTotalHits() );
            return result;
        } );
    }

    private FindNodesByParentResult executeFindByParent( final FindNodesByParentParams params )
    {
        verifyContext();
        if ( params.isRecursive() )
        {
            return FindNodeIdsByParentCommand.create().
                parentId( params.getParentId() ).
                parentPath( params.getParentPath() ).
                recursive( true ).
                queryFilters( params.getQueryFilters() ).
                from( params.getFrom() ).
                size( params.getSize() ).
                countOnly( params.isCountOnly() ).
                childOrder( params.getChildOrder() ).
                indexServiceInternal( this.indexServiceInternal ).
                searchService( this.nodeSearchService ).
                storageService( this.nodeStorageService ).
                build().
                execute();
        }

        return FindNodesByParentCommand.create().
            params( params ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            build().
            execute();
    }

    @Override
    public FindNodesByQueryResult findByQuery( final NodeQuery nodeQuery )
    {
        final Trace trace = Tracer.newTrace( "node.findByQuery" );
        if ( trace == null )
        {
            return executeFindByQuery( nodeQuery );
        }

        return Tracer.trace( trace, () -> {
            trace.put( "query", nodeQuery.getQuery() != null ? nodeQuery.getQuery().toString() : "" );
            trace.put( "from", nodeQuery.getFrom() );
            trace.put( "size", nodeQuery.getSize() );
            final FindNodesByQueryResult result = executeFindByQuery( nodeQuery );
            trace.put( "hits", result.getTotalHits() );
            return result;
        } );
    }

    private FindNodesByQueryResult executeFindByQuery( final NodeQuery nodeQuery )
    {
        verifyContext();
        return FindNodesByQueryCommand.create().
            query( nodeQuery ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            build().
            execute();
    }

    @Override
    public FindNodePathsByQueryResult findNodePathsByQuery( NodeQuery nodeQuery )
    {
        verifyContext();
        return FindNodePathsByQueryCommand.create().
            query( nodeQuery ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            build().
            execute();
    }

    @Override
    public FindNodesByMultiRepoQueryResult findByQuery( final MultiRepoNodeQuery nodeQuery )
    {
        verifyContext();
        return FindNodesByMultiRepoQueryCommand.create().
            query( nodeQuery ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            build().
            execute();
    }

    @Override
    public Node create( final CreateNodeParams params )
    {
        verifyContext();
        return doCreate( params );
    }

    private Node doCreate( final CreateNodeParams params )
    {
        final Node createdNode = CreateNodeCommand.create().
            params( params ).
            indexServiceInternal( this.indexServiceInternal ).
            binaryService( this.binaryService ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            build().
            execute();

        if ( createdNode != null )
        {
            this.eventPublisher.publish( NodeEvents.created( createdNode ) );
        }
        return createdNode;
    }

    @Override
    public Node update( final UpdateNodeParams params )
    {
        verifyContext();
        final Node updatedNode = UpdateNodeCommand.create().
            params( params ).
            indexServiceInternal( this.indexServiceInternal ).
            binaryService( this.binaryService ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            build().
            execute();

        if ( updatedNode != null )
        {
            this.eventPublisher.publish( NodeEvents.updated( updatedNode ) );
        }
        return updatedNode;
    }

    @Override
    public Node rename( final RenameNodeParams params )
    {
        verifyContext();
        final MoveNodeResult moveNodeResult = RenameNodeCommand.create().
            params( params ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            build().
            execute();

        if ( moveNodeResult.getTargetNode() != null )
        {
            this.eventPublisher.publish( NodeEvents.renamed( moveNodeResult.getSourceNode(), moveNodeResult.getTargetNode() ) );
            return moveNodeResult.getTargetNode();
        }
        else
        {
            return moveNodeResult.getSourceNode();
        }
    }

    @Override
    public NodeIds deleteById( final NodeId id )
    {
        verifyContext();
        final NodeBranchEntries deletedNodes = DeleteNodeByIdCommand.create().
            nodeId( id ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            build().
            execute();

        if ( deletedNodes.isNotEmpty() )
        {
            this.eventPublisher.publish( NodeEvents.deleted( deletedNodes ) );
        }

        return NodeIds.from( deletedNodes.getKeys() );
    }

    @Override
    public NodeIds deleteByPath( final NodePath path )
    {
        verifyContext();
        final NodeBranchEntries deletedNodes = DeleteNodeByPathCommand.create().
            nodePath( path ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            build().
            execute();

        if ( deletedNodes.isNotEmpty() )
        {
            this.eventPublisher.publish( NodeEvents.deleted( deletedNodes ) );
        }
        return NodeIds.from( deletedNodes.getKeys() );
    }

    @Override
    public PushNodesResult push( final NodeIds ids, final Branch target )
    {
        return push( ids, target, null );
    }

    @Override
    public PushNodesResult push( final NodeIds ids, final Branch target, final PushNodesListener pushListener )
    {
        verifyContext();
        verifyBranchExists( target );

        final InternalPushNodesResult pushNodesResult = PushNodesCommand.create().
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            ids( ids ).
            target( target ).
            pushListener( pushListener ).
            build().
            execute();

        if ( pushNodesResult.getPushNodeEntries().isNotEmpty() )
        {
            this.eventPublisher.publish( NodeEvents.pushed( pushNodesResult.getPushNodeEntries() ) );
        }

        return pushNodesResult;
    }

    @Override
    public Node duplicate( final DuplicateNodeParams params )
    {
        verifyContext();
        final Node duplicatedNode = DuplicateNodeCommand.create().
            params( params ).
            indexServiceInternal( this.indexServiceInternal ).
            binaryService( this.binaryService ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            build().
            execute();

        if ( duplicatedNode != null )
        {
            this.eventPublisher.publish( NodeEvents.duplicated( duplicatedNode ) );
        }
        return duplicatedNode;
    }

    @Override
    public Node move( final NodeId nodeId, final NodePath parentNodePath, final MoveNodeListener moveListener )
    {
        verifyContext();
        final MoveNodeResult moveNodeResult = MoveNodeCommand.create().
            id( nodeId ).
            newParent( parentNodePath ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            moveListener( moveListener ).
            build().
            execute();

        if ( moveNodeResult.getTargetNode() != null )
        {
            this.eventPublisher.publish( NodeEvents.moved( moveNodeResult.getSourceNode(), moveNodeResult.getTargetNode() ) );
            return moveNodeResult.getTargetNode();
        }
        else
        {
            return moveNodeResult.getSourceNode();
        }
    }

    @Override
    public Nodes move( final NodeIds nodeIds, final NodePath parentNodePath, final MoveNodeListener moveListener )
    {
        verifyContext();
        return Nodes.from( nodeIds.
            stream().
            map( nodeId -> this.move( nodeId, parentNodePath, moveListener ) ).collect( Collectors.toList() ) );
    }


    @Override
    public NodeComparison compare( final NodeId nodeId, final Branch target )
    {
        verifyContext();
        return CompareNodeCommand.create().
            nodeId( nodeId ).
            target( target ).
            storageService( this.nodeStorageService ).
            build().
            execute();
    }

    @Override
    public NodeComparisons compare( final NodeIds nodeIds, final Branch target )
    {
        verifyContext();
        return CompareNodesCommand.create().
            nodeIds( nodeIds ).
            target( target ).
            storageService( this.nodeStorageService ).
            build().
            execute();
    }

    @Override
    public NodeVersionQueryResult findVersions( final GetNodeVersionsParams params )
    {
        verifyContext();

        final NodeVersionQuery query = NodeVersionQuery.create().
            size( params.getSize() ).
            from( params.getFrom() ).
            nodeId( params.getNodeId() ).
            addOrderBy( FieldOrderExpr.create( VersionIndexPath.TIMESTAMP, OrderExpr.Direction.DESC ) ).
            build();

        return FindNodeVersionsCommand.create().
            query( query ).
            searchService( this.nodeSearchService ).
            build().
            execute();
    }

    @Override
    public NodeVersionQueryResult findVersions( final NodeVersionQuery query )
    {
        verifyContext();

        return FindNodeVersionsCommand.create().
            query( query ).
            searchService( this.nodeSearchService ).
            build().
            execute();
    }

    @Override
    public Nodes findInternalDependencies( final Map<NodeId, NodePath> sourceNodeIds )
    {
        verifyContext();

        return FindDependenciesWithinPathCommand.create().
            searchService( this.nodeSearchService ).
            storageService( this.nodeStorageService ).
            indexServiceInternal( this.indexServiceInternal ).
            nodeIds( sourceNodeIds ).
            build().
            execute();
    }

    @Override
    public boolean deleteVersion( final NodeVersionId nodeVersionId )
    {
        return DeleteVersionCommand.create().
            nodeVersionId( nodeVersionId ).
            repositoryService( this.repositoryService ).
            searchService( this.nodeSearchService ).
            storageService( this.nodeStorageService ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();
    }

    @Override
    public GetActiveNodeVersionsResult getActiveVersions( final GetActiveNodeVersionsParams params )
    {
        verifyContext();
        return GetActiveNodeVersionsCommand.create().
            nodeId( params.getNodeId() ).
            branches( params.getBranches() ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            build().
            execute();
    }


    @Override
    public NodeVersionId setActiveVersion( final NodeId nodeId, final NodeVersionId nodeVersionId )
    {
        verifyContext();
        final NodeVersionId result = SetActiveVersionCommand.create().
            nodeVersionId( nodeVersionId ).
            nodeId( nodeId ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            build().
            execute();

        final Node node = this.getById( nodeId );

        if ( node != null )
        {
            this.eventPublisher.publish( NodeEvents.updated( node ) );
        }

        return result;
    }

    @Override
    public NodeVersion getByNodeVersion( final NodeVersionId nodeVersionId )
    {
        verifyContext();
        return this.nodeStorageService.get( nodeVersionId );
    }

    @Override
    public ResolveSyncWorkResult resolveSyncWork( final SyncWorkResolverParams params )
    {
        verifyContext();
        return ResolveSyncWorkCommand.create().
            target( params.getBranch() ).
            nodeId( params.getNodeId() ).
            excludedNodeIds( params.getExcludedNodeIds() ).
            includeChildren( params.isIncludeChildren() ).
            includeDependencies( params.isIncludeDependencies() ).
            initialDiffFilter( params.getInitialDiffFilter() ).
            indexServiceInternal( indexServiceInternal ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            build().
            execute();
    }

    @Override
    public Node setChildOrder( final SetNodeChildOrderParams params )
    {
        verifyContext();
        final Node sortedNode = SetNodeChildOrderCommand.create().
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            childOrder( params.getChildOrder() ).
            nodeId( params.getNodeId() ).
            build().
            execute();

        if ( sortedNode != null )
        {
            this.eventPublisher.publish( NodeEvents.sorted( sortedNode ) );
        }
        return sortedNode;
    }

    @Override
    public ReorderChildNodesResult reorderChildren( final ReorderChildNodesParams params )
    {
        verifyContext();
        final ReorderChildNodesResult reorderChildNodesResult = ReorderChildNodesCommand.create().
            params( params ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            build().
            execute();

        for ( Node parentNode : reorderChildNodesResult.getParentNodes() )
        {
            this.eventPublisher.publish( NodeEvents.sorted( parentNode ) );
        }

        return reorderChildNodesResult;
    }

    @Override
    public void refresh( final RefreshMode refreshMode )
    {
        verifyContext();
        RefreshCommand.create().
            indexServiceInternal( this.indexServiceInternal ).
            refreshMode( refreshMode ).
            build().
            execute();
    }

    @Override
    public int applyPermissions( final ApplyNodePermissionsParams params )
    {
        verifyContext();
        final Nodes updatedNodes = ApplyNodePermissionsCommand.create().
            params( params ).
            indexServiceInternal( this.indexServiceInternal ).
            searchService( this.nodeSearchService ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            build().
            execute();

        for ( final Node node : updatedNodes )
        {
            this.eventPublisher.publish( NodeEvents.updated( node ) );
        }

        return updatedNodes.getSize();
    }

    @Override
    public ByteSource getBinary( final NodeId nodeId, final BinaryReference reference )
    {
        verifyContext();
        return GetBinaryCommand.create().
            binaryReference( reference ).
            nodeId( nodeId ).
            indexServiceInternal( this.indexServiceInternal ).
            binaryService( this.binaryService ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            build().
            execute();
    }

    @Override
    public ByteSource getBinary( final NodeVersionId nodeVersionId, final BinaryReference reference )
    {
        verifyContext();
        return GetBinaryByVersionCommand.create().
            binaryReference( reference ).
            nodeVersionId( nodeVersionId ).
            indexServiceInternal( this.indexServiceInternal ).
            binaryService( this.binaryService ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            build().
            execute();
    }

    @Override
    public String getBinaryKey( final NodeId nodeId, final BinaryReference reference )
    {
        verifyContext();
        return GetBinaryKeyCommand.create().
            binaryReference( reference ).
            nodeId( nodeId ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            build().
            execute();
    }

    @Override
    public Node createRootNode( final CreateRootNodeParams params )
    {
        verifyContext();
        final Node createdNode = CreateRootNodeCommand.create().
            params( params ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            build().
            execute();

        if ( createdNode != null )
        {
            this.eventPublisher.publish( NodeEvents.created( createdNode ) );
        }
        return createdNode;
    }

    @Override
    public SetNodeStateResult setNodeState( final SetNodeStateParams params )
    {
        verifyContext();
        final SetNodeStateResult setNodeStateResult = SetNodeStateCommand.create().
            params( params ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            build().
            execute();

        if ( setNodeStateResult.getUpdatedNodes().isNotEmpty() )
        {
            this.eventPublisher.publish( NodeEvents.stateUpdated( setNodeStateResult.getUpdatedNodes() ) );
        }

        return setNodeStateResult;
    }

    @Override
    public Node getRoot()
    {
        verifyContext();
        final Node node = doGetByPath( NodePath.ROOT );

        if ( node == null || node.isRoot() )
        {
            return node;
        }

        throw new RuntimeException( "Expected node with path " + NodePath.ROOT.toString() + " to be of type RootNode, found " + node.id() );
    }

    @Override
    public Node setRootPermissions( final AccessControlList acl, final boolean inheritPermissions )
    {
        return SetRootPermissionsCommand.create().
            permissions( acl ).
            inheritPermissions( inheritPermissions ).
            indexServiceInternal( indexServiceInternal ).
            searchService( this.nodeSearchService ).
            storageService( this.nodeStorageService ).
            build().
            execute();
    }

    @Override
    public ImportNodeResult importNode( final ImportNodeParams params )
    {
        verifyContext();
        final ImportNodeResult importNodeResult = ImportNodeCommand.create().
            binaryAttachments( params.getBinaryAttachments() ).
            importNode( params.getNode() ).
            insertManualStrategy( params.getInsertManualStrategy() ).
            dryRun( params.isDryRun() ).
            importPermissions( params.isImportPermissions() ).
            binaryBlobStore( this.binaryService ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            build().
            execute();

        if ( importNodeResult.isPreExisting() )
        {
            this.eventPublisher.publish( NodeEvents.updated( importNodeResult.getNode() ) );
        }
        else
        {
            this.eventPublisher.publish( NodeEvents.created( importNodeResult.getNode() ) );
        }

        return importNodeResult;
    }

    @Override
    public LoadNodeResult loadNode( final LoadNodeParams params )
    {
        return LoadNodeCommand.create().
            params( params ).
            searchService( this.nodeSearchService ).
            storageService( this.nodeStorageService ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();
    }

    @Override
    public boolean nodeExists( final NodeId nodeId )
    {
        verifyContext();
        return NodeHelper.runAsAdmin( () -> this.doGetById( nodeId ) ) != null;
    }

    @Override
    public boolean nodeExists( final NodePath nodePath )
    {
        verifyContext();
        return NodeHelper.runAsAdmin( () -> this.doGetByPath( nodePath ) ) != null;
    }

    @Override
    public NodesHasChildrenResult hasChildren( final Nodes nodes )
    {
        verifyContext();
        return NodeHasChildResolver.create().
            searchService( this.nodeSearchService ).
            build().
            resolve( nodes );
    }

    @Override
    public boolean hasChildren( final Node node )
    {
        verifyContext();
        return NodeHasChildResolver.create().
            searchService( this.nodeSearchService ).
            build().
            resolve( node );
    }

    @Override
    public boolean hasUnpublishedChildren( final NodeId parent, final Branch target )
    {
        verifyContext();
        return HasUnpublishedChildrenCommand.create().
            parent( parent ).
            target( target ).
            indexServiceInternal( indexServiceInternal ).
            storageService( nodeStorageService ).
            searchService( nodeSearchService ).
            build().
            execute();
    }

    @Override
    public void importNodeVersion( final ImportNodeVersionParams params )
    {
        verifyRepositoryExists();

        LoadNodeVersionCommand.create().
            nodeId( params.getNodeId() ).
            nodePath( params.getNodePath() ).
            nodeVersion( params.getNodeVersion() ).
            timestamp( params.getTimestamp() ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();
    }

    private void verifyContext()
    {
        verifyBranchExists( ContextAccessor.current().getBranch() );
    }

    private void verifyRepositoryExists()
    {
        NodeHelper.runAsAdmin( () -> {
            final RepositoryId repoId = ContextAccessor.current().
                getRepositoryId();
            final Repository repository = this.repositoryService.get( repoId );
            if ( repository == null )
            {
                throw new RepositoryNotFoundException( repoId );
            }
        } );
    }

    private void verifyBranchExists( Branch branch )
    {
        NodeHelper.runAsAdmin( () -> {
            final RepositoryId repoId = ContextAccessor.current().
                getRepositoryId();
            final Repository repository = this.repositoryService.get( repoId );
            if ( repository == null )
            {
                throw new RepositoryNotFoundException( repoId );
            }

            if ( !repository.getBranches().contains( branch ) )
            {
                throw new BranchNotFoundException( branch );
            }
        } );
    }

    @Reference
    public void setIndexServiceInternal( final IndexServiceInternal indexServiceInternal )
    {
        this.indexServiceInternal = indexServiceInternal;
    }

    @Reference
    public void setNodeStorageService( final NodeStorageService nodeStorageService )
    {
        this.nodeStorageService = nodeStorageService;
    }

    @Reference
    public void setNodeSearchService( final NodeSearchService nodeSearchService )
    {
        this.nodeSearchService = nodeSearchService;
    }

    @Reference
    public void setEventPublisher( final EventPublisher eventPublisher )
    {
        this.eventPublisher = eventPublisher;
    }

    @Reference
    public void setBinaryService( final BinaryService binaryService )
    {
        this.binaryService = binaryService;
    }

    @Reference
    public void setRepositoryService( final RepositoryService repositoryService )
    {
        this.repositoryService = repositoryService;
    }
}
