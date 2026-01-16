package com.enonic.xp.repo.impl.node;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.io.ByteSource;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.ApplyNodePermissionsParams;
import com.enonic.xp.node.ApplyNodePermissionsResult;
import com.enonic.xp.node.ApplyVersionAttributesParams;
import com.enonic.xp.node.Attributes;
import com.enonic.xp.node.CommitNodeParams;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.DeleteNodeParams;
import com.enonic.xp.node.DeleteNodeResult;
import com.enonic.xp.node.DuplicateNodeParams;
import com.enonic.xp.node.DuplicateNodeResult;
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
import com.enonic.xp.node.MoveNodeParams;
import com.enonic.xp.node.MoveNodeResult;
import com.enonic.xp.node.MultiRepoNodeQuery;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.node.NodeCommitQuery;
import com.enonic.xp.node.NodeCommitQueryResult;
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
import com.enonic.xp.node.NodeVersionIds;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.node.NodeVersionQuery;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.PatchNodeParams;
import com.enonic.xp.node.PatchNodeResult;
import com.enonic.xp.node.PushNodeParams;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.ResolveSyncWorkResult;
import com.enonic.xp.node.SearchTarget;
import com.enonic.xp.node.SortNodeParams;
import com.enonic.xp.node.SortNodeResult;
import com.enonic.xp.node.SyncWorkResolverParams;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeBranchEntries;
import com.enonic.xp.repo.impl.NodeBranchEntry;
import com.enonic.xp.repo.impl.NodeEvents;
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
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.util.BinaryReference;

