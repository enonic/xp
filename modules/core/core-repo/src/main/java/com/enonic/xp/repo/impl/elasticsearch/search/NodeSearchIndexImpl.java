package com.enonic.xp.repo.impl.elasticsearch.search;

import java.util.Collection;
import java.util.stream.Collectors;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsAction;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequest;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.index.IndexType;
import com.enonic.xp.repo.impl.StorageSource;
import com.enonic.xp.repo.impl.repository.IndexNameResolver;
import com.enonic.xp.repo.impl.search.SearchDao;
import com.enonic.xp.repo.impl.search.SearchStorageName;
import com.enonic.xp.repo.impl.search.SearchStorageType;
import com.enonic.xp.repo.impl.storage.DeleteRequests;
import com.enonic.xp.repo.impl.storage.GetByIdRequest;
import com.enonic.xp.repo.impl.storage.GetResult;
import com.enonic.xp.repo.impl.storage.IndexStoreRequest;
import com.enonic.xp.repo.impl.storage.RoutableId;
import com.enonic.xp.repo.impl.storage.SearchPreferences;
import com.enonic.xp.repo.impl.storage.StorageDao;
import com.enonic.xp.repository.IndexException;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.storage.spi.IndexDocumentRecord;
import com.enonic.xp.storage.spi.IndexMapping;
import com.enonic.xp.storage.spi.IndexSettings;
import com.enonic.xp.storage.spi.NodeSearchIndex;
import com.enonic.xp.storage.spi.ReturnFields;
import com.enonic.xp.storage.spi.ReturnValues;
import com.enonic.xp.storage.spi.SearchPreference;
import com.enonic.xp.storage.spi.SearchRequest;
import com.enonic.xp.storage.spi.SearchResult;
import com.enonic.xp.storage.spi.StorageIndexExistsException;
import com.enonic.xp.storage.spi.StorageIndexNotFoundException;
import com.enonic.xp.storage.spi.UpdateIndexSettings;

/**
 * Elasticsearch-backed {@link NodeSearchIndex}: the search-<repo> index half of the storage
 * SPI (Phase 0, Gate C — see {@code nodb/BUILD-PHASE-0.md}). Composes the existing
 * {@link SearchDao} (queries, including the storage-index branch/version/commit
 * listing/diffing carried by {@link SearchRequest}'s {@code SearchSource}) and
 * {@link StorageDao} (single search-document get/store/delete, unchanged from before the
 * extraction), plus the same raw Elasticsearch index-admin calls as
 * {@code IndexServiceInternalImpl} — kept as a separate class rather than folded into that
 * one because {@link NodeSearchIndex} and {@code RepositoryStorageAdmin} both declare
 * identically-named/-shaped lifecycle methods (createIndex/deleteIndex/indexExists/refresh/
 * updateSettings) for two different physical indices, which a single class cannot implement
 * with two different bodies.
 * <p>
 * Registered with the {@code storage.backend=elasticsearch} service property (Phase 0,
 * Gate D). Only one backend exists yet, so consumers still plain-{@code @Reference} this
 * service; Phase 1 backend selection becomes a {@code @Reference(target = "(storage.backend=...)")}
 * filter on this same property, not a rewrite.
 */
