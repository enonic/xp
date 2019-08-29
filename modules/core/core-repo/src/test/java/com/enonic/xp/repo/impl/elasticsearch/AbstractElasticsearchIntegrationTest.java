package com.enonic.xp.repo.impl.elasticsearch;

import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.search.SearchAction;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.repo.impl.elasticsearch.storage.StorageDaoImpl;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Tag("elasticsearch")
@ExtendWith(AbstractElasticsearchIntegrationTest.EmbeddedElasticsearchExtension.class)
public abstract class AbstractElasticsearchIntegrationTest
{
    protected static final Repository TEST_REPO = Repository.create().
        id( RepositoryId.from( "com.enonic.cms.default" ) ).
        branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) ).
        build();

    private final static Logger LOG = LoggerFactory.getLogger( AbstractElasticsearchIntegrationTest.class );

    @TempDir
    public Path temporaryFolder;

    protected static IndexServiceInternalImpl elasticsearchIndexService;

    protected static Client client;

    private static EmbeddedElasticsearchServer server;

    @BeforeEach
    void cleanupEmbeddedElasticsearchServer()
    {
        client.admin().indices().prepareDelete("_all").execute().actionGet();
    }

    protected boolean indexExists( String index )
    {
        IndicesExistsResponse actionGet = client.admin().indices().prepareExists( index ).execute().actionGet();
        return actionGet.isExists();
    }


    protected void printAllIndexContent( final String indexName, final String indexType )
    {
        String termQuery = "{\n" +
            "  \"query\": { \"match_all\": {} }\n" +
            "}";

        SearchRequestBuilder searchRequest = new SearchRequestBuilder( client, SearchAction.INSTANCE ).
            setSize( 100 ).
            setIndices( indexName ).
            setTypes( indexType ).
            setSource( termQuery ).
            addFields( "_source" );

        final SearchResponse searchResponse = client.search( searchRequest.request() ).actionGet();

        System.out.println( "\n\n---------- CONTENT --------------------------------" );
        System.out.println( searchResponse.toString() );
        System.out.println( "\n\n" );
    }

    public void waitForClusterHealth()
    {
        elasticsearchIndexService.getClusterHealth( "10s" );
    }

    protected final RefreshResponse refresh()
    {
        RefreshResponse actionGet = client.admin().indices().prepareRefresh().execute().actionGet();
        return actionGet;
    }

    public EmbeddedElasticsearchServer getServer()
    {
        return server;
    }

    static class EmbeddedElasticsearchExtension implements BeforeAllCallback
    {
        @Override
        public void beforeAll(ExtensionContext context)
        {
            context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL).getOrComputeIfAbsent(ElasticsearchFixture.class);
        }
    }

    static class ElasticsearchFixture implements ExtensionContext.Store.CloseableResource
    {

        public ElasticsearchFixture()
            throws IOException
        {
            LOG.info( "Starting up Elasticsearch" );

            Path elasticsearchTemporaryFolder = Files.createTempDirectory("elasticsearchFixture");

            server = new EmbeddedElasticsearchServer( elasticsearchTemporaryFolder.toFile() );

            client = server.getClient();

            final StorageDaoImpl storageDao = new StorageDaoImpl();
            storageDao.setClient( client );

            elasticsearchIndexService = new IndexServiceInternalImpl();
            elasticsearchIndexService.setClient( client );
        }

        @Override
        public void close()
        {
            LOG.info( "Shutting down Elasticsearch" );
            if (client != null) {
                client.close();
            }
            if (server != null) {
                server.shutdown();
            }
        }
    }

}
