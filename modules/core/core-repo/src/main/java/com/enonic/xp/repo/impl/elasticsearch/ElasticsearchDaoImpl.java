package com.enonic.xp.repo.impl.elasticsearch;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Set;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.cluster.repositories.delete.DeleteRepositoryRequest;
import org.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesRequest;
import org.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesResponse;
import org.elasticsearch.action.admin.cluster.repositories.put.PutRepositoryRequestBuilder;
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotRequestBuilder;
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotResponse;
import org.elasticsearch.action.admin.cluster.snapshots.delete.DeleteSnapshotRequest;
import org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsRequest;
import org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsRequestBuilder;
import org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsResponse;
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotRequestBuilder;
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotResponse;
import org.elasticsearch.action.admin.indices.close.CloseIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.open.OpenIndexRequestBuilder;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.collect.ImmutableList;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.repositories.RepositoryException;
import org.elasticsearch.snapshots.SnapshotInfo;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import com.enonic.xp.home.HomeDir;
import com.enonic.xp.node.RestoreParams;
import com.enonic.xp.node.RestoreResult;
import com.enonic.xp.node.SnapshotParams;
import com.enonic.xp.node.SnapshotResult;
import com.enonic.xp.node.SnapshotResults;
import com.enonic.xp.repo.impl.elasticsearch.document.DeleteDocument;
import com.enonic.xp.repo.impl.elasticsearch.document.IndexDocument;
import com.enonic.xp.repo.impl.elasticsearch.query.ElasticsearchQuery;
import com.enonic.xp.repo.impl.elasticsearch.result.SearchResultFactory;
import com.enonic.xp.repo.impl.elasticsearch.xcontent.StoreDocumentXContentBuilderFactory;
import com.enonic.xp.repo.impl.index.IndexException;
import com.enonic.xp.repo.impl.repository.IndexNameResolver;
import com.enonic.xp.repo.impl.search.SearchService;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repository.RepositoryId;

