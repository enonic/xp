package com.enonic.xp.repo.impl.repository;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.index.IndexType;
import com.enonic.xp.repo.impl.index.CreateIndexRequest;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.IndexMapping;
import com.enonic.xp.repository.IndexSettings;
import com.enonic.xp.repository.NodeRepositoryService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositorySettings;
import com.enonic.xp.security.SystemConstants;

@Component
public class NodeRepositoryServiceImpl
    implements NodeRepositoryService
{
    private static final Logger LOG = LoggerFactory.getLogger( NodeRepositoryServiceImpl.class );
    private final IndexServiceInternal indexServiceInternal;

    @Activate
    public NodeRepositoryServiceImpl( @Reference final IndexServiceInternal indexServiceInternal )
    {
        this.indexServiceInternal = indexServiceInternal;
    }

    @Override
    public void create( final CreateRepositoryParams params )
    {
        final RepositoryId repositoryId = params.getRepositoryId();

        final RepositorySettings repositorySettings = params.getRepositorySettings();
        createIndex( params, IndexType.VERSION,
                     Map.ofEntries( mergeWithDefaultMapping( repositorySettings, IndexType.VERSION ),
                                    mergeWithDefaultMapping( repositorySettings, IndexType.BRANCH ),
                                    mergeWithDefaultMapping( repositorySettings, IndexType.COMMIT ) ) );

        createIndex( params, IndexType.SEARCH,
                     Map.ofEntries( mergeWithDefaultMapping( repositorySettings, IndexType.SEARCH ) ) );

        indexServiceInternal.waitForYellowStatus( resolveIndexNames( repositoryId ) );
    }

    @Override
    public void delete( final RepositoryId repositoryId )
    {
        indexServiceInternal.deleteIndices( resolveIndexNames( repositoryId ) );
    }

    @Override
    public boolean isInitialized( final RepositoryId repositoryId )
    {
        return indexServiceInternal.indicesExists( resolveIndexNames( repositoryId ) );
    }

    private void createIndex( final CreateRepositoryParams params, final IndexType indexType, final Map<IndexType, IndexMapping> mappings )
    {
        final IndexSettings repositorySettingsIndexSettings = params.getRepositorySettings().getIndexSettings( indexType );
        final IndexSettings defaultIndexSettings = getDefaultIndexSettings( params.getRepositoryId(), indexType );
        final IndexSettings indexSettings =
            repositorySettingsIndexSettings != null ? defaultIndexSettings.merge( repositorySettingsIndexSettings ) : defaultIndexSettings;

        indexServiceInternal.createIndex( CreateIndexRequest.create()
                                              .indexName( resolveIndexName( params.getRepositoryId(), indexType ) )
                                              .mappings( mappings )
                                              .indexSettings( indexSettings )
                                              .build() );
    }

    public IndexSettings getDefaultIndexSettings( final RepositoryId repositoryId, final IndexType indexType )
    {
        final IndexSettings defaultSettings = DefaultIndexResourceProvider.INSTANCE.getSettings( indexType );
        if ( SystemConstants.SYSTEM_REPO_ID.equals( repositoryId ) )
        {
            return defaultSettings;
        }

        final Integer numberOfReplicas;
        try
        {
            numberOfReplicas = indexServiceInternal.getIndexSettings( SystemConstants.SYSTEM_REPO_ID, IndexType.VERSION )
                .getInteger( "index.number_of_replicas" );
        }
        catch ( Exception e )
        {
            LOG.warn( "Failed to retrieve number of replicas from [" +
                          IndexNameResolver.resolveStorageIndexName( SystemConstants.SYSTEM_REPO_ID ) + "]", e );
            return defaultSettings;
        }
        if ( numberOfReplicas == null )
        {
            return defaultSettings;
        }
        return defaultSettings.merge( IndexSettings.from( Map.of( "index", Map.of( "number_of_replicas", numberOfReplicas ) ) ) );
    }

    private static Map.Entry<IndexType, IndexMapping> mergeWithDefaultMapping( final RepositorySettings repositorySettings, final IndexType indexType )
    {
        final IndexMapping repositorySettingsIndexMapping = repositorySettings.getIndexMappings( indexType );
        final IndexMapping defaultIndexMapping = DefaultIndexResourceProvider.INSTANCE.getMapping( indexType );
        final IndexMapping indexMapping =
            repositorySettingsIndexMapping != null ? defaultIndexMapping.merge( repositorySettingsIndexMapping ) : defaultIndexMapping;
        return Map.entry( indexType, indexMapping );
    }

    private static String[] resolveIndexNames( final RepositoryId repositoryId )
    {
        return IndexNameResolver.resolveIndexNames( repositoryId ).toArray( String[]::new );
    }

    private static String resolveIndexName( final RepositoryId repositoryId, final IndexType indexType )
    {
        switch ( indexType )
        {
            case SEARCH:
                return IndexNameResolver.resolveSearchIndexName( repositoryId );
            case VERSION:
            case BRANCH:
            case COMMIT:
                return IndexNameResolver.resolveStorageIndexName( repositoryId );
            default:
                throw new IllegalArgumentException( "Cannot resolve index name for indexType [" + indexType.getName() + "]" );
        }
    }
}
