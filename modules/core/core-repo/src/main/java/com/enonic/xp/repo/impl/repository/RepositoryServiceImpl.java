package com.enonic.xp.repo.impl.repository;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.index.IndexType;
import com.enonic.xp.repo.impl.index.ApplyMappingRequest;
import com.enonic.xp.repo.impl.index.CreateIndexRequest;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repository.IndexConfig;
import com.enonic.xp.repository.IndexConfigs;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.repository.RepositorySettings;

@Component(immediate = true)
public class RepositoryServiceImpl
    implements RepositoryService
{
    private IndexServiceInternal indexServiceInternal;

    @Override
    public RepositoryId create( final RepositorySettings repositorySettings )
    {
        doCreateIndexes( repositorySettings.getRepositoryId(), repositorySettings.getIndexConfigs() );
        applyMappings( repositorySettings.getRepositoryId(), repositorySettings.getIndexConfigs() );

        return repositorySettings.getRepositoryId();
    }

    private void doCreateIndexes( final RepositoryId repositoryId, final IndexConfigs indexConfigs )
    {
        doCreateIndex( repositoryId, IndexType.SEARCH, indexConfigs.get( IndexType.SEARCH ) );
        doCreateIndex( repositoryId, IndexType.VERSION, indexConfigs.get( IndexType.VERSION ) );
    }

    private void applyMappings( final RepositoryId repositoryId, final IndexConfigs indexConfigs )
    {
        applyMapping( repositoryId, IndexType.SEARCH, indexConfigs.get( IndexType.SEARCH ) );
        applyMapping( repositoryId, IndexType.VERSION, indexConfigs.get( IndexType.VERSION ) );
        applyMapping( repositoryId, IndexType.BRANCH, indexConfigs.get( IndexType.BRANCH ) );
    }

    private void applyMapping( final RepositoryId repositoryId, final IndexType indexType, final IndexConfig indexConfig )
    {
        this.indexServiceInternal.applyMapping( ApplyMappingRequest.create().
            indexName( resolveIndexName( repositoryId, indexType ) ).
            indexType( indexType ).
            mapping( indexConfig.getMapping() ).
            build() );
    }

    private void doCreateIndex( final RepositoryId repositoryId, final IndexType indexType, final IndexConfig indexConfig )
    {
        indexServiceInternal.createIndex( CreateIndexRequest.create().
            indexName( resolveIndexName( repositoryId, indexType ) ).
            indexSettings( indexConfig.getSettings() ).
            build() );
    }

    private String resolveIndexName( final RepositoryId repositoryId, final IndexType indexType )
    {
        switch ( indexType )
        {
            case SEARCH:
            {
                return IndexNameResolver.resolveSearchIndexName( repositoryId );
            }
            case VERSION:
            {
                return IndexNameResolver.resolveStorageIndexName( repositoryId );
            }
            case BRANCH:
            {
                return IndexNameResolver.resolveStorageIndexName( repositoryId );
            }
        }

        throw new IllegalArgumentException( "Cannot resolve index name for indexType [" + indexType.getName() + "]" );
    }

    @Reference
    public void setIndexServiceInternal( final IndexServiceInternal indexServiceInternal )
    {
        this.indexServiceInternal = indexServiceInternal;
    }
}