@Component
public class ElasticsearchDaoImpl
    implements ElasticsearchDao
{
    private static final boolean DEFAULT_REFRESH = false;

    private final static Logger LOG = LoggerFactory.getLogger( ElasticsearchIndexServiceInternal.class );

    private final static String SNAPSHOT_REPOSITORY_NAME = "enonic-xp-snapshot-repo";

    private final String searchPreference = "_local";

    private final String searchTimeout = "5s";

    private final String storeTimeout = "5s";

    private final String deleteTimeout = "5s";

    private Client client;

    private static int safeLongToInt( long l )
    {
        if ( l < Integer.MIN_VALUE || l > Integer.MAX_VALUE )
        {
            throw new IllegalArgumentException( l + " cannot be cast to int without changing its value." );
        }
        return (int) l;
    }

    @Override
    public void store( final Collection<IndexDocument> indexDocuments )
    {
        for ( IndexDocument indexDocument : indexDocuments )
        {
            final String id = indexDocument.getId();

            final XContentBuilder xContentBuilder = StoreDocumentXContentBuilderFactory.create( indexDocument );

            final IndexRequest req = Requests.indexRequest().
                id( id ).
                index( indexDocument.getIndexName() ).
                type( indexDocument.getIndexTypeName() ).
                source( xContentBuilder ).
                refresh( indexDocument.isRefreshAfterOperation() );

            this.client.index( req ).actionGet( storeTimeout );
        }
    }

    @Override
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

    @Override
    public SearchResult search( final ElasticsearchQuery query )
    {
        final SearchRequestBuilder searchRequest = SearchRequestBuilderFactory.newFactory().
            query( query ).
            client( this.client ).
            resolvedSize( query.getSize() == SearchService.GET_ALL_SIZE_FLAG ? resolveSize( query ) : query.getSize() ).
            build().
            create();

        //System.out.println( "######################\n\r" + searchRequest.toString() );

        return doSearchRequest( searchRequest );
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

    @Override
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

    @Override
    public SnapshotResult snapshot( final SnapshotParams params )
    {
        checkSnapshotRepository();

        final Set<String> indices = getSnapshotIndexNames( params.getRepositoryId(), params.isIncludeIndexedData() );

        final CreateSnapshotRequestBuilder createRequest = new CreateSnapshotRequestBuilder( this.client.admin().cluster() ).
            setIndices( indices.toArray( new String[indices.size()] ) ).
            setIncludeGlobalState( false ).
            setWaitForCompletion( true ).
            setRepository( SNAPSHOT_REPOSITORY_NAME ).
            setSnapshot( params.getSnapshotName() ).
            setSettings( ImmutableSettings.settingsBuilder().
                put( "ignore_unavailable", true ) );

        final CreateSnapshotResponse createSnapshotResponse =
            this.client.admin().cluster().createSnapshot( createRequest.request() ).actionGet();

        return SnapshotResultFactory.create( createSnapshotResponse );
    }

    @Override
    public RestoreResult restoreSnapshot( final RestoreParams params )
    {
        checkSnapshotRepository();

        final RepositoryId repositoryId = params.getRepositoryId();
        final Set<String> indices = getSnapshotIndexNames( repositoryId, params.isIncludeIndexedData() );

        closeIndices( indices );

        final RestoreSnapshotResponse response;
        try
        {
            final RestoreSnapshotRequestBuilder restoreSnapshotRequestBuilder =
                new RestoreSnapshotRequestBuilder( this.client.admin().cluster() ).
                    setRestoreGlobalState( false ).
                    setIndices( indices.toArray( new String[indices.size()] ) ).
                    setRepository( SNAPSHOT_REPOSITORY_NAME ).
                    setSnapshot( params.getSnapshotName() ).
                    setWaitForCompletion( true );

            response = this.client.admin().cluster().restoreSnapshot( restoreSnapshotRequestBuilder.request() ).actionGet();

            return RestoreResultFactory.create( response, repositoryId );
        }
        catch ( ElasticsearchException e )
        {
            return RestoreResult.create().
                repositoryId( repositoryId ).
                indices( indices ).
                failed( true ).
                name( params.getSnapshotName() ).
                message( "Could not restore snapshot: " + e.toString() + " to repository " + repositoryId ).
                build();
        }
        finally
        {
            openIndices( indices );
        }
    }

    @Override
    public SnapshotInfo getSnapshot( final String snapshotName )
    {
        final GetSnapshotsRequestBuilder getSnapshotsRequestBuilder = new GetSnapshotsRequestBuilder( this.client.admin().cluster() ).
            setRepository( SNAPSHOT_REPOSITORY_NAME ).
            setSnapshots( snapshotName );

        final GetSnapshotsResponse getSnapshotsResponse =
            this.client.admin().cluster().getSnapshots( getSnapshotsRequestBuilder.request() ).actionGet();

        final ImmutableList<SnapshotInfo> snapshots = getSnapshotsResponse.getSnapshots();

        if ( snapshots.size() == 0 )
        {
            return null;
        }
        else
        {
            return snapshots.get( 0 );
        }
    }

    private void checkSnapshotRepository()
    {
        if ( !snapshotRepositoryExists() )
        {
            registerRepository();
        }
    }

    @Override
    public void deleteSnapshot( final String snapshotName )
    {
        checkSnapshotRepository();

        final DeleteSnapshotRequest deleteSnapshotRequest = new DeleteSnapshotRequest( SNAPSHOT_REPOSITORY_NAME, snapshotName );

        this.client.admin().cluster().deleteSnapshot( deleteSnapshotRequest ).actionGet();
    }

    @Override
    public void deleteSnapshotRepository()
    {
        checkSnapshotRepository();

        final DeleteRepositoryRequest deleteRepositoryRequest = new DeleteRepositoryRequest( SNAPSHOT_REPOSITORY_NAME );
        this.client.admin().cluster().deleteRepository( deleteRepositoryRequest ).actionGet();
    }

    @Override
    public SnapshotResults listSnapshots()
    {
        checkSnapshotRepository();

        final GetSnapshotsRequest getSnapshotsRequest = new GetSnapshotsRequest( SNAPSHOT_REPOSITORY_NAME );

        final GetSnapshotsResponse getSnapshotsResponse = this.client.admin().cluster().getSnapshots( getSnapshotsRequest ).actionGet();

        return SnapshotResultsFactory.create( getSnapshotsResponse );
    }

    private Set<String> getSnapshotIndexNames( final RepositoryId repositoryId, final boolean includeIndexedData )
    {
        final Set<String> indices = Sets.newHashSet();

        indices.add( IndexNameResolver.resolveStorageIndexName( repositoryId ) );

        if ( includeIndexedData )
        {
            indices.add( IndexNameResolver.resolveSearchIndexName( repositoryId ) );
        }
        return indices;
    }

    private void openIndices( final Set<String> indexNames )
    {
        for ( final String indexName : indexNames )
        {
            OpenIndexRequestBuilder openIndexRequestBuilder = new OpenIndexRequestBuilder( this.client.admin().indices() ).
                setIndices( indexName );

            this.client.admin().indices().open( openIndexRequestBuilder.request() ).actionGet();

            LOG.info( "Opened index " + indexName );
        }
    }

    private void closeIndices( final Set<String> indexNames )
    {
        for ( final String indexName : indexNames )
        {
            CloseIndexRequestBuilder closeIndexRequestBuilder = new CloseIndexRequestBuilder( this.client.admin().indices() ).
                setIndices( indexName );

            this.client.admin().indices().close( closeIndexRequestBuilder.request() ).actionGet();

            LOG.info( "Closed index " + indexName );
        }
    }

    private boolean snapshotRepositoryExists()
    {
        final GetRepositoriesRequest getRepositoriesRequest = new GetRepositoriesRequest( new String[]{SNAPSHOT_REPOSITORY_NAME} );

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

    private void registerRepository()
    {
        final Path SNAPSHOT_PATH = Paths.get( HomeDir.get().toString(), "data", "snapshot" );

        final PutRepositoryRequestBuilder requestBuilder = new PutRepositoryRequestBuilder( this.client.admin().cluster() ).
            setName( SNAPSHOT_REPOSITORY_NAME ).
            setType( "fs" ).
            setSettings( ImmutableSettings.settingsBuilder().
                put( "compress", true ).
                put( "location", SNAPSHOT_PATH.toFile() ).
                build() );

        this.client.admin().cluster().putRepository( requestBuilder.request() ).actionGet();
    }

    private int resolveSize( final ElasticsearchQuery query )
    {
        if ( query.getSize() == SearchService.GET_ALL_SIZE_FLAG )
        {
            return safeLongToInt( this.count( query ) );
        }
        else
        {
            return query.getSize();
        }
    }

    @Reference
    public void setClient( final Client client )
    {
        this.client = client;
    }
}
