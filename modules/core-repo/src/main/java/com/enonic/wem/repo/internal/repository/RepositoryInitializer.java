package com.enonic.wem.repo.internal.repository;

import java.util.Set;

import org.elasticsearch.common.unit.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.repo.internal.elasticsearch.ClusterHealthStatus;
import com.enonic.wem.repo.internal.elasticsearch.ClusterStatusCode;
import com.enonic.wem.repo.internal.index.IndexService;
import com.enonic.wem.repo.internal.index.IndexType;

public final class RepositoryInitializer
{
    private final IndexService indexService;

    private final static TimeValue CLUSTER_HEALTH_TIMEOUT_VALUE = TimeValue.timeValueSeconds( 10 );

    private final static Logger LOG = LoggerFactory.getLogger( RepositoryInitializer.class );

    public RepositoryInitializer( final IndexService indexService )
    {
        this.indexService = indexService;
    }

    public void initializeRepository( final Repository repository )
    {
        if ( !isInitialized( repository ) )
        {
            doInitializeRepo( repository );
        }
        else
        {
            waitForInitialized( repository );
        }
    }

    private void doInitializeRepo( final Repository repository )
    {
        LOG.info( "Initializing repositoryId {}", repository.getId() );

        final ClusterHealthStatus clusterHealth = indexService.getClusterHealth( CLUSTER_HEALTH_TIMEOUT_VALUE );

        if ( clusterHealth.isTimedOut() || !clusterHealth.getClusterStatusCode().equals( ClusterStatusCode.RED ) )
        {
            deleteExistingRepoIndices( repository );
        }

        createIndexes( repository );

        final String storageIndexName = getStoreIndexName( repository );
        final String searchIndexName = getSearchIndexName( repository );

        indexService.applyMapping( storageIndexName, IndexType.BRANCH.getName(),
                                   RepositoryIndexMappingProvider.getBranchMapping( repository ) );

        indexService.applyMapping( storageIndexName, IndexType.VERSION.getName(),
                                   RepositoryIndexMappingProvider.getVersionMapping( repository ) );

        indexService.applyMapping( searchIndexName, IndexType._DEFAULT_.getName(),
                                   RepositoryIndexMappingProvider.getSearchMappings( repository ) );

        indexService.refresh( storageIndexName, searchIndexName );
    }

    public void waitForInitialized( final Repository repository )
    {
        LOG.info( "Waiting for cluster to be initialized" );

        final String storageIndexName = getStoreIndexName( repository );
        final String searchIndexName = getSearchIndexName( repository );

        indexService.getClusterHealth( CLUSTER_HEALTH_TIMEOUT_VALUE, storageIndexName, searchIndexName );
    }

    private void createIndexes( final Repository repository )
    {
        LOG.info( "Create storage-index for repositoryId {}", repository.getId() );
        final String storageIndexName = getStoreIndexName( repository );
        final String storageIndexSettings = RepositoryStorageSettingsProvider.getSettings( repository );
        indexService.createIndex( storageIndexName, storageIndexSettings );

        LOG.info( "Create search-index for repositoryId {}", repository.getId() );
        final String searchIndexName = getSearchIndexName( repository );
        final String searchIndexSettings = RepositorySearchIndexSettingsProvider.getSettings( repository );
        indexService.createIndex( searchIndexName, searchIndexSettings );
    }

    private void deleteExistingRepoIndices( final Repository repository )
    {
        if ( isInitialized( repository ) )
        {
            LOG.info( "Deleting existing repository indices" );

            final Set<String> repoIndexes = Sets.newHashSet();

            repoIndexes.add( getStoreIndexName( repository ) );
            repoIndexes.add( getSearchIndexName( repository ) );

            if ( !repoIndexes.isEmpty() )
            {
                indexService.deleteIndices( repoIndexes );
            }

            indexService.getClusterHealth( CLUSTER_HEALTH_TIMEOUT_VALUE );
        }
        else
        {
            LOG.info( "No existing indices found" );
        }
    }

    private boolean isInitialized( final Repository repository )
    {
        final String storageIndexName = getStoreIndexName( repository );
        final String searchIndexName = getSearchIndexName( repository );

        return indexService.indicesExists( storageIndexName, searchIndexName );
    }

    private String getStoreIndexName( final Repository repository )
    {
        return StorageNameResolver.resolveStorageIndexName( repository.getId() );
    }

    private String getSearchIndexName( final Repository repository )
    {
        return IndexNameResolver.resolveSearchIndexName( repository.getId() );
    }
}
