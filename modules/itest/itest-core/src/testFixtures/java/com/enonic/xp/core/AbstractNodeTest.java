package com.enonic.xp.core;

import java.nio.file.Path;
import java.util.Map;
import java.util.function.Consumer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextAccessorSupport;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.app.VirtualAppInitializer;
import com.enonic.xp.core.impl.audit.AuditLogConstants;
import com.enonic.xp.core.impl.audit.AuditLogRepoInitializer;
import com.enonic.xp.core.nodb.NodbTenant;
import com.enonic.xp.core.nodb.NodbTestCluster;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.home.HomeDirSupport;
import com.enonic.xp.impl.scheduler.SchedulerRepoInitializer;
import com.enonic.xp.internal.blobstore.MemoryBlobStore;
import com.enonic.xp.itest.AbstractElasticsearchIntegrationTest;
import com.enonic.xp.node.CommitNodeParams;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.CreateRootNodeParams;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.MoveNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeVersionIds;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.PatchNodeParams;
import com.enonic.xp.node.PushNodeParams;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.query.parser.QueryParser;
import com.enonic.xp.repo.impl.NodeBranchEntries;
import com.enonic.xp.repo.impl.binary.BinaryServiceImpl;
import com.enonic.xp.repo.impl.branch.storage.BranchServiceImpl;
import com.enonic.xp.repo.impl.commit.CommitServiceImpl;
import com.enonic.xp.repo.impl.config.RepoConfiguration;
import com.enonic.xp.repo.impl.elasticsearch.IndexServiceInternalImpl;
import com.enonic.xp.repo.impl.elasticsearch.search.NodeSearchIndexImpl;
import com.enonic.xp.repo.impl.elasticsearch.search.SearchDaoImpl;
import com.enonic.xp.repo.impl.elasticsearch.storage.ElasticsearchNodeStore;
import com.enonic.xp.repo.impl.elasticsearch.storage.StorageDaoImpl;
import com.enonic.xp.repo.impl.index.IndexServiceImpl;
import com.enonic.xp.repo.impl.node.CreateNodeCommand;
import com.enonic.xp.repo.impl.node.CreateRootNodeCommand;
import com.enonic.xp.repo.impl.node.DeleteNodeCommand;
import com.enonic.xp.repo.impl.node.FindNodeIdsByParentCommand;
import com.enonic.xp.repo.impl.node.FindNodesByQueryCommand;
import com.enonic.xp.repo.impl.node.GetNodeByIdCommand;
import com.enonic.xp.repo.impl.node.GetNodeByPathCommand;
import com.enonic.xp.repo.impl.node.GetNodesByIdsCommand;
import com.enonic.xp.repo.impl.node.MoveNodeCommand;
import com.enonic.xp.repo.impl.node.NodeServiceImpl;
import com.enonic.xp.repo.impl.node.PatchNodeCommand;
import com.enonic.xp.repo.impl.node.PushNodesCommand;
import com.enonic.xp.repo.impl.node.dao.NodeVersionServiceImpl;
import com.enonic.xp.repo.impl.repository.IndexNameResolver;
import com.enonic.xp.repo.impl.repository.NodeRepositoryServiceImpl;
import com.enonic.xp.repo.impl.repository.RepositoryEntryServiceImpl;
import com.enonic.xp.repo.impl.repository.RepositoryServiceImpl;
import com.enonic.xp.repo.impl.repository.SystemRepoInitializer;
import com.enonic.xp.repo.impl.search.NodeSearchServiceImpl;
import com.enonic.xp.repo.impl.storage.IndexDataServiceImpl;
import com.enonic.xp.repo.impl.storage.NodeStorageServiceImpl;
import com.enonic.xp.repo.impl.version.VersionServiceImpl;
import com.enonic.xp.repository.CreateBranchParams;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.scheduler.SchedulerConstants;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.storage.nodb.NodbBinaryBlobStore;
import com.enonic.xp.storage.spi.NodeSearchIndex;
import com.enonic.xp.storage.spi.NodeStore;
import com.enonic.xp.storage.spi.RepositoryStorageAdmin;
import com.enonic.xp.util.Reference;

