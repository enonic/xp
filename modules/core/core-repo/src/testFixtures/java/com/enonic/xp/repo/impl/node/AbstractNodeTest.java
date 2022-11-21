package com.enonic.xp.repo.impl.node;

import java.nio.file.Path;
import java.util.Iterator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import com.enonic.xp.blob.Segment;
import com.enonic.xp.blob.SegmentLevel;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.app.VirtualAppInitializer;
import com.enonic.xp.core.impl.audit.AuditLogConstants;
import com.enonic.xp.core.impl.audit.AuditLogRepoInitializer;
import com.enonic.xp.core.impl.security.SecurityServiceImpl;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.impl.scheduler.SchedulerRepoInitializer;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.internal.blobstore.MemoryBlobStore;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.CreateRootNodeParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.query.parser.QueryParser;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.binary.BinaryServiceImpl;
import com.enonic.xp.repo.impl.branch.storage.BranchServiceImpl;
import com.enonic.xp.repo.impl.commit.CommitServiceImpl;
import com.enonic.xp.repo.impl.config.RepoConfiguration;
import com.enonic.xp.repo.impl.elasticsearch.AbstractElasticsearchIntegrationTest;
import com.enonic.xp.repo.impl.elasticsearch.IndexServiceInternalImpl;
import com.enonic.xp.repo.impl.elasticsearch.search.SearchDaoImpl;
import com.enonic.xp.repo.impl.elasticsearch.storage.StorageDaoImpl;
import com.enonic.xp.repo.impl.index.IndexServiceImpl;
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
import com.enonic.xp.repository.RepositorySegmentUtils;
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
    protected static final RepositoryId TEST_REPO_ID = RepositoryId.from( "com.enonic.cms.test" );

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

    @TempDir
    public Path temporaryFolder;

    public BinaryServiceImpl binaryService;

    protected NodeVersionServiceImpl nodeDao;

    protected VersionServiceImpl versionService;

    protected BranchServiceImpl branchService;

    protected CommitServiceImpl commitService;

    protected IndexServiceInternalImpl indexServiceInternal;

    protected NodeStorageServiceImpl storageService;

    protected NodeSearchServiceImpl searchService;

    private SearchDaoImpl searchDao;

    protected IndexDataServiceImpl indexedDataService;

    protected NodeRepositoryServiceImpl nodeRepositoryService;

    protected RepositoryEntryServiceImpl repositoryEntryService;

    protected RepositoryServiceImpl repositoryService;

    protected MemoryBlobStore blobStore;

    protected NodeServiceImpl nodeService;

    protected StorageDaoImpl storageDao;

    protected EventPublisher eventPublisher;

    protected IndexServiceImpl indexService;

    protected SecurityServiceImpl securityService;

    protected static Context ctxDefault()
    {
        return ContextBuilder.create().
            branch( WS_DEFAULT ).
            repositoryId( TEST_REPO_ID ).
            authInfo( TEST_DEFAULT_USER_AUTHINFO ).
            build();
    }

    protected static Context ctxOther()
    {
        return ContextBuilder.create().branch( WS_OTHER ).repositoryId( TEST_REPO_ID ).authInfo( TEST_DEFAULT_USER_AUTHINFO ).build();
    }

    protected static Context ctxDefaultAdmin()
    {
        return ContextBuilder.create()
            .branch( WS_DEFAULT )
            .repositoryId( TEST_REPO_ID )
            .authInfo( AuthenticationInfo.create()
                           .principals( RoleKeys.ADMIN )
                           .user( User.create().key( PrincipalKey.ofSuperUser() ).login( PrincipalKey.ofSuperUser().getId() ).build() )
                           .build() )
            .build();
    }

    @BeforeEach
    protected void setUpNode()
        throws Exception
    {
        eventPublisher = Mockito.mock( EventPublisher.class );

        deleteAllIndices();

        final RepoConfiguration repoConfig = Mockito.mock( RepoConfiguration.class );
        Mockito.when( repoConfig.getSnapshotsDir() ).thenReturn( this.temporaryFolder.resolve( "repo" ).resolve( "snapshots" ) );

        System.setProperty( "xp.home", temporaryFolder.toFile().getPath() );
        System.setProperty( "mapper.allow_dots_in_name", "true" );

        ContextAccessor.INSTANCE.set( ctxDefault() );

        this.blobStore = new MemoryBlobStore();

        this.binaryService = new BinaryServiceImpl();
        this.binaryService.setBlobStore( blobStore );

        storageDao = new StorageDaoImpl();
        storageDao.setClient( client );

        this.searchDao = new SearchDaoImpl();
        this.searchDao.setClient( client );

        this.indexServiceInternal = new IndexServiceInternalImpl();
        this.indexServiceInternal.setClient( client );

        // Branch and version-services

        this.branchService = new BranchServiceImpl();
        this.branchService.setStorageDao( storageDao );
        this.branchService.setSearchDao( this.searchDao );

        this.versionService = new VersionServiceImpl();
        this.versionService.setStorageDao( storageDao );

        this.commitService = new CommitServiceImpl();
        this.commitService.setStorageDao( storageDao );

        // Storage-service
        this.nodeDao = new NodeVersionServiceImpl();
        this.nodeDao.setBlobStore( blobStore );

        this.indexedDataService = new IndexDataServiceImpl();
        this.indexedDataService.setStorageDao( storageDao );

        this.storageService = new NodeStorageServiceImpl();
        this.storageService.setVersionService( this.versionService );
        this.storageService.setBranchService( this.branchService );
        this.storageService.setCommitService( this.commitService );
        this.storageService.setNodeVersionService( this.nodeDao );
        this.storageService.setIndexDataService( this.indexedDataService );

        // Search-service

        this.searchService = new NodeSearchServiceImpl();
        this.searchService.setSearchDao( this.searchDao );

        setUpRepositoryServices();

        indexService = new IndexServiceImpl();
        indexService.setIndexDataService( this.indexedDataService );
        indexService.setIndexServiceInternal( this.indexServiceInternal );
        indexService.setNodeSearchService( this.searchService );
        indexService.setNodeVersionService( this.nodeDao );
        indexService.setRepositoryEntryService( this.repositoryEntryService );

        bootstrap();

        createTestRepository();
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

        VirtualAppInitializer.create().setIndexService( indexService ).setRepositoryService( repositoryService ).setSecurityService( mock( SecurityService.class ) ).build().initialize();
    }

    private void setUpRepositoryServices()
    {
        nodeService = new NodeServiceImpl();
        nodeService.setIndexServiceInternal( indexServiceInternal );
        nodeService.setNodeStorageService( this.storageService );
        nodeService.setNodeSearchService( this.searchService );
        nodeService.setBinaryService( this.binaryService );
        nodeService.setEventPublisher( eventPublisher );

        this.nodeRepositoryService = new NodeRepositoryServiceImpl();
        this.nodeRepositoryService.setIndexServiceInternal( this.indexServiceInternal );

        this.repositoryEntryService = new RepositoryEntryServiceImpl();
        this.repositoryEntryService.setIndexServiceInternal( this.indexServiceInternal );
        this.repositoryEntryService.setNodeStorageService( this.storageService );
        this.repositoryEntryService.setEventPublisher( eventPublisher );
        this.repositoryEntryService.setNodeSearchService( this.searchService );
        this.repositoryEntryService.setBinaryService( this.binaryService );

        this.repositoryService =
            new RepositoryServiceImpl( this.repositoryEntryService, this.indexServiceInternal, nodeRepositoryService, this.storageService,
                                       this.searchService );

        this.nodeService.setRepositoryService( this.repositoryService );
    }

    private void createTestRepository()
    {
        final AccessControlList rootPermissions = AccessControlList.of( AccessControlEntry.create().
            principal( TEST_DEFAULT_USER.getKey() ).
            allowAll().
            build() );

        ContextBuilder.from( ContextAccessor.current() ).
            authInfo( AuthenticationInfo.create().
                principals( RoleKeys.ADMIN ).
                user( User.ANONYMOUS ).
                build() ).
            build().
            callWith( () -> {
                this.repositoryService.createRepository( CreateRepositoryParams.create().
                    repositoryId( TEST_REPO_ID ).
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

    protected InternalContext createInternalContext()
    {
        final Context currentContext = ContextAccessor.current();
        return InternalContext.create( currentContext ).
            build();
    }

    protected Segment createSegment( SegmentLevel blobTypeLevel )
    {
        final RepositoryId repositoryId = ContextAccessor.current().getRepositoryId();
        return RepositorySegmentUtils.toSegment( repositoryId, blobTypeLevel );
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


    protected Node createNode( final CreateNodeParams createNodeParams, final boolean refresh )
    {
        final CreateNodeParams.Builder createParamsWithAnalyzer = CreateNodeParams.create( createNodeParams );

        if ( createNodeParams.getIndexConfigDocument() == null )
        {
            createParamsWithAnalyzer.indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( ContentConstants.DOCUMENT_INDEX_DEFAULT_ANALYZER ).
                build() );
        }

        final Node createdNode = CreateNodeCommand.create().
            indexServiceInternal( this.indexServiceInternal ).
            binaryService( this.binaryService ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            params( createParamsWithAnalyzer.build() ).
            build().
            execute();

        if ( refresh )
        {
            refresh();
        }

        return createdNode;
    }


    protected Node createNode( final CreateNodeParams createNodeParams )
    {
        return createNode( createNodeParams, true );
    }

    Node getNodeById( final NodeId nodeId )
    {
        return GetNodeByIdCommand.create().
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            id( nodeId ).
            build().
            execute();
    }

    Node getNodeByPath( final NodePath nodePath )
    {
        return GetNodeByPathCommand.create().
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            nodePath( nodePath ).
            build().
            execute();
    }

    FindNodesByParentResult findByParent( final NodePath parentPath )
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
        printAllIndexContent( IndexNameResolver.resolveSearchIndexName( TEST_REPO_ID ), WS_DEFAULT.getValue() );
    }


    protected PushNodesResult pushNodes( final Branch target, final NodeId... nodeIds )
    {
        return doPushNodes( NodeIds.from( nodeIds ), target );
    }

    protected PushNodesResult pushNodes( final NodeIds nodeIds, final Branch target )
    {
        return doPushNodes( nodeIds, target );
    }

    private PushNodesResult doPushNodes( final NodeIds nodeIds, final Branch target )
    {
        return PushNodesCommand.create().
            ids( nodeIds ).
            target( target ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();
    }

    protected NodeIds doDeleteNode( final NodeId nodeId )
    {
        final NodeBranchEntries result = DeleteNodeByIdCommand.create().
            nodeId( nodeId ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        return NodeIds.from( result.getKeys() );
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

    protected void assertOrder( final FindNodesByQueryResult result, Node... nodes )
    {
        assertEquals( nodes.length, result.getHits() );

        final Iterator<Node> iterator = getNodes( result.getNodeIds() ).iterator();

        for ( final Node node : nodes )
        {
            assertEquals( node.id(), iterator.next().id() );
        }
    }

    protected void assertOrder( final FindNodesByQueryResult result, String... ids )
    {
        assertEquals( ids.length, result.getHits() );

        final Iterator<Node> iterator = getNodes( result.getNodeIds() ).iterator();

        for ( final String id : ids )
        {
            assertEquals( id, iterator.next().id().toString() );
        }
    }

    protected final void createNodes( final Node parent, final int numberOfNodes, final int maxLevels, final int level )
    {
        for ( int i = 0; i < numberOfNodes; i++ )
        {
            final PropertyTree data = new PropertyTree();
            data.addReference( "myRef", new Reference( parent.id() ) );

            final Node node = createNode( CreateNodeParams.create().
                name( "nodeName_" + level + "-" + i ).
                parent( parent.path() ).
                data( data ).
                build(), false );

            if ( level < maxLevels )
            {
                createNodes( node, numberOfNodes, maxLevels, level + 1 );
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
        return GetNodesByIdsCommand.create().
            ids( nodeIds ).
            indexServiceInternal( indexServiceInternal ).
            storageService( storageService ).
            searchService( searchService ).
            build().
            execute();

    }
}
