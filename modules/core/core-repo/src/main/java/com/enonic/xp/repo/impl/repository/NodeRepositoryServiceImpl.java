package com.enonic.xp.repo.impl.repository;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.index.IndexType;
import com.enonic.xp.repo.impl.index.ApplyMappingRequest;
import com.enonic.xp.repo.impl.index.CreateIndexRequest;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.IndexMapping;
import com.enonic.xp.repository.IndexSettings;
import com.enonic.xp.repository.NodeRepositoryService;
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
        createIndexes( params );
        applyMappings( params );
    }

    @Override
    public void delete( final RepositoryId repositoryId )
    {
        indexServiceInternal.deleteIndices( IndexNameResolver.resolveIndexNames( repositoryId ).toArray( String[]::new ) );
    }

    @Override
    public boolean isInitialized( final RepositoryId repositoryId )
    {
        return indexServiceInternal.indicesExists( IndexNameResolver.resolveIndexNames( repositoryId ).toArray( String[]::new ) );
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
                indexServiceInternal.getIndexSettings( SystemConstants.SYSTEM_REPO_ID, IndexType.VERSION ).getNode().
                    get( "index.number_of_replicas" ).
                    textValue();
            final int numberOfReplicas = Integer.parseInt( numberOfReplicasString );
            final ObjectNode indexNodeObject = (ObjectNode) defaultSettings.getNode().get( "index" );
            indexNodeObject.put( "number_of_replicas", numberOfReplicas );
        }
        catch ( Exception e )
        {
            LOG.warn(
                "Failed to retrieve number of replicas from [" + resolveIndexName( SystemConstants.SYSTEM_REPO_ID, IndexType.VERSION ) +
                    "]" );
        }

        return defaultSettings;
    }

    private void applyMappings( final CreateRepositoryParams params )
    {
        applyMapping( params, IndexType.SEARCH );
        applyMapping( params, IndexType.BRANCH );
        applyMapping( params, IndexType.VERSION );
        applyMapping( params, IndexType.COMMIT );
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
            case COMMIT:
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
