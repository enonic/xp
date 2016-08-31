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
import com.enonic.xp.repo.impl.storage.StorageService;
import com.enonic.xp.repository.IndexMapping;
import com.enonic.xp.repository.IndexSettings;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.repository.RepositorySettings;
import com.enonic.xp.util.JsonHelper;

@Component(immediate = true)
public class RepositoryServiceImpl
    implements RepositoryService
{
    private IndexServiceInternal indexServiceInternal;

    private final static Logger LOG = LoggerFactory.getLogger( RepositoryServiceImpl.class );

    private final static String CLUSTER_HEALTH_TIMEOUT_VALUE = "10s";

    private final static String DEFAULT_INDEX_RESOURCE_FOLDER = "/com/enonic/xp/repo/impl/repository/index";

    private final static IndexResourceProvider DEFAULT_INDEX_RESOURCE_PROVIDER =
        new DefaultIndexResourceProvider( DEFAULT_INDEX_RESOURCE_FOLDER );

    private StorageService storageService;

    private RepositoryId store( final Repository repository )
    {
        return null;
    }

    @Override
    public RepositoryId create( final RepositorySettings repositorySettings )
    {
        if ( !this.indexServiceInternal.isMaster() )
        {
            throw new RepositoryException( "Only master-nodes can initialize repositories" );
        }

        store( Repository.create().
            id( repositorySettings.getRepositoryId() ).
            build() );

        createIndexes( repositorySettings );
        applyMappings( repositorySettings );
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

    private void createIndexes( final RepositorySettings repositorySettings )
    {
        doCreateIndex( repositorySettings, IndexType.SEARCH );
        doCreateIndex( repositorySettings, IndexType.VERSION );
    }


    private void doCreateIndex( final RepositorySettings repositorySettings, final IndexType indexType )
    {
        final RepositoryId repositoryId = repositorySettings.getRepositoryId();
        final IndexSettings mergedSettings = mergeWithDefaultSettings( repositorySettings, indexType );

        indexServiceInternal.createIndex( CreateIndexRequest.create().
            indexName( resolveIndexName( repositoryId, indexType ) ).
            indexSettings( mergedSettings ).
            build() );
    }


    private IndexSettings mergeWithDefaultSettings( final RepositorySettings repositorySettings, final IndexType indexType )
    {
        final IndexSettings defaultSetting = DEFAULT_INDEX_RESOURCE_PROVIDER.getSettings( repositorySettings.getRepositoryId(), indexType );

        if ( repositorySettings.getIndexSettings( indexType ) != null )
        {
            return new IndexSettings(
                JsonHelper.merge( defaultSetting.getNode(), repositorySettings.getIndexSettings( indexType ).getNode() ) );
        }

        return defaultSetting;
    }

    private void applyMappings( final RepositorySettings repositorySettings )
    {
        applyMapping( repositorySettings, IndexType.SEARCH );
        applyMapping( repositorySettings, IndexType.VERSION );
        applyMapping( repositorySettings, IndexType.BRANCH );
    }

    private void applyMapping( final RepositorySettings repositorySettings, final IndexType indexType )
    {
        final RepositoryId repositoryId = repositorySettings.getRepositoryId();
        final IndexMapping mergedMapping = mergeWithDefaultMapping( repositorySettings, indexType );

        this.indexServiceInternal.applyMapping( ApplyMappingRequest.create().
            indexName( resolveIndexName( repositoryId, indexType ) ).
            indexType( indexType ).
            mapping( mergedMapping ).
            build() );
    }

    private IndexMapping mergeWithDefaultMapping( final RepositorySettings repositorySettings, final IndexType indexType )
    {
        final IndexMapping defaultMapping = DEFAULT_INDEX_RESOURCE_PROVIDER.getMapping( repositorySettings.getRepositoryId(), indexType );

        if ( repositorySettings.getIndexMappings( indexType ) != null )
        {
            return new IndexMapping(
                JsonHelper.merge( defaultMapping.getNode(), repositorySettings.getIndexMappings( indexType ).getNode() ) );
        }

        return defaultMapping;
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
