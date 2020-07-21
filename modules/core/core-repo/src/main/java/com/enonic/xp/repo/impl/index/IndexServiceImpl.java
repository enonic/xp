package com.enonic.xp.repo.impl.index;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.index.IndexType;
import com.enonic.xp.index.PurgeIndexParams;
import com.enonic.xp.index.ReindexParams;
import com.enonic.xp.index.ReindexResult;
import com.enonic.xp.index.UpdateIndexSettingsParams;
import com.enonic.xp.index.UpdateIndexSettingsResult;
import com.enonic.xp.repo.impl.node.dao.NodeVersionService;
import com.enonic.xp.repo.impl.repository.DefaultIndexResourceProvider;
import com.enonic.xp.repo.impl.repository.IndexNameResolver;
import com.enonic.xp.repo.impl.repository.IndexResourceProvider;
import com.enonic.xp.repo.impl.repository.RepositoryEntryService;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.storage.IndexDataService;
import com.enonic.xp.repository.IndexMapping;
import com.enonic.xp.repository.IndexSettings;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.util.JsonHelper;

@Component
public class IndexServiceImpl
    implements IndexService
{
    private IndexServiceInternal indexServiceInternal;

    private IndexDataService indexDataService;

    private NodeSearchService nodeSearchService;

    private NodeVersionService nodeVersionService;

    private RepositoryEntryService repositoryEntryService;

    private final static Logger LOG = LoggerFactory.getLogger( IndexServiceImpl.class );

    private final static String DEFAULT_INDEX_RESOURCE_FOLDER = "/com/enonic/xp/repo/impl/repository/index";

    private final static IndexResourceProvider DEFAULT_INDEX_RESOURCE_PROVIDER =
        new DefaultIndexResourceProvider( DEFAULT_INDEX_RESOURCE_FOLDER );

    @Override
    public ReindexResult reindex( final ReindexParams params )
    {
        if ( params.isInitialize() )
        {
            doInitializeSearchIndex( params.getRepositoryId() );
        }

        return ReindexExecutor.create().
            branches( params.getBranches() ).
            repositoryId( params.getRepositoryId() ).
            indexDataService( this.indexDataService ).
            nodeSearchService( this.nodeSearchService ).
            nodeVersionService( this.nodeVersionService ).
            listener( params.getListener() ).
            build().
            execute();
    }


    @Override
    public UpdateIndexSettingsResult updateIndexSettings( final UpdateIndexSettingsParams params )
    {
        final UpdateIndexSettingsResult.Builder result = UpdateIndexSettingsResult.create();

        final UpdateIndexSettings updateIndexSettings = UpdateIndexSettings.from( params.getSettings() );

        for ( final RepositoryId repositoryId : params.getRepositoryIds() )
        {
            updateIndexSettings( repositoryId, updateIndexSettings, params.isRequireClosedIndex(), result );
        }

        return result.build();
    }

    private void updateIndexSettings( final RepositoryId repositoryId, final UpdateIndexSettings updateIndexSettings,
                                      final boolean closeIndex, final UpdateIndexSettingsResult.Builder result )
    {
        final String searchIndexName = IndexNameResolver.resolveSearchIndexName( repositoryId );
        final String storageIndexName = IndexNameResolver.resolveStorageIndexName( repositoryId );
        updateIndexSettings( searchIndexName, updateIndexSettings, result, closeIndex );
        updateIndexSettings( storageIndexName, updateIndexSettings, result, closeIndex );
    }

    private void updateIndexSettings( final String indexName, final UpdateIndexSettings settings,
                                      final UpdateIndexSettingsResult.Builder result, final boolean closeIndex )
    {
        if ( closeIndex )
        {
            this.indexServiceInternal.closeIndices( indexName );
        }

        try
        {
            indexServiceInternal.updateIndex( indexName, settings );
        }
        finally
        {
            if ( closeIndex )
            {
                indexServiceInternal.openIndices( indexName );
            }
        }

        result.addUpdatedIndex( indexName );
    }

    @Override
    public IndexSettings getIndexSettings( final RepositoryId repositoryId, final IndexType indexType )
    {
        return this.indexServiceInternal.getIndexSettings( repositoryId, indexType );
    }

    @Override
    public Map<String, Object> getIndexMapping( final RepositoryId repositoryId, final Branch branch, final IndexType indexType )
    {
        return this.indexServiceInternal.getIndexMapping( repositoryId, branch, indexType );
    }

    @Override
    public boolean isMaster()
    {

        return indexServiceInternal.isMaster();
    }

    @Override
    public boolean waitForYellowStatus()
    {
        return indexServiceInternal.waitForYellowStatus();
    }

    @Override
    public void purgeSearchIndex( final PurgeIndexParams params )
    {
        doInitializeSearchIndex( params.getRepositoryId() );
    }

    private void doInitializeSearchIndex( final RepositoryId repositoryId )
    {
        final String searchIndexName = IndexNameResolver.resolveSearchIndexName( repositoryId );

        indexServiceInternal.deleteIndices( searchIndexName );

        final IndexSettings indexSettings = getSearchIndexSettings( repositoryId );
        indexServiceInternal.createIndex( CreateIndexRequest.create().
            indexName( searchIndexName ).
            indexSettings( indexSettings ).
            build() );

        final IndexMapping indexMapping = getSearchIndexMapping( repositoryId );
        indexServiceInternal.applyMapping( ApplyMappingRequest.create().
            indexName( searchIndexName ).
            indexType( IndexType.SEARCH ).
            mapping( indexMapping ).
            build() );
    }

    private IndexSettings getSearchIndexSettings( final RepositoryId repositoryId )
    {
        final IndexSettings defaultIndexSettings = DEFAULT_INDEX_RESOURCE_PROVIDER.getSettings( repositoryId, IndexType.SEARCH );

        final Repository repositoryEntry = repositoryEntryService.getRepositoryEntry( repositoryId );
        if ( repositoryEntry != null )
        {
            final IndexSettings indexSettings = repositoryEntry.getSettings().
                getIndexSettings( IndexType.SEARCH );

            if ( indexSettings != null )
            {
                return new IndexSettings( JsonHelper.merge( defaultIndexSettings.getNode(), indexSettings.getNode() ) );
            }
        }

        return defaultIndexSettings;
    }

    private IndexMapping getSearchIndexMapping( final RepositoryId repositoryId )
    {
        final IndexMapping defaultIndexMapping = DEFAULT_INDEX_RESOURCE_PROVIDER.getMapping( repositoryId, IndexType.SEARCH );

        final Repository repositoryEntry = repositoryEntryService.getRepositoryEntry( repositoryId );
        if ( repositoryEntry != null )
        {
            final IndexMapping indexMapping = repositoryEntry.getSettings().
                getIndexMappings( IndexType.SEARCH );

            if ( indexMapping != null )
            {
                return new IndexMapping( JsonHelper.merge( defaultIndexMapping.getNode(), indexMapping.getNode() ) );
            }
        }

        return defaultIndexMapping;
    }

    @Reference
    public void setIndexServiceInternal( final IndexServiceInternal indexServiceInternal )
    {
        this.indexServiceInternal = indexServiceInternal;
    }

    @Reference
    public void setNodeSearchService( final NodeSearchService nodeSearchService )
    {
        this.nodeSearchService = nodeSearchService;
    }

    @Reference
    public void setNodeVersionService( final NodeVersionService nodeVersionService )
    {
        this.nodeVersionService = nodeVersionService;
    }

    @Reference
    public void setIndexDataService( final IndexDataService indexDataService )
    {
        this.indexDataService = indexDataService;
    }

    @Reference
    public void setRepositoryEntryService( final RepositoryEntryService repositoryEntryService )
    {
        this.repositoryEntryService = repositoryEntryService;
    }
}
