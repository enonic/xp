package com.enonic.wem.core.repository;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.index.IndexType;

public final class RepositoryInitializer
{
    private IndexService indexService;

    private final static Logger LOG = LoggerFactory.getLogger( RepositoryInitializer.class );

    public final void init( final Repository repository )
    {
        LOG.info( "Initializing repositoryId {}", repository.getId() );

        deleteExistingRepoIndices( repository );

        createIndexes( repository );

        indexService.applyMapping( StorageNameResolver.resolveStorageIndexName( repository.getId() ), IndexType.WORKSPACE.getName(),
                                   RepositoryIndexMappingProvider.getWorkspaceMapping( repository ) );

        indexService.applyMapping( StorageNameResolver.resolveStorageIndexName( repository.getId() ), IndexType.VERSION.getName(),
                                   RepositoryIndexMappingProvider.getVersionMapping( repository ) );

        indexService.applyMapping( IndexNameResolver.resolveSearchIndexName( repository.getId() ), IndexType._DEFAULT_.getName(),
                                   RepositoryIndexMappingProvider.getSearchMappings( repository ) );

        indexService.getIndexStatus( StorageNameResolver.resolveStorageIndexName( repository.getId() ), true );
    }

    private void createIndexes( final Repository repository )
    {
        indexService.getIndexStatus( StorageNameResolver.resolveStorageIndexName( repository.getId() ), true );

        LOG.info( "Create storage-index for repositoryId {}", repository.getId() );
        final String storageIndexName = StorageNameResolver.resolveStorageIndexName( repository.getId() );
        final String storageIndexSettings = RepositoryStorageSettingsProvider.getSettings( repository );
        indexService.createIndex( storageIndexName, storageIndexSettings );

        LOG.info( "Create search-index for repositoryId {}", repository.getId() );
        final String searchIndexName = IndexNameResolver.resolveSearchIndexName( repository.getId() );
        final String searchIndexSettings = RepositorySearchIndexSettingsProvider.getSettings( repository );
        indexService.createIndex( searchIndexName, searchIndexSettings );
    }

    private void deleteExistingRepoIndices( final Repository repository )
    {
        final Set<String> repoIndexes = Sets.newHashSet();

        repoIndexes.add( StorageNameResolver.resolveStorageIndexName( repository.getId() ) );
        repoIndexes.add( IndexNameResolver.resolveSearchIndexName( repository.getId() ) );

        //indexService.getAllRepositoryIndices( repository.getId() );

        if ( !repoIndexes.isEmpty() )
        {
            indexService.deleteIndices( repoIndexes );
        }

        //indexService.deleteIndices( StorageNameResolver.resolveStorageIndexName( repository.getId() ),
        //                            IndexNameResolver.resolveSearchIndexName( repository.getId() ) );

    }

    public boolean isInitialized( final Repository repository )
    {
        final String storageIndexName = StorageNameResolver.resolveStorageIndexName( repository.getId() );
        final String searchIndexName = IndexNameResolver.resolveSearchIndexName( repository.getId() );

        return indexService.indicesExists( storageIndexName, searchIndexName );
    }

    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }
}
