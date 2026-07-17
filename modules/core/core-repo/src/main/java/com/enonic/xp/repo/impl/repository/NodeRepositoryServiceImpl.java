package com.enonic.xp.repo.impl.repository;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.index.IndexType;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.storage.spi.IndexMapping;
import com.enonic.xp.storage.spi.IndexSettings;
import com.enonic.xp.storage.spi.IndexSettingsMerger;
import com.enonic.xp.storage.spi.NodeSearchIndex;
import com.enonic.xp.storage.spi.RepositoryStorageAdmin;

@Component
public class NodeRepositoryServiceImpl
    implements NodeRepositoryService
{
    private static final Logger LOG = LoggerFactory.getLogger( NodeRepositoryServiceImpl.class );

    private static final IndexResourceProvider DEFAULT_INDEX_RESOURCE_PROVIDER = new DefaultIndexResourceProvider();

    private final IndexServiceInternal indexServiceInternal;

    private final RepositoryStorageAdmin repositoryStorageAdmin;

    private final NodeSearchIndex nodeSearchIndex;

    @Activate
    public NodeRepositoryServiceImpl( @Reference final IndexServiceInternal indexServiceInternal,
                                      @Reference final RepositoryStorageAdmin repositoryStorageAdmin,
                                      @Reference final NodeSearchIndex nodeSearchIndex )
    {
        this.indexServiceInternal = indexServiceInternal;
        this.repositoryStorageAdmin = repositoryStorageAdmin;
        this.nodeSearchIndex = nodeSearchIndex;
    }

    @Override
    public void create( final RepositoryId repositoryId, final RepositorySettings repositorySettings )
    {
        final IndexSettings storageSettings = mergeWithDefaultSettings( repositoryId, repositorySettings.getIndexSettings( IndexType.VERSION ), IndexType.VERSION );
        final Map<IndexType, IndexMapping> storageMappings =
            Map.of( IndexType.VERSION, DEFAULT_INDEX_RESOURCE_PROVIDER.getMapping( IndexType.VERSION ), IndexType.BRANCH,
                    DEFAULT_INDEX_RESOURCE_PROVIDER.getMapping( IndexType.BRANCH ), IndexType.COMMIT,
                    DEFAULT_INDEX_RESOURCE_PROVIDER.getMapping( IndexType.COMMIT ) );
        repositoryStorageAdmin.createIndex( repositoryId, storageSettings, storageMappings );

        final IndexSettings searchSettings = mergeWithDefaultSettings( repositoryId, repositorySettings.getIndexSettings( IndexType.SEARCH ), IndexType.SEARCH );
        final IndexMapping searchMapping = IndexSettingsMerger.merge( DEFAULT_INDEX_RESOURCE_PROVIDER.getMapping( IndexType.SEARCH ),
                                                                      repositorySettings.getIndexMappings( IndexType.SEARCH ) );
        nodeSearchIndex.createIndex( repositoryId, searchSettings, searchMapping );

        indexServiceInternal.waitForYellowStatus( resolveIndexNames( repositoryId ) );
    }

    @Override
    public void updateMappings( final RepositoryId repositoryId, final RepositorySettings repositorySettings )
    {
        updateIndexMappings( repositoryId,
                             Map.of( IndexType.VERSION, DEFAULT_INDEX_RESOURCE_PROVIDER.getMapping( IndexType.VERSION ), IndexType.BRANCH,
                                     DEFAULT_INDEX_RESOURCE_PROVIDER.getMapping( IndexType.BRANCH ), IndexType.COMMIT,
                                     DEFAULT_INDEX_RESOURCE_PROVIDER.getMapping( IndexType.COMMIT ) ) );

        updateIndexMappings( repositoryId, Map.of( IndexType.SEARCH, IndexSettingsMerger.merge(
            DEFAULT_INDEX_RESOURCE_PROVIDER.getMapping( IndexType.SEARCH ), repositorySettings.getIndexMappings( IndexType.SEARCH ) ) ) );

        indexServiceInternal.waitForYellowStatus( resolveIndexNames( repositoryId ) );
    }

    @Override
    public void delete( final RepositoryId repositoryId )
    {
        repositoryStorageAdmin.deleteIndex( repositoryId );
        nodeSearchIndex.deleteIndex( repositoryId );
    }

    @Override
    public boolean isInitialized( final RepositoryId repositoryId )
    {
        return repositoryStorageAdmin.indexExists( repositoryId ) && nodeSearchIndex.indexExists( repositoryId );
    }

    @Override
    public void refresh( final RepositoryId repositoryId )
    {
        repositoryStorageAdmin.refresh( repositoryId );
        nodeSearchIndex.refresh( repositoryId );
    }

    private void updateIndexMappings( final RepositoryId repositoryId, final Map<IndexType, IndexMapping> mappings )
    {
        for ( Map.Entry<IndexType, IndexMapping> entry : mappings.entrySet() )
        {
            repositoryStorageAdmin.putIndexMapping( repositoryId, entry.getKey(), entry.getValue().getData() );
        }
    }

    private IndexSettings mergeWithDefaultSettings( final RepositoryId repositoryId, final IndexSettings indexSettings,
                                                    final IndexType indexType )
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
            final String numberOfReplicas = repositoryStorageAdmin.getIndexSettings( SystemConstants.SYSTEM_REPO_ID, IndexType.VERSION )
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

    private static String[] resolveIndexNames( final RepositoryId repositoryId )
    {
        return IndexNameResolver.resolveIndexNames( repositoryId ).toArray( String[]::new );
    }
}
