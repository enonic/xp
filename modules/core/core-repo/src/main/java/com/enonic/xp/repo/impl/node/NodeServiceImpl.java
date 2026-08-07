package com.enonic.xp.repo.impl.node;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.elasticsearch.index.IndexNotFoundException;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.io.ByteSource;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.index.ChildOrder;
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
import com.enonic.xp.node.GetNodeVersionsResult;
import com.enonic.xp.node.ImportNodeParams;
import com.enonic.xp.node.ImportNodeResult;
import com.enonic.xp.node.ListNodesByParentParams;
import com.enonic.xp.node.ListNodesByParentResult;
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
import com.enonic.xp.node.NodeListEntry;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionIds;
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
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeBranchEntries;
import com.enonic.xp.repo.impl.NodeBranchEntry;
import com.enonic.xp.repo.impl.NodeEvents;
import com.enonic.xp.repo.impl.SearchPreference;
import com.enonic.xp.repo.impl.binary.BinaryService;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.repository.BranchNotFoundException;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryNotFoundException;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.trace.Traced;
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

    @SuppressWarnings("unused")
    @Reference
    private @Nullable RepositoryService repositoryService;

    @Activate
    public NodeServiceImpl( @Reference final IndexServiceInternal indexServiceInternal,
                            @Reference final NodeStorageService nodeStorageService, @Reference final NodeSearchService nodeSearchService,
                            @Reference final EventPublisher eventPublisher, @Reference final BinaryService binaryService )
    {
        this.indexServiceInternal = indexServiceInternal;
        this.nodeStorageService = nodeStorageService;
        this.nodeSearchService = nodeSearchService;
        this.eventPublisher = eventPublisher;
        this.binaryService = binaryService;
    }

    @Override
    @Traced("node.getById")
    public Node getById( final NodeId id )
    {
        Tracer.withCurrent( trace -> {
            trace.attribute( "id", id.toString() );
            trace.attribute( "repo", Objects.toString( ContextAccessor.current().getRepositoryId(), null ) );
            trace.attribute( "branch", Objects.toString( ContextAccessor.current().getBranch(), null ) );
        } );

        final Node node = executeGetById( id );

        Tracer.attribute( "path", node.path().toString() );

        return node;
    }

    private Node executeGetById( final NodeId id )
    {
        final Node node = doGetWithExceptionTranslation( () -> doGetById( id ) );
        if ( node == null )
        {
            final Context ctx = ContextAccessor.current();
            verifyBranchExists( ctx.getRepositoryId(), ctx.getBranch() );
            throw new NodeNotFoundException( "Node with id " + id + " not found in branch " + ctx.getBranch() );
        }
        return node;
    }

    @Override
    @Traced("node.getByIdAndVersionId")
    public Node getByIdAndVersionId( final NodeId id, final NodeVersionId versionId )
    {
        verifyContext();
        Tracer.withCurrent( trace -> {
            trace.attribute( "id", id.toString() );
            trace.attribute( "versionId", versionId.toString() );
            trace.attribute( "repo", Objects.toString( ContextAccessor.current().getRepositoryId(), null ) );
            trace.attribute( "branch", Objects.toString( ContextAccessor.current().getBranch(), null ) );
        } );

        final Node node = executeGetByIdAndVersionId( id, versionId );

        Tracer.attribute( "path", node.path().toString() );

        return node;
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

    @Override
    public @Nullable NodeVersion getVersion( final NodeId nodeId, final NodeVersionId nodeVersionId )
    {
        verifyContext();
        final NodeVersion nodeVersion =
            this.nodeStorageService.getVersion( nodeVersionId, InternalContext.from( ContextAccessor.current() ) );
        if ( nodeVersion == null || !nodeVersion.getNodeId().equals( nodeId ) )
        {
            return null;
        }
        return nodeVersion;
    }

    private @Nullable Node doGetById( final NodeId id )
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
    @Traced("node.getByPath")
    public @Nullable Node getByPath( final NodePath path )
    {
        Tracer.withCurrent( trace -> {
            trace.attribute( "path", path.toString() );
            trace.attribute( "repo", Objects.toString( ContextAccessor.current().getRepositoryId(), null ) );
            trace.attribute( "branch", Objects.toString( ContextAccessor.current().getBranch(), null ) );
        } );

        final Node node = executeGetByPath( path );

        Tracer.withCurrent( trace -> {
            if ( node != null )
            {
                trace.attribute( "id", node.id().toString() );
            }
        } );

        return node;
    }

    private @Nullable Node executeGetByPath( final NodePath path )
    {
        final Node node = doGetWithExceptionTranslation( () -> doGetByPath( path ) );
        if ( node == null )
        {
            final Context ctx = ContextAccessor.current();
            verifyBranchExists( ctx.getRepositoryId(), ctx.getBranch() );
        }
        return node;
    }

    private @Nullable Node doGetByPath( final NodePath path )
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
    @Traced("node.getByIds")
    public Nodes getByIds( final NodeIds ids )
    {
        verifyContext();

        Tracer.withCurrent( trace -> {
            trace.attribute( "id", ids.stream().map( NodeId::toString ).toList() );
            trace.attribute( "repo", Objects.toString( ContextAccessor.current().getRepositoryId(), null ) );
            trace.attribute( "branch", Objects.toString( ContextAccessor.current().getBranch(), null ) );
        } );

        return executeGetByIds( ids );
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
    @Traced("node.getByPaths")
    public Nodes getByPaths( final NodePaths paths )
    {
        verifyContext();

        Tracer.withCurrent( trace -> {
            trace.attribute( "path", paths.stream().map( NodePath::toString ).toList() );
            trace.attribute( "repo", Objects.toString( ContextAccessor.current().getRepositoryId(), null ) );
            trace.attribute( "branch", Objects.toString( ContextAccessor.current().getBranch(), null ) );
        } );

        return executeGetByPaths( paths );
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
    @Deprecated
    @Traced("node.findByParent")
    public FindNodesByParentResult findByParent( final FindNodesByParentParams params )
    {
        verifyContext();
        Tracer.withCurrent( trace -> {
            trace.attribute( "parent", Objects.toString( params.getParentPath() != null ? params.getParentPath() : params.getParentId(), null ) );
            if ( params.getFrom() != null )
            {
                trace.attribute( "from", params.getFrom() );
            }
            if ( params.getSize() != null )
            {
                trace.attribute( "size", params.getSize() );
            }
            trace.attribute( "repo", Objects.toString( ContextAccessor.current().getRepositoryId(), null ) );
            trace.attribute( "branch", Objects.toString( ContextAccessor.current().getBranch(), null ) );
        } );

        final FindNodesByParentResult result = executeFindByParent( params );

        Tracer.attribute( "hits", result.getTotalHits() );

        return result;
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
    @Traced("node.list")
    public ListNodesByParentResult list( final ListNodesByParentParams params )
    {
        verifyContext();
        Tracer.withCurrent( trace -> {
            trace.attribute( "parent", params.getParentPath().toString() );
            trace.attribute( "repo", Objects.toString( ContextAccessor.current().getRepositoryId(), null ) );
            trace.attribute( "branch", Objects.toString( ContextAccessor.current().getBranch(), null ) );
        } );

        final NodeBranchEntries entries = FindNodeBranchEntriesByParentCommand.create()
            .parentPath( params.getParentPath() )
            .recursive( params.isRecursive() )
            .requiredPermission( Permission.READ )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.nodeStorageService )
            .searchService( this.nodeSearchService )
            .build()
            .execute();

        final ListNodesByParentResult.Builder result = ListNodesByParentResult.create();
        for ( final NodeBranchEntry entry : entries )
        {
            result.addEntry( NodeListEntry.create()
                                 .nodeId( entry.getNodeId() )
                                 .nodePath( entry.getNodePath() )
                                 .timestamp( entry.getTimestamp() )
                                 .build() );
        }

        final ListNodesByParentResult listResult = result.build();

        Tracer.attribute( "hits", (long) listResult.getSize() );

        return listResult;
    }

    @Override
    @Traced("node.findByQuery")
    public FindNodesByQueryResult findByQuery( final NodeQuery nodeQuery )
    {
        verifyContext();
        Tracer.withCurrent( trace -> {
            trace.attribute( "query", Objects.toString( nodeQuery.getQuery(), null ) );
            trace.attribute( "filter", Objects.toString( nodeQuery.getQueryFilters(), null ) );
            trace.attribute( "from", nodeQuery.getFrom() );
            trace.attribute( "size", nodeQuery.getSize() );
            trace.attribute( "repo", Objects.toString( ContextAccessor.current().getRepositoryId(), null ) );
            trace.attribute( "branch", Objects.toString( ContextAccessor.current().getBranch(), null ) );
        } );

        final FindNodesByQueryResult result = executeFindByQuery( nodeQuery );

        Tracer.attribute( "hits", result.getTotalHits() );

        return result;
    }

    private FindNodesByQueryResult executeFindByQuery( final NodeQuery nodeQuery )
    {
        return FindNodesByQueryCommand.create()
            .query( applyChildOrderOfParent( nodeQuery ) )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.nodeStorageService )
            .searchService( this.nodeSearchService )
            .build()
            .execute();
    }

    /**
     * A query restricted to a parent and carrying no order expressions of its own comes back in the child order of the parent, the same
     * order findByParent used. Resolving the order costs a read of the parent, so it is skipped whenever the query orders explicitly or
     * fetches no hits at all.
     */
    private NodeQuery applyChildOrderOfParent( final NodeQuery nodeQuery )
    {
        if ( nodeQuery.getParent() == null || !nodeQuery.getOrderBys().isEmpty() || nodeQuery.getSize() == 0 )
        {
            return nodeQuery;
        }

        final Node parentNode = NodeHelper.runAsAdmin( () -> doGetByPath( nodeQuery.getParent() ) );
        final ChildOrder childOrder = parentNode != null ? parentNode.getChildOrder() : ChildOrder.defaultOrder();

        return NodeQuery.create( nodeQuery ).setOrderExpressions( childOrder.getOrderExpressions() ).build();
    }

    @Override
    @Traced("node.findByQueryMulti")
    public FindNodesByMultiRepoQueryResult findByQuery( final MultiRepoNodeQuery multiNodeQuery )
    {
        if ( multiNodeQuery.getSearchTargets().isEmpty() )
        {
            throw new IllegalArgumentException( "SearchTargets must not be empty" );
        }

        multiNodeQuery.getSearchTargets()
            .forEach( searchTarget -> verifyBranchExists( searchTarget.getRepositoryId(), searchTarget.getBranch() ) );

        Tracer.withCurrent( trace -> {
            trace.attribute( "query", Objects.toString( multiNodeQuery.getNodeQuery().getQuery(), null ) );
            trace.attribute( "filter", Objects.toString( multiNodeQuery.getNodeQuery().getQueryFilters(), null ) );
            trace.attribute( "from", multiNodeQuery.getNodeQuery().getFrom() );
            trace.attribute( "size", multiNodeQuery.getNodeQuery().getSize() );

            trace.attribute( "searchTargets", StreamSupport.stream( multiNodeQuery.getSearchTargets().spliterator(), false )
                .map( searchTarget -> searchTarget.getRepositoryId() + ":" + searchTarget.getBranch() )
                .toList() );
        } );

        final FindNodesByMultiRepoQueryResult result = executeFindByQuery( multiNodeQuery );

        Tracer.attribute( "hits", result.getTotalHits() );

        return result;
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
        verifyBranches( ContextAccessor.current().getBranch(), params.getTarget() );

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
    public GetNodeVersionsResult getVersions( final GetNodeVersionsParams params )
    {
        verifyContext();
        return GetNodeVersionsCommand.create().params( params ).searchService( this.nodeSearchService ).build().execute();
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
    @Traced("node.refresh")
    public void refresh( final RefreshMode refreshMode )
    {
        verifyContext();
        Tracer.withCurrent( trace -> {
            trace.attribute( "refreshMode", refreshMode.toString() );
            trace.attribute( "repo", Objects.toString( ContextAccessor.current().getRepositoryId(), null ) );
        } );

        RefreshCommand.create().indexServiceInternal( this.indexServiceInternal ).refreshMode( refreshMode ).build().execute();
    }

    @Override
    public ApplyNodePermissionsResult applyPermissions( final ApplyNodePermissionsParams params )
    {
        verifyContext();
        final ApplyPermissionsResult internalResult = ApplyNodePermissionsCommand.create()
            .params( params )
            .indexServiceInternal( this.indexServiceInternal )
            .searchService( this.nodeSearchService )
            .storageService( this.nodeStorageService )
            .searchService( this.nodeSearchService )
            .build()
            .execute();

        final ApplyNodePermissionsResult.Builder result = ApplyNodePermissionsResult.create();

        for ( final Map.Entry<NodeId, List<ApplyPermissionsResult.BranchResult>> entry : internalResult.getResults().entrySet() )
        {
            for ( final ApplyPermissionsResult.BranchResult br : entry.getValue() )
            {
                result.addResult( entry.getKey(), br.branch(), br.nodeVersion() != null ? br.nodeVersion().getNodeVersionId() : null,
                                  br.permissions() );

                if ( br.nodeVersion() != null )
                {
                    final InternalContext internalContext =
                        InternalContext.create( ContextAccessor.current() ).branch( br.branch() ).build();
                    eventPublisher.publish( NodeEvents.permissionsUpdated( br.nodeVersion(), internalContext ) );
                }
            }
        }

        return result.build();
    }

    @Override
    @Traced("node.getBinary")
    public ByteSource getBinary( final NodeId nodeId, final BinaryReference reference )
    {
        verifyContext();
        Tracer.withCurrent( trace -> {
            trace.attribute( "id", nodeId.toString() );
            trace.attribute( "reference", reference.toString() );
            trace.attribute( "repo", Objects.toString( ContextAccessor.current().getRepositoryId(), null ) );
            trace.attribute( "branch", Objects.toString( ContextAccessor.current().getBranch(), null ) );
        } );

        final ByteSource byteSource = executeGetBinary( nodeId, reference );

        Tracer.attribute( "size", byteSource.sizeIfKnown().or( -1L ) );

        return byteSource;
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
    @Traced("node.getBinary")
    public ByteSource getBinary( final NodeId nodeId, final NodeVersionId nodeVersionId, final BinaryReference reference )
    {
        verifyContext();
        Tracer.withCurrent( trace -> {
            trace.attribute( "id", nodeId.toString() );
            trace.attribute( "versionId", nodeVersionId.toString() );
            trace.attribute( "reference", reference.toString() );
            trace.attribute( "repo", Objects.toString( ContextAccessor.current().getRepositoryId(), null ) );
            trace.attribute( "branch", Objects.toString( ContextAccessor.current().getBranch(), null ) );
        } );

        final ByteSource byteSource = executeGetBinary( nodeId, nodeVersionId, reference );

        Tracer.attribute( "size", byteSource.sizeIfKnown().or( -1L ) );

        return byteSource;
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
            .versionAttributesResolver( params.getVersionAttributesResolver() )
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
    @Traced("node.exists")
    public boolean nodeExists( final NodeId nodeId )
    {
        verifyContext();
        Tracer.withCurrent( trace -> {
            trace.attribute( "id", nodeId.toString() );
            trace.attribute( "repo", Objects.toString( ContextAccessor.current().getRepositoryId(), null ) );
            trace.attribute( "branch", Objects.toString( ContextAccessor.current().getBranch(), null ) );
        } );

        final boolean exists = NodeHelper.runAsAdmin( () -> doGetById( nodeId ) ) != null;

        Tracer.attribute( "exists", exists );

        return exists;
    }

    @Override
    @Traced("node.exists")
    public boolean nodeExists( final NodePath nodePath )
    {
        verifyContext();

        Tracer.withCurrent( trace -> {
            trace.attribute( "path", nodePath.toString() );
            trace.attribute( "repo", Objects.toString( ContextAccessor.current().getRepositoryId(), null ) );
            trace.attribute( "branch", Objects.toString( ContextAccessor.current().getBranch(), null ) );
        } );

        final boolean exists = NodeHelper.runAsAdmin( () -> executeGetByPath( nodePath ) ) != null;

        Tracer.attribute( "exists", exists );

        return exists;
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

        final NodeVersionIds nodeVersionIds = nodeStorageService.getNodeBranchEntries( nodeIds, context )
            .stream()
            .map( NodeBranchEntry::getVersionId )
            .collect( NodeVersionIds.collector() );

        return doCommit( nodeCommitEntry, nodeVersionIds );
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
        verifyBranches( currentContext.getBranch() );
    }

    private void verifyBranches( final Branch... branches )
    {
        final Context currentContext = ContextAccessor.current();
        for ( Branch branch : branches )
        {
            verifyBranchExists( currentContext.getRepositoryId(), branch );
        }
    }

    private static <T> T doGetWithExceptionTranslation( final Supplier<T> action )
    {
        try
        {
            return action.get();
        }
        catch ( IndexNotFoundException e )
        {
            throw new RepositoryNotFoundException( ContextAccessor.current().getRepositoryId() );
        }
    }

    private void verifyBranchExists( final RepositoryId repositoryId, final Branch branch )
    {
        final boolean rootExists;
        try
        {
            rootExists = this.nodeStorageService.exists( NodeId.ROOT, InternalContext.create( ContextAccessor.current() )
                .repositoryId( repositoryId )
                .branch( branch )
                .build() );
        }
        catch ( IndexNotFoundException e )
        {
            throw new RepositoryNotFoundException( repositoryId );
        }
        if ( !rootExists )
        {
            throw new BranchNotFoundException( branch );
        }
    }

    private PatchNodeParams convertUpdateParams( final UpdateNodeParams params )
    {
        return PatchNodeParams.create()
            .id( params.getId() )
            .path( params.getPath() )
            .editor( params.getEditor() )
            .setBinaryAttachments( params.getBinaryAttachments() )
            .versionAttributesResolver( params.getVersionAttributesResolver() )
            .refresh( params.getRefresh() )
            .build();
    }
}
