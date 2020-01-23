package com.enonic.xp.repo.bootstrap;

import java.io.File;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.mockito.Mockito;

import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.elasticsearch.client.impl.EsClient;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.internal.blobstore.cache.CachedBlobStore;
import com.enonic.xp.internal.blobstore.file.FileBlobStore;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.CreateRootNodeParams;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.binary.BinaryServiceImpl;
import com.enonic.xp.repo.impl.branch.storage.BranchServiceImpl;
import com.enonic.xp.repo.impl.commit.CommitServiceImpl;
import com.enonic.xp.repo.impl.elasticsearch.IndexServiceInternalImpl;
import com.enonic.xp.repo.impl.elasticsearch.search.SearchDaoImpl;
import com.enonic.xp.repo.impl.elasticsearch.storage.StorageDaoImpl;
import com.enonic.xp.repo.impl.index.IndexServiceImpl;
import com.enonic.xp.repo.impl.node.CreateNodeCommand;
import com.enonic.xp.repo.impl.node.CreateRootNodeCommand;
import com.enonic.xp.repo.impl.node.HasUnpublishedChildrenCommand;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repo.impl.node.NodeServiceImpl;
import com.enonic.xp.repo.impl.node.ResolveSyncWorkCommand;
import com.enonic.xp.repo.impl.node.dao.NodeVersionServiceImpl;
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
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class PerformanceTestBootstrap
{
    public static final User TEST_DEFAULT_USER =
        User.create().key( PrincipalKey.ofUser( IdProviderKey.system(), "test-user" ) ).login( "test-user" ).build();

    protected static final Repository MY_REPO = Repository.create().
        id( RepositoryId.from( "myrepo" ) ).
        branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) ).
        build();

    public static final Context CONTEXT_DRAFT = ContextBuilder.create().
        branch( ContentConstants.BRANCH_DRAFT ).
        repositoryId( MY_REPO.getId() ).
        authInfo( NodeConstants.NODE_SU_AUTH_INFO ).
        build();

    public static final Context CONTEXT_MASTER = ContextBuilder.create().
        branch( ContentConstants.BRANCH_MASTER ).
        repositoryId( MY_REPO.getId() ).
        authInfo( NodeConstants.NODE_SU_AUTH_INFO ).
        build();

    public final int NODE_SIZE = 22000;

    protected Node ROOT_NODE;

    public Node NON_PUBLISHED_NODES_ROOT;

    public Node HALF_PUBLISHED_NODES_ROOT;

    public Node PUBLISHED_NODES_ROOT;

    public Node PUBLISHED_DYNAMIC_ROOT;

    protected NodeServiceImpl nodeService;

    private BinaryServiceImpl binaryService;

    private VersionServiceImpl versionService;

    private BranchServiceImpl branchService;

    private IndexServiceInternalImpl indexServiceInternal;

    private NodeStorageServiceImpl storageService;

    private NodeSearchServiceImpl searchService;

    private IndexDataServiceImpl indexedDataService;

    private RepositoryEntryServiceImpl repositoryEntryService;

    private RepositoryServiceImpl repositoryService;

    private EsClient client;

    public static void boostrap()
        throws Exception
    {
        final PerformanceTestBootstrap beforeTestSetup = new PerformanceTestBootstrap();
        beforeTestSetup.startClient();
        beforeTestSetup.deleteAllIndices();
        beforeTestSetup.setupServices();
        beforeTestSetup.initTestData();
        beforeTestSetup.stopClient();
    }

    public static void main( String[] args )
        throws Exception
    {
        boostrap();
    }

    public void startClient()
    {
        client = new EsClient( "localhost", 9200 );
    }

    protected void stopClient()
        throws Exception
    {
        client.close();
    }

    public void setupServices()
    {
        final BlobStore blobStore = CachedBlobStore.create().
            blobStore( new FileBlobStore( new File( "C:\\es\\elasticsearch-7.5.1\\data\\blobs" ) ) ).
            build();

        this.binaryService = new BinaryServiceImpl();
        this.binaryService.setBlobStore( blobStore );

        final StorageDaoImpl storageDao = new StorageDaoImpl();
        storageDao.setClient( client );

        final SearchDaoImpl searchDao = new SearchDaoImpl();
        searchDao.setClient( client );

        this.indexServiceInternal = new IndexServiceInternalImpl();
        this.indexServiceInternal.setClient( client );

        // Branch and version-services

        this.branchService = new BranchServiceImpl();
        this.branchService.setStorageDao( storageDao );
        this.branchService.setSearchDao( searchDao );

        this.versionService = new VersionServiceImpl();
        this.versionService.setStorageDao( storageDao );

        final CommitServiceImpl commitService = new CommitServiceImpl();
        commitService.setStorageDao( storageDao );

        // Storage-service
        final NodeVersionServiceImpl nodeDao = new NodeVersionServiceImpl();
        nodeDao.setBlobStore( blobStore );

        this.indexedDataService = new IndexDataServiceImpl();
        this.indexedDataService.setStorageDao( storageDao );

        this.storageService = new NodeStorageServiceImpl();
        this.storageService.setVersionService( this.versionService );
        this.storageService.setBranchService( this.branchService );
        this.storageService.setCommitService( commitService );
        this.storageService.setNodeVersionService( nodeDao );
        this.storageService.setIndexDataService( this.indexedDataService );

        // Search-service

        this.searchService = new NodeSearchServiceImpl();
        this.searchService.setSearchDao( searchDao );

        final IndexServiceImpl indexService = new IndexServiceImpl();
        indexService.setIndexDataService( indexedDataService );
        indexService.setIndexServiceInternal( indexServiceInternal );
        indexService.setNodeSearchService( this.searchService );
        indexService.setRepositoryEntryService( this.repositoryEntryService );

        setUpRepositoryServices();
    }

    private void setUpRepositoryServices()
    {
        nodeService = new NodeServiceImpl();
        nodeService.setIndexServiceInternal( indexServiceInternal );
        nodeService.setNodeStorageService( this.storageService );
        nodeService.setNodeSearchService( this.searchService );
        nodeService.setBinaryService( this.binaryService );
        nodeService.setEventPublisher( Mockito.mock( EventPublisher.class ) );

        final NodeRepositoryServiceImpl nodeRepositoryService = new NodeRepositoryServiceImpl();
        nodeRepositoryService.setIndexServiceInternal( this.indexServiceInternal );

        this.repositoryEntryService = new RepositoryEntryServiceImpl();
        this.repositoryEntryService.setIndexServiceInternal( this.indexServiceInternal );
        this.repositoryEntryService.setNodeRepositoryService( nodeRepositoryService );
        this.repositoryEntryService.setNodeStorageService( this.storageService );
        this.repositoryEntryService.setEventPublisher( Mockito.mock( EventPublisher.class ) );
        this.repositoryEntryService.setNodeSearchService( this.searchService );
        this.repositoryEntryService.setBinaryService( this.binaryService );

        final IndexServiceImpl indexService = new IndexServiceImpl();
        indexService.setIndexDataService( indexedDataService );
        indexService.setIndexServiceInternal( indexServiceInternal );
        indexService.setRepositoryEntryService( repositoryEntryService );

        this.repositoryService = new RepositoryServiceImpl();
        this.repositoryService.setRepositoryEntryService( this.repositoryEntryService );
        this.repositoryService.setIndexServiceInternal( this.indexServiceInternal );
        this.repositoryService.setNodeRepositoryService( nodeRepositoryService );
        this.repositoryService.setNodeStorageService( this.storageService );
        this.repositoryService.setNodeSearchService( this.searchService );
        this.repositoryService.setIndexService( indexService );

        this.nodeService.setRepositoryService( this.repositoryService );
    }

    public ResolveSyncWorkCommand.Builder prepareResolveSyncWorkCommandBuilder()
    {
        return ResolveSyncWorkCommand.create().
            target( CONTEXT_MASTER.getBranch() ).
            indexServiceInternal( indexServiceInternal ).
            storageService( storageService ).
            searchService( searchService );
    }

    public HasUnpublishedChildrenCommand.Builder prepareHasUnpublishedChildrenCommandBuilder()
    {
        return HasUnpublishedChildrenCommand.create().
            target( CONTEXT_MASTER.getBranch() ).
            storageService( this.storageService ).
            indexServiceInternal( this.indexServiceInternal ).
            searchService( this.searchService );
    }

    protected RefreshResponse refresh()
    {
        return client.indicesRefresh( new RefreshRequest() );
    }

    protected Node createDefaultRootNode()
    {
        if ( this.nodeService.getById( Node.ROOT_UUID ) != null )
        {
            final InternalContext internalContext = InternalContext.from( ContextAccessor.current() );
            final NodeBranchEntry nodeBranchEntry = branchService.get( Node.ROOT_UUID, internalContext );

            if ( nodeBranchEntry != null )
            {
                final NodeVersionMetadata nodeVersionMetadata =
                    this.versionService.getVersion( nodeBranchEntry.getNodeId(), nodeBranchEntry.getVersionId(), internalContext );

                this.versionService.store( NodeVersionMetadata.
                    create( nodeVersionMetadata ).
                    setBranches( Branches.empty() ).
                    build(), internalContext );
            }
        }

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

    protected void createRepository( final Repository repository )
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
                    repositoryId( repository.getId() ).
                    rootPermissions( rootPermissions ).
                    build() );

                repository.getBranches().
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

    protected Node createNode( final CreateNodeParams createNodeParams )
    {
        final CreateNodeParams.Builder createParamsWithAnalyzer = CreateNodeParams.create( createNodeParams );

        if ( createNodeParams.getIndexConfigDocument() == null )
        {
            createParamsWithAnalyzer.indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( ContentConstants.DOCUMENT_INDEX_DEFAULT_ANALYZER ).
                build() );
        }

        return CreateNodeCommand.create().
            indexServiceInternal( this.indexServiceInternal ).
            binaryService( this.binaryService ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            params( createParamsWithAnalyzer.build() ).
            build().
            execute();
    }

    protected final void createNodes( final Node parent, final int numberOfNodes, final int maxLevels, final int level )
    {
        final PropertyTree data = new PropertyTree();
        for ( int i = 0; i < numberOfNodes; i++ )
        {
            if ( i % 100 == 0 )
            {
                System.out.println( i + " from " + numberOfNodes );
            }

            final Node node = createNode( CreateNodeParams.create().
                name( "nodeName_" + level + "-" + i ).
                parent( parent.path() ).
                data( data ).
                build() );

            if ( level < maxLevels )
            {
                createNodes( node, numberOfNodes, maxLevels, level + 1 );
            }
        }
    }

    public void initTestData()
    {
        SystemRepoInitializer.create().
            setIndexServiceInternal( indexServiceInternal ).
            setRepositoryService( repositoryService ).
            setNodeStorageService( storageService ).
            build().
            initialize();

        CONTEXT_DRAFT.callWith( () -> {
            if ( repositoryService.get( MY_REPO.getId() ) == null )
            {
                createRepository( MY_REPO );
            }
            createDefaultRootNode();
            ROOT_NODE = createNode( CreateNodeParams.create().
                name( "rootNode" ).
                parent( NodePath.ROOT ).
                build() );

            NON_PUBLISHED_NODES_ROOT = createNode( CreateNodeParams.create().
                name( "nonPublishedRoot" ).
                parent( ROOT_NODE.path() ).
                data( new PropertyTree() ).
                build() );

            PUBLISHED_NODES_ROOT = createNode( CreateNodeParams.create().
                name( "publishedRoot" ).
                parent( ROOT_NODE.path() ).
                data( new PropertyTree() ).
                build() );

            HALF_PUBLISHED_NODES_ROOT = createNode( CreateNodeParams.create().
                name( "halfPublishedRoot" ).
                parent( ROOT_NODE.path() ).
                data( new PropertyTree() ).
                build() );

            PUBLISHED_DYNAMIC_ROOT = createNode( CreateNodeParams.create().
                name( "publishedDynamicRoot" ).
                parent( ROOT_NODE.path() ).
                data( new PropertyTree() ).
                build() );

            createNodes( NON_PUBLISHED_NODES_ROOT, NODE_SIZE, 1, 1 );
            createNodes( PUBLISHED_NODES_ROOT, NODE_SIZE, 1, 1 );
            createNodes( HALF_PUBLISHED_NODES_ROOT, NODE_SIZE, 1, 1 );
            createNodes( PUBLISHED_DYNAMIC_ROOT, NODE_SIZE, 1, 1 );

            nodeService.refresh( RefreshMode.ALL );

            this.publish( 0, this.ROOT_NODE );
            this.publish( NODE_SIZE, PUBLISHED_NODES_ROOT );
            this.publish( NODE_SIZE / 2, HALF_PUBLISHED_NODES_ROOT );

            return refresh();
        } );
    }

    public void publish( final int number, final Node root )
    {
        CONTEXT_DRAFT.callWith( () -> {
            final FindNodesByParentResult findNodesByParentResult = nodeService.findByParent( FindNodesByParentParams.create().
                recursive( true ).
                parentPath( root.path() ).
                build() );

            NodeIds.Builder nodesToPublish = NodeIds.create();

            int i = 0;

            for ( NodeId nodeId : findNodesByParentResult.getNodeIds() )
            {
                if ( i < number )
                {
                    if ( i % 100 == 0 )
                    {
                        System.out.println( i + " from " + number );
                    }
                    nodesToPublish.add( nodeId );
                }
                else
                {
                    break;
                }
                i++;
            }

            nodeService.push( NodeIds.create().
                add( root.id() ).
                addAll( nodesToPublish.build() ).
                build(), CONTEXT_MASTER.getBranch() );

            nodeService.refresh( RefreshMode.ALL );

            return 1;
        } );
    }

    public void unpublish( final Node node, final boolean includeChildren )
    {
        CONTEXT_MASTER.callWith( () -> {

            if ( includeChildren )
            {
                final FindNodesByParentResult result =
                    this.nodeService.findByParent( FindNodesByParentParams.create().parentPath( node.path() ).build() );
                result.getNodeIds().forEach( this::doUnpublish );
            }

            this.doUnpublish( node.id() );

            this.nodeService.refresh( RefreshMode.ALL );

            return 1;
        } );
    }

    private void doUnpublish( final NodeId nodeId )
    {
        CONTEXT_MASTER.callWith( () -> {
            final FindNodesByParentResult result =
                this.nodeService.findByParent( FindNodesByParentParams.create().parentId( nodeId ).build() );

            result.getNodeIds().forEach( this::doUnpublish );

            this.nodeService.deleteById( nodeId );
            return 1;
        } );
    }

    private AcknowledgedResponse deleteAllIndices()
    {
        return client.indicesDelete( new DeleteIndexRequest( "search-*", "storage-*", "commit-*" ) );
    }

}
