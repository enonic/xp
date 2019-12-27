package com.enonic.xp.repo.impl.elasticsearch;

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
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.elasticsearch.client.impl.EsClient;
import com.enonic.xp.repo.impl.elasticsearch.distro.ElasticsearchInstance;

@Tag("elasticsearch")
@ExtendWith(AbstractElasticsearchIntegrationTest.ElasticsearchExtension.class)
public abstract class AbstractElasticsearchIntegrationTest
{
    private final static Logger LOG = LoggerFactory.getLogger( AbstractElasticsearchIntegrationTest.class );

    protected static EsClient client;

    private static IndexServiceInternalImpl indexService;

    private static Path snapshotsDir;

    protected boolean indexExists( String index )
    {
        final GetResponse response = client.get( new GetRequest().index( index ) );
        return response.isExists();
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

        final SearchResponse searchResponse = client.search( searchRequest );

        System.out.println( "\n\n---------- CONTENT --------------------------------" );
        System.out.println( searchResponse.toString() );
        System.out.println( "\n\n" );
    }

    public static void waitForClusterHealth()
    {
        indexService.getClusterHealth( "10s" );
    }

    protected static RefreshResponse refresh()
    {
        return client.indicesRefresh( new RefreshRequest() );
    }

    protected static AcknowledgedResponse deleteAllIndices()
    {
        return client.indicesDelete( new DeleteIndexRequest( "search-*", "branch-*", "version-*", "commit-*" ) );
    }

    static class ElasticsearchExtension
        implements BeforeAllCallback
    {
        static ElasticsearchInstance elasticsearchInstance;

        @Override
        public void beforeAll( ExtensionContext context )
        {
            ExtensionContext.Store rootStore = context.getRoot().getStore( ExtensionContext.Namespace.GLOBAL );
            rootStore.getOrComputeIfAbsent( "elasticsearch-fixture", key -> {
                try
                {
                    Path rootDirectory = Files.createTempDirectory( "elasticsearch-fixture" );

                    elasticsearchInstance = new ElasticsearchInstance( rootDirectory );
                    elasticsearchInstance.start();
                }
                catch ( IOException e )
                {
                    throw new UncheckedIOException( e );
                }
                snapshotsDir = elasticsearchInstance.getSnapshotsDir();

                client = new EsClient( "localhost", 9200 );
                indexService = new IndexServiceInternalImpl();
                indexService.setClient( client );
                return new ElasticsearchResource();
            } );

        }

        static class ElasticsearchResource
            implements ExtensionContext.Store.CloseableResource
        {
            @Override
            public void close()
                throws Throwable
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

}
