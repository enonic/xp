package com.enonic.xp.core.issue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;

import com.google.common.net.HttpHeaders;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ContentConstants;
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
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class AbstractIssueServiceTest
    extends AbstractElasticsearchIntegrationTest
{

    protected static final Repository TEST_REPO = Repository.create().
            id( RepositoryId.from( "com.enonic.cms.default" ) ).
            branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) ).
            build();

    public static final User TEST_DEFAULT_USER =
        User.create().key( PrincipalKey.ofUser( IdProviderKey.system(), "test-user" ) ).login( "test-user" ).build();

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

    private IndexServiceImpl indexService;

    private RepositoryServiceImpl repositoryService;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        deleteAllIndices();

        ContextAccessor.INSTANCE.set( CTX_DEFAULT );

        final MemoryBlobStore blobStore = new MemoryBlobStore();

        binaryService = new BinaryServiceImpl();
        binaryService.setBlobStore( blobStore );

        final StorageDaoImpl storageDao = new StorageDaoImpl();
        storageDao.setClient( client );

        final EventPublisherImpl eventPublisher = new EventPublisherImpl();

        SearchDaoImpl searchDao = new SearchDaoImpl();
        searchDao.setClient( client );

        BranchServiceImpl branchService = new BranchServiceImpl();
        branchService.setStorageDao( storageDao );
        branchService.setSearchDao(searchDao);

        VersionServiceImpl versionService = new VersionServiceImpl();
        versionService.setStorageDao( storageDao );

        IndexServiceInternalImpl indexServiceInternal = new IndexServiceInternalImpl();
        indexServiceInternal.setClient( client );

        NodeVersionServiceImpl nodeDao = new NodeVersionServiceImpl();
        nodeDao.setBlobStore( blobStore );

        issueService = new IssueServiceImpl();

        IndexDataServiceImpl indexedDataService = new IndexDataServiceImpl();
        indexedDataService.setStorageDao( storageDao );

        NodeStorageServiceImpl storageService = new NodeStorageServiceImpl();
        storageService.setBranchService(branchService);
        storageService.setVersionService(versionService);
        storageService.setNodeVersionService(nodeDao);
        storageService.setIndexDataService(indexedDataService);

        NodeSearchServiceImpl searchService = new NodeSearchServiceImpl();
        searchService.setSearchDao(searchDao);

        final NodeRepositoryServiceImpl nodeRepositoryService = new NodeRepositoryServiceImpl();
        nodeRepositoryService.setIndexServiceInternal(indexServiceInternal);

        final IndexServiceInternalImpl elasticsearchIndexService = new IndexServiceInternalImpl();
        elasticsearchIndexService.setClient( client );

        final RepositoryEntryServiceImpl repositoryEntryService = new RepositoryEntryServiceImpl();
        repositoryEntryService.setIndexServiceInternal( elasticsearchIndexService );
        repositoryEntryService.setNodeRepositoryService( nodeRepositoryService );
        repositoryEntryService.setNodeStorageService(storageService);
        repositoryEntryService.setNodeSearchService(searchService);
        repositoryEntryService.setEventPublisher( eventPublisher );
        repositoryEntryService.setBinaryService( binaryService );

        indexService = new IndexServiceImpl();
        indexService.setIndexServiceInternal( indexServiceInternal );
        indexService.setRepositoryEntryService( repositoryEntryService );

        repositoryService = new RepositoryServiceImpl();
        repositoryService.setRepositoryEntryService( repositoryEntryService );
        repositoryService.setIndexServiceInternal( elasticsearchIndexService );
        repositoryService.setNodeRepositoryService( nodeRepositoryService );
        repositoryService.setNodeStorageService(storageService);
        repositoryService.setIndexService( indexService );
        repositoryService.initialize();

        nodeService = new NodeServiceImpl();
        nodeService.setIndexServiceInternal(indexServiceInternal);
        nodeService.setNodeStorageService(storageService);
        nodeService.setNodeSearchService(searchService);
        nodeService.setEventPublisher( eventPublisher );
        nodeService.setBinaryService( binaryService );
        nodeService.setRepositoryService( repositoryService );
        nodeService.initialize();

        Map<String, List<String>> metadata = new HashMap<>();
        metadata.put( HttpHeaders.CONTENT_TYPE, List.of( "image/jpg" ) );

        issueService.setNodeService( nodeService );

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
