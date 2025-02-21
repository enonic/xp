package com.enonic.xp.core;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextAccessorSupport;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.app.VirtualAppInitializer;
import com.enonic.xp.core.impl.audit.AuditLogConstants;
import com.enonic.xp.core.impl.audit.AuditLogRepoInitializer;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.home.HomeDirSupport;
import com.enonic.xp.impl.scheduler.SchedulerRepoInitializer;
import com.enonic.xp.internal.blobstore.MemoryBlobStore;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.CreateRootNodeParams;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.query.parser.QueryParser;
import com.enonic.xp.repo.impl.binary.BinaryServiceImpl;
import com.enonic.xp.repo.impl.branch.storage.BranchServiceImpl;
import com.enonic.xp.repo.impl.commit.CommitServiceImpl;
import com.enonic.xp.repo.impl.config.RepoConfiguration;
import com.enonic.xp.repo.impl.elasticsearch.AbstractElasticsearchIntegrationTest;
import com.enonic.xp.repo.impl.elasticsearch.IndexServiceInternalImpl;
import com.enonic.xp.repo.impl.elasticsearch.search.SearchDaoImpl;
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
import com.enonic.xp.repo.impl.node.PushNodesCommand;
import com.enonic.xp.repo.impl.node.UpdateNodeCommand;
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
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.util.Reference;

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

    protected static final Branches TEST_REPO_BRANCHES = Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER );

    public static final User TEST_DEFAULT_USER =
        User.create().key( PrincipalKey.ofUser( IdProviderKey.system(), "test-user" ) ).login( "test-user" ).build();

    private static final AuthenticationInfo TEST_DEFAULT_USER_AUTHINFO = AuthenticationInfo.create().
        principals( RoleKeys.AUTHENTICATED, RoleKeys.EVERYONE ).
        user( TEST_DEFAULT_USER ).
        build();

    protected static final Branch WS_DEFAULT = Branch.create().
        value( "draft" ).
        build();

    protected static final Branch WS_OTHER = Branch.create().
        value( "master" ).
        build();

    protected static final RepositoryId AUDIT_LOG_REPO_ID = AuditLogConstants.AUDIT_LOG_REPO_ID;

    protected static final RepositoryId SCHEDULER_REPO_ID = SchedulerConstants.SCHEDULER_REPO_ID;

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

    protected NodeStorageServiceImpl storageService;

    protected NodeSearchServiceImpl searchService;

    protected IndexDataServiceImpl indexedDataService;

    protected NodeRepositoryServiceImpl nodeRepositoryService;

    protected RepositoryEntryServiceImpl repositoryEntryService;

    protected RepositoryServiceImpl repositoryService;

    protected static final MemoryBlobStore BLOB_STORE = new MemoryBlobStore();

    protected NodeServiceImpl nodeService;

    protected StorageDaoImpl storageDao;

    protected EventPublisher eventPublisher;

    protected IndexServiceImpl indexService;

    private Context initialContext;

    private final boolean clearBeforeEach;

    protected Context ctxDefault()
    {
        return ContextBuilder.create().
            branch( WS_DEFAULT ).
            repositoryId( testRepoId ).
            authInfo( TEST_DEFAULT_USER_AUTHINFO ).
            build();
    }

    protected Context ctxOther()
    {
        return ContextBuilder.create().branch( WS_OTHER ).repositoryId( testRepoId ).authInfo( TEST_DEFAULT_USER_AUTHINFO ).build();
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
        }
        eventPublisher = mock( EventPublisher.class );

        initialContext = ContextAccessor.current();
        ContextAccessorSupport.getInstance().set( ctxDefault() );

        HomeDirSupport.set( temporaryFolder.toFile().toPath() );

        this.binaryService = new BinaryServiceImpl( BLOB_STORE );

        NodeVersionServiceImpl nodeDao = new NodeVersionServiceImpl( BLOB_STORE, new RepoConfiguration( Map.of() ) );

        this.storageDao = new StorageDaoImpl( client );

        final SearchDaoImpl searchDao = new SearchDaoImpl( client );

        this.branchService = new BranchServiceImpl( storageDao, searchDao );

        this.versionService = new VersionServiceImpl( storageDao );

        this.commitService = new CommitServiceImpl( storageDao );

        this.indexedDataService = new IndexDataServiceImpl( storageDao );

        this.storageService = new NodeStorageServiceImpl( versionService, branchService, commitService, nodeDao, indexedDataService );

        this.searchService = new NodeSearchServiceImpl( searchDao );

        this.indexServiceInternal = new IndexServiceInternalImpl( client );

        this.nodeRepositoryService = new NodeRepositoryServiceImpl( indexServiceInternal );

        this.repositoryEntryService =
            new RepositoryEntryServiceImpl( indexServiceInternal, storageService, searchService, eventPublisher, binaryService );

        this.repositoryService =
            new RepositoryServiceImpl( repositoryEntryService, indexServiceInternal, nodeRepositoryService, storageService,
                                       searchService );

        this.nodeService =
            new NodeServiceImpl( indexServiceInternal, storageService, searchService, eventPublisher, binaryService, repositoryService );

        this.indexService =
            new IndexServiceImpl( indexServiceInternal, indexedDataService, searchService, nodeDao, repositoryEntryService );

        bootstrap();

        createTestRepository();
    }

    @AfterEach
    void tearDownAbstractNodeTest()
    {
        ContextAccessorSupport.getInstance().set( initialContext );
    }

    protected void bootstrap()
    {
        SystemRepoInitializer.create()
            .setIndexServiceInternal( indexServiceInternal )
            .setRepositoryService( repositoryService )
            .setNodeStorageService( storageService )
            .build()
            .initialize();

        AuditLogRepoInitializer.create().setIndexService( indexService ).setRepositoryService( repositoryService ).build().initialize();

        SchedulerRepoInitializer.create().setIndexService( indexService ).setRepositoryService( repositoryService ).build().initialize();

        VirtualAppInitializer.create()
            .setIndexService( indexService )
            .setRepositoryService( repositoryService )
            .setSecurityService( mock( SecurityService.class ) )
            .build()
            .initialize();
    }

    private void createTestRepository()
    {
        final AccessControlList rootPermissions = AccessControlList.of( AccessControlEntry.create().
            principal( TEST_DEFAULT_USER.getKey() ).
            allowAll().
            build() );

        ctxDefaultAdmin().
            callWith( () -> {
                this.repositoryService.createRepository( CreateRepositoryParams.create().
                    repositoryId( testRepoId ).
                    rootPermissions( rootPermissions ).
                    build() );

                TEST_REPO_BRANCHES.
                    stream().
                    filter( branch -> !RepositoryConstants.MASTER_BRANCH.equals( branch ) ).
                    forEach( branch -> {
                        final CreateBranchParams createBranchParams = CreateBranchParams.from( branch.toString() );
                        this.repositoryService.createBranch( createBranchParams );
                    } );

                refresh();
                return null;
            } );
    }

    protected Node createDefaultRootNode()
    {
        final AccessControlList rootPermissions = AccessControlList.of( AccessControlEntry.create().
            principal( TEST_DEFAULT_USER.getKey() ).
            allowAll().
            build() );

        final CreateRootNodeParams createRootParams = CreateRootNodeParams.create().
            permissions( rootPermissions ).
            build();

        return CreateRootNodeCommand.create().
            params( createRootParams ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();
    }

    protected Node updateNode( final UpdateNodeParams updateNodeParams )
    {
        return UpdateNodeCommand.create().
            params( updateNodeParams ).
            indexServiceInternal( this.indexServiceInternal ).
            binaryService( this.binaryService ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();
    }

    protected Node createNode( final NodePath parent, final String name )
    {
        return createNode( CreateNodeParams.create().
            parent( parent ).
            name( name ).
            setNodeId( NodeId.from( name ) ).
            build() );
    }

    protected NodeCommitEntry commit( NodeIds nodeIds )
    {
        final NodeCommitEntry nodeCommitEntry = NodeCommitEntry.create().
            message( "commit" ).
            build();
        return nodeService.commit( nodeCommitEntry, nodeIds );
    }


    protected Node createNodeSkipVerification( final CreateNodeParams createNodeParams )
    {
        return CreateNodeCommand.create()
            .indexServiceInternal( this.indexServiceInternal )
            .binaryService( this.binaryService )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .params( createNodeParams )
            .skipVerification( true )
            .build().
            execute();
    }

    protected Node createNode( final CreateNodeParams createNodeParams )
    {
        return CreateNodeCommand.create()
            .indexServiceInternal( this.indexServiceInternal )
            .binaryService( this.binaryService )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .params( createNodeParams )
            .build()
            .execute();
    }

    protected Node getNodeById( final NodeId nodeId )
    {
        return GetNodeByIdCommand.create().
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            id( nodeId ).
            build().
            execute();
    }

    protected Node getNodeByPath( final NodePath nodePath )
    {
        return GetNodeByPathCommand.create().
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            nodePath( nodePath ).
            build().
            execute();
    }

    protected FindNodesByParentResult findByParent( final NodePath parentPath )
    {
        return FindNodeIdsByParentCommand.create().
            parentPath( parentPath ).
            indexServiceInternal( indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();
    }

    public FindNodesByQueryResult doFindByQuery( final NodeQuery query )
    {
        return FindNodesByQueryCommand.create().
            query( query ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();
    }

    protected void printContentRepoIndex()
    {
        printAllIndexContent( IndexNameResolver.resolveSearchIndexName( testRepoId ), WS_DEFAULT.getValue() );
    }

    protected PushNodesResult pushNodes( final Branch target, final NodeId... nodeIds )
    {
        return PushNodesCommand.create().ids( NodeIds.from( nodeIds ) ).target( target ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();
    }

    protected NodeIds doDeleteNode( final NodeId nodeId )
    {
        final NodeBranchEntries result = DeleteNodeCommand.create()
            .nodeId( nodeId )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();

        return NodeIds.from( result.getKeys() );
    }

    protected void renameNode( final NodeId nodeId, final String newName )
    {
        MoveNodeCommand.create()
            .id( nodeId )
            .newNodeName( NodeName.from( newName ) )
            .indexServiceInternal( this.indexServiceInternal )
            .searchService( this.searchService )
            .storageService( this.storageService )
            .build()
            .execute();
    }

    protected Node moveNode( final NodeId nodeId, final NodePath newParent )
    {
        return MoveNodeCommand.create()
            .id( nodeId )
            .newParent( newParent )
            .indexServiceInternal( this.indexServiceInternal )
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
        final NodeQuery query = NodeQuery.create().
            query( QueryParser.parse( queryString ) ).
            build();

        return doFindByQuery( query );
    }

    protected void assertOrder( final FindNodesByQueryResult result, NodeId... ids )
    {
        assertEquals( ids.length, result.getHits() );

        final Iterator<Node> iterator = getNodes( result.getNodeIds() ).iterator();

        for ( final NodeId id : ids )
        {
            assertEquals( id, iterator.next().id() );
        }
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
        return GetNodeByIdCommand.create().
            id( nodeId ).
            indexServiceInternal( indexServiceInternal ).
            storageService( storageService ).
            searchService( searchService ).
            build().
            execute();

    }

    protected Nodes getNodes( final NodeIds nodeIds )
    {
        return GetNodesByIdsCommand.create()
            .ids( nodeIds )
            .indexServiceInternal( indexServiceInternal )
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
        System.out.println( " ".repeat( ident ) + "'--" + Objects.requireNonNullElse( root.name(), "" ) + " (" + root.id() + ")" );

        ident += 3;

        final FindNodesByParentResult result =
            this.nodeService.findByParent( FindNodesByParentParams.create().parentId( root.id() ).size( -1 ).build() );

        for ( final NodeId nodeId : result.getNodeIds() )
        {
            doPrintChildren( ident, this.nodeService.getById( nodeId ) );
        }
    }

}
