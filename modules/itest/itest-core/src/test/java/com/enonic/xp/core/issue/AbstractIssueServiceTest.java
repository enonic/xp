package com.enonic.xp.core.issue;

import java.util.List;
import java.util.Map;

import org.junit.Before;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.net.HttpHeaders;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.content.ContentInitializer;
import com.enonic.xp.core.impl.event.EventPublisherImpl;
import com.enonic.xp.core.impl.issue.IssueInitializer;
import com.enonic.xp.core.impl.issue.IssueServiceImpl;
import com.enonic.xp.internal.blobstore.MemoryBlobStore;
import com.enonic.xp.issue.CreateIssueParams;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.repo.impl.binary.BinaryServiceImpl;
import com.enonic.xp.repo.impl.branch.storage.BranchServiceImpl;
import com.enonic.xp.repo.impl.elasticsearch.AbstractElasticsearchIntegrationTest;
import com.enonic.xp.repo.impl.elasticsearch.IndexServiceInternalImpl;
import com.enonic.xp.repo.impl.elasticsearch.search.SearchDaoImpl;
import com.enonic.xp.repo.impl.elasticsearch.storage.StorageDaoImpl;
import com.enonic.xp.repo.impl.index.IndexServiceImpl;
import com.enonic.xp.repo.impl.node.NodeServiceImpl;
import com.enonic.xp.repo.impl.node.dao.NodeVersionServiceImpl;
import com.enonic.xp.repo.impl.repository.NodeRepositoryServiceImpl;
import com.enonic.xp.repo.impl.repository.RepositoryEntryServiceImpl;
import com.enonic.xp.repo.impl.repository.RepositoryServiceImpl;
import com.enonic.xp.repo.impl.search.NodeSearchServiceImpl;
import com.enonic.xp.repo.impl.storage.IndexDataServiceImpl;
import com.enonic.xp.repo.impl.storage.NodeStorageServiceImpl;
import com.enonic.xp.repo.impl.version.VersionServiceImpl;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class AbstractIssueServiceTest
    extends AbstractElasticsearchIntegrationTest
{
    public static final User TEST_DEFAULT_USER =
        User.create().key( PrincipalKey.ofUser( UserStoreKey.system(), "test-user" ) ).login( "test-user" ).build();

    public static final AuthenticationInfo TEST_DEFAULT_USER_AUTHINFO = AuthenticationInfo.create().
        principals( RoleKeys.AUTHENTICATED ).
        principals( RoleKeys.CONTENT_MANAGER_ADMIN ).
        user( TEST_DEFAULT_USER ).
        build();

    protected static final Branch WS_DEFAULT = Branch.create().
        value( "draft" ).
        build();

    protected static final Context CTX_DEFAULT = ContextBuilder.create().
        branch( WS_DEFAULT ).
        repositoryId( TEST_REPO.getId() ).
        authInfo( TEST_DEFAULT_USER_AUTHINFO ).
        build();

    protected IssueServiceImpl issueService;

    protected NodeServiceImpl nodeService;

    protected BinaryServiceImpl binaryService;

    private NodeVersionServiceImpl nodeDao;

    private VersionServiceImpl versionService;

    private BranchServiceImpl branchService;

    private IndexServiceInternalImpl indexServiceInternal;

    private NodeStorageServiceImpl storageService;

    private NodeSearchServiceImpl searchService;

    private IndexDataServiceImpl indexedDataService;

    private IndexServiceImpl indexService;

    private RepositoryServiceImpl repositoryService;

    private SearchDaoImpl searchDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();

        ContextAccessor.INSTANCE.set( CTX_DEFAULT );

        final MemoryBlobStore blobStore = new MemoryBlobStore();

        this.binaryService = new BinaryServiceImpl();
        this.binaryService.setBlobStore( blobStore );

        final StorageDaoImpl storageDao = new StorageDaoImpl();
        storageDao.setClient( this.client );

        final EventPublisherImpl eventPublisher = new EventPublisherImpl();

        this.searchDao = new SearchDaoImpl();
        this.searchDao.setClient( this.client );

        this.branchService = new BranchServiceImpl();
        this.branchService.setStorageDao( storageDao );
        this.branchService.setSearchDao( this.searchDao );

        this.versionService = new VersionServiceImpl();
        this.versionService.setStorageDao( storageDao );

        this.indexServiceInternal = new IndexServiceInternalImpl();
        this.indexServiceInternal.setClient( client );

        this.nodeDao = new NodeVersionServiceImpl();
        this.nodeDao.setBlobStore( blobStore );

        this.issueService = new IssueServiceImpl();

        this.indexedDataService = new IndexDataServiceImpl();
        this.indexedDataService.setStorageDao( storageDao );

        this.indexService = new IndexServiceImpl();
        this.indexService.setIndexServiceInternal( this.indexServiceInternal );

        this.storageService = new NodeStorageServiceImpl();
        this.storageService.setBranchService( this.branchService );
        this.storageService.setVersionService( this.versionService );
        this.storageService.setNodeVersionService( this.nodeDao );
        this.storageService.setIndexDataService( this.indexedDataService );

        this.searchService = new NodeSearchServiceImpl();
        this.searchService.setSearchDao( this.searchDao );

        final NodeRepositoryServiceImpl nodeRepositoryService = new NodeRepositoryServiceImpl();
        nodeRepositoryService.setIndexServiceInternal( this.indexServiceInternal );

        final RepositoryEntryServiceImpl repositoryEntryService = new RepositoryEntryServiceImpl();
        repositoryEntryService.setIndexServiceInternal( elasticsearchIndexService );
        repositoryEntryService.setNodeRepositoryService( nodeRepositoryService );
        repositoryEntryService.setNodeStorageService( this.storageService );
        repositoryEntryService.setNodeSearchService( this.searchService );
        repositoryEntryService.setEventPublisher( eventPublisher );
        repositoryEntryService.setBinaryService( this.binaryService );

        this.repositoryService = new RepositoryServiceImpl();
        this.repositoryService.setRepositoryEntryService( repositoryEntryService );
        this.repositoryService.setIndexServiceInternal( elasticsearchIndexService );
        this.repositoryService.setNodeRepositoryService( nodeRepositoryService );
        this.repositoryService.setNodeStorageService( this.storageService );
        this.repositoryService.initialize();

        this.nodeService = new NodeServiceImpl();
        this.nodeService.setIndexServiceInternal( indexServiceInternal );
        this.nodeService.setNodeStorageService( storageService );
        this.nodeService.setNodeSearchService( searchService );
        this.nodeService.setEventPublisher( eventPublisher );
        this.nodeService.setBinaryService( this.binaryService );
        this.nodeService.setRepositoryService( this.repositoryService );
        this.nodeService.initialize();

        Map<String, List<String>> metadata = Maps.newHashMap();
        metadata.put( HttpHeaders.CONTENT_TYPE, Lists.newArrayList( "image/jpg" ) );

        this.issueService.setNodeService( this.nodeService );

        initializeRepository();
    }

    protected Issue createIssue( CreateIssueParams.Builder builder )
    {
        return this.issueService.create( builder.build() );
    }

    private void initializeRepository()
    {
        ContentInitializer.create().
            setIndexService( indexService ).
            setNodeService( nodeService ).
            setRepositoryService( repositoryService ).
            build().
            initialize();
        IssueInitializer.create().
            setIndexService( indexService ).
            setNodeService( nodeService ).
            build().
            initialize();
        waitForClusterHealth();
    }
}
