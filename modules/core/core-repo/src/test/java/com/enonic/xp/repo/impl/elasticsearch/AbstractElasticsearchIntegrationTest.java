package com.enonic.xp.repo.impl.elasticsearch;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.search.SearchAction;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.MoreFiles;
import com.google.common.io.RecursiveDeleteOption;

@Tag("elasticsearch")
@ExtendWith(AbstractElasticsearchIntegrationTest.EmbeddedElasticsearchExtension.class)
public abstract class AbstractElasticsearchIntegrationTest
{
    private final static Logger LOG = LoggerFactory.getLogger( AbstractElasticsearchIntegrationTest.class );

    protected static Client client;

    protected boolean indexExists( String index )
    {
        IndicesExistsResponse actionGet = client.admin().indices().prepareExists( index ).execute().actionGet();
        return actionGet.isExists();
    }

    protected File getSnapshotsDir() {
        return ElasticsearchFixture.server.getSnapshotsDir();
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

    public static void waitForClusterHealth()
    {
        ElasticsearchFixture.elasticsearchIndexService.getClusterHealth( "10s" );
    }

    protected static final RefreshResponse refresh()
    {
        RefreshResponse actionGet = client.admin().indices().prepareRefresh().execute().actionGet();
        return actionGet;
    }

    protected static final DeleteIndexResponse deleteAllIndices()
    {
        DeleteIndexResponse actionGet = client.admin().indices().prepareDelete("_all").execute().actionGet();
        return actionGet;
    }

    static class EmbeddedElasticsearchExtension implements BeforeAllCallback
    {
        @Override
        public void beforeAll(ExtensionContext context)
        {
            context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL).getOrComputeIfAbsent(ElasticsearchFixture.class);
        }
    }

    static class ElasticsearchFixture
        implements ExtensionContext.Store.CloseableResource
    {

        static IndexServiceInternalImpl elasticsearchIndexService;

        static EmbeddedElasticsearchServer server;

        static Path elasticsearchTemporaryFolder;

        public ElasticsearchFixture()
            throws IOException
        {
            LOG.info( "Starting up Elasticsearch" );

            elasticsearchTemporaryFolder = Files.createTempDirectory( "elasticsearchFixture" );

            server = new EmbeddedElasticsearchServer( elasticsearchTemporaryFolder.toFile() );

            client = server.getClient();

            elasticsearchIndexService = new IndexServiceInternalImpl();
            elasticsearchIndexService.setClient( client );
        }

        @Override
        public void close()
            throws IOException
        {
            LOG.info( "Shutting down Elasticsearch" );
            if (client != null) {
                client.close();
            }
            if (server != null) {
                server.shutdown();
            }
            MoreFiles.deleteRecursively( elasticsearchTemporaryFolder, RecursiveDeleteOption.ALLOW_INSECURE );
        }
    }

}
