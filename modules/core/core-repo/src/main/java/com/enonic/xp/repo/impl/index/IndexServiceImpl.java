package com.enonic.xp.repo.impl.index;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
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
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositorySettings;

@Component
public class IndexServiceImpl
    implements IndexService
{
    private static final Logger LOG = LoggerFactory.getLogger( IndexServiceImpl.class );

    private static final String DEFAULT_INDEX_RESOURCE_FOLDER = "/com/enonic/xp/repo/impl/repository/index";

    private static final IndexResourceProvider DEFAULT_INDEX_RESOURCE_PROVIDER =
        new DefaultIndexResourceProvider( DEFAULT_INDEX_RESOURCE_FOLDER );

    private IndexServiceInternal indexServiceInternal;

    private final IndexDataService indexDataService;

    private final NodeSearchService nodeSearchService;

    private final NodeVersionService nodeVersionService;

    private final RepositoryEntryService repositoryEntryService;

    @Activate
    public IndexServiceImpl( @Reference final IndexServiceInternal indexServiceInternal, @Reference final IndexDataService indexDataService,
                             @Reference final NodeSearchService nodeSearchService, @Reference final NodeVersionService nodeVersionService,
                             @Reference final RepositoryEntryService repositoryEntryService )
    {
        this.indexServiceInternal = indexServiceInternal;
        this.indexDataService = indexDataService;
        this.nodeSearchService = nodeSearchService;
        this.nodeVersionService = nodeVersionService;
        this.repositoryEntryService = repositoryEntryService;
    }

    @Override
    public ReindexResult reindex( final ReindexParams params )
    {
        if ( params.isInitialize() )
        {
            doPurgeSearchIndex( params.getRepositoryId() );
        }

        return ReindexExecutor.create()
            .branches( params.getBranches() )
            .repositoryId( params.getRepositoryId() )
            .indexDataService( this.indexDataService )
            .nodeSearchService( this.nodeSearchService )
            .nodeVersionService( this.nodeVersionService )
            .listener( params.getListener() )
            .build()
            .execute();
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
        return IndexSettings.from( this.indexServiceInternal.getIndexSettings( repositoryId, indexType ) );
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
        doPurgeSearchIndex( params.getRepositoryId() );
    }

    private void doPurgeSearchIndex( final RepositoryId repositoryId )
    {
        final String searchIndexName = IndexNameResolver.resolveSearchIndexName( repositoryId );

        final RepositorySettings repositorySettings = repositoryEntryService.getRepositoryEntry( repositoryId ).getSettings();

        indexServiceInternal.deleteIndices( searchIndexName );

        final IndexSettings indexSettings = IndexSettingsMerger.merge( DEFAULT_INDEX_RESOURCE_PROVIDER.getSettings( IndexType.SEARCH ),
                                                                       repositorySettings.getIndexSettings( IndexType.SEARCH ) );
        final IndexMapping indexMapping = IndexSettingsMerger.merge( DEFAULT_INDEX_RESOURCE_PROVIDER.getMapping( IndexType.SEARCH ),
                                                                     repositorySettings.getIndexMappings( IndexType.SEARCH ) );

        indexServiceInternal.createIndex( CreateIndexRequest.create()
                                              .indexName( searchIndexName )
                                              .indexSettings( indexSettings )
                                              .mappings( Map.of( IndexType.SEARCH, indexMapping ) )
                                              .build() );

        indexServiceInternal.waitForYellowStatus( searchIndexName );
    }

    public void setIndexServiceInternal( final IndexServiceInternal indexServiceInternal )
    {
        this.indexServiceInternal = indexServiceInternal;
    }
}
