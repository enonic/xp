package com.enonic.wem.repo.internal.elasticsearch;

import java.time.Instant;
import java.util.Collection;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesRequest;
import org.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesResponse;
import org.elasticsearch.action.admin.cluster.repositories.put.PutRepositoryRequestBuilder;
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotRequestBuilder;
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotRequestBuilder;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.repositories.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.api.home.HomeDir;
import com.enonic.wem.repo.internal.elasticsearch.document.DeleteDocument;
import com.enonic.wem.repo.internal.elasticsearch.document.StoreDocument;
import com.enonic.wem.repo.internal.elasticsearch.query.ElasticsearchQuery;
import com.enonic.wem.repo.internal.elasticsearch.result.GetResultFactory;
import com.enonic.wem.repo.internal.elasticsearch.result.SearchResultFactory;
import com.enonic.wem.repo.internal.elasticsearch.xcontent.StoreDocumentXContentBuilderFactory;
import com.enonic.wem.repo.internal.index.IndexException;
import com.enonic.wem.repo.internal.index.query.QueryService;
import com.enonic.wem.repo.internal.index.result.GetResult;
import com.enonic.wem.repo.internal.index.result.SearchResult;

public class ElasticsearchDao
{
    private static final boolean DEFAULT_REFRESH = true;

    private final String searchPreference = "_local";

    private final String searchTimeout = "5s";

    private final String storeTimeout = "5s";

    private final String deleteTimeout = "5s";

    private final static Logger LOG = LoggerFactory.getLogger( ElasticsearchIndexService.class );

    private Client client;

    public String store( final IndexRequest indexRequest )
    {
        final IndexResponse indexResponse = this.client.index( indexRequest ).
            actionGet( storeTimeout );

        return indexResponse.getId();
    }

    public void store( final Collection<StoreDocument> storeDocuments )
    {
        for ( StoreDocument storeDocument : storeDocuments )
        {
            final String id = storeDocument.getId();

            final XContentBuilder xContentBuilder = StoreDocumentXContentBuilderFactory.create( storeDocument );

            final IndexRequest req = Requests.indexRequest().
                id( id ).
                index( storeDocument.getIndexName() ).
                type( storeDocument.getIndexTypeName() ).
                source( xContentBuilder ).
                refresh( storeDocument.isRefreshAfterOperation() );

            this.client.index( req ).actionGet( storeTimeout );
        }
    }

    public boolean delete( final DeleteRequest deleteRequest )
    {
        return doDelete( deleteRequest );
    }

    public boolean delete( final DeleteDocument deleteDocument )
    {
        DeleteRequest deleteRequest = new DeleteRequest( deleteDocument.getIndexName() ).
            type( deleteDocument.getIndexTypeName() ).
            id( deleteDocument.getId() ).
            refresh( DEFAULT_REFRESH );

        return doDelete( deleteRequest );
    }

    private boolean doDelete( final DeleteRequest deleteRequest )
    {
        final DeleteResponse deleteResponse = this.client.delete( deleteRequest ).
            actionGet( deleteTimeout );

        return deleteResponse.isFound();
    }

    public SearchResult find( final ElasticsearchQuery query )
    {
        final SearchRequestBuilder searchRequest = SearchRequestBuilderFactory.newFactory().
            query( query ).
            client( this.client ).
            resolvedSize( query.getSize() == QueryService.GET_ALL_SIZE_FLAG ? resolveSize( query ) : query.getSize() ).
            build().
            create();

        //System.out.println( searchRequest.toString() );

        return doSearchRequest( searchRequest );
    }

    public GetResult get( final GetQuery getQuery )
    {
        final GetRequest getRequest = new GetRequest( getQuery.getIndexName() ).
            type( getQuery.getIndexTypeName() ).
            preference( searchPreference ).
            id( getQuery.getId() );

        if ( getQuery.getReturnFields().isNotEmpty() )
        {
            getRequest.fields( getQuery.getReturnFields().getReturnFieldNames() );
        }

        if ( getQuery.getRouting() != null )
        {
            getRequest.routing( getQuery.getRouting() );
        }

        final GetResponse getResponse = client.get( getRequest ).
            actionGet( searchTimeout );

        return GetResultFactory.create( getResponse );
    }

