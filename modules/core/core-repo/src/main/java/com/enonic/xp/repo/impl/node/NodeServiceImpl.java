package com.enonic.xp.repo.impl.node;

import java.util.Collection;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.NodeVersionKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.ApplyNodePermissionsParams;
import com.enonic.xp.node.ApplyNodePermissionsResult;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.CreateRootNodeParams;
import com.enonic.xp.node.DeleteNodeListener;
import com.enonic.xp.node.DeleteNodeParams;
import com.enonic.xp.node.DeleteNodeResult;
import com.enonic.xp.node.DuplicateNodeParams;
import com.enonic.xp.node.FindNodePathsByQueryResult;
import com.enonic.xp.node.FindNodesByMultiRepoQueryResult;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.GetActiveNodeVersionsParams;
import com.enonic.xp.node.GetActiveNodeVersionsResult;
import com.enonic.xp.node.GetNodeVersionsParams;
import com.enonic.xp.node.ImportNodeCommitParams;
import com.enonic.xp.node.ImportNodeParams;
import com.enonic.xp.node.ImportNodeResult;
import com.enonic.xp.node.ImportNodeVersionParams;
import com.enonic.xp.node.LoadNodeParams;
import com.enonic.xp.node.LoadNodeResult;
import com.enonic.xp.node.MoveNodeListener;
import com.enonic.xp.node.MoveNodeParams;
import com.enonic.xp.node.MoveNodeResult;
import com.enonic.xp.node.MultiRepoNodeQuery;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.node.NodeCommitQuery;
import com.enonic.xp.node.NodeCommitQueryResult;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeComparisons;
import com.enonic.xp.node.NodeHit;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeIndexPath;
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
import com.enonic.xp.node.RoutableNodeVersionId;
import com.enonic.xp.node.RoutableNodeVersionIds;
import com.enonic.xp.node.SetNodeChildOrderParams;
import com.enonic.xp.node.SetNodeStateParams;
import com.enonic.xp.node.SetNodeStateResult;
import com.enonic.xp.node.SyncWorkResolverParams;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeEvents;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.SearchPreference;
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
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.util.BinaryReference;

