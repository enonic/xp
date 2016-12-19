package com.enonic.xp.repo.impl.elasticsearch;

import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.repo.impl.elasticsearch.storage.StorageDaoImpl;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;

public abstract class AbstractElasticsearchIntegrationTest
{
    protected static final Repository TEST_REPO = Repository.create().
        id( RepositoryId.from( "cms-repo" ) ).
        branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) ).
        build();

    private final static Logger LOG = LoggerFactory.getLogger( AbstractElasticsearchIntegrationTest.class );

    @Rule
    public TemporaryFolder xpHome = new TemporaryFolder();

    protected IndexServiceInternalImpl elasticsearchIndexService;

    protected Client client;

    private EmbeddedElasticsearchServer server;

    @Before
    public void setUp()
        throws Exception
    {
        server = new EmbeddedElasticsearchServer( xpHome.getRoot() );

        this.client = server.getClient();

        final StorageDaoImpl storageDao = new StorageDaoImpl();
        storageDao.setClient( this.client );

        this.elasticsearchIndexService = new IndexServiceInternalImpl();
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
            addFields( "_source" );

        final SearchResponse searchResponse = this.client.search( searchRequest.request() ).actionGet();

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

    @After
    public void cleanUp()
    {
        LOG.info( "Shutting down" );
        this.client.close();
        server.shutdown();
    }
}
