package com.enonic.wem.repo.internal.entity;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import com.enonic.wem.api.branch.Branch;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.context.ContextBuilder;
import com.enonic.wem.api.index.IndexType;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.CreateRootNodeParams;
import com.enonic.wem.api.node.FindNodesByParentParams;
import com.enonic.wem.api.node.FindNodesByParentResult;
import com.enonic.wem.api.node.FindNodesByQueryResult;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeIds;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeQuery;
import com.enonic.wem.api.node.PushNodesResult;
import com.enonic.wem.api.node.UpdateNodeParams;
import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.RoleKeys;
import com.enonic.wem.api.security.User;
import com.enonic.wem.api.security.UserStoreKey;
import com.enonic.wem.api.security.acl.AccessControlEntry;
import com.enonic.wem.api.security.acl.AccessControlList;
import com.enonic.wem.api.security.auth.AuthenticationInfo;
import com.enonic.wem.repo.internal.blob.BlobStore;
import com.enonic.wem.repo.internal.blob.file.FileBlobStore;
import com.enonic.wem.repo.internal.elasticsearch.AbstractElasticsearchIntegrationTest;
import com.enonic.wem.repo.internal.elasticsearch.ElasticsearchIndexServiceInternal;
import com.enonic.wem.repo.internal.elasticsearch.ElasticsearchQueryService;
import com.enonic.wem.repo.internal.elasticsearch.branch.ElasticsearchBranchService;
import com.enonic.wem.repo.internal.elasticsearch.snapshot.ElasticsearchSnapshotService;
import com.enonic.wem.repo.internal.elasticsearch.version.ElasticsearchVersionService;
import com.enonic.wem.repo.internal.entity.dao.NodeDaoImpl;
import com.enonic.wem.repo.internal.repository.IndexNameResolver;
import com.enonic.wem.repo.internal.repository.RepositoryInitializer;

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
    public TemporaryFolder WEM_HOME = new TemporaryFolder();

    public BlobStore binaryBlobStore;

    protected NodeDaoImpl nodeDao;

    protected ElasticsearchVersionService versionService;

    protected ElasticsearchBranchService branchService;

    protected ElasticsearchIndexServiceInternal indexServiceInternal;

    protected ElasticsearchQueryService queryService;

    protected ElasticsearchSnapshotService snapshotService;

    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();

        System.setProperty( "wem.home", WEM_HOME.getRoot().getPath() );

        ContextAccessor.INSTANCE.set( CTX_DEFAULT );

        this.binaryBlobStore = new FileBlobStore( "test" );

        this.queryService = new ElasticsearchQueryService();
        this.queryService.setElasticsearchDao( elasticsearchDao );

        this.branchService = new ElasticsearchBranchService();
        this.branchService.setElasticsearchDao( elasticsearchDao );

        this.versionService = new ElasticsearchVersionService();
        this.versionService.setElasticsearchDao( elasticsearchDao );

        this.indexServiceInternal = new ElasticsearchIndexServiceInternal();
        this.indexServiceInternal.setClient( client );
        this.indexServiceInternal.setElasticsearchDao( elasticsearchDao );

        this.snapshotService = new ElasticsearchSnapshotService();
        this.snapshotService.setElasticsearchDao( this.elasticsearchDao );

        this.nodeDao = new NodeDaoImpl();
        this.nodeDao.setBranchService( this.branchService );

        createContentRepository();
        waitForClusterHealth();
    }

    void createRepository( final Repository repository )
    {
        NodeServiceImpl nodeService = new NodeServiceImpl();
        nodeService.setIndexServiceInternal( indexServiceInternal );
        nodeService.setQueryService( queryService );
        nodeService.setNodeDao( nodeDao );
        nodeService.setVersionService( versionService );
        nodeService.setBranchService( branchService );
        nodeService.setSnapshotService( this.snapshotService );

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
            queryService( this.queryService ).
            branchService( this.branchService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();
    }

    protected Node updateNode( final UpdateNodeParams updateNodeParams )
    {
        return UpdateNodeCommand.create().
            params( updateNodeParams ).
            queryService( this.queryService ).
            indexServiceInternal( this.indexServiceInternal ).
            nodeDao( this.nodeDao ).
            branchService( this.branchService ).
            versionService( this.versionService ).
            binaryBlobStore( this.binaryBlobStore ).
            build().
            execute();
    }

    void createContentRepository()
    {
        createRepository( TEST_REPO );
    }


    protected Node createNode( final CreateNodeParams createNodeParams )
    {
        final Node createdNode = CreateNodeCommand.create().
            branchService( this.branchService ).
            nodeDao( this.nodeDao ).
            indexServiceInternal( this.indexServiceInternal ).
            versionService( this.versionService ).
            queryService( this.queryService ).
            binaryBlobStore( this.binaryBlobStore ).
            params( createNodeParams ).
            build().
            execute();

        refresh();

        return createdNode;
    }

    Node getNodeById( final NodeId nodeId )
    {
        return GetNodeByIdCommand.create().
            versionService( this.versionService ).
            indexServiceInternal( this.indexServiceInternal ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            branchService( this.branchService ).
            queryService( this.queryService ).
            id( nodeId ).
            resolveHasChild( false ).
            build().
            execute();
    }

    Node getNodeByPath( final NodePath nodePath )
    {
        return GetNodeByPathCommand.create().
            versionService( this.versionService ).
            indexServiceInternal( this.indexServiceInternal ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            branchService( this.branchService ).
            queryService( this.queryService ).
            nodePath( nodePath ).
            resolveHasChild( false ).
            build().
            execute();
    }


    FindNodesByParentResult findByParent( final FindNodesByParentParams params )
    {
        return FindNodesByParentCommand.create().
            params( params ).
            queryService( queryService ).
            branchService( branchService ).
            indexServiceInternal( indexServiceInternal ).
            versionService( versionService ).
            nodeDao( nodeDao ).
            build().
            execute();
    }

    public FindNodesByQueryResult doFindByQuery( final NodeQuery query )
    {
        return FindNodesByQueryCommand.create().
            query( query ).
            queryService( this.queryService ).
            versionService( this.versionService ).
            branchService( this.branchService ).
            nodeDao( this.nodeDao ).
            indexServiceInternal( this.indexServiceInternal ).
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
            queryService( this.queryService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            branchService( this.branchService ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();
    }

    protected Node doDeleteNode( final NodeId nodeId )
    {
        return DeleteNodeByIdCommand.create().
            nodeId( nodeId ).
            queryService( this.queryService ).
            indexServiceInternal( this.indexServiceInternal ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            branchService( this.branchService ).
            build().
            execute();
    }
}