@Component(service = NodeSearchIndex.class, property = "storage.backend=elasticsearch")
public class NodeSearchIndexImpl
    implements NodeSearchIndex
{
    private static final Logger LOG = LoggerFactory.getLogger( NodeSearchIndexImpl.class );

    private static final String ES_DEFAULT_INDEX_TYPE_NAME = "_default_";

    private static final String CREATE_INDEX_TIMEOUT = "5s";

    private static final String UPDATE_INDEX_TIMEOUT = "5s";

    private static final String INDEX_EXISTS_TIMEOUT = "5s";

    private final Client client;

    private final SearchDao searchDao;

    private final StorageDao storageDao;

    @Activate
    public NodeSearchIndexImpl( @Reference final Client client, @Reference final SearchDao searchDao,
                                @Reference final StorageDao storageDao )
    {
        this.client = client;
        this.searchDao = searchDao;
        this.storageDao = storageDao;
    }

    @Override
    public SearchResult search( final SearchRequest searchRequest )
    {
        return searchDao.search( searchRequest );
    }

    @Override
    public void index( final RepositoryId repositoryId, final Branch branch, final IndexDocumentRecord doc )
    {
        storageDao.store( new IndexStoreRequest( doc, branch.getValue(), IndexNameResolver.resolveSearchIndexName( repositoryId ) ) );
    }

    @Override
    public void delete( final RepositoryId repositoryId, final Branch branch, final Collection<String> nodeIds )
    {
        storageDao.delete( DeleteRequests.create()
                                .settings( searchStorageSettings( repositoryId, branch ) )
                                .ids( nodeIds.stream().map( RoutableId::new ).collect( Collectors.toList() ) )
                                .build() );
    }

    @Override
    public ReturnValues get( final RepositoryId repositoryId, final Branch branch, final String nodeId, final ReturnFields returnFields,
                              final SearchPreference searchPreference )
    {
        final GetByIdRequest getByIdRequest = GetByIdRequest.create()
            .storageSettings( searchStorageSettings( repositoryId, branch ) )
            .searchPreference( SearchPreferences.fromSpi( searchPreference ) )
            .returnFields( returnFields )
            .id( nodeId )
            .build();

        final GetResult result = storageDao.getById( getByIdRequest );

        return result.getReturnValues();
    }

    private static StorageSource searchStorageSettings( final RepositoryId repositoryId, final Branch branch )
    {
        return StorageSource.create()
            .storageType( SearchStorageType.from( branch ) )
            .storageName( SearchStorageName.from( repositoryId ) )
            .build();
    }

    @Override
    public void refresh( final RepositoryId repositoryId )
    {
        final String indexName = IndexNameResolver.resolveSearchIndexName( repositoryId );
        try
        {
            client.admin().indices().prepareRefresh( indexName ).execute().actionGet();
        }
        catch ( IndexNotFoundException e )
        {
            throw new StorageIndexNotFoundException( "Index not found: " + indexName, e );
        }
    }

    @Override
    public void createIndex( final RepositoryId repositoryId, final IndexSettings settings, final IndexMapping mapping )
    {
        final String indexName = IndexNameResolver.resolveSearchIndexName( repositoryId );
        LOG.info( "creating index {}", indexName );

        final CreateIndexRequest createIndexRequest = new CreateIndexRequest( indexName );
        createIndexRequest.settings( settings.getData() );
        createIndexRequest.mapping( IndexType.SEARCH.isDynamicTypes() ? ES_DEFAULT_INDEX_TYPE_NAME : IndexType.SEARCH.getName(),
                                     mapping.getData() );

        try
        {
            final CreateIndexResponse createIndexResponse =
                client.admin().indices().create( createIndexRequest ).actionGet( CREATE_INDEX_TIMEOUT );

            LOG.info( "Index {} created with status {}", indexName, createIndexResponse.isAcknowledged() );
        }
        catch ( IndexAlreadyExistsException e )
        {
            throw new IndexException( "Failed to create index: " + indexName,
                                       new StorageIndexExistsException( "Index already exists: " + indexName, e ) );
        }
        catch ( ElasticsearchException e )
        {
            throw new IndexException( "Failed to create index: " + indexName, e );
        }
    }

    @Override
    public void deleteIndex( final RepositoryId repositoryId )
    {
        final String indexName = IndexNameResolver.resolveSearchIndexName( repositoryId );
        try
        {
            client.admin().indices().delete( new DeleteIndexRequest( indexName ) ).actionGet();
            LOG.info( "Deleted index {}", indexName );
        }
        catch ( ElasticsearchException e )
        {
            LOG.warn( "Failed to delete index {}", indexName, e );
        }
    }

    @Override
    public boolean indexExists( final RepositoryId repositoryId )
    {
        final String indexName = IndexNameResolver.resolveSearchIndexName( repositoryId );

        final IndicesExistsRequest request =
            new IndicesExistsRequestBuilder( this.client.admin().indices(), IndicesExistsAction.INSTANCE ).setIndices( indexName )
                .request();

        final IndicesExistsResponse response = client.admin().indices().exists( request ).actionGet( INDEX_EXISTS_TIMEOUT );

        return response.isExists();
    }

    @Override
    public void updateSettings( final RepositoryId repositoryId, final UpdateIndexSettings settings )
    {
        final String indexName = IndexNameResolver.resolveSearchIndexName( repositoryId );
        LOG.info( "updating index {}", indexName );

        final UpdateSettingsRequest updateSettingsRequest =
            new UpdateSettingsRequest().indices( indexName ).settings( settings.getSettingsAsString() );
        try
        {
            final UpdateSettingsResponse updateSettingsResponse =
                client.admin().indices().updateSettings( updateSettingsRequest ).actionGet( UPDATE_INDEX_TIMEOUT );

            LOG.info( "Index {} updated with status {}", indexName, updateSettingsResponse.isAcknowledged() );
        }
        catch ( ElasticsearchException e )
        {
            throw new IndexException( "Failed to update index: " + indexName, e );
        }
    }
}
