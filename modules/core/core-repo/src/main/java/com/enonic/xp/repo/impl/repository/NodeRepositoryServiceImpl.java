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
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.IndexMapping;
import com.enonic.xp.repository.IndexSettings;
import com.enonic.xp.repository.NodeRepositoryService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.util.JsonHelper;

@Component(immediate = true)
public class NodeRepositoryServiceImpl
    implements NodeRepositoryService
{
    private IndexServiceInternal indexServiceInternal;

    private final static Logger LOG = LoggerFactory.getLogger( NodeRepositoryServiceImpl.class );

    private final static String CLUSTER_HEALTH_TIMEOUT_VALUE = "10s";

    private final static String DEFAULT_INDEX_RESOURCE_FOLDER = "/com/enonic/xp/repo/impl/repository/index";

    private final static IndexResourceProvider DEFAULT_INDEX_RESOURCE_PROVIDER =
        new DefaultIndexResourceProvider( DEFAULT_INDEX_RESOURCE_FOLDER );

    @Override
    public void create( final CreateRepositoryParams params )
    {
        createIndexes( params );
        applyMappings( params );

        checkClusterHealth();
    }

    @Override
    public void delete( final RepositoryId repositoryId )
    {
        delete( repositoryId, IndexType.SEARCH );
        delete( repositoryId, IndexType.VERSION );
    }

    private void delete( final RepositoryId repositoryId, final IndexType indexType )
    {
        final String indexName = resolveIndexName( repositoryId, indexType );
        indexServiceInternal.deleteIndices( indexName );
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

    private void createIndexes( final CreateRepositoryParams params )
    {
        doCreateIndex( params, IndexType.SEARCH );
        doCreateIndex( params, IndexType.VERSION );
    }


    private void doCreateIndex( final CreateRepositoryParams params, final IndexType indexType )
    {
        final RepositoryId repositoryId = params.getRepositoryId();
        final IndexSettings mergedSettings = mergeWithDefaultSettings( params, indexType );

        indexServiceInternal.createIndex( CreateIndexRequest.create().
            indexName( resolveIndexName( repositoryId, indexType ) ).
            indexSettings( mergedSettings ).
            build() );
    }


    private IndexSettings mergeWithDefaultSettings( final CreateRepositoryParams params, final IndexType indexType )
    {
        final IndexSettings defaultSetting = DEFAULT_INDEX_RESOURCE_PROVIDER.getSettings( params.getRepositoryId(), indexType );

        final IndexSettings indexSettings = params.getRepositorySettings().getIndexSettings( indexType );
        if ( indexSettings != null )
        {
            return new IndexSettings( JsonHelper.merge( defaultSetting.getNode(), indexSettings.getNode() ) );
        }

        return defaultSetting;
    }

    private void applyMappings( final CreateRepositoryParams params )
    {
        applyMapping( params, IndexType.SEARCH );
        applyMapping( params, IndexType.BRANCH );
        applyMapping( params, IndexType.VERSION );
    }

    private void applyMapping( final CreateRepositoryParams params, final IndexType indexType )
    {
        final RepositoryId repositoryId = params.getRepositoryId();
        final IndexMapping mergedMapping = mergeWithDefaultMapping( params, indexType );

        this.indexServiceInternal.applyMapping( ApplyMappingRequest.create().
            indexName( resolveIndexName( repositoryId, indexType ) ).
            indexType( indexType ).
            mapping( mergedMapping ).
            build() );
    }

    private IndexMapping mergeWithDefaultMapping( final CreateRepositoryParams params, final IndexType indexType )
    {
        final IndexMapping defaultMapping = DEFAULT_INDEX_RESOURCE_PROVIDER.getMapping( params.getRepositoryId(), indexType );

        final IndexMapping indexMappings = params.getRepositorySettings().getIndexMappings( indexType );
        if ( indexMappings != null )
        {
            return new IndexMapping( JsonHelper.merge( defaultMapping.getNode(), indexMappings.getNode() ) );
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
