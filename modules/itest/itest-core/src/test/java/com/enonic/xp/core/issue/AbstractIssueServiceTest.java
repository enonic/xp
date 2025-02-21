package com.enonic.xp.core.issue;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextAccessorSupport;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.event.EventPublisherImpl;
import com.enonic.xp.core.impl.issue.IssueServiceImpl;
import com.enonic.xp.core.impl.project.init.ContentInitializer;
import com.enonic.xp.core.impl.project.init.IssueInitializer;
import com.enonic.xp.internal.blobstore.MemoryBlobStore;
import com.enonic.xp.issue.CreateIssueParams;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.repo.impl.binary.BinaryServiceImpl;
import com.enonic.xp.repo.impl.branch.storage.BranchServiceImpl;
import com.enonic.xp.repo.impl.commit.CommitServiceImpl;
import com.enonic.xp.repo.impl.config.RepoConfiguration;
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
import com.enonic.xp.repo.impl.repository.SystemRepoInitializer;
import com.enonic.xp.repo.impl.search.NodeSearchServiceImpl;
import com.enonic.xp.repo.impl.storage.IndexDataServiceImpl;
import com.enonic.xp.repo.impl.storage.NodeStorageServiceImpl;
import com.enonic.xp.repo.impl.version.VersionServiceImpl;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

public abstract class AbstractIssueServiceTest
    extends AbstractElasticsearchIntegrationTest
{
    public static final RepositoryId TEST_REPO_ID = RepositoryId.from( "com.enonic.cms.test-repo" );

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

    protected IssueServiceImpl issueService;

    protected NodeServiceImpl nodeService;

    private IndexServiceImpl indexService;

    private RepositoryServiceImpl repositoryService;

    private ExecutorService executorService;

    private Context initialContext;

    @BeforeEach
    void setUpAbstractIssueServiceTest()
    {
        deleteAllIndices();

        executorService = Executors.newSingleThreadExecutor();

        final Context ctx = ContextBuilder.create().
            branch( WS_DEFAULT ).
            repositoryId( TEST_REPO_ID ).
            authInfo( TEST_DEFAULT_USER_AUTHINFO ).
            build();

        initialContext = ContextAccessor.current();
        ContextAccessorSupport.getInstance().set( ctx );

        final MemoryBlobStore blobStore = new MemoryBlobStore();

        final BinaryServiceImpl binaryService = new BinaryServiceImpl( blobStore );

        final StorageDaoImpl storageDao = new StorageDaoImpl( client );

        final EventPublisherImpl eventPublisher = new EventPublisherImpl( executorService );

        final SearchDaoImpl searchDao = new SearchDaoImpl( client );

        BranchServiceImpl branchService = new BranchServiceImpl( storageDao, searchDao );

        VersionServiceImpl versionService = new VersionServiceImpl( storageDao );

        CommitServiceImpl commitService = new CommitServiceImpl( storageDao );

        IndexServiceInternalImpl indexServiceInternal = new IndexServiceInternalImpl( client );

        NodeVersionServiceImpl nodeDao = new NodeVersionServiceImpl( blobStore, new RepoConfiguration( Map.of() ) );

        issueService = new IssueServiceImpl();

        IndexDataServiceImpl indexedDataService = new IndexDataServiceImpl( storageDao );

        NodeStorageServiceImpl storageService =
            new NodeStorageServiceImpl( versionService, branchService, commitService, nodeDao, indexedDataService );

        NodeSearchServiceImpl searchService = new NodeSearchServiceImpl( searchDao );

        final NodeRepositoryServiceImpl nodeRepositoryService = new NodeRepositoryServiceImpl( indexServiceInternal );

        final RepositoryEntryServiceImpl repositoryEntryService =
            new RepositoryEntryServiceImpl( indexServiceInternal, storageService, searchService, eventPublisher, binaryService );

        indexService = new IndexServiceImpl( indexServiceInternal, indexedDataService, searchService, nodeDao, repositoryEntryService );


        repositoryService =
            new RepositoryServiceImpl( repositoryEntryService, indexServiceInternal, nodeRepositoryService, storageService,
                                       searchService );
        SystemRepoInitializer.create().
            setIndexServiceInternal( indexServiceInternal ).
            setRepositoryService( repositoryService ).
            setNodeStorageService( storageService ).
            build().
            initialize();

        nodeService = new NodeServiceImpl( indexServiceInternal, storageService, searchService, eventPublisher, binaryService, repositoryService );

        issueService.setNodeService( nodeService );

        initializeRepository();
    }

    @AfterEach
    void tearDownAbstractIssueServiceTest()
    {
        ContextAccessorSupport.getInstance().set( initialContext );
        executorService.shutdownNow();
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
            repositoryId( TEST_REPO_ID ).
            build().
            initialize();
        IssueInitializer.create().
            setIndexService( indexService ).
            setNodeService( nodeService ).
            repositoryId( TEST_REPO_ID ).
            build().
            initialize();
    }
}
