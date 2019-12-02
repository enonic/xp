package com.enonic.xp.core.audit;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import com.enonic.xp.core.impl.audit.AuditLogRepoInitializer;
import com.enonic.xp.core.impl.audit.AuditLogServiceImpl;
import com.enonic.xp.core.impl.audit.config.AuditLogConfig;
import com.enonic.xp.core.impl.event.EventPublisherImpl;
import com.enonic.xp.internal.blobstore.MemoryBlobStore;
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

public class AbstractAuditLogServiceTest
    extends AbstractElasticsearchIntegrationTest
{
    protected AuditLogServiceImpl auditLogService;

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

    @BeforeEach
    public void setUp()
        throws Exception
    {
        deleteAllIndices();

        final MemoryBlobStore blobStore = new MemoryBlobStore();

        this.binaryService = new BinaryServiceImpl();
        this.binaryService.setBlobStore( blobStore );

        final StorageDaoImpl storageDao = new StorageDaoImpl();
        storageDao.setClient( client );

        final EventPublisherImpl eventPublisher = new EventPublisherImpl();

        this.searchDao = new SearchDaoImpl();
        this.searchDao.setClient( client );

        this.branchService = new BranchServiceImpl();
        this.branchService.setStorageDao( storageDao );
        this.branchService.setSearchDao( this.searchDao );

        this.versionService = new VersionServiceImpl();
        this.versionService.setStorageDao( storageDao );

        this.indexServiceInternal = new IndexServiceInternalImpl();
        this.indexServiceInternal.setClient( client );

        this.nodeDao = new NodeVersionServiceImpl();
        this.nodeDao.setBlobStore( blobStore );

        this.indexedDataService = new IndexDataServiceImpl();
        this.indexedDataService.setStorageDao( storageDao );

        this.storageService = new NodeStorageServiceImpl();
        this.storageService.setBranchService( this.branchService );
        this.storageService.setVersionService( this.versionService );
        this.storageService.setNodeVersionService( this.nodeDao );
        this.storageService.setIndexDataService( this.indexedDataService );

        this.searchService = new NodeSearchServiceImpl();
        this.searchService.setSearchDao( this.searchDao );

        final NodeRepositoryServiceImpl nodeRepositoryService = new NodeRepositoryServiceImpl();
        nodeRepositoryService.setIndexServiceInternal( this.indexServiceInternal );

        final IndexServiceInternalImpl elasticsearchIndexService = new IndexServiceInternalImpl();
        elasticsearchIndexService.setClient( client );

        final RepositoryEntryServiceImpl repositoryEntryService = new RepositoryEntryServiceImpl();
        repositoryEntryService.setIndexServiceInternal( elasticsearchIndexService );
        repositoryEntryService.setNodeRepositoryService( nodeRepositoryService );
        repositoryEntryService.setNodeStorageService( this.storageService );
        repositoryEntryService.setNodeSearchService( this.searchService );
        repositoryEntryService.setEventPublisher( eventPublisher );
        repositoryEntryService.setBinaryService( this.binaryService );

        this.indexService = new IndexServiceImpl();
        this.indexService.setIndexServiceInternal( this.indexServiceInternal );
        this.indexService.setRepositoryEntryService( repositoryEntryService );

        this.repositoryService = new RepositoryServiceImpl();
        this.repositoryService.setRepositoryEntryService( repositoryEntryService );
        this.repositoryService.setIndexServiceInternal( elasticsearchIndexService );
        this.repositoryService.setNodeRepositoryService( nodeRepositoryService );
        this.repositoryService.setNodeStorageService( this.storageService );
        this.repositoryService.setIndexService( this.indexService );
        this.repositoryService.initialize();

        this.nodeService = new NodeServiceImpl();
        this.nodeService.setIndexServiceInternal( indexServiceInternal );
        this.nodeService.setNodeStorageService( storageService );
        this.nodeService.setNodeSearchService( searchService );
        this.nodeService.setEventPublisher( eventPublisher );
        this.nodeService.setBinaryService( this.binaryService );
        this.nodeService.setRepositoryService( this.repositoryService );
        this.nodeService.initialize();

        this.auditLogService = new AuditLogServiceImpl();
        this.auditLogService.setNodeService( this.nodeService );
        this.auditLogService.setIndexService( this.indexService );
        this.auditLogService.setRepositoryService( this.repositoryService );

        AuditLogConfig config = Mockito.mock( AuditLogConfig.class );
        Mockito.when( config.isEnabled() ).thenReturn( true );
        Mockito.when( config.isOutputLogs() ).thenReturn( true );
        this.auditLogService.setConfig( config );
        this.auditLogService.initialize();

        initializeRepository();
    }

    private void initializeRepository()
    {
        AuditLogRepoInitializer.create().
            setIndexService( indexService ).
            setRepositoryService( repositoryService ).
            build().
            initialize();

        waitForClusterHealth();
    }
}
