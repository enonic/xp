package com.enonic.xp.repo.impl.repository;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.index.IndexType;
import com.enonic.xp.repo.impl.elasticsearch.ClusterHealthStatus;
import com.enonic.xp.repo.impl.elasticsearch.ClusterStatusCode;
import com.enonic.xp.repo.impl.index.CreateIndexRequest;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.IndexMapping;
import com.enonic.xp.repository.IndexSettings;
import com.enonic.xp.repository.NodeRepositoryService;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.SystemConstants;
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
        doCreateIndex( params, IndexType.VERSION );
        doCreateIndex( params, IndexType.BRANCH );
        doCreateIndex( params, IndexType.COMMIT );

        checkClusterHealth();
    }

    @Override
    public void delete( final RepositoryId repositoryId )
    {
        delete( repositoryId, IndexType.SEARCH );
        delete( repositoryId, IndexType.VERSION );
        delete( repositoryId, IndexType.BRANCH );
        delete( repositoryId, IndexType.COMMIT );
    }

    private void delete( final RepositoryId repositoryId, final IndexType indexType )
    {
        if ( IndexType.SEARCH == indexType )
        {
            indexServiceInternal.deleteIndices( IndexNameResolver.resolveSearchIndexPrefix( repositoryId ) );
        }
        else
        {
            indexServiceInternal.deleteIndices( resolveStorageIndexName( repositoryId, indexType ) );
        }
    }

    @Override
    public boolean isInitialized( final RepositoryId repositoryId )
    {
        if ( !checkClusterHealth() )
        {
            throw new RepositoryException( "Unable to initialize repositories" );
        }

        final String versionIndexName = IndexNameResolver.resolveVersionIndexName( repositoryId );
        final String branchIndexName = IndexNameResolver.resolveBranchIndexName( repositoryId );
        final String commitIndexName = IndexNameResolver.resolveCommitIndexName( repositoryId );
        final String masterSearchIndexName = IndexNameResolver.resolveSearchIndexName( repositoryId, RepositoryConstants.MASTER_BRANCH );

        return indexServiceInternal.indicesExists( versionIndexName, branchIndexName, commitIndexName, masterSearchIndexName );
    }

    private void doCreateIndex( final CreateRepositoryParams params, final IndexType indexType )
    {
        final RepositoryId repositoryId = params.getRepositoryId();
        final IndexSettings mergedSettings = mergeWithDefaultSettings( params, indexType );

        final IndexMapping mergedMapping = mergeWithDefaultMapping( params, indexType );

        indexServiceInternal.createIndex( CreateIndexRequest.create().
            indexName( resolveStorageIndexName( repositoryId, indexType ) ).
            indexSettings( mergedSettings ).
            mapping( mergedMapping ).
            build() );
    }


    private IndexSettings mergeWithDefaultSettings( final CreateRepositoryParams params, final IndexType indexType )
    {
        final IndexSettings defaultSettings = getDefaultSettings( params, indexType );

        final IndexSettings indexSettings = params.getRepositorySettings().getIndexSettings( indexType );
        if ( indexSettings != null )
        {
            return new IndexSettings( JsonHelper.merge( defaultSettings.getNode(), indexSettings.getNode() ) );
        }

        return defaultSettings;
    }

    private IndexSettings getDefaultSettings( final CreateRepositoryParams params, final IndexType indexType )
    {
        final IndexSettings defaultSettings = DEFAULT_INDEX_RESOURCE_PROVIDER.getSettings( params.getRepositoryId(), indexType );
        if ( SystemConstants.SYSTEM_REPO.getId().equals( params.getRepositoryId() ) )
        {
            return defaultSettings;
        }

        try
        {
            final String numberOfReplicasString =
                indexServiceInternal.getIndexSettings( SystemConstants.SYSTEM_REPO.getId(), IndexType.VERSION ).getNode().
                    get( "index.number_of_replicas" ).
                    textValue();
            final int numberOfReplicas = Integer.parseInt( numberOfReplicasString );
            final ObjectNode indexNodeObject = (ObjectNode) defaultSettings.getNode().get( "index" );
            indexNodeObject.put( "number_of_replicas", numberOfReplicas );
        }
        catch ( Exception e )
        {
            LOG.warn( "Failed to retrieve number of replicas from [" +
                          resolveStorageIndexName( SystemConstants.SYSTEM_REPO.getId(), IndexType.VERSION ) + "]" );
        }

        return defaultSettings;
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

    private String resolveStorageIndexName( final RepositoryId repositoryId, final IndexType indexType )
    {
        final String indexName = IndexNameResolver.resolveStorageIndexName( repositoryId, indexType );

        if ( indexName != null )
        {
            return indexName;
        }

        throw new IllegalArgumentException( indexType != null
                                                ? ( "Cannot resolve index name for indexType [" + indexType.getName() + "]" )
                                                : "Cannot resolve index name for empty index type." );
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
