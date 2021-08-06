package com.enonic.xp.repo.impl.repository;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.index.IndexType;
import com.enonic.xp.repo.impl.index.CreateIndexRequest;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.IndexMapping;
import com.enonic.xp.repository.IndexSettings;
import com.enonic.xp.repository.NodeRepositoryService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositorySettings;
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
        final RepositoryId repositoryId = params.getRepositoryId();

        final RepositorySettings repositorySettings = params.getRepositorySettings();
        createIndex( params, IndexType.VERSION,
                     Map.ofEntries( mergeWithDefaultMapping( repositorySettings, repositoryId, IndexType.VERSION ),
                                    mergeWithDefaultMapping( repositorySettings, repositoryId, IndexType.BRANCH ),
                                    mergeWithDefaultMapping( repositorySettings, repositoryId, IndexType.COMMIT ) ) );

        createIndex( params, IndexType.SEARCH,
                     Map.ofEntries( mergeWithDefaultMapping( repositorySettings, repositoryId, IndexType.SEARCH ) ) );

        indexServiceInternal.waitForYellowStatus( resolveIndexNames( repositoryId ) );
    }

    @Override
    public void delete( final RepositoryId repositoryId )
    {
        indexServiceInternal.deleteIndices( resolveIndexNames( repositoryId ) );
    }

    @Override
    public boolean isInitialized( final RepositoryId repositoryId )
    {
        return indexServiceInternal.indicesExists( resolveIndexNames( repositoryId ) );
    }

    private void createIndex( final CreateRepositoryParams params, final IndexType indexType, final Map<IndexType, IndexMapping> mappings )
    {
        final RepositoryId repositoryId = params.getRepositoryId();
        final IndexSettings mergedSettings = mergeWithDefaultSettings( params, indexType );

        indexServiceInternal.createIndex( CreateIndexRequest.create()
                                              .indexName( resolveIndexName( repositoryId, indexType ) )
                                              .mappings( mappings )
                                              .indexSettings( mergedSettings )
                                              .build() );
    }


    private IndexSettings mergeWithDefaultSettings( final CreateRepositoryParams params, final IndexType indexType )
    {
        final IndexSettings defaultSettings = getDefaultSettings( params.getRepositoryId(), indexType );

        final IndexSettings indexSettings = params.getRepositorySettings().getIndexSettings( indexType );
        if ( indexSettings != null )
        {
            return new IndexSettings( JsonHelper.merge( defaultSettings.getNode(), indexSettings.getNode() ) );
        }

        return defaultSettings;
    }

    private IndexSettings getDefaultSettings( final RepositoryId repositoryId, final IndexType indexType )
    {
        final IndexSettings defaultSettings = DEFAULT_INDEX_RESOURCE_PROVIDER.getSettings( repositoryId, indexType );
        if ( SystemConstants.SYSTEM_REPO_ID.equals( repositoryId ) )
        {
            return defaultSettings;
        }

        try
        {
            final String numberOfReplicasString = indexServiceInternal.getIndexSettings( SystemConstants.SYSTEM_REPO_ID, IndexType.VERSION )
                .getNode()
                .get( "index.number_of_replicas" )
                .textValue();
            final int numberOfReplicas = Integer.parseInt( numberOfReplicasString );
            final ObjectNode indexNodeObject = (ObjectNode) defaultSettings.getNode().get( "index" );
            indexNodeObject.put( "number_of_replicas", numberOfReplicas );
        }
        catch ( Exception e )
        {
            LOG.warn( "Failed to retrieve number of replicas from [" +
                          IndexNameResolver.resolveStorageIndexName( SystemConstants.SYSTEM_REPO_ID ) + "]" );
        }

        return defaultSettings;
    }

    private static Map.Entry<IndexType, IndexMapping> mergeWithDefaultMapping( final RepositorySettings repositorySettings,
                                                                               final RepositoryId repositoryId, final IndexType indexType )
    {
        return Map.entry( indexType, mergeMappings( DEFAULT_INDEX_RESOURCE_PROVIDER.getMapping( repositoryId, indexType ),
                                                    repositorySettings.getIndexMappings( indexType ) ) );
    }

    private static IndexMapping mergeMappings( IndexMapping... indexMappings )
    {
        JsonNode intermediate = JsonHelper.from( Map.of() );

        Arrays.stream( indexMappings )
            .filter( Objects::nonNull )
            .forEach( indexMapping -> JsonHelper.merge( intermediate, indexMapping.getNode() ) );
        return new IndexMapping( intermediate );
    }

    private static String[] resolveIndexNames( final RepositoryId repositoryId )
    {
        return IndexNameResolver.resolveIndexNames( repositoryId ).toArray( String[]::new );
    }

    private static String resolveIndexName( final RepositoryId repositoryId, final IndexType indexType )
    {
        switch ( indexType )
        {
            case SEARCH:
                return IndexNameResolver.resolveSearchIndexName( repositoryId );
            case VERSION:
            case BRANCH:
            case COMMIT:
                return IndexNameResolver.resolveStorageIndexName( repositoryId );
            default:
                throw new IllegalArgumentException( "Cannot resolve index name for indexType [" + indexType.getName() + "]" );
        }
    }

    @Reference
    public void setIndexServiceInternal( final IndexServiceInternal indexServiceInternal )
    {
        this.indexServiceInternal = indexServiceInternal;
    }
}