@Component(immediate = true)
public class NodeServiceImpl
    implements NodeService
{
    private final IndexServiceInternal indexServiceInternal;

    private final NodeStorageService nodeStorageService;

    private final  NodeSearchService nodeSearchService;

    private final  EventPublisher eventPublisher;

    private final BinaryService binaryService;

    private final RepositoryService repositoryService;

    @Activate
    public NodeServiceImpl( @Reference final IndexServiceInternal indexServiceInternal, @Reference final NodeStorageService nodeStorageService,
                            @Reference final NodeSearchService nodeSearchService, @Reference final EventPublisher eventPublisher,
                            @Reference final BinaryService binaryService, @Reference final RepositoryService repositoryService )
    {
        this.indexServiceInternal = indexServiceInternal;
        this.nodeStorageService = nodeStorageService;
        this.nodeSearchService = nodeSearchService;
        this.eventPublisher = eventPublisher;
        this.binaryService = binaryService;
        this.repositoryService = repositoryService;
    }


    @Override
    public Node getById( final NodeId id )
    {
        return Tracer.trace( "node.getById", trace -> trace.put( "id", id ), () -> executeGetById( id ),
                             ( trace, node ) -> trace.put( "path", node.path() ) );
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

    @Override
    public Node getByIdAndVersionId( final NodeId id, final NodeVersionId versionId )
    {
        return Tracer.trace( "node.getByIdAndVersionId", trace -> {
            trace.put( "id", id );
            trace.put( "versionId", versionId );
        }, () -> executeGetByIdAndVersionId( id, versionId ), ( trace, node ) -> trace.put( "path", node.path() ) );
    }

    private Node executeGetByIdAndVersionId( final NodeId id, final NodeVersionId versionId )
    {
        verifyContext();
        final Node node = GetNodeByIdAndVersionIdCommand.create()
            .nodeId( id )
            .versionId( versionId )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.nodeStorageService )
            .searchService( this.nodeSearchService )
            .build()
            .execute();

        if ( node == null )
        {
            throw new NodeNotFoundException( "Node with id " + id + " and versionId " + versionId + " not found in branch " +
                                                 ContextAccessor.current().getBranch().getValue() );
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
        return Tracer.trace( "node.getByPath", trace -> trace.put( "path", path ), () -> executeGetByPath( path ), ( trace, node ) -> {
            if ( node != null )
            {
                trace.put( "id", node.id() );
            }
        } );
    }

    private Node executeGetByPath( final NodePath path )
    {
        verifyContext();
        return GetNodeByPathCommand.create().
            nodePath( path ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            build().
            execute();
    }

    @Override
    @Deprecated
    public Node getByPathAndVersionId( final NodePath path, final NodeVersionId versionId )
    {
        return Tracer.trace( "node.getByPathAndVersionId", trace -> {
            trace.put( "path", path );
            trace.put( "versionId", versionId );
        }, () -> executeGetByPathAndVersionId( path, versionId ), ( trace, node ) -> trace.put( "id", node.id() ) );
    }

    private Node executeGetByPathAndVersionId( final NodePath path, final NodeVersionId versionId )
    {
        verifyContext();
        final Node node = GetNodeByPathAndVersionIdCommand.create().
            nodePath( path ).
            versionId( versionId ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            build().
            execute();

        if ( node == null )
        {
            throw new NodeNotFoundException( "Node with path " + path + " and versionId " + versionId + " not found in branch " +
                                                 ContextAccessor.current().getBranch().getValue() );
        }

        return node;
    }

    @Override
    public Nodes getByIds( final NodeIds ids )
    {
        return Tracer.trace( "node.getByIds", trace -> trace.put( "id", ids ), () -> executeGetByIds( ids ) );
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
        return Tracer.trace( "node.getByPaths", trace -> trace.put( "path", paths ), () -> executeGetByPaths( paths ) );
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
        return Tracer.trace( "node.findByParent", trace -> {
            trace.put( "parent", params.getParentPath() != null ? params.getParentPath() : params.getParentId() );
            trace.put( "from", params.getFrom() );
            trace.put( "size", params.getSize() );
        }, () -> executeFindByParent( params ), ( ( trace, result ) -> trace.put( "hits", result.getTotalHits() ) ) );
    }

    private FindNodesByParentResult executeFindByParent( final FindNodesByParentParams params )
    {
        verifyContext();
        return FindNodeIdsByParentCommand.create()
            .parentId( params.getParentId() )
            .parentPath( params.getParentPath() )
            .recursive( params.isRecursive() )
            .queryFilters( params.getQueryFilters() )
            .from( params.getFrom() )
            .size( params.getSize() )
            .countOnly( params.isCountOnly() )
            .childOrder( params.getChildOrder() )
            .indexServiceInternal( this.indexServiceInternal )
            .searchService( this.nodeSearchService )
            .storageService( this.nodeStorageService )
            .build()
            .execute();
    }

    @Override
    public FindNodesByQueryResult findByQuery( final NodeQuery nodeQuery )
    {
        return Tracer.trace( "node.findByQuery", trace -> {
            trace.put( "query", nodeQuery.getQuery() );
            trace.put( "filter", nodeQuery.getQueryFilters() );
            trace.put( "from", nodeQuery.getFrom() );
            trace.put( "size", nodeQuery.getSize() );
        }, () -> executeFindByQuery( nodeQuery ), ( trace, result ) -> trace.put( "hits", result.getTotalHits() ) );
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
    @Deprecated
    public FindNodePathsByQueryResult findNodePathsByQuery( NodeQuery nodeQuery )
    {
        verifyContext();
        final FindNodesByQueryResult queryResult = FindNodesByQueryCommand.create()
            .query( nodeQuery )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.nodeStorageService )
            .searchService( this.nodeSearchService )
            .returnFields( ReturnFields.from( NodeIndexPath.PATH ) )
            .build()
            .execute();

        return FindNodePathsByQueryResult.create()
            .paths( NodePaths.from( queryResult.getNodeHits().stream().map( NodeHit::getNodePath ).toArray( NodePath[]::new ) ) )
            .build();
    }

    @Override
    public FindNodesByMultiRepoQueryResult findByQuery( final MultiRepoNodeQuery multiNodeQuery )
    {
        return Tracer.trace( "node.findByQueryMulti", trace -> {
            trace.put( "query", multiNodeQuery.getNodeQuery().getQuery() );
            trace.put( "filter", multiNodeQuery.getNodeQuery().getQueryFilters() );
            trace.put( "from", multiNodeQuery.getNodeQuery().getFrom() );
            trace.put( "size", multiNodeQuery.getNodeQuery().getSize() );
        }, () -> executeFindByQuery( multiNodeQuery ), ( trace, result ) -> trace.put( "hits", result.getTotalHits() ) );
    }

    private FindNodesByMultiRepoQueryResult executeFindByQuery( final MultiRepoNodeQuery nodeQuery )
    {
        verifyContext();
        return FindNodesByMultiRepoQueryCommand.create()
            .query( nodeQuery )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.nodeStorageService )
            .searchService( this.nodeSearchService )
            .build()
            .execute();
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

        this.eventPublisher.publish( NodeEvents.created( createdNode ) );

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

        this.eventPublisher.publish( NodeEvents.updated( updatedNode ) );

        return updatedNode;
    }

    @Override
    public Node rename( final RenameNodeParams params )
    {
        verifyContext();
        final MoveNodeResult moveNodeResult = MoveNodeCommand.create().
            id( params.getNodeId() ).
            newNodeName( params.getNewNodeName() ).
            processor( params.getProcessor() ).
            refresh( params.getRefresh() ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            build().
            execute();

        if ( !moveNodeResult.getMovedNodes().isEmpty() )
        {
            this.eventPublisher.publish( NodeEvents.renamed( moveNodeResult.getMovedNodes().get( 0 ).getPreviousPath(),
                                                             moveNodeResult.getMovedNodes().get( 0 ).getNode() ) );
            return moveNodeResult.getMovedNodes().get( 0 ).getNode();
        }
        else
        {
            return doGetById( params.getNodeId() );
        }
    }

    @Override
    @Deprecated
    public NodeIds deleteById( final NodeId id )
    {
        return deleteById( id, null );
    }

    @Override
    @Deprecated
    public NodeIds deleteById( final NodeId id, final DeleteNodeListener deleteNodeListener )
    {
        final DeleteNodeResult result =
            delete( DeleteNodeParams.create().nodeId( id ).refresh( RefreshMode.ALL ).deleteNodeListener( deleteNodeListener ).build() );
        return NodeIds.from( result.getNodeBranchEntries().getKeys() );
    }

    @Override
    @Deprecated
    public NodeIds deleteByPath( final NodePath path )
    {
        final DeleteNodeResult result = delete( DeleteNodeParams.create().nodePath( path ).refresh( RefreshMode.ALL ).build() );
        return NodeIds.from( result.getNodeBranchEntries().getKeys() );
    }

    @Override
    public DeleteNodeResult delete( final DeleteNodeParams deleteNodeParams )
    {
        verifyContext();
        final NodeBranchEntries deletedNodes = DeleteNodeCommand.create()
            .nodeId( deleteNodeParams.getNodeId() )
            .nodePath( deleteNodeParams.getNodePath() )
            .deleteNodeListener( deleteNodeParams.getDeleteNodeListener() )
            .refresh( deleteNodeParams.getRefresh() )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.nodeStorageService )
            .searchService( this.nodeSearchService )
            .build()
            .execute();

        if ( deletedNodes.isNotEmpty() )
        {
            this.eventPublisher.publish( NodeEvents.deleted( deletedNodes ) );
        }

        return DeleteNodeResult.create().nodeBranchEntries( deletedNodes ).build();
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

        final PushNodesResult pushNodesResult = PushNodesCommand.create()
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.nodeStorageService )
            .searchService( this.nodeSearchService )
            .ids( ids )
            .target( target )
            .pushListener( pushListener )
            .build()
            .execute();

        if ( !pushNodesResult.getSuccessfulEntries().isEmpty() )
        {
            this.eventPublisher.publish( NodeEvents.pushed( pushNodesResult.getSuccessfulEntries(), target ) );
        }

        return pushNodesResult;
    }

    @Override
    public Node duplicate( final DuplicateNodeParams params )
    {
        verifyContext();
        final DuplicateNodeResult result = DuplicateNodeCommand.create()
            .params( params )
            .indexServiceInternal( this.indexServiceInternal )
            .binaryService( this.binaryService )
            .storageService( this.nodeStorageService )
            .searchService( this.nodeSearchService )
            .build()
            .execute();

        this.eventPublisher.publish( NodeEvents.duplicated( result.getNode() ) );
        result.getChildren().forEach( child -> this.eventPublisher.publish( NodeEvents.created( child ) ) );

        return result.getNode();
    }

    @Override
    @Deprecated
    public Node move( final NodeId nodeId, final NodePath parentNodePath, final MoveNodeListener moveListener )
    {
        return move( MoveNodeParams.create().
            nodeId( nodeId ).
            parentNodePath( parentNodePath ).
            moveListener( moveListener ).
            build() );
    }

    @Override
    public Node move( final MoveNodeParams params )
    {
        verifyContext();
        final MoveNodeResult moveNodeResult = MoveNodeCommand.create().
            id( params.getNodeId() ).
            newParent( params.getParentNodePath() ).
            refresh( params.getRefresh() ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            moveListener( params.getMoveListener() ).
            processor( params.getProcessor() ).
            build().
            execute();

        if ( !moveNodeResult.getMovedNodes().isEmpty() )
        {
            this.eventPublisher.publish( NodeEvents.moved( moveNodeResult ) );
            return moveNodeResult.getMovedNodes().get( 0 ).getNode();
        }
        else
        {
            return doGetById( params.getNodeId() );
        }
    }

    @Override
    @Deprecated
    public Nodes move( final NodeIds nodeIds, final NodePath parentNodePath, final MoveNodeListener moveListener )
    {
        verifyContext();
        return Nodes.from(
            nodeIds.stream().map( nodeId -> this.move( nodeId, parentNodePath, moveListener ) ).collect( Collectors.toList() ) );
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
    public NodeCommitQueryResult findCommits( final NodeCommitQuery query )
    {
        verifyContext();

        return FindNodeCommitsCommand.create().
            query( query ).
            searchService( this.nodeSearchService ).
            build().
            execute();
    }

    @Override
    public boolean deleteVersion( final NodeId nodeId, final NodeVersionId nodeVersionId )
    {
        throw new UnsupportedOperationException( "deleteVersion is not supported" );
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
    public NodeVersion getByNodeVersionKey( final NodeVersionKey nodeVersionKey )
    {
        verifyContext();

        final Context currentContext = ContextAccessor.current();
        return this.nodeStorageService.getNodeVersion( nodeVersionKey, InternalContext.from( currentContext ) );
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
            statusesToStopDependenciesSearch( params.getStatusesToStopDependenciesSearch() ).
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
            processor( params.getProcessor() ).
            refresh( params.getRefresh() ).
            build().
            execute();

        this.eventPublisher.publish( NodeEvents.sorted( sortedNode ) );

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

        for ( NodeId nodeId : reorderChildNodesResult.getNodeIds() )
        {
            this.eventPublisher.publish( NodeEvents.manualOrderUpdated( getById( nodeId ) ) );
        }

        return reorderChildNodesResult;
    }

    @Override
    public void refresh( final RefreshMode refreshMode )
    {
        Tracer.trace( "node.refresh", trace -> trace.put( "refreshMode", refreshMode ), () -> executeRefresh( refreshMode ) );
    }

    private Void executeRefresh( final RefreshMode refreshMode )
    {
        verifyContext();
        RefreshCommand.create().indexServiceInternal( this.indexServiceInternal ).refreshMode( refreshMode ).build().execute();
        return null;
    }

    @Override
    public ApplyNodePermissionsResult applyPermissions( final ApplyNodePermissionsParams params )
    {
        verifyContext();
        final ApplyNodePermissionsResult result = ApplyNodePermissionsCommand.create()
            .params( params )
            .indexServiceInternal( this.indexServiceInternal )
            .searchService( this.nodeSearchService )
            .storageService( this.nodeStorageService )
            .searchService( this.nodeSearchService )
            .build()
            .execute();

        result.getBranchResults()
            .values()
            .stream()
            .flatMap( Collection::stream )
            .filter( br -> br.getNode() != null )
            .forEach( br -> ContextBuilder.from( ContextAccessor.current() )
                .branch( br.getBranch() )
                .build()
                .runWith( () -> this.eventPublisher.publish( NodeEvents.permissionsUpdated( br.getNode() ) ) ) );

        return result;
    }

    @Override
    public ByteSource getBinary( final NodeId nodeId, final BinaryReference reference )
    {
        return Tracer.trace( "node.getBinary", trace -> {
            trace.put( "id", nodeId );
            trace.put( "reference", reference );
        }, () -> executeGetBinary( nodeId, reference ), ( trace, byteSource ) -> {
            if ( byteSource != null )
            {
                trace.put( "size", byteSource.sizeIfKnown().or( -1L ) );
            }
        } );
    }

    private ByteSource executeGetBinary( final NodeId nodeId, final BinaryReference reference )
    {
        verifyContext();
        return GetBinaryCommand.create()
            .binaryReference( reference )
            .nodeId( nodeId )
            .indexServiceInternal( this.indexServiceInternal )
            .binaryService( this.binaryService )
            .storageService( this.nodeStorageService )
            .searchService( this.nodeSearchService )
            .build()
            .execute();
    }

    @Override
    public ByteSource getBinary( final NodeId nodeId, final NodeVersionId nodeVersionId, final BinaryReference reference )
    {
        return Tracer.trace( "node.getBinary", trace -> {
            trace.put( "id", nodeId );
            trace.put( "versionId", nodeVersionId );
            trace.put( "reference", reference );
        }, () -> executeGetBinary( nodeId, nodeVersionId, reference ), ( trace, byteSource ) -> {
            if ( byteSource != null )
            {
                trace.put( "size", byteSource.sizeIfKnown().or( -1L ) );
            }
        } );
    }

    private ByteSource executeGetBinary( final NodeId nodeId, final NodeVersionId nodeVersionId, final BinaryReference reference )
    {
        verifyContext();
        return GetBinaryByVersionCommand.create()
            .binaryReference( reference )
            .nodeId( nodeId )
            .nodeVersionId( nodeVersionId )
            .indexServiceInternal( this.indexServiceInternal )
            .binaryService( this.binaryService )
            .storageService( this.nodeStorageService )
            .searchService( this.nodeSearchService )
            .build()
            .execute();
    }

    @Override
    public String getBinaryKey( final NodeId nodeId, final BinaryReference reference )
    {
        return Tracer.trace( "node.getBinaryKey", trace -> {
            trace.put( "id", nodeId );
            trace.put( "reference", reference );
        }, () -> executeGetBinaryKey( nodeId, reference ), ( trace, binaryKey ) -> trace.put( "binaryKey", binaryKey ) );
    }

    private String executeGetBinaryKey( final NodeId nodeId, final BinaryReference reference )
    {
        verifyContext();
        return GetBinaryKeyCommand.create()
            .binaryReference( reference )
            .nodeId( nodeId )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.nodeStorageService )
            .searchService( this.nodeSearchService )
            .build()
            .execute();
    }

    @Override
    @Deprecated
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
    @Deprecated
    public SetNodeStateResult setNodeState( final SetNodeStateParams params )
    {
        return SetNodeStateResult.create().build();
    }

    @Override
    public Node getRoot()
    {
        final Node node = executeGetByPath( NodePath.ROOT );

        if ( node == null || node.isRoot() )
        {
            return node;
        }

        throw new RuntimeException( "Expected node with path " + NodePath.ROOT + " to be of type RootNode, found " + node.id() );
    }

    @Override
    public Node setRootPermissions( final AccessControlList acl )
    {
        return SetRootPermissionsCommand.create().
            permissions( acl ).
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
            refresh( params.getRefresh() ).
            importPermissions( params.isImportPermissions() ).
            importPermissionsOnCreate( params.isImportPermissionsOnCreate() ).
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
        return Tracer.trace( "node.exists", trace -> trace.put( "id", nodeId ), () -> {
            verifyContext();
            return NodeHelper.runAsAdmin( () -> doGetById( nodeId ) ) != null;
        }, (trace, exists) -> trace.put( "exists", exists ) );
    }

    @Override
    public boolean nodeExists( final NodePath nodePath )
    {
        return Tracer.trace( "node.exists", trace -> trace.put( "path", nodePath ),
                             () -> NodeHelper.runAsAdmin( () -> executeGetByPath( nodePath ) ) != null,
                             ( trace, exists ) -> trace.put( "exists", exists ) );
    }

    @Override
    @Deprecated
    public NodesHasChildrenResult hasChildren( final Nodes nodes )
    {
        verifyContext();
        final NodesHasChildrenResult.Builder builder = NodesHasChildrenResult.create();

        for ( final Node node : nodes )
        {
            builder.add( node.id(),
                         NodeHasChildResolver.create().searchService( this.nodeSearchService ).build().resolve( node.path() ) );
        }

        return builder.build();
    }

    @Override
    public boolean hasChildren( final Node node )
    {
        verifyContext();
        return Tracer.trace( "node.hasChildren", trace -> trace.put( "path", node.path() ),
                             () -> NodeHasChildResolver.create().
                                 searchService( this.nodeSearchService ).
                                 build().
                                 resolve( node.path() ),
                             ( trace, hasChildren ) -> trace.put( "hasChildren", hasChildren ) );

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
            nodeVersionId( params.getNodeVersionId() ).
            nodeCommitId( params.getNodeCommitId() ).
            timestamp( params.getTimestamp() ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();
    }

    @Override
    public void importNodeCommit( final ImportNodeCommitParams params )
    {
        verifyRepositoryExists();

        LoadNodeCommitCommand.create().
            nodeCommitId( params.getNodeCommitId() ).
            message( params.getMessage() ).
            committer( params.getCommitter() ).
            timestamp( params.getTimestamp() ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();
    }

    @Override
    public NodeCommitEntry commit( final NodeCommitEntry nodeCommitEntry, final RoutableNodeVersionIds routableNodeVersionIds )
    {
        verifyContext();
        final NodeCommitEntry commit =
            nodeStorageService.commit( nodeCommitEntry, routableNodeVersionIds, InternalContext.from( ContextAccessor.current() ) );

        refresh( RefreshMode.STORAGE );

        return commit;
    }

    @Override
    public NodeCommitEntry commit( final NodeCommitEntry nodeCommitEntry, final NodeIds nodeIds )
    {
        verifyContext();

        final InternalContext context =
            InternalContext.create( ContextAccessor.current() ).searchPreference( SearchPreference.PRIMARY ).build();
        final RoutableNodeVersionIds.Builder routableNodeVersionIds = RoutableNodeVersionIds.create();
        final NodeBranchEntries branchNodeVersions = nodeStorageService.getBranchNodeVersions( nodeIds, context );
        branchNodeVersions.stream()
            .map( branchEntry -> RoutableNodeVersionId.from( branchEntry.getNodeId(), branchEntry.getVersionId() ) )
            .forEach( routableNodeVersionIds::add );
        final NodeCommitEntry commitEntry = nodeStorageService.commit( nodeCommitEntry, routableNodeVersionIds.build(), context );

        refresh( RefreshMode.STORAGE );

        return commitEntry;
    }

    @Override
    public NodeCommitEntry getCommit( final NodeCommitId nodeCommitId )
    {
        verifyContext();
        return nodeStorageService.getCommit( nodeCommitId, InternalContext.from( ContextAccessor.current() ) );
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
}
