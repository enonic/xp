package com.enonic.wem.repo.internal.elasticsearch;

import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.repo.internal.elasticsearch.storage.ElasticsearchStorageDao;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;

public abstract class AbstractElasticsearchIntegrationTest
{
    protected static final Repository TEST_REPO = Repository.create().
        id( RepositoryId.from( "cms-repo" ) ).
        build();

    private final static Logger LOG = LoggerFactory.getLogger( AbstractElasticsearchIntegrationTest.class );

    protected ElasticsearchDaoImpl elasticsearchDao;

    protected ElasticsearchStorageDao storageDao;

    protected ElasticsearchIndexServiceInternal elasticsearchIndexService;

    protected Client client;

    private EmbeddedElasticsearchServer server;

    @Before
    public void setUp()
        throws Exception
    {
        server = new EmbeddedElasticsearchServer();

        this.client = server.getClient();
        this.elasticsearchDao = new ElasticsearchDaoImpl();
        this.elasticsearchDao.setClient( client );
        this.storageDao = new ElasticsearchStorageDao();
        this.storageDao.setClient( client );

        this.elasticsearchIndexService = new ElasticsearchIndexServiceInternal();
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
            setSource( termQuery ).
            addFields( "_source", "_parent" );

        final SearchResponse searchResponse = this.client.search( searchRequest.request() ).actionGet();

        System.out.println( "\n\n---------- CONTENT --------------------------------" );
        System.out.println( searchResponse.toString() );
        System.out.println( "\n\n" );
    }

    public void waitForClusterHealth()
    {
        elasticsearchIndexService.getClusterHealth( TimeValue.timeValueSeconds( 10 ) );
    }

    protected final RefreshResponse refresh()
    {
        RefreshResponse actionGet = client.admin().indices().prepareRefresh().execute().actionGet();
        return actionGet;
    }

    @After
    public void cleanUp()
    {
        LOG.info( "Shutting down" );
        this.client.close();
        server.shutdown();
    }
}
