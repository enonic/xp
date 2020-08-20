package com.enonic.xp.repo.impl.repository;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.index.IndexType;
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

    private static final Logger LOG = LoggerFactory.getLogger( NodeRepositoryServiceImpl.class );

    private static final String DEFAULT_INDEX_RESOURCE_FOLDER = "/com/enonic/xp/repo/impl/repository/index";

    private static final IndexResourceProvider DEFAULT_INDEX_RESOURCE_PROVIDER =
        new DefaultIndexResourceProvider( DEFAULT_INDEX_RESOURCE_FOLDER );

    @Override
    public void create( final CreateRepositoryParams params )
    {
        doCreateIndex( params, IndexType.STORAGE );
        doCreateIndex( params, IndexType.COMMIT );
    }

    @Override
    public void delete( final RepositoryId repositoryId )
    {
        delete( repositoryId, IndexType.SEARCH );
        delete( repositoryId, IndexType.STORAGE );
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
            indexServiceInternal.deleteIndices( resolveIndexName( repositoryId, indexType ) );
        }
    }

    @Override
    public boolean isInitialized( final RepositoryId repositoryId )
    {
        if ( !indexServiceInternal.waitForYellowStatus() )
        {
            throw new RepositoryException( "Unable to initialize repositories" );
        }

        final String storageIndexName = IndexNameResolver.resolveStorageIndexName( repositoryId );
        final String commitIndexName = IndexNameResolver.resolveCommitIndexName( repositoryId );
        final String masterSearchIndexName = IndexNameResolver.resolveSearchIndexName( repositoryId, RepositoryConstants.MASTER_BRANCH );

        return indexServiceInternal.indicesExists( storageIndexName, commitIndexName, masterSearchIndexName );
    }

    private void doCreateIndex( final CreateRepositoryParams params, final IndexType indexType )
    {
        final RepositoryId repositoryId = params.getRepositoryId();
        final IndexSettings mergedSettings = mergeWithDefaultSettings( params, indexType );

        final IndexMapping mergedMapping = mergeWithDefaultMapping( params, indexType );

        indexServiceInternal.createIndex( CreateIndexRequest.create().
            indexName( resolveIndexName( repositoryId, indexType ) ).
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
        if ( SystemConstants.SYSTEM_REPO_ID.equals( params.getRepositoryId() ) )
        {
            return defaultSettings;
        }

        try
        {
            final String numberOfReplicasString =
                indexServiceInternal.getIndexSettings( SystemConstants.SYSTEM_REPO_ID, IndexType.STORAGE ).getNode().
                    get( "index.number_of_replicas" ).
                    textValue();
            final int numberOfReplicas = Integer.parseInt( numberOfReplicasString );
            final ObjectNode indexNodeObject = (ObjectNode) defaultSettings.getNode().get( "index" );
            indexNodeObject.put( "number_of_replicas", numberOfReplicas );
        }
        catch ( Exception e )
        {
            LOG.warn(
                "Failed to retrieve number of replicas from [" + resolveIndexName( SystemConstants.SYSTEM_REPO_ID, indexType ) + "]" );
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

    private String resolveIndexName( final RepositoryId repositoryId, final IndexType indexType )
    {
        final String indexName = IndexNameResolver.resolveIndexName( repositoryId, indexType );

        if ( indexName != null )
        {
            return indexName;
        }

        throw new IllegalArgumentException( "Cannot resolve index name." );
    }

    @Reference
    public void setIndexServiceInternal( final IndexServiceInternal indexServiceInternal )
    {
        this.indexServiceInternal = indexServiceInternal;
    }
}
