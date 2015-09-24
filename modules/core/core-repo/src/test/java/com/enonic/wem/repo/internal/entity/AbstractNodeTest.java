package com.enonic.wem.repo.internal.entity;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import com.enonic.wem.repo.internal.blob.BlobStore;
import com.enonic.wem.repo.internal.blob.file.FileBlobStore;
import com.enonic.wem.repo.internal.elasticsearch.AbstractElasticsearchIntegrationTest;
import com.enonic.wem.repo.internal.elasticsearch.ElasticsearchIndexServiceInternal;
import com.enonic.wem.repo.internal.elasticsearch.search.ElasticsearchSearchDao;
import com.enonic.wem.repo.internal.elasticsearch.snapshot.ElasticsearchSnapshotService;
import com.enonic.wem.repo.internal.elasticsearch.storage.ElasticsearchStorageDao;
import com.enonic.wem.repo.internal.elasticsearch.version.VersionServiceImpl;
import com.enonic.wem.repo.internal.entity.dao.NodeDaoImpl;
import com.enonic.wem.repo.internal.repository.IndexNameResolver;
import com.enonic.wem.repo.internal.repository.RepositoryInitializer;
import com.enonic.wem.repo.internal.search.SearchServiceImpl;
import com.enonic.wem.repo.internal.storage.StorageServiceImpl;
import com.enonic.wem.repo.internal.storage.branch.BranchServiceImpl;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.index.IndexType;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.CreateRootNodeParams;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.query.parser.QueryParser;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static org.junit.Assert.*;

