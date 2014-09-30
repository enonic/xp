package com.enonic.wem.core.repository;

import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.index.IndexType;

public class RepositoryInitializer
{
    @Inject
    private IndexService indexService;

    private final static Logger LOG = LoggerFactory.getLogger( RepositoryInitializer.class );

    public final void init( final Repository repository )
    {
        LOG.info( "Initializing repository {}", repository.getId() );

        deleteExistingRepoIndices( repository );

        createIndexes( repository );

        indexService.applyMapping( StorageNameResolver.resolveStorageIndexName( repository ), IndexType.WORKSPACE.getName(),
                                   RepositoryIndexMappingProvider.getWorkspaceMapping( repository ) );

        indexService.applyMapping( StorageNameResolver.resolveStorageIndexName( repository ), IndexType.VERSION.getName(),
                                   RepositoryIndexMappingProvider.getVersionMapping( repository ) );

        indexService.applyMapping( IndexNameResolver.resolveSearchIndexName( repository ), IndexType._DEFAULT_.getName(),
                                   RepositoryIndexMappingProvider.getSearchMappings( repository ) );
    }

    private void createIndexes( final Repository repository )
    {
        LOG.info( "Create storage-index for repository {}", repository.getId() );
        final String storageIndexName = StorageNameResolver.resolveStorageIndexName( repository );
        final String storageIndexSettings = RepositoryStorageSettingsProvider.getSettings( repository );
        indexService.createIndex( storageIndexName, storageIndexSettings );

        LOG.info( "Create search-index for repository {}", repository.getId() );
        final String searchIndexName = IndexNameResolver.resolveSearchIndexName( repository );
        final String searchIndexSettings = RepositorySearchIndexSettingsProvider.getSettings( repository );
        indexService.createIndex( searchIndexName, searchIndexSettings );
    }

    private void deleteExistingRepoIndices( final Repository repository )
    {
        final Set<String> repoIndexes = indexService.getAllRepositoryIndices( repository );

        if ( !repoIndexes.isEmpty() )
        {
            indexService.deleteIndex( repoIndexes );
        }
    }

    public boolean isInitialized( final Repository repository )
    {
        final String storageIndexName = StorageNameResolver.resolveStorageIndexName( repository );
        final String searchIndexName = IndexNameResolver.resolveSearchIndexName( repository );

        return indexService.indicesExists( storageIndexName, searchIndexName );
    }

    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }
}
