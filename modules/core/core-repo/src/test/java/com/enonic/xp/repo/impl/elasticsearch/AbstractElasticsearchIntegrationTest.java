package com.enonic.xp.repo.impl.elasticsearch;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Tag("elasticsearch")
@ExtendWith(AbstractElasticsearchIntegrationTest.EmbeddedElasticsearchExtension.class)
public abstract class AbstractElasticsearchIntegrationTest
{
    private final static Logger LOG = LoggerFactory.getLogger( AbstractElasticsearchIntegrationTest.class );

    protected static RestHighLevelClient client;

    protected boolean indexExists( String index )
    {
        try
        {
            final GetResponse response = client.get( new GetRequest().index( index ), RequestOptions.DEFAULT );
            return response.isExists();
        }
        catch ( IOException e )
        {
            return false;
        }
    }

    protected File getSnapshotsDir()
    {
        return ElasticsearchFixture.server.getSnapshotsDir();
    }

    protected void printAllIndexContent( final String indexName, final String indexType )
    {
        final org.elasticsearch.action.search.SearchRequest searchRequest = new org.elasticsearch.action.search.SearchRequest().
            indices( indexName ).
            types( indexType ).
            source( new SearchSourceBuilder().
                size( 100 ).
                storedField( "_source" ).
                query( QueryBuilders.matchAllQuery() ) );

        try
        {
            final SearchResponse searchResponse = client.search( searchRequest, RequestOptions.DEFAULT );

            System.out.println( "\n\n---------- CONTENT --------------------------------" );
            System.out.println( searchResponse.toString() );
            System.out.println( "\n\n" );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    public static void waitForClusterHealth()
    {
        ElasticsearchFixture.elasticsearchIndexService.getClusterHealth( "10s" );
    }

    protected static RefreshResponse refresh()
    {
        try
        {
            return client.indices().refresh( new RefreshRequest(), RequestOptions.DEFAULT );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    protected static AcknowledgedResponse deleteAllIndices()
    {
        try
        {
            return client.indices().delete( new DeleteIndexRequest( "_all" ), RequestOptions.DEFAULT );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    static class EmbeddedElasticsearchExtension
        implements BeforeAllCallback
    {
        @Override
        public void beforeAll( ExtensionContext context )
        {
            context.getRoot().getStore( ExtensionContext.Namespace.GLOBAL ).getOrComputeIfAbsent( ElasticsearchFixture.class );
        }
    }

    static class ElasticsearchFixture
        implements ExtensionContext.Store.CloseableResource
    {

        static IndexServiceInternalImpl elasticsearchIndexService;

        static EmbeddedElasticsearchServer server;

        public ElasticsearchFixture()
            throws IOException
        {
            LOG.info( "Starting up Elasticsearch" );

            Path elasticsearchTemporaryFolder = Files.createTempDirectory( "elasticsearchFixture" );

            server = new EmbeddedElasticsearchServer( elasticsearchTemporaryFolder.toFile() );
//            Embedded Elasticsearch not supported https://www.elastic.co/blog/elasticsearch-the-server
//            client = server.getClient();

            elasticsearchIndexService = new IndexServiceInternalImpl();
            elasticsearchIndexService.setClient( client );
        }

        @Override
        public void close()
        {
            try
            {
                LOG.info( "Shutting down Elasticsearch" );
                if ( client != null )
                {
                    client.close();
                }
                if ( server != null )
                {
                    server.shutdown();
                }
            }
            catch ( IOException e )
            {
                throw new UncheckedIOException( e );
            }
        }
    }

}
