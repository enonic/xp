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
import com.enonic.xp.repo.impl.index.IndexSettingsMerger;
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

    private static final String DEFAULT_INDEX_RESOURCE_FOLDER = "/com/enonic/xp/repo/impl/repository/index";

    private static final IndexResourceProvider DEFAULT_INDEX_RESOURCE_PROVIDER =
        new DefaultIndexResourceProvider( DEFAULT_INDEX_RESOURCE_FOLDER );

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
        final RepositoryId repositoryId = params.getRepositoryId();
        final IndexSettings mergedSettings = mergeWithDefaultSettings( repositoryId, params.getRepositorySettings().getIndexSettings( indexType ), indexType );

        indexServiceInternal.createIndex( CreateIndexRequest.create()
                                              .indexName( resolveIndexName( repositoryId, indexType ) )
                                              .mappings( mappings )
                                              .indexSettings( mergedSettings )
                                              .build() );
    }


    private IndexSettings mergeWithDefaultSettings( final RepositoryId repositoryId, final IndexSettings indexSettings, final IndexType indexType )
    {
        final IndexSettings defaultFromFile = DEFAULT_INDEX_RESOURCE_PROVIDER.getSettings( indexType );
        if ( SystemConstants.SYSTEM_REPO_ID.equals( repositoryId ) )
        {
            return defaultFromFile;
        }
        final IndexSettings defaultSettings = adjustNumberOfReplicas( defaultFromFile );

        return IndexSettingsMerger.merge( defaultSettings, indexSettings );
    }

    private IndexSettings adjustNumberOfReplicas( final IndexSettings defaultSettings )
    {
        try
        {
            final String numberOfReplicas = indexServiceInternal.getIndexSettings( SystemConstants.SYSTEM_REPO_ID, IndexType.VERSION )
                .get( "index.number_of_replicas" );

            return IndexSettingsMerger.merge( defaultSettings,
                                       IndexSettings.from( Map.of( "index", Map.of( "number_of_replicas", numberOfReplicas ) ) ) );
        }
        catch ( Exception e )
        {
            LOG.warn( "Failed to retrieve number of replicas from [{}]",
                      IndexNameResolver.resolveStorageIndexName( SystemConstants.SYSTEM_REPO_ID ) );
        }

        return defaultSettings;
    }

    private static Map.Entry<IndexType, IndexMapping> mergeWithDefaultMapping( final RepositorySettings repositorySettings, final IndexType indexType )
    {
        final IndexMapping defaultMapping = DEFAULT_INDEX_RESOURCE_PROVIDER.getMapping( indexType );
        final IndexMapping settingsMapping = repositorySettings.getIndexMappings( indexType );

        return Map.entry( indexType, IndexSettingsMerger.merge( defaultMapping, settingsMapping ) );
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