public abstract class AbstractNodeTest
    extends AbstractElasticsearchIntegrationTest
{
    public static final User TEST_DEFAULT_USER =
        User.create().key( PrincipalKey.ofUser( UserStoreKey.system(), "test-user" ) ).login( "test-user" ).build();

    public static final AuthenticationInfo TEST_DEFAULT_USER_AUTHINFO = AuthenticationInfo.create().
        principals( RoleKeys.AUTHENTICATED ).
        user( TEST_DEFAULT_USER ).
        build();

    protected static final Branch WS_DEFAULT = Branch.create().
        name( "draft" ).
        build();

    protected static final Branch WS_OTHER = Branch.create().
        name( "master" ).
        build();

    protected static final Context CTX_DEFAULT = ContextBuilder.create().
        branch( WS_DEFAULT ).
        repositoryId( TEST_REPO.getId() ).
        authInfo( TEST_DEFAULT_USER_AUTHINFO ).
        build();

    protected static final Context CTX_OTHER = ContextBuilder.create().
        branch( WS_OTHER ).
        repositoryId( TEST_REPO.getId() ).
        authInfo( TEST_DEFAULT_USER_AUTHINFO ).
        build();

    @Rule
    public TemporaryFolder xpHome = new TemporaryFolder();

    public BlobStore binaryBlobStore;

    protected NodeDaoImpl nodeDao;

    protected VersionServiceImpl versionService;

    protected BranchServiceImpl branchService;

    protected ElasticsearchIndexServiceInternal indexServiceInternal;

    protected ElasticsearchSnapshotService snapshotService;

    protected StorageServiceImpl storageService;

    protected SearchServiceImpl searchService;

    protected ElasticsearchSearchDao searchDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();

        System.setProperty( "xp.home", xpHome.getRoot().getPath() );

        ContextAccessor.INSTANCE.set( CTX_DEFAULT );

        this.binaryBlobStore = new FileBlobStore( "test" );

        final ElasticsearchStorageDao storageDao = new ElasticsearchStorageDao();
        storageDao.setClient( this.client );

        this.searchDao = new ElasticsearchSearchDao();
        this.searchDao.setElasticsearchDao( this.elasticsearchDao );

        this.indexServiceInternal = new ElasticsearchIndexServiceInternal();
        this.indexServiceInternal.setClient( client );
        this.indexServiceInternal.setElasticsearchDao( elasticsearchDao );

        // Branch and version-services

        this.branchService = new BranchServiceImpl();
        this.branchService.setStorageDao( storageDao );

        this.versionService = new VersionServiceImpl();
        this.versionService.setStorageDao( storageDao );

        // Storage-service

        this.nodeDao = new NodeDaoImpl();
        this.nodeDao.setBranchService( this.branchService );

        this.storageService = new StorageServiceImpl();
        this.storageService.setVersionService( this.versionService );
        this.storageService.setBranchService( this.branchService );
        this.storageService.setIndexServiceInternal( this.indexServiceInternal );
        this.storageService.setNodeDao( this.nodeDao );

        // Search-service

        this.searchService = new SearchServiceImpl();
        this.searchService.setSearchDao( this.searchDao );

        this.snapshotService = new ElasticsearchSnapshotService();
        this.snapshotService.setElasticsearchDao( this.elasticsearchDao );

        createContentRepository();
        waitForClusterHealth();
    }

    void createRepository( final Repository repository )
    {
        NodeServiceImpl nodeService = new NodeServiceImpl();
        nodeService.setIndexServiceInternal( indexServiceInternal );
        nodeService.setSnapshotService( this.snapshotService );
        nodeService.setStorageService( this.storageService );

        RepositoryInitializer repositoryInitializer = new RepositoryInitializer( indexServiceInternal );
        repositoryInitializer.initializeRepository( repository.getId() );

        refresh();
    }

    protected Node createDefaultRootNode()
    {
        final AccessControlList rootPermissions =
            AccessControlList.of( AccessControlEntry.create().principal( TEST_DEFAULT_USER.getKey() ).allowAll().build() );
        final CreateRootNodeParams createRootParams = CreateRootNodeParams.create().permissions( rootPermissions ).
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
            binaryBlobStore( this.binaryBlobStore ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();
    }

    void createContentRepository()
    {
        createRepository( TEST_REPO );
    }


    protected Node createNode( final CreateNodeParams createNodeParams )
    {
        final CreateNodeParams createParamsWithAnalyzer = CreateNodeParams.create( createNodeParams ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( ContentConstants.DOCUMENT_INDEX_DEFAULT_ANALYZER ).
                build() ).
            build();

        final Node createdNode = CreateNodeCommand.create().
            indexServiceInternal( this.indexServiceInternal ).
            binaryBlobStore( this.binaryBlobStore ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            params( createParamsWithAnalyzer ).
            build().
            execute();

        refresh();

        return createdNode;
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


    FindNodesByParentResult findByParent( final FindNodesByParentParams params )
    {
        return FindNodesByParentCommand.create().
            params( params ).
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
        printAllIndexContent( IndexNameResolver.resolveSearchIndexName( TEST_REPO.getId() ), WS_DEFAULT.getName() );
    }

    protected void printBranchIndex()
    {
        printAllIndexContent( IndexNameResolver.resolveStorageIndexName( CTX_DEFAULT.getRepositoryId() ), IndexType.BRANCH.getName() );
    }

    protected void printVersionIndex()
    {
        printAllIndexContent( IndexNameResolver.resolveStorageIndexName( CTX_DEFAULT.getRepositoryId() ), IndexType.VERSION.getName() );
    }

    protected PushNodesResult pushNodes( final Branch target, final NodeId... nodeIds )
    {
        return doPushNodes( NodeIds.from( Arrays.asList( nodeIds ) ), target );
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

    protected Node doDeleteNode( final NodeId nodeId )
    {
        return DeleteNodeByIdCommand.create().
            nodeId( nodeId ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();
    }

    protected void queryAndAssert( final String queryString, final int expected )
    {
        final FindNodesByQueryResult result = doQuery( queryString );

        assertEquals( expected, result.getNodes().getSize() );
    }

    protected FindNodesByQueryResult doQuery( final String queryString )
    {
        final NodeQuery query = NodeQuery.create().
            query( QueryParser.parse( queryString ) ).
            build();

        return doFindByQuery( query );
    }

    protected void assertOrder( final FindNodesByQueryResult result, String... ids )
    {
        assertEquals( ids.length, result.getHits() );

        final Iterator<Node> iterator = result.getNodes().iterator();

        for ( final String id : ids )
        {
            assertEquals( id, iterator.next().id().toString() );
        }
    }
}