import static java.util.Objects.requireNonNullElse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public abstract class AbstractNodeTest
    extends AbstractElasticsearchIntegrationTest
{
    public AbstractNodeTest()
    {
        this( false );
    }

    /**
     *
     * @param clearBeforeEach if set to true, removes all indices. SIGNIFICANTLY SLOWS DOWN TEST INITIALIZATION!
     */
    public AbstractNodeTest( final boolean clearBeforeEach )
    {
        this.clearBeforeEach = clearBeforeEach;
    }

    public static final User TEST_DEFAULT_USER =
        User.create().key( PrincipalKey.ofUser( IdProviderKey.system(), "test-user" ) ).login( "test-user" ).build();

    private static final AuthenticationInfo TEST_DEFAULT_USER_AUTHINFO =
        AuthenticationInfo.create().principals( RoleKeys.AUTHENTICATED, RoleKeys.EVERYONE ).user( TEST_DEFAULT_USER ).build();

    protected static final Branch WS_DEFAULT = Branch.create().value( "draft" ).build();

    protected static final Branch WS_OTHER = RepositoryConstants.MASTER_BRANCH;

    protected static final Branches TEST_REPO_BRANCHES = Branches.from( WS_DEFAULT, WS_OTHER );

    protected static final Branch AUDIT_LOG_BRANCH = AuditLogConstants.AUDIT_LOG_BRANCH;

    protected static final Branch SCHEDULER_BRANCH = SchedulerConstants.SCHEDULER_BRANCH;

    protected final RepositoryId testRepoId = RepositoryId.from( "com.test." + System.currentTimeMillis() );

    @TempDir
    protected Path temporaryFolder;

    protected BinaryServiceImpl binaryService;

    protected VersionServiceImpl versionService;

    protected BranchServiceImpl branchService;

    protected CommitServiceImpl commitService;

    protected IndexServiceInternalImpl indexServiceInternal;

    protected NodeSearchIndexImpl nodeSearchIndex;

    protected NodeStorageServiceImpl storageService;

    protected NodeSearchServiceImpl searchService;

    protected IndexDataServiceImpl indexedDataService;

    protected NodeRepositoryServiceImpl nodeRepositoryService;

    protected RepositoryEntryServiceImpl repositoryEntryService;

    protected RepositoryServiceImpl repositoryService;

    /**
     * Storage-side {@link RepositoryStorageAdmin}: {@code this.indexServiceInternal} in
     * default (elasticsearch) mode, the gate-B nodb client in nodb mode -- see
     * {@link NodbTestCluster}'s javadoc. Every command builder in this class uses this
     * field, never {@code indexServiceInternal} directly, for the {@code RepositoryStorageAdmin}
     * role.
     */
    protected RepositoryStorageAdmin repositoryStorageAdmin;

    /**
     * Non-null only in nodb mode -- one per concrete test CLASS, memoized and shared across
     * that class's test methods by {@link NodbTestCluster#tenantForClass} (see its javadoc
     * for why this is class-scoped rather than per-method), so it is deliberately NOT closed
     * in {@link #tearDownAbstractNodeTest()}: closing it after the first method would break
     * every subsequent method in the same class that reuses it. The underlying gRPC channel
     * lives until the JVM exits (bounded by the test run itself).
     */
    private NodbTenant nodbTenant;

    /**
     * The raw storage-side {@link NodeStore} backing {@link #branchService}/
     * {@link #versionService}/{@link #commitService} -- {@code ElasticsearchNodeStore} or
     * the nodb gate-B client depending on mode. Exposed directly (rather than only via the
     * three wrapper services above) for storage-only itests that need to call
     * SPI methods with no default-mode equivalent, e.g. {@code NodeStore#getChildren}
     * (Phase 1 Gate C).
     */
    protected NodeStore nodeStore;

    protected static final MemoryBlobStore BLOB_STORE = new MemoryBlobStore();

    /**
     * The {@link BlobStore} {@link #binaryService}/the version dao are actually built on
     * for THIS test instance: {@link #BLOB_STORE} directly in default (elasticsearch) mode,
     * or a {@link NodbBinaryBlobStore} wrapping it in nodb mode (Phase 2 Gate C,
     * nodb/BUILD-PHASE-2.md) -- the decorator diverts only the binary segment to the shared
     * {@link NodbTestCluster}'s NoDB server (backed by its MinIO container), delegating
     * every other segment (node/index/ACL blobs) through to {@link #BLOB_STORE} unchanged,
     * exactly like production's {@code storage.backend=nodb} wiring (Gate B). Set in
     * {@link #setUpAbstractNodeTest()}, after {@link #nodbTenant} is resolved.
     */
    protected BlobStore blobStore;

    protected NodeServiceImpl nodeService;

    protected StorageDaoImpl storageDao;

    protected EventPublisher eventPublisher;

    protected IndexServiceImpl indexService;

    private Context initialContext;

    private final boolean clearBeforeEach;

    protected Context ctxDefault()
    {
        return ContextBuilder.create().branch( WS_DEFAULT ).repositoryId( testRepoId ).authInfo( TEST_DEFAULT_USER_AUTHINFO ).build();
    }

    protected Context ctxOther()
    {
        return ContextBuilder.create().branch( WS_OTHER ).repositoryId( testRepoId ).authInfo( TEST_DEFAULT_USER_AUTHINFO ).build();
    }

    protected Context ctxOtherAdmin()
    {
        return ContextBuilder.create()
            .branch( WS_OTHER )
            .repositoryId( testRepoId )
            .authInfo( AuthenticationInfo.create()
                           .principals( RoleKeys.ADMIN )
                           .user( User.create().key( PrincipalKey.ofSuperUser() ).login( PrincipalKey.ofSuperUser().getId() ).build() )
                           .build() )
            .build();
    }

    protected Context ctxDefaultAdmin()
    {
        return ContextBuilder.create()
            .branch( WS_DEFAULT )
            .repositoryId( testRepoId )
            .authInfo( AuthenticationInfo.create()
                           .principals( RoleKeys.ADMIN )
                           .user( User.create().key( PrincipalKey.ofSuperUser() ).login( PrincipalKey.ofSuperUser().getId() ).build() )
                           .build() )
            .build();
    }

    @BeforeAll
    static void initAbstractNodeTest()
    {
        deleteAllIndices();
        BLOB_STORE.clear();
    }

    @BeforeEach
    void setUpAbstractNodeTest()
    {
        if ( clearBeforeEach )
        {
            deleteAllIndices();
            BLOB_STORE.clear();
        }
        eventPublisher = mock( EventPublisher.class );

        initialContext = ContextAccessor.current();
        ContextAccessorSupport.getInstance().set( ctxDefault() );

        HomeDirSupport.set( temporaryFolder.toFile().toPath() );

        this.storageDao = new StorageDaoImpl( client );

        final SearchDaoImpl searchDao = new SearchDaoImpl( client );

        this.indexServiceInternal = new IndexServiceInternalImpl( client );

        // Phase 1 Gate C (nodb/BUILD-PHASE-1.md): xp.itest.storage=nodb swaps the STORAGE
        // side (NodeStore + RepositoryStorageAdmin) for the gate-B gRPC client against a
        // real NoDB server; the SEARCH side (nodeSearchIndex below) stays on embedded ES
        // unconditionally -- hybrid mode is the Phase 1 scope, not a full nodb backend.
        // this.indexServiceInternal remains the concrete ES admin regardless of mode: it is
        // also used for IndexServiceInternal-typed params below (search-<repo> index
        // lifecycle/health), which never move to nodb.
        //
        // Phase 2 Gate C (nodb/BUILD-PHASE-2.md): nodb mode also resolves nodbTenant here,
        // BEFORE binaryService/nodeDao are built below, so this.blobStore can wrap BLOB_STORE
        // with a NodbBinaryBlobStore against this exact tenant's gRPC client -- the binary
        // segment routes to NoDB/MinIO, every other segment still goes straight to BLOB_STORE.
        if ( NodbTestCluster.isEnabled() )
        {
            this.nodbTenant = NodbTestCluster.get().tenantForClass( this.getClass() );
            this.nodeStore = nodbTenant.nodeStore();
            this.repositoryStorageAdmin = nodbTenant.repositoryStorageAdmin();
            this.blobStore = new NodbBinaryBlobStore( BLOB_STORE, nodbTenant.client() );
        }
        else
        {
            // Phase 3 Gate B (nodb/BUILD-PHASE-3.md): ElasticsearchNodeStore now persists the
            // node/index-config/ACL payload segments itself (relocated from
            // NodeVersionServiceImpl), so it needs a BlobStore reference too -- BLOB_STORE
            // directly, same instance this.blobStore is set to just below.
            this.nodeStore = new ElasticsearchNodeStore( storageDao, searchDao, BLOB_STORE );
            this.repositoryStorageAdmin = this.indexServiceInternal;
            this.blobStore = BLOB_STORE;
        }

        this.binaryService = new BinaryServiceImpl( blobStore );

        NodeVersionServiceImpl nodeDao = new NodeVersionServiceImpl( blobStore, new RepoConfiguration( Map.of() ) );

        this.branchService = new BranchServiceImpl( nodeStore );

        this.versionService = new VersionServiceImpl( nodeStore );

        this.commitService = new CommitServiceImpl( nodeStore );

        this.nodeSearchIndex = new NodeSearchIndexImpl( client, searchDao, storageDao );

        this.indexedDataService = new IndexDataServiceImpl( nodeSearchIndex );

        this.storageService = new NodeStorageServiceImpl( versionService, branchService, commitService, nodeDao, indexedDataService );

        this.searchService = new NodeSearchServiceImpl( nodeSearchIndex );

        this.nodeRepositoryService = new NodeRepositoryServiceImpl( indexServiceInternal, this.repositoryStorageAdmin, nodeSearchIndex );

        this.repositoryEntryService =
            new RepositoryEntryServiceImpl( this.repositoryStorageAdmin, nodeSearchIndex, storageService, searchService, eventPublisher,
                                            binaryService );

        this.repositoryService =
            new RepositoryServiceImpl( repositoryEntryService, nodeRepositoryService, storageService, searchService, branchService,
                                       () -> null );

        this.nodeService =
            new NodeServiceImpl( this.repositoryStorageAdmin, nodeSearchIndex, storageService, searchService, nodeStore, eventPublisher,
                                binaryService );

        this.indexService =
            new IndexServiceImpl( indexServiceInternal, this.repositoryStorageAdmin, nodeSearchIndex, indexedDataService, searchService,
                                  nodeDao, repositoryEntryService );

        bootstrap();

        createTestRepository();
    }

    @AfterEach
    void tearDownAbstractNodeTest()
    {
        ContextAccessorSupport.getInstance().set( initialContext );
        // nodbTenant (nodb mode only) is intentionally NOT closed here -- it is shared
        // across this class's test methods (NodbTestCluster#tenantForClass); see its
        // field javadoc.
    }

    protected void bootstrap()
    {
        SystemRepoInitializer.create()
            .setIndexServiceInternal( indexServiceInternal )
            .setNodeStorageService( storageService )
            .setRepositoryEntryService( repositoryEntryService )
            .setNodeRepositoryService( nodeRepositoryService )
            .build()
            .initialize();

        AuditLogRepoInitializer.create().setIndexService( indexService ).setRepositoryService( repositoryService ).build().initialize();

        SchedulerRepoInitializer.create().setIndexService( indexService ).setRepositoryService( repositoryService ).build().initialize();

        VirtualAppInitializer.create().setIndexService( indexService ).setRepositoryService( repositoryService ).build().initialize();
    }

    private void createTestRepository()
    {
        final AccessControlList rootPermissions =
            AccessControlList.of( AccessControlEntry.create().principal( TEST_DEFAULT_USER.getKey() ).allowAll().build() );

        ctxDefaultAdmin().callWith( () -> {
            this.repositoryService.createRepository(
                CreateRepositoryParams.create().repositoryId( testRepoId ).rootPermissions( rootPermissions ).build() );

            TEST_REPO_BRANCHES.stream().filter( branch -> !RepositoryConstants.MASTER_BRANCH.equals( branch ) ).forEach( branch -> {
                final CreateBranchParams createBranchParams = CreateBranchParams.from( branch.toString() );
                this.repositoryService.createBranch( createBranchParams );
            } );

            refresh();
            return null;
        } );
    }

    protected Node createDefaultRootNode()
    {
        final AccessControlList rootPermissions =
            AccessControlList.of( AccessControlEntry.create().principal( TEST_DEFAULT_USER.getKey() ).allowAll().build() );

        final CreateRootNodeParams createRootParams = CreateRootNodeParams.create().permissions( rootPermissions ).build();

        return CreateRootNodeCommand.create()
            .params( createRootParams )
            .repositoryStorageAdmin( this.repositoryStorageAdmin )
            .nodeSearchIndex( this.nodeSearchIndex )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();
    }

    protected Node updateNode( final UpdateNodeParams updateNodeParams )
    {
        return PatchNodeCommand.create()
            .params( convertUpdateParams( updateNodeParams ) )
            .binaryService( this.binaryService )
            .repositoryStorageAdmin( this.repositoryStorageAdmin )
            .nodeSearchIndex( this.nodeSearchIndex )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute()
            .getResult( ContextAccessor.current().getBranch() );
    }

    private PatchNodeParams convertUpdateParams( final UpdateNodeParams params )
    {
        return PatchNodeParams.create()
            .id( params.getId() )
            .path( params.getPath() )
            .editor( params.getEditor() )
            .setBinaryAttachments( params.getBinaryAttachments() )
            .refresh( params.getRefresh() )
            .branches( Branches.from( ContextAccessor.current().getBranch() ) )
            .build();
    }

    protected Node createNode( final NodePath parent, final String name )
    {
        return createNode( CreateNodeParams.create().parent( parent ).name( name ).setNodeId( NodeId.from( name ) ).build() );
    }

    protected NodeCommitEntry commit( Node node )
    {
        final NodeCommitEntry nodeCommitEntry = NodeCommitEntry.create().message( "commit" ).build();
        return nodeService.commit( CommitNodeParams.create()
                                       .nodeCommitEntry( nodeCommitEntry )
                                       .nodeVersionIds( NodeVersionIds.from( node.getNodeVersionId() ) )
                                       .build() );
    }

    protected Node createNodeSkipVerification( final CreateNodeParams createNodeParams )
    {
        return CreateNodeCommand.create()
            .repositoryStorageAdmin( this.repositoryStorageAdmin )
            .nodeSearchIndex( this.nodeSearchIndex )
            .binaryService( this.binaryService )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .params( createNodeParams )
            .skipVerification( true )
            .build()
            .execute();
    }

    protected Node createNode( final CreateNodeParams createNodeParams )
    {
        return CreateNodeCommand.create()
            .repositoryStorageAdmin( this.repositoryStorageAdmin )
            .nodeSearchIndex( this.nodeSearchIndex )
            .binaryService( this.binaryService )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .params( createNodeParams )
            .build()
            .execute();
    }

    protected Node getNodeById( final NodeId nodeId )
    {
        return GetNodeByIdCommand.create()
            .repositoryStorageAdmin( this.repositoryStorageAdmin )
            .nodeSearchIndex( this.nodeSearchIndex )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .id( nodeId )
            .build()
            .execute();
    }

    protected Node getNodeByPath( final NodePath nodePath )
    {
        return GetNodeByPathCommand.create()
            .repositoryStorageAdmin( this.repositoryStorageAdmin )
            .nodeSearchIndex( this.nodeSearchIndex )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .nodePath( nodePath )
            .build()
            .execute();
    }

    protected FindNodesByParentResult findByParent( final NodePath parentPath )
    {
        return FindNodeIdsByParentCommand.create()
            .parentPath( parentPath )
            .repositoryStorageAdmin( this.repositoryStorageAdmin )
            .nodeSearchIndex( nodeSearchIndex )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();
    }

    public FindNodesByQueryResult doFindByQuery( final NodeQuery query )
    {
        return FindNodesByQueryCommand.create()
            .query( query )
            .repositoryStorageAdmin( this.repositoryStorageAdmin )
            .nodeSearchIndex( this.nodeSearchIndex )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();
    }

    protected void printContentRepoIndex()
    {
        printAllIndexContent( IndexNameResolver.resolveSearchIndexName( testRepoId ), WS_DEFAULT.getValue() );
    }

    protected PushNodesResult pushNodes( final Branch target, final NodeId... nodeIds )
    {
        return PushNodesCommand.create()
            .params( PushNodeParams.create().ids( NodeIds.from( nodeIds ) ).target( target ).build() )
            .repositoryStorageAdmin( this.repositoryStorageAdmin )
            .nodeSearchIndex( this.nodeSearchIndex )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();
    }

    protected NodeIds doDeleteNode( final NodeId nodeId )
    {
        final NodeBranchEntries result = DeleteNodeCommand.create()
            .nodeId( nodeId )
            .repositoryStorageAdmin( this.repositoryStorageAdmin )
            .nodeSearchIndex( this.nodeSearchIndex )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();

        return NodeIds.from( result.getKeys() );
    }

    protected void renameNode( final NodeId nodeId, final String newName )
    {
        MoveNodeCommand.create()
            .params( MoveNodeParams.create().nodeId( nodeId ).newName( NodeName.from( newName ) ).build() )
            .repositoryStorageAdmin( this.repositoryStorageAdmin )
            .nodeSearchIndex( this.nodeSearchIndex )
            .searchService( this.searchService )
            .storageService( this.storageService )
            .build()
            .execute();
    }

    protected Node moveNode( final NodeId nodeId, final NodePath newParent )
    {
        return MoveNodeCommand.create()
            .params( MoveNodeParams.create().nodeId( nodeId ).newParentPath( newParent ).build() )
            .repositoryStorageAdmin( this.repositoryStorageAdmin )
            .nodeSearchIndex( this.nodeSearchIndex )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute()
            .getMovedNodes()
            .get( 0 )
            .getNode();
    }

    protected void queryAndAssert( final String queryString, final int expected )
    {
        final FindNodesByQueryResult result = doQuery( queryString );

        assertEquals( expected, result.getNodeIds().getSize() );
    }

    protected FindNodesByQueryResult doQuery( final String queryString )
    {
        final NodeQuery query = NodeQuery.create().query( QueryParser.parse( queryString ) ).build();

        return doFindByQuery( query );
    }

    protected final void createNodes( final Node parent, final int numberOfNodes, final int maxLevels, final int level )
    {
        this.createNodes( parent, numberOfNodes, maxLevels, level, ( child ) -> {
        } );
    }

    protected final void createNodes( final Node parent, final int numberOfNodes, final int maxLevels, final int level,
                                      final Consumer<Node> childConsumer )
    {

        for ( int i = 0; i < numberOfNodes; i++ )
        {
            final PropertyTree data = new PropertyTree();
            data.addReference( "myRef", new Reference( parent.id() ) );

            final Node node = createNodeSkipVerification(
                CreateNodeParams.create().name( "nodeName_" + level + "-" + i ).parent( parent.path() ).data( data ).build() );

            childConsumer.accept( node );

            if ( level < maxLevels )
            {
                createNodes( node, numberOfNodes, maxLevels, level + 1, childConsumer );
            }
        }
    }

    protected Node getNode( final NodeId nodeId )
    {
        return GetNodeByIdCommand.create()
            .id( nodeId )
            .repositoryStorageAdmin( this.repositoryStorageAdmin )
            .nodeSearchIndex( nodeSearchIndex )
            .storageService( storageService )
            .searchService( searchService )
            .build()
            .execute();

    }

    protected Nodes getNodes( final NodeIds nodeIds )
    {
        return GetNodesByIdsCommand.create()
            .ids( nodeIds )
            .repositoryStorageAdmin( this.repositoryStorageAdmin )
            .nodeSearchIndex( nodeSearchIndex )
            .storageService( storageService )
            .searchService( searchService )
            .build()
            .execute();

    }

    protected void printContentTree( final Branch branch )
    {
        ContextBuilder.from( ContextAccessor.current() )
            .branch( branch )
            .build()
            .runWith( () -> doPrintContentTree( nodeService.getByPath( NodePath.ROOT ).id() ) );
    }

    private void doPrintContentTree( final NodeId rootId )
    {
        final Node root = this.nodeService.getById( rootId );

        final Branch branch = ContextAccessor.current().getBranch();
        System.out.println( "** Node-tree in branch [" + branch.getValue() + "], starting with path [" + root.path() + "]" );

        doPrintChildren( 0, root );
    }

    private void doPrintChildren( int ident, final Node root )
    {
        System.out.println( " ".repeat( ident ) + "'--" + requireNonNullElse( root.name(), "" ) + " (" + root.id() + ")" );

        ident += 3;

        final FindNodesByParentResult result =
            this.nodeService.findByParent( FindNodesByParentParams.create().parentId( root.id() ).size( -1 ).build() );

        for ( final NodeId nodeId : result.getNodeIds() )
        {
            doPrintChildren( ident, this.nodeService.getById( nodeId ) );
        }
    }

}
