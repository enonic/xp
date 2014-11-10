package com.enonic.wem.itests.core.elasticsearch;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.unit.TimeValue;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.core.elasticsearch.ElasticsearchDao;
import com.enonic.wem.core.elasticsearch.ElasticsearchIndexService;

public abstract class AbstractElasticsearchIntegrationTest
    //extends ElasticsearchIntegrationTest
{
    protected ElasticsearchDao elasticsearchDao;

    protected ElasticsearchIndexService elasticsearchIndexService;

    private EmbeddedElasticsearchServer server;

    protected Client client;

    private final static Logger LOG = LoggerFactory.getLogger( AbstractElasticsearchIntegrationTest.class );

    @Before
    public void setUp()
        throws Exception
    {
        server = new EmbeddedElasticsearchServer();

        this.client = server.getClient();
        this.elasticsearchDao = new ElasticsearchDao();
        this.elasticsearchDao.setClient( client );

        this.elasticsearchIndexService = new ElasticsearchIndexService();
        elasticsearchIndexService.setElasticsearchDao( elasticsearchDao );
        elasticsearchIndexService.setClient( client );
    }


    protected boolean indexExists( String index )
    {
        IndicesExistsResponse actionGet = this.client.admin().indices().prepareExists( index ).execute().actionGet();
        return actionGet.isExists();
    }


    protected void printAllIndexContent( final String indexName, final String indexType )
    {
        String termQuery = "{\n" +
            "  \"query\": { \"match_all\": {} }\n" +
            "}";

        SearchRequestBuilder searchRequest = new SearchRequestBuilder( this.client ).
            setSize( 100 ).
            setIndices( indexName ).
            setTypes( indexType ).
            setSource( termQuery );

        final SearchResponse searchResponse = this.client.search( searchRequest.request() ).actionGet();

        System.out.println( "\n\n---------- CONTENT --------------------------------" );
        System.out.println( searchResponse.toString() );
        System.out.println( "\n\n" );

    }


    String getContentRepoSearchDefaultSettings()
    {
        return RepositoryTestSearchIndexSettingsProvider.getSettings( ContentConstants.CONTENT_REPO );
    }

    protected Client client()
    {
        return this.client;
    }


    public ClusterHealthStatus waitForRelocation()
    {
        return waitForRelocation( ClusterHealthStatus.YELLOW );
    }

    /**
     * Waits for all relocating shards to become active and the cluster has reached the given health status
     * using the cluster health API.
     */
    public ClusterHealthStatus waitForRelocation( ClusterHealthStatus status )
    {
        ClusterHealthRequest request = Requests.clusterHealthRequest().waitForRelocatingShards( 0 );
        if ( status != null )
        {
            request.waitForStatus( status );
        }
        ClusterHealthResponse actionGet = client().admin().cluster().health( request ).actionGet( TimeValue.timeValueSeconds( 1 ) );
        if ( actionGet.isTimedOut() )
        {
            LOG.info( "waitForRelocation timed out (status={}), cluster state:\n{}\n{}" );
        }
        return actionGet.getStatus();
    }

    protected final RefreshResponse refresh()
    {
        waitForRelocation();
        RefreshResponse actionGet = client.admin().indices().prepareRefresh().execute().actionGet();
        return actionGet;
    }

    @After
    public void cleanUp()
    {
        server.shutdown();
    }


}
