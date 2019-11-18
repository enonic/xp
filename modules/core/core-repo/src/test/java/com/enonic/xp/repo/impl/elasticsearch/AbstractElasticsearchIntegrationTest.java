package com.enonic.xp.repo.impl.elasticsearch;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.repo.impl.elasticsearch.distro.ElasticsearchInstance;

@Tag("elasticsearch")
@ExtendWith(AbstractElasticsearchIntegrationTest.ElasticsearchExtension.class)
public abstract class AbstractElasticsearchIntegrationTest
{
    private final static Logger LOG = LoggerFactory.getLogger( AbstractElasticsearchIntegrationTest.class );

    protected static RestHighLevelClient client;

    private static IndexServiceInternalImpl indexService;

    private static Path snapshotsDir;

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

    protected Path getSnapshotsDir()
    {
        return snapshotsDir;
    }

    protected void printAllIndexContent( final String indexName, final String indexType )
    {
        final org.elasticsearch.action.search.SearchRequest searchRequest = new org.elasticsearch.action.search.SearchRequest().
            indices( indexName ).
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
        indexService.getClusterHealth( "10s" );
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
            return client.indices().delete( new DeleteIndexRequest( "search-*", "branch-*", "version-*", "commit-*" ),
                                            RequestOptions.DEFAULT );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    static class ElasticsearchExtension
        implements BeforeAllCallback, AfterAllCallback
    {
        ElasticsearchInstance elasticsearchInstance;

        @Override
        public void beforeAll( ExtensionContext context )
            throws IOException, InterruptedException
        {
            elasticsearchInstance = new ElasticsearchInstance();
            elasticsearchInstance.start();

            snapshotsDir = elasticsearchInstance.getSnapshotsDir();

            client = new RestHighLevelClient( RestClient.builder( new HttpHost( "localhost", 9200, "http" ) ) );

            indexService = new IndexServiceInternalImpl();
            indexService.setClient( client );
        }

        @Override
        public void afterAll( final ExtensionContext context )
            throws Exception
        {
            elasticsearchInstance.stop();

            if ( client != null )
            {
                LOG.info( "Disconnect from Elasticsearch Client" );
                client.close();
            }
        }
    }

}