@NullMarked
@Component(immediate = true)
public class NodeServiceImpl
    implements NodeService
{
    private final IndexServiceInternal indexServiceInternal;

    private final NodeStorageService nodeStorageService;

    private final NodeSearchService nodeSearchService;

    private final EventPublisher eventPublisher;

    private final BinaryService binaryService;

    private final RepositoryService repositoryService;

    @Activate
    public NodeServiceImpl( @Reference final IndexServiceInternal indexServiceInternal,
                            @Reference final NodeStorageService nodeStorageService, @Reference final NodeSearchService nodeSearchService,
                            @Reference final EventPublisher eventPublisher, @Reference final BinaryService binaryService,
                            @Reference final RepositoryService repositoryService )
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
        verifyContext();
        return Tracer.trace( "node.getById", trace -> {
            trace.put( "id", id );
            trace.put( "repo", ContextAccessor.current().getRepositoryId() );
            trace.put( "branch", ContextAccessor.current().getBranch() );
        }, () -> executeGetById( id ), ( trace, node ) -> trace.put( "path", node.path() ) );
    }

    private Node executeGetById( final NodeId id )
    {
        final Node node = doGetById( id );

        if ( node == null )
        {
            throw new NodeNotFoundException( "Node with id " + id + " not found in branch " + ContextAccessor.current().getBranch() );
        }

        return node;
    }

    @Override
    public Node getByIdAndVersionId( final NodeId id, final NodeVersionId versionId )
    {
        verifyContext();
        return Tracer.trace( "node.getByIdAndVersionId", trace -> {
            trace.put( "id", id );
            trace.put( "versionId", versionId );
            trace.put( "repo", ContextAccessor.current().getRepositoryId() );
            trace.put( "branch", ContextAccessor.current().getBranch() );
        }, () -> executeGetByIdAndVersionId( id, versionId ), ( trace, node ) -> trace.put( "path", node.path() ) );
    }

    private Node executeGetByIdAndVersionId( final NodeId id, final NodeVersionId versionId )
    {
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
            throw new NodeNotFoundException(
                "Node with id " + id + " and versionId " + versionId + " not found in branch " + ContextAccessor.current().getBranch() );
        }

        return node;
    }

    @Nullable
    private Node doGetById( final NodeId id )
    {
        return GetNodeByIdCommand.create()
            .id( id )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.nodeStorageService )
            .searchService( this.nodeSearchService )
            .build()
            .execute();
    }

    @Override
    public Node getByPath( final NodePath path )
    {
        verifyContext();

        return Tracer.trace( "node.getByPath", trace -> {
            trace.put( "path", path );
            trace.put( "repo", ContextAccessor.current().getRepositoryId() );
            trace.put( "branch", ContextAccessor.current().getBranch() );
        }, () -> executeGetByPath( path ), ( trace, node ) -> {
            if ( node != null )
            {
                trace.put( "id", node.id() );
            }
        } );
    }

    @Nullable
    private Node executeGetByPath( final NodePath path )
    {
        return GetNodeByPathCommand.create()
            .nodePath( path )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.nodeStorageService )
            .searchService( this.nodeSearchService )
            .build()
            .execute();
    }

    @Override
    public Nodes getByIds( final NodeIds ids )
    {
        verifyContext();

        return Tracer.trace( "node.getByIds", trace -> {
            trace.put( "id", ids );
            trace.put( "repo", ContextAccessor.current().getRepositoryId() );
            trace.put( "branch", ContextAccessor.current().getBranch() );
        }, () -> executeGetByIds( ids ) );
    }

    private Nodes executeGetByIds( final NodeIds ids )
    {
        return GetNodesByIdsCommand.create()
            .ids( ids )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.nodeStorageService )
            .searchService( this.nodeSearchService )
            .build()
            .execute();
    }

    @Override
    public Nodes getByPaths( final NodePaths paths )
    {
        verifyContext();

        return Tracer.trace( "node.getByPaths", trace -> {
            trace.put( "path", paths );
            trace.put( "repo", ContextAccessor.current().getRepositoryId() );
            trace.put( "branch", ContextAccessor.current().getBranch() );
        }, () -> executeGetByPaths( paths ) );
    }

    private Nodes executeGetByPaths( final NodePaths paths )
    {
        return GetNodesByPathsCommand.create()
            .paths( paths )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.nodeStorageService )
            .searchService( this.nodeSearchService )
            .build()
            .execute();
    }

    @Override
    public FindNodesByParentResult findByParent( final FindNodesByParentParams params )
    {
        verifyContext();
        return Tracer.trace( "node.findByParent", trace -> {
            trace.put( "parent", params.getParentPath() != null ? params.getParentPath() : params.getParentId() );
            trace.put( "from", params.getFrom() );
            trace.put( "size", params.getSize() );
            trace.put( "repo", ContextAccessor.current().getRepositoryId() );
            trace.put( "branch", ContextAccessor.current().getBranch() );
        }, () -> executeFindByParent( params ), ( ( trace, result ) -> trace.put( "hits", result.getTotalHits() ) ) );
    }

    private FindNodesByParentResult executeFindByParent( final FindNodesByParentParams params )
    {
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
        verifyContext();
        return Tracer.trace( "node.findByQuery", trace -> {
            trace.put( "query", nodeQuery.getQuery() );
            trace.put( "filter", nodeQuery.getQueryFilters() );
            trace.put( "from", nodeQuery.getFrom() );
            trace.put( "size", nodeQuery.getSize() );
            trace.put( "repo", ContextAccessor.current().getRepositoryId() );
            trace.put( "branch", ContextAccessor.current().getBranch() );
        }, () -> executeFindByQuery( nodeQuery ), ( trace, result ) -> trace.put( "hits", result.getTotalHits() ) );
    }

    private FindNodesByQueryResult executeFindByQuery( final NodeQuery nodeQuery )
    {
        return FindNodesByQueryCommand.create()
            .query( nodeQuery )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.nodeStorageService )
            .searchService( this.nodeSearchService )
            .build()
            .execute();
    }

    @Override
    public FindNodesByMultiRepoQueryResult findByQuery( final MultiRepoNodeQuery multiNodeQuery )
    {
        multiNodeQuery.getSearchTargets()
            .forEach( searchTarget -> verifyBranchExists( searchTarget.getBranch(), searchTarget.getRepositoryId() ) );

        return Tracer.trace( "node.findByQueryMulti", trace -> {
            trace.put( "query", multiNodeQuery.getNodeQuery().getQuery() );
            trace.put( "filter", multiNodeQuery.getNodeQuery().getQueryFilters() );
            trace.put( "from", multiNodeQuery.getNodeQuery().getFrom() );
            trace.put( "size", multiNodeQuery.getNodeQuery().getSize() );

            final Spliterator<SearchTarget> searchTargetSpliterator = multiNodeQuery.getSearchTargets().spliterator();
            trace.put( "repo", StreamSupport.stream( searchTargetSpliterator, false )
                .map( searchTarget -> searchTarget.getRepositoryId().toString() )
                .collect( Collectors.joining( "," ) ) );
            trace.put( "branch", StreamSupport.stream( searchTargetSpliterator, false )
                .map( searchTarget -> searchTarget.getBranch().toString() )
                .collect( Collectors.joining( "," ) ) );
        }, () -> executeFindByQuery( multiNodeQuery ), ( trace, result ) -> trace.put( "hits", result.getTotalHits() ) );
    }

    private FindNodesByMultiRepoQueryResult executeFindByQuery( final MultiRepoNodeQuery nodeQuery )
    {
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
        final Node createdNode = CreateNodeCommand.create()
            .params( params )
            .indexServiceInternal( this.indexServiceInternal )
            .binaryService( this.binaryService )
            .storageService( this.nodeStorageService )
            .searchService( this.nodeSearchService )
            .build()
            .execute();

        this.eventPublisher.publish( NodeEvents.created( createdNode, InternalContext.from( ContextAccessor.current() ) ) );

        return createdNode;
    }

    public Node update( final UpdateNodeParams params )
    {
        verifyContext();

        final PatchNodeResult result = PatchNodeCommand.create()
            .params( convertUpdateParams( params ) )
            .binaryService( this.binaryService )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.nodeStorageService )
            .searchService( this.nodeSearchService )
            .build()
            .execute();

        for ( PatchNodeResult.BranchResult branchResult : result.getResults() )
        {
            if ( branchResult.node() != null )
            {
                final InternalContext internalContext =
                    InternalContext.create( ContextAccessor.current() ).branch( branchResult.branch() ).build();
                this.eventPublisher.publish( NodeEvents.updated( branchResult.node(), internalContext ) );
            }
        }

        return result.getResult( ContextAccessor.current().getBranch() );
    }

    @Override
    public PatchNodeResult patch( final PatchNodeParams params )
    {
        verifyContext();

        final PatchNodeResult result = PatchNodeCommand.create()
            .params( params )
            .indexServiceInternal( this.indexServiceInternal )
            .binaryService( this.binaryService )
            .storageService( this.nodeStorageService )
            .searchService( this.nodeSearchService )
            .build()
            .execute();

        final Branch mainBranch = ContextAccessor.current().getBranch();
        final Node mainBranchNode = result.getResult( ContextAccessor.current().getBranch() );
        final NodeVersionId mainBranchVersion = mainBranchNode != null ? mainBranchNode.getNodeVersionId() : null;

        for ( PatchNodeResult.BranchResult br : result.getResults() )
        {
            if ( br.node() == null )
            {
                continue;
            }

            ContextBuilder.from( ContextAccessor.current() ).branch( br.branch() ).build().runWith( () -> {
                final InternalContext internalContext = InternalContext.from( ContextAccessor.current() );
                if ( ( br.branch().equals( mainBranch ) ) || !br.node().getNodeVersionId().equals( mainBranchVersion ) )
                {
                    eventPublisher.publish( NodeEvents.patched( br.node(), internalContext ) );
                }
                else
                {
                    eventPublisher.publish( NodeEvents.pushed( br.node(), internalContext ) );
                }
            } );
        }

        return result;
    }

    @Override
    public MoveNodeResult move( final MoveNodeParams params )
    {
        verifyContext();
        final MoveNodeResult moveNodeResult = MoveNodeCommand.create()
            .params( params )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.nodeStorageService )
            .searchService( this.nodeSearchService )
            .build()
            .execute();

        final List<MoveNodeResult.MovedNode> movedNodes = moveNodeResult.getMovedNodes();
        final InternalContext internalContext = InternalContext.from( ContextAccessor.current() );
        this.eventPublisher.publish( NodeEvents.moved( movedNodes, internalContext ) );

        return moveNodeResult;
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
            this.eventPublisher.publish( NodeEvents.deleted( deletedNodes, InternalContext.from( ContextAccessor.current() ) ) );
        }

        final DeleteNodeResult.Builder builder = DeleteNodeResult.create();
        for ( NodeBranchEntry deletedNode : deletedNodes )
        {
            builder.add( new DeleteNodeResult.Result( deletedNode.getNodeId(), deletedNode.getVersionId() ) );
        }
        return builder.build();
    }

    @Override
    public PushNodesResult push( final PushNodeParams params )
    {
        verifyContext();
        verifyBranchExists( params.getTarget(), ContextAccessor.current().getRepositoryId() );

        final PushNodesResult pushNodesResult = PushNodesCommand.create()
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.nodeStorageService )
            .searchService( this.nodeSearchService )
            .params( params )
            .build()
            .execute();

        if ( !pushNodesResult.getSuccessful().isEmpty() )
        {
            this.eventPublisher.publish( NodeEvents.pushed( pushNodesResult.getSuccessful(),
                                                            InternalContext.create( ContextAccessor.current() )
                                                                .branch( params.getTarget() )
                                                                .build() ) );
        }

        return pushNodesResult;
    }

    @Override
    public DuplicateNodeResult duplicate( final DuplicateNodeParams params )
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

        final InternalContext internalContext = InternalContext.from( ContextAccessor.current() );

        this.eventPublisher.publish( NodeEvents.duplicated( result.getNode(), internalContext ) );
        result.getChildren().forEach( child -> this.eventPublisher.publish( NodeEvents.created( child, internalContext ) ) );

        return result;
    }

    @Override
    public NodeComparison compare( final NodeId nodeId, final Branch target )
    {
        verifyContext();
        return CompareNodeCommand.create().nodeId( nodeId ).target( target ).storageService( this.nodeStorageService ).build().execute();
    }

    @Override
    public NodeComparisons compare( final NodeIds nodeIds, final Branch target )
    {
        verifyContext();
        return CompareNodesCommand.create().nodeIds( nodeIds ).target( target ).storageService( this.nodeStorageService ).build().execute();
    }

    @Override
    public NodeVersionQueryResult findVersions( final GetNodeVersionsParams params )
    {
        verifyContext();

        final NodeVersionQuery query = NodeVersionQuery.create()
            .size( params.getSize() )
            .from( params.getFrom() )
            .nodeId( params.getNodeId() )
            .addOrderBy( FieldOrderExpr.create( VersionIndexPath.TIMESTAMP, OrderExpr.Direction.DESC ) )
            .build();

        return FindNodeVersionsCommand.create().query( query ).searchService( this.nodeSearchService ).build().execute();
    }

    @Override
    public NodeVersionQueryResult findVersions( final NodeVersionQuery query )
    {
        verifyContext();

        return FindNodeVersionsCommand.create().query( query ).searchService( this.nodeSearchService ).build().execute();
    }

    @Override
    public NodeCommitQueryResult findCommits( final NodeCommitQuery query )
    {
        verifyContext();

        return FindNodeCommitsCommand.create().query( query ).searchService( this.nodeSearchService ).build().execute();
    }

    @Override
    public GetActiveNodeVersionsResult getActiveVersions( final GetActiveNodeVersionsParams params )
    {
        verifyContext();
        return GetActiveNodeVersionsCommand.create()
            .nodeId( params.getNodeId() )
            .branches( params.getBranches() )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.nodeStorageService )
            .searchService( this.nodeSearchService )
            .build()
            .execute();
    }

    @Override
    public NodeVersion getByNodeVersionKey( final NodeVersionKey nodeVersionKey )
    {
        verifyContext();
        return this.nodeStorageService.getNodeVersion( nodeVersionKey, InternalContext.from( ContextAccessor.current() ) );
    }

    @Override
    public ResolveSyncWorkResult resolveSyncWork( final SyncWorkResolverParams params )
    {
        verifyContext();
        return ResolveSyncWorkCommand.create()
            .target( params.getBranch() )
            .nodeId( params.getNodeId() )
            .excludedNodeIds( params.getExcludedNodeIds() )
            .includeChildren( params.isIncludeChildren() )
            .includeDependencies( params.isIncludeDependencies() )
            .filter( params.getFilter() )
            .statusesToStopDependenciesSearch( params.getStatusesToStopDependenciesSearch() )
            .indexServiceInternal( indexServiceInternal )
            .storageService( this.nodeStorageService )
            .searchService( this.nodeSearchService )
            .build()
            .execute();
    }

    @Override
    public SortNodeResult sort( final SortNodeParams params )
    {
        verifyContext();
        final SortNodeResult result = SortNodeCommand.create()
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.nodeStorageService )
            .searchService( this.nodeSearchService )
            .params( params )
            .build()
            .execute();

        final InternalContext internalContext = InternalContext.from( ContextAccessor.current() );

        this.eventPublisher.publish( NodeEvents.sorted( result.getNode(), internalContext ) );

        result.getReorderedNodes()
            .stream()
            .map( node -> NodeEvents.updated( node, internalContext ) )
            .forEach( this.eventPublisher::publish );
        return result;
    }

    @Override
    public void refresh( final RefreshMode refreshMode )
    {
        verifyContext();
        Tracer.trace( "node.refresh", trace -> {
            trace.put( "refreshMode", refreshMode );
            trace.put( "repo", ContextAccessor.current().getRepositoryId() );
        }, () -> executeRefresh( refreshMode ) );
    }

    private Void executeRefresh( final RefreshMode refreshMode )
    {
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

        final Map<NodeId, List<ApplyNodePermissionsResult.BranchResult>> resultsByNodeId = result.getResults()
            .entrySet()
            .stream()
            .flatMap( entry -> entry.getValue()
                .stream()
                .filter( br -> br.nodeVersionId() != null )
                .collect( Collectors.groupingBy( br -> entry.getKey() ) )
                .entrySet()
                .stream() )
            .collect( Collectors.toMap( Map.Entry::getKey, Map.Entry::getValue ) );

        for ( final Map.Entry<NodeId, List<ApplyNodePermissionsResult.BranchResult>> entry : resultsByNodeId.entrySet() )
        {
            final List<ApplyNodePermissionsResult.BranchResult> branchResults = entry.getValue();

            for ( final ApplyNodePermissionsResult.BranchResult br : branchResults )
            {
                if ( br.nodeVersionId() == null )
                {
                    continue;
                }

                final Context context = ContextBuilder.from( ContextAccessor.current() ).branch( br.branch() ).build();

                context.runWith( () -> eventPublisher.publish(
                    NodeEvents.permissionsUpdated( entry.getKey(), InternalContext.from( ContextAccessor.current() ) ) ) );
            }
        }

        return result;
    }

    @Override
    public ByteSource getBinary( final NodeId nodeId, final BinaryReference reference )
    {
        verifyContext();
        return Tracer.trace( "node.getBinary", trace -> {
            trace.put( "id", nodeId );
            trace.put( "reference", reference );
            trace.put( "repo", ContextAccessor.current().getRepositoryId() );
            trace.put( "branch", ContextAccessor.current().getBranch() );
        }, () -> executeGetBinary( nodeId, reference ), ( trace, byteSource ) -> trace.put( "size", byteSource.sizeIfKnown().or( -1L ) ) );
    }

    private ByteSource executeGetBinary( final NodeId nodeId, final BinaryReference reference )
    {
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
        verifyContext();
        return Tracer.trace( "node.getBinary", trace -> {
            trace.put( "id", nodeId );
            trace.put( "versionId", nodeVersionId );
            trace.put( "reference", reference );
            trace.put( "repo", ContextAccessor.current().getRepositoryId() );
            trace.put( "branch", ContextAccessor.current().getBranch() );
        }, () -> executeGetBinary( nodeId, nodeVersionId, reference ), ( trace, byteSource ) -> trace.put( "size", byteSource.sizeIfKnown().or( -1L ) ) );
    }

    private ByteSource executeGetBinary( final NodeId nodeId, final NodeVersionId nodeVersionId, final BinaryReference reference )
    {
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
    public ImportNodeResult importNode( final ImportNodeParams params )
    {
        verifyContext();
        final ImportNodeResult importNodeResult = ImportNodeCommand.create()
            .binaryAttachments( params.getBinaryAttachments() )
            .importNode( params.getNode() )
            .insertManualStrategy( params.getInsertManualStrategy() )
            .refresh( params.getRefresh() )
            .importPermissions( params.isImportPermissions() )
            .importPermissionsOnCreate( params.isImportPermissionsOnCreate() )
            .versionAttributes( params.getVersionAttributes() )
            .binaryBlobStore( this.binaryService )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.nodeStorageService )
            .searchService( this.nodeSearchService )
            .build()
            .execute();

        final InternalContext internalContext = InternalContext.from( ContextAccessor.current() );
        if ( importNodeResult.isPreExisting() )
        {
            this.eventPublisher.publish( NodeEvents.updated( importNodeResult.getNode(), internalContext ) );
        }
        else
        {
            this.eventPublisher.publish( NodeEvents.created( importNodeResult.getNode(), internalContext ) );
        }

        return importNodeResult;
    }

    @Override
    public LoadNodeResult loadNode( final LoadNodeParams params )
    {
        return LoadNodeCommand.create()
            .params( params )
            .searchService( this.nodeSearchService )
            .storageService( this.nodeStorageService )
            .indexServiceInternal( this.indexServiceInternal )
            .build()
            .execute();
    }

    @Override
    public boolean nodeExists( final NodeId nodeId )
    {
        verifyContext();
        return Tracer.trace( "node.exists", trace -> {
            trace.put( "id", nodeId );
            trace.put( "repo", ContextAccessor.current().getRepositoryId() );
            trace.put( "branch", ContextAccessor.current().getBranch() );
        }, () -> NodeHelper.runAsAdmin( () -> doGetById( nodeId ) ) != null, ( trace, exists ) -> trace.put( "exists", exists ) );
    }

    @Override
    public boolean nodeExists( final NodePath nodePath )
    {
        verifyContext();

        return Tracer.trace( "node.exists", trace -> {
            trace.put( "path", nodePath );
            trace.put( "repo", ContextAccessor.current().getRepositoryId() );
            trace.put( "branch", ContextAccessor.current().getBranch() );
        }, () -> NodeHelper.runAsAdmin( () -> executeGetByPath( nodePath ) ) != null, ( trace, exists ) -> trace.put( "exists", exists ) );
    }

    @Override
    public boolean hasUnpublishedChildren( final NodeId parent, final Branch target )
    {
        verifyContext();
        return HasUnpublishedChildrenCommand.create()
            .parent( parent )
            .target( target )
            .indexServiceInternal( indexServiceInternal )
            .storageService( nodeStorageService )
            .searchService( nodeSearchService )
            .build()
            .execute();
    }

    @Override
    public void importNodeVersion( final ImportNodeVersionParams params )
    {
        verifyRepositoryExists();

        LoadNodeVersionCommand.create()
            .nodeId( params.getNodeId() )
            .nodePath( params.getNodePath() )
            .nodeVersion( params.getNodeVersion() )
            .nodeVersionId( params.getNodeVersionId() )
            .nodeCommitId( params.getNodeCommitId() )
            .timestamp( params.getTimestamp() )
            .attributes( params.getAttributes() )
            .storageService( this.nodeStorageService )
            .searchService( this.nodeSearchService )
            .indexServiceInternal( this.indexServiceInternal )
            .build()
            .execute();
    }

    @Override
    public void importNodeCommit( final ImportNodeCommitParams params )
    {
        verifyRepositoryExists();

        LoadNodeCommitCommand.create()
            .nodeCommitId( params.getNodeCommitId() )
            .message( params.getMessage() )
            .committer( params.getCommitter() )
            .timestamp( params.getTimestamp() )
            .storageService( this.nodeStorageService )
            .searchService( this.nodeSearchService )
            .indexServiceInternal( this.indexServiceInternal )
            .build()
            .execute();
    }

    @Override
    public NodeCommitEntry commit( final CommitNodeParams params )
    {
        verifyContext();
        return doCommit( params.getNodeCommitEntry(), params.getNodeVersionIds() );
    }

    @Override
    public NodeCommitEntry commit( final NodeCommitEntry nodeCommitEntry, final NodeIds nodeIds )
    {
        verifyContext();

        final InternalContext context =
            InternalContext.create( ContextAccessor.current() ).searchPreference( SearchPreference.PRIMARY ).build();

        final NodeVersionIds nodeVersionIds = nodeStorageService.getBranchNodeVersions( nodeIds, context )
            .stream()
            .map( NodeBranchEntry::getVersionId )
            .collect( NodeVersionIds.collector() );

        return  doCommit( nodeCommitEntry , nodeVersionIds );
    }

    private NodeCommitEntry doCommit( NodeCommitEntry entry, NodeVersionIds versionIds )
    {
        verifyContext();

        final InternalContext context =
            InternalContext.create( ContextAccessor.current() ).searchPreference( SearchPreference.PRIMARY ).build();

        final NodeCommitEntry commit = nodeStorageService.commit( entry, versionIds, context );

        refresh( RefreshMode.STORAGE );

        return commit;
    }

    @Override
    @NullMarked
    public Attributes applyVersionAttributes( final ApplyVersionAttributesParams params )
    {
        verifyContext();

        final InternalContext context =
            InternalContext.create( ContextAccessor.current() ).searchPreference( SearchPreference.PRIMARY ).build();
        final Attributes result =
            nodeStorageService.changeAttributes( params.getNodeVersionId(), params.getAddAttributes(), params.getRemoveAttributes(),
                                                 context );
        refresh( RefreshMode.STORAGE );
        return result;
    }

    @Override
    public NodeCommitEntry getCommit( final NodeCommitId nodeCommitId )
    {
        verifyContext();
        return nodeStorageService.getCommit( nodeCommitId, InternalContext.from( ContextAccessor.current() ) );
    }

    private void verifyContext()
    {
        final Context currentContext = ContextAccessor.current();
        verifyBranchExists( currentContext.getBranch(), currentContext.getRepositoryId() );
    }

    private void verifyRepositoryExists()
    {
        NodeHelper.runAsAdmin( () -> {
            final RepositoryId repoId = ContextAccessor.current().getRepositoryId();
            final Repository repository = this.repositoryService.get( repoId );
            if ( repository == null )
            {
                throw new RepositoryNotFoundException( repoId );
            }
        } );
    }

    private void verifyBranchExists( final Branch branch, final RepositoryId repositoryId )
    {
        Objects.requireNonNull( branch, "Branch cannot be null" );
        NodeHelper.runAsAdmin( () -> {
            final Repository repository = this.repositoryService.get( repositoryId );
            if ( repository == null )
            {
                throw new RepositoryNotFoundException( repositoryId );
            }

            if ( !repository.getBranches().contains( branch ) )
            {
                throw new BranchNotFoundException( branch );
            }
        } );
    }

    private PatchNodeParams convertUpdateParams( final UpdateNodeParams params )
    {
        return PatchNodeParams.create()
            .id( params.getId() )
            .path( params.getPath() )
            .editor( params.getEditor() )
            .setBinaryAttachments( params.getBinaryAttachments() )
            .versionAttributes( params.getVersionAttributes() )
            .refresh( params.getRefresh() )
            .addBranches( Branches.from( ContextAccessor.current().getBranch() ) )
            .build();
    }
}