    private SearchResult doSearchRequest( final SearchRequestBuilder searchRequestBuilder )
    {
        try
        {
            final SearchResponse searchResponse = searchRequestBuilder.
                setPreference( searchPreference ).
                execute().
                actionGet( searchTimeout );

            return SearchResultFactory.create( searchResponse );
        }
        catch ( ElasticsearchException e )
        {
            LOG.error( "Search request failed", e.getRootCause() );

            throw new IndexException( "Search request failed", e );
        }
    }

    public long count( final ElasticsearchQuery query )
    {
        SearchRequestBuilder searchRequestBuilder = new SearchRequestBuilder( this.client ).
            setIndices( query.getIndexName() ).
            setTypes( query.getIndexType() ).
            setQuery( query.getQuery() ).
            setSearchType( SearchType.COUNT ).
            setPreference( searchPreference );

        final SearchResult searchResult = doSearchRequest( searchRequestBuilder );

        return searchResult.getResults().getTotalHits();
    }

    public void snapshot( final String name )
    {
        if ( !snapshotRepositoryExists( name ) )
        {
            createSnapshotRepository( name, createSnapshotRepoPath( name ) );
        }

        final CreateSnapshotRequestBuilder createRequest = new CreateSnapshotRequestBuilder( this.client.admin().cluster() ).
            setIndices( "search-cms-repo", "storage-cms-repo" ).
            setIncludeGlobalState( false ).
            setWaitForCompletion( true ).
            setRepository( name ).
            setSnapshot( Instant.now().toString().toLowerCase() ).
            setSettings( ImmutableSettings.settingsBuilder().
                put( "ignore_unavailable", true ) );

        this.client.admin().cluster().createSnapshot( createRequest.request() ).actionGet();
    }

    public void restore( final String repositoryName, final String snapshotName )
    {
        RestoreSnapshotRequestBuilder restoreSnapshotRequestBuilder = new RestoreSnapshotRequestBuilder( this.client.admin().cluster() ).
            setRestoreGlobalState( false ).
            setIndices( "search-cms-repo", "storage-cms-repo" ).
            setRepository( repositoryName ).
            setSnapshot( snapshotName ).
            setWaitForCompletion( true );

        this.client.admin().cluster().restoreSnapshot( restoreSnapshotRequestBuilder.request() ).actionGet();
    }

    private String createSnapshotRepoPath( final String name )
    {
        return HomeDir.get().toString() + "/repo/snapshots/" + name;
    }

    private boolean snapshotRepositoryExists( final String name )
    {
        final GetRepositoriesRequest getRepositoriesRequest = new GetRepositoriesRequest( new String[]{name} );

        try
        {
            final GetRepositoriesResponse response = this.client.admin().cluster().getRepositories( getRepositoriesRequest ).actionGet();
            return !response.repositories().isEmpty();
        }
        catch ( RepositoryException e )
        {
            return false;
        }
    }

    private void createSnapshotRepository( final String name, final String path )
    {
        final PutRepositoryRequestBuilder requestBuilder = new PutRepositoryRequestBuilder( this.client.admin().cluster() ).
            setName( name ).
            setType( "fs" ).
            setSettings( ImmutableSettings.settingsBuilder().
                put( "compress", true ).
                put( "location", path ).
                build() );

        this.client.admin().cluster().putRepository( requestBuilder.request() ).actionGet();
    }

    private int resolveSize( final ElasticsearchQuery query )
    {
        if ( query.getSize() == QueryService.GET_ALL_SIZE_FLAG )
        {
            return safeLongToInt( this.count( query ) );
        }
        else
        {
            return query.getSize();
        }
    }

    private static int safeLongToInt( long l )
    {
        if ( l < Integer.MIN_VALUE || l > Integer.MAX_VALUE )
        {
            throw new IllegalArgumentException( l + " cannot be cast to int without changing its value." );
        }
        return (int) l;
    }

    public void setClient( final Client client )
    {
        this.client = client;
    }

}
