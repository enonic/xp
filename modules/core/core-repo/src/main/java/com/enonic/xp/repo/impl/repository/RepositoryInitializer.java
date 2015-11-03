package com.enonic.xp.repo.impl.repository;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import com.enonic.xp.index.IndexType;
import com.enonic.xp.repo.impl.elasticsearch.ClusterHealthStatus;
import com.enonic.xp.repo.impl.elasticsearch.ClusterStatusCode;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.index.IndexSettings;
import com.enonic.xp.repository.RepositoryId;

public final class RepositoryInitializer
{
    private final static String CLUSTER_HEALTH_TIMEOUT_VALUE = "10s";

    private final static Logger LOG = LoggerFactory.getLogger( RepositoryInitializer.class );

    private final static int NUMBER_OF_TRIES_ON_GET_HEALTH = 3;

    private final IndexServiceInternal indexServiceInternal;

    public RepositoryInitializer( final IndexServiceInternal indexServiceInternal )
    {
        this.indexServiceInternal = indexServiceInternal;
    }

    public void initializeRepositories( final RepositoryId... repositoryIds )
    {
        if ( !checkClusterHealth( 1 ) )
        {
            LOG.error( "Cannot initialize repositories: cannot get cluster health state in " +
                           NUMBER_OF_TRIES_ON_GET_HEALTH + " attempts" );

            throw new RepositoryException( "Not able to initialize repositories" );
        }

        for ( final RepositoryId repositoryId : repositoryIds )
        {
            if ( !isInitialized( repositoryId ) )
            {
                doInitializeRepo( repositoryId );
            }
            else
            {
                waitForInitialized( repositoryId );
            }
        }
    }

    private boolean checkClusterHealth( int numberOfTries )
    {
        if ( numberOfTries > NUMBER_OF_TRIES_ON_GET_HEALTH )
        {
            return false;
        }

        LOG.info( "Getting cluster health status, attempt " + numberOfTries + " of " + NUMBER_OF_TRIES_ON_GET_HEALTH );

        try
        {
            final ClusterHealthStatus clusterHealth = indexServiceInternal.getClusterHealth( CLUSTER_HEALTH_TIMEOUT_VALUE );

            if ( clusterHealth.isTimedOut() || clusterHealth.getClusterStatusCode().equals( ClusterStatusCode.RED ) )
            {
                LOG.error( "Failed to get cluster health status; " + "timed out: " + clusterHealth.isTimedOut() + ", state: " +
                               clusterHealth.getClusterStatusCode() );

                checkClusterHealth( ++numberOfTries );
            }

            return true;
        }
        catch ( Exception e )
        {
            LOG.error( "Failed to get cluster health status", e );
            checkClusterHealth( ++numberOfTries );
        }

        return false;
    }

    private void doInitializeRepo( final RepositoryId repositoryId )
    {
        LOG.info( "Initializing repositoryId {}", repositoryId );

        createIndexes( repositoryId );

        final String storageIndexName = getStoreIndexName( repositoryId );
        final String searchIndexName = getSearchIndexName( repositoryId );

        indexServiceInternal.applyMapping( storageIndexName, IndexType.BRANCH,
                                           RepositoryIndexMappingProvider.getBranchMapping( repositoryId ) );

        indexServiceInternal.applyMapping( storageIndexName, IndexType.VERSION,
                                           RepositoryIndexMappingProvider.getVersionMapping( repositoryId ) );

        indexServiceInternal.applyMapping( searchIndexName, IndexType.SEARCH,
                                           RepositoryIndexMappingProvider.getSearchMappings( repositoryId ) );

        indexServiceInternal.refresh( storageIndexName, searchIndexName );
    }

    private void waitForInitialized( final RepositoryId repositoryId )
    {
        LOG.info( "Waiting for cluster to be initialized" );

        final String storageIndexName = getStoreIndexName( repositoryId );
        final String searchIndexName = getSearchIndexName( repositoryId );

        indexServiceInternal.getClusterHealth( CLUSTER_HEALTH_TIMEOUT_VALUE, storageIndexName, searchIndexName );
    }

    private void createIndexes( final RepositoryId repositoryId )
    {
        createStorageIndex( repositoryId );
        createSearchIndex( repositoryId );
    }

    private void createSearchIndex( final RepositoryId repositoryId )
    {
        LOG.info( "Create search-index for repositoryId {}", repositoryId );
        final String searchIndexName = getSearchIndexName( repositoryId );
        final IndexSettings searchIndexSettings = RepositorySearchIndexSettingsProvider.getSettings( repositoryId );
        LOG.info( "Applying search-index settings for repo {}: {}", repositoryId, searchIndexSettings.getSettingsAsString() );
        indexServiceInternal.createIndex( searchIndexName, searchIndexSettings );
    }

    private void createStorageIndex( final RepositoryId repositoryId )
    {
        LOG.info( "Create storage-index for repositoryId {}", repositoryId );
        final String storageIndexName = getStoreIndexName( repositoryId );
        final IndexSettings storageIndexSettings = RepositoryStorageSettingsProvider.getSettings( repositoryId );
        LOG.info( "Applying storage-index settings for repo {}: {}", repositoryId, storageIndexSettings.getSettingsAsString() );
        indexServiceInternal.createIndex( storageIndexName, storageIndexSettings );
    }

    private void deleteExistingRepoIndices( final RepositoryId repositoryId )
    {
        if ( isInitialized( repositoryId ) )
        {
            LOG.info( "Deleting existing repository indices" );

            final Set<String> repoIndexes = Sets.newHashSet();

            repoIndexes.add( getStoreIndexName( repositoryId ) );
            repoIndexes.add( getSearchIndexName( repositoryId ) );

            if ( !repoIndexes.isEmpty() )
            {
                indexServiceInternal.deleteIndices( repoIndexes.toArray( new String[repoIndexes.size()] ) );
            }

            indexServiceInternal.getClusterHealth( CLUSTER_HEALTH_TIMEOUT_VALUE );
        }
        else
        {
            LOG.info( "No existing indices found" );
        }
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
