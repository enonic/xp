package com.enonic.xp.repo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.client.Client;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.MoreFiles;
import com.google.common.io.RecursiveDeleteOption;

import com.enonic.xp.blob.NodeVersionKey;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.internal.blobstore.file.FileBlobStore;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.binary.BinaryServiceImpl;
import com.enonic.xp.repo.impl.branch.storage.BranchServiceImpl;
import com.enonic.xp.repo.impl.commit.CommitServiceImpl;
import com.enonic.xp.repo.impl.elasticsearch.EmbeddedElasticsearchServer;
import com.enonic.xp.repo.impl.elasticsearch.IndexServiceInternalImpl;
import com.enonic.xp.repo.impl.elasticsearch.search.SearchDaoImpl;
import com.enonic.xp.repo.impl.elasticsearch.storage.StorageDaoImpl;
import com.enonic.xp.repo.impl.node.CreateNodeCommand;
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
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityConstants;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

@Disabled
public class NodeVersionDeserializationTest
{
    private static final Logger LOG = LoggerFactory.getLogger( NodeVersionDeserializationTest.class );

    @Test
    void performance()
        throws Exception
    {
        Options opt = new OptionsBuilder().include( this.getClass().getName() + ".*" ).mode( Mode.AverageTime ).timeUnit(
            TimeUnit.MILLISECONDS ).warmupTime( TimeValue.seconds( 1 ) ).warmupIterations( 10 ).measurementTime(
            TimeValue.seconds( 1 ) ).measurementIterations( 10 ).threads( 1 ).forks( 1 ).shouldFailOnError( true ).build();

        new Runner( opt ).run();
    }

    @Benchmark
    public void nodeVersionService_get( MyState state, Blackhole bh )
    {

        bh.consume( state.nodeDao.get( state.nodeVersionKey, state.internalContext ) );
    }

    @Test
    void simulation()
        throws Exception
    {
        final MyState state = new MyState();
        state.setup();
        System.out.println( state.nodeDao.get( state.nodeVersionKey, state.internalContext ) );
        state.teardown();
    }

    @State(Scope.Benchmark)
    public static class MyState
    {
        EmbeddedElasticsearchServer server;

        Client client;

        Path performanceTemporaryFolder;

        NodeVersionServiceImpl nodeDao;

        InternalContext internalContext;

        NodeVersionKey nodeVersionKey;

        IndexServiceInternalImpl indexServiceInternal;

        BranchServiceImpl branchService;

        BinaryServiceImpl binaryService;

        NodeSearchServiceImpl searchService;

        NodeStorageServiceImpl storageService;

        VersionServiceImpl versionService;

        FileBlobStore blobStore;

        StorageDaoImpl storageDao;

        SearchDaoImpl searchDao;

        CommitServiceImpl commitService;

        IndexDataServiceImpl indexedDataService;

        NodeServiceImpl nodeService;

        RepositoryServiceImpl repositoryService;

        RepositoryEntryServiceImpl repositoryEntryService;

        @Setup
        public void setup()
            throws Exception
        {
            setupXpNode();
            createInternalContext();

            final CreateNodeParams createNodeParams = CreateNodeParams.create().
                name( "my-node" ).
                parent( NodePath.ROOT ).
                build();
            final Node createdNode = createNode( createNodeParams );
            createNodeVersionKey( createdNode );
        }

        @TearDown
        public void teardown()
            throws Exception
        {
            client.close();
            server.shutdown();
            MoreFiles.deleteRecursively( performanceTemporaryFolder, RecursiveDeleteOption.ALLOW_INSECURE );
        }

        void createInternalContext()
        {
            final Context currentContext = ContextAccessor.current();
            internalContext = InternalContext.create( currentContext ).build();
        }

        void createNodeVersionKey( Node node )
        {
            nodeVersionKey = branchService.get( node.id(), internalContext ).
                getNodeVersionKey();
        }

        Node createNode( final CreateNodeParams createNodeParams )
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

            refresh();

            return createdNode;
        }


        RefreshResponse refresh()
        {
            return client.admin().indices().prepareRefresh().execute().actionGet();
        }

        private Context createAdminContext()
        {
            final PrincipalKey superUser = PrincipalKey.ofSuperUser();
            final User admin = User.create().key( superUser ).login( superUser.getId() ).build();
            final AuthenticationInfo authInfo = AuthenticationInfo.create().principals( RoleKeys.ADMIN ).user( admin ).build();
            return ContextBuilder.from( SecurityConstants.CONTEXT_SECURITY ).authInfo( authInfo ).build();
        }

        protected void setupXpNode()
            throws Exception
        {
            performanceTemporaryFolder = Files.createTempDirectory( "performanceTest" );
            LOG.info( "{}", performanceTemporaryFolder );
            server = new EmbeddedElasticsearchServer( performanceTemporaryFolder.toFile() );

            client = server.getClient();

            ContextAccessor.INSTANCE.set( createAdminContext() );

            this.blobStore = new FileBlobStore( performanceTemporaryFolder.resolve( "blobs" ) );

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

            SystemRepoInitializer.create().
                setIndexServiceInternal( indexServiceInternal ).
                setRepositoryService( repositoryService ).
                setNodeStorageService( storageService ).
                build().
                initialize();
        }

        private void setUpRepositoryServices()
        {
            final NodeRepositoryServiceImpl nodeRepositoryService = new NodeRepositoryServiceImpl();
            nodeRepositoryService.setIndexServiceInternal( this.indexServiceInternal );

            this.repositoryEntryService = new RepositoryEntryServiceImpl();
            this.repositoryEntryService.setIndexServiceInternal( this.indexServiceInternal );
            this.repositoryEntryService.setNodeRepositoryService( nodeRepositoryService );
            this.repositoryEntryService.setNodeStorageService( this.storageService );
            this.repositoryEntryService.setEventPublisher( Mockito.mock( EventPublisher.class ) );
            this.repositoryEntryService.setNodeSearchService( this.searchService );
            this.repositoryEntryService.setBinaryService( this.binaryService );

            this.repositoryService = new RepositoryServiceImpl();
            this.repositoryService.setRepositoryEntryService( this.repositoryEntryService );
            this.repositoryService.setIndexServiceInternal( this.indexServiceInternal );
            this.repositoryService.setNodeRepositoryService( nodeRepositoryService );
            this.repositoryService.setNodeStorageService( this.storageService );
            this.repositoryService.setNodeSearchService( this.searchService );

            this.nodeService = new NodeServiceImpl();
            this.nodeService.setIndexServiceInternal( indexServiceInternal );
            this.nodeService.setNodeStorageService( this.storageService );
            this.nodeService.setNodeSearchService( this.searchService );
            this.nodeService.setBinaryService( this.binaryService );
            this.nodeService.setEventPublisher( Mockito.mock( EventPublisher.class ) );
            this.nodeService.setRepositoryService( this.repositoryService );
        }
    }
}
