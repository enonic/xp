package com.enonic.xp.repo.impl.repository;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.index.IndexType;
import com.enonic.xp.repo.impl.elasticsearch.ClusterHealthStatus;
import com.enonic.xp.repo.impl.elasticsearch.ClusterStatusCode;
import com.enonic.xp.repo.impl.index.ApplyMappingRequest;
import com.enonic.xp.repo.impl.index.CreateIndexRequest;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repository.IndexConfig;
import com.enonic.xp.repository.IndexConfigs;
import com.enonic.xp.repository.IndexResourceProvider;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.repository.RepositorySettings;

@Component(immediate = true)
public class RepositoryServiceImpl
    implements RepositoryService
{
    private IndexServiceInternal indexServiceInternal;

    private final static Logger LOG = LoggerFactory.getLogger( RepositoryServiceImpl.class );

    private final static String CLUSTER_HEALTH_TIMEOUT_VALUE = "10s";

    private final static String DEFAULT_INDEX_RESOURCE_FOLDER = "/com/enonic/xp/repo/impl/repository/index";

    private final static IndexResourceProvider DEFAULT_INDEX_RESOURCE_PROVIDER =
        new IndexResourceClasspathProvider( DEFAULT_INDEX_RESOURCE_FOLDER );

    @Override
    public RepositoryId create( final RepositorySettings repositorySettings )
    {
        if ( !this.indexServiceInternal.isMaster() )
        {
            throw new RepositoryException( "Only master-nodes can initialize repositories" );
        }

        final IndexConfigs indexConfigs = createIndexConfigs( repositorySettings.getRepositoryId(), DEFAULT_INDEX_RESOURCE_PROVIDER );

        doCreateIndexes( repositorySettings.getRepositoryId(), indexConfigs );
        applyMappings( repositorySettings.getRepositoryId(), indexConfigs );

        checkClusterHealth();

        return repositorySettings.getRepositoryId();
    }

    @Override
    public boolean isInitialized( final RepositoryId repositoryId )
    {
        if ( !checkClusterHealth() )
        {
            throw new RepositoryException( "Unable to initialize repositories" );
        }

        final String storageIndexName = IndexNameResolver.resolveStorageIndexName( repositoryId );
        final String searchIndexName = IndexNameResolver.resolveSearchIndexName( repositoryId );

        return indexServiceInternal.indicesExists( storageIndexName, searchIndexName );
    }

    private void doCreateIndexes( final RepositoryId repositoryId, final IndexConfigs indexConfigs )
    {
        doCreateIndex( repositoryId, IndexType.SEARCH, indexConfigs.get( IndexType.SEARCH ) );
        doCreateIndex( repositoryId, IndexType.VERSION, indexConfigs.get( IndexType.VERSION ) );
    }

    private IndexConfigs createIndexConfigs( final RepositoryId repositoryId, final IndexResourceProvider indexResourceProvider )
    {
        return IndexConfigs.create().
            add( IndexType.SEARCH, IndexConfig.create().
                mapping( indexResourceProvider.getMapping( repositoryId, IndexType.SEARCH ) ).
                settings( indexResourceProvider.getSettings( repositoryId, IndexType.SEARCH ) ).
                build() ).
            add( IndexType.BRANCH, IndexConfig.create().
                mapping( indexResourceProvider.getMapping( repositoryId, IndexType.BRANCH ) ).
                settings( indexResourceProvider.getSettings( repositoryId, IndexType.BRANCH ) ).
                build() ).
            add( IndexType.VERSION, IndexConfig.create().
                mapping( indexResourceProvider.getMapping( repositoryId, IndexType.VERSION ) ).
                settings( indexResourceProvider.getSettings( repositoryId, IndexType.VERSION ) ).
                build() ).
            build();
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

    @Reference
    public void setIndexServiceInternal( final IndexServiceInternal indexServiceInternal )
    {
        this.indexServiceInternal = indexServiceInternal;
    }

}
