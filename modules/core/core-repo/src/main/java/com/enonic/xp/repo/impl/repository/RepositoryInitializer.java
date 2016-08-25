package com.enonic.xp.repo.impl.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.index.IndexType;
import com.enonic.xp.repo.impl.elasticsearch.ClusterHealthStatus;
import com.enonic.xp.repo.impl.elasticsearch.ClusterStatusCode;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repository.IndexConfig;
import com.enonic.xp.repository.IndexConfigs;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.repository.RepositorySettings;

public final class RepositoryInitializer
{
    private final static String CLUSTER_HEALTH_TIMEOUT_VALUE = "10s";

    private final static Logger LOG = LoggerFactory.getLogger( RepositoryInitializer.class );

    private final IndexServiceInternal indexServiceInternal;

    private final RepositoryService repositoryService;

    private final static String INDEX_RESOURCE_BASE_FOLDER = "/com/enonic/xp/repo/impl/repository/index";

    public RepositoryInitializer( final IndexServiceInternal indexServiceInternal, final RepositoryService repositoryService )
    {
        this.indexServiceInternal = indexServiceInternal;
        this.repositoryService = repositoryService;
    }

    public void initializeRepositories( final RepositoryId... repositoryIds )
    {
        final IndexResourceProvider indexResourceProvider = new IndexResourceClasspathProvider( INDEX_RESOURCE_BASE_FOLDER );

        if ( !checkClusterHealth() )
        {
            throw new RepositoryException( "Unable to initialize repositories" );
        }

        final boolean isMaster = indexServiceInternal.isMaster();

        for ( final RepositoryId repositoryId : repositoryIds )
        {
            if ( !isInitialized( repositoryId ) && isMaster )
            {
                final RepositorySettings repoSettings = RepositorySettings.create().
                    repositoryId( repositoryId ).
                    indexConfigs( IndexConfigs.create().
                        add( IndexType.SEARCH, IndexConfig.create().
                            mapping( IndexMappingProvider.get( repositoryId, IndexType.SEARCH, indexResourceProvider ) ).
                            settings( IndexSettingsProvider.get( repositoryId, IndexType.SEARCH, indexResourceProvider ) ).
                            build() ).
                        add( IndexType.BRANCH, IndexConfig.create().
                            mapping( IndexMappingProvider.get( repositoryId, IndexType.BRANCH, indexResourceProvider ) ).
                            settings( IndexSettingsProvider.get( repositoryId, IndexType.BRANCH, indexResourceProvider ) ).
                            build() ).
                        add( IndexType.VERSION, IndexConfig.create().
                            mapping( IndexMappingProvider.get( repositoryId, IndexType.VERSION, indexResourceProvider ) ).
                            settings( IndexSettingsProvider.get( repositoryId, IndexType.VERSION, indexResourceProvider ) ).
                            build() ).
                        build() ).
                    build();

                this.repositoryService.create( repoSettings );
            }
            else
            {
                waitForInitialized( repositoryId );
            }
        }
    }

    private boolean checkClusterHealth()
    {
        try
        {
            final ClusterHealthStatus clusterHealth = indexServiceInternal.getClusterHealth( CLUSTER_HEALTH_TIMEOUT_VALUE );

            if ( clusterHealth.isTimedOut() || clusterHealth.getClusterStatusCode().equals( ClusterStatusCode.RED ) )
            {
                LOG.error( "Cluster not healthy: " + "timed out: " + clusterHealth.isTimedOut() + ", state: " +
                               clusterHealth.getClusterStatusCode() );
                return false;
            }

            return true;
        }
        catch ( Exception e )
        {
            LOG.error( "Failed to get cluster health status", e );
        }

        return false;
    }

    private void waitForInitialized( final RepositoryId repositoryId )
    {
        LOG.info( "Waiting for repository '{}' to be initialized", repositoryId );

        final String storageIndexName = getStoreIndexName( repositoryId );
        final String searchIndexName = getSearchIndexName( repositoryId );

        indexServiceInternal.getClusterHealth( CLUSTER_HEALTH_TIMEOUT_VALUE, storageIndexName, searchIndexName );
    }

    private boolean isInitialized( final RepositoryId repositoryId )
    {
        final String storageIndexName = getStoreIndexName( repositoryId );
        final String searchIndexName = getSearchIndexName( repositoryId );

        return indexServiceInternal.indicesExists( storageIndexName, searchIndexName );
    }

    private String getStoreIndexName( final RepositoryId repositoryId )
    {
        return IndexNameResolver.resolveStorageIndexName( repositoryId );
    }

    private String getSearchIndexName( final RepositoryId repositoryId )
    {
        return IndexNameResolver.resolveSearchIndexName( repositoryId );
    }


}
