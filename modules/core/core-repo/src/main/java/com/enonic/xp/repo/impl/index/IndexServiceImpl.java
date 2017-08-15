package com.enonic.xp.repo.impl.index;

import java.time.Duration;
import java.time.Instant;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.index.IndexType;
import com.enonic.xp.index.PurgeIndexParams;
import com.enonic.xp.index.ReindexParams;
import com.enonic.xp.index.ReindexResult;
import com.enonic.xp.index.UpdateIndexSettingsParams;
import com.enonic.xp.index.UpdateIndexSettingsResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.SingleRepoStorageSource;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQuery;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQueryResult;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQueryResultFactory;
import com.enonic.xp.repo.impl.branch.storage.BranchIndexPath;
import com.enonic.xp.repo.impl.branch.storage.NodeFactory;
import com.enonic.xp.repo.impl.node.dao.NodeVersionService;
import com.enonic.xp.repo.impl.repository.DefaultIndexResourceProvider;
import com.enonic.xp.repo.impl.repository.IndexNameResolver;
import com.enonic.xp.repo.impl.repository.IndexResourceProvider;
import com.enonic.xp.repo.impl.repository.RepositoryEntryService;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.search.result.SearchResult;
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
    private final static String CLUSTER_HEALTH_TIMEOUT_VALUE = "10s";

    private final static int BATCH_SIZE = 10_000;

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
        final ReindexResult.Builder builder = ReindexResult.create();

        final long start = System.currentTimeMillis();
        builder.startTime( Instant.ofEpochMilli( start ) );
        builder.branches( params.getBranches() );
        builder.repositoryId( params.getRepositoryId() );

        if ( params.isInitialize() )
        {
            doInitializeSearchIndex( params.getRepositoryId() );
        }

        for ( final Branch branch : params.getBranches() )
        {
            final CompareExpr compareExpr =
                CompareExpr.create( FieldExpr.from( BranchIndexPath.BRANCH_NAME.getPath() ), CompareExpr.Operator.EQ,
                                    ValueExpr.string( branch.getValue() ) );

            final Context reindexContext = ContextBuilder.from( ContextAccessor.current() ).
                repositoryId( params.getRepositoryId() ).
                branch( branch ).
                build();

            final SearchResult searchResult = this.nodeSearchService.query( NodeBranchQuery.create().
                query( QueryExpr.from( compareExpr ) ).
                batchSize( BATCH_SIZE ).
                size( NodeSearchService.GET_ALL_SIZE_FLAG ).
                build(), SingleRepoStorageSource.create( reindexContext.getRepositoryId(), SingleRepoStorageSource.Type.BRANCH ) );

            final NodeBranchQueryResult result = NodeBranchQueryResultFactory.create( searchResult );

            long nodeIndex = 1;
            final long total = result.getSize();
            final long logStep = total < 10 ? 1 : total < 100 ? 10 : total < 1000 ? 100 : 1000;

            LOG.info( "Starting reindexing '" + branch + "' branch in '" + params.getRepositoryId() + "' repository: " + total +
                          " items to process" );

            for ( final NodeBranchEntry nodeBranchEntry : result )
            {
                if ( nodeIndex % logStep == 0 )
                {
                    LOG.info( "Reindexing '" + branch + "' in '" + params.getRepositoryId() + "'" + ": processed " + nodeIndex + " of " +
                                  total +
                                  "..." );
                }

                final NodeVersion nodeVersion = this.nodeVersionService.get( nodeBranchEntry.getVersionId() );

                final Node node = NodeFactory.create( nodeVersion, nodeBranchEntry );

                this.indexDataService.store( node, InternalContext.create( ContextAccessor.current() ).
                    repositoryId( params.getRepositoryId() ).
                    branch( branch ).
                    build() );

                builder.add( node.id() );

                nodeIndex++;
            }

            LOG.info( "Finished reindexing '" + branch + "' branch in '" + params.getRepositoryId() + "' repository: " + total +
                          " items reindexed" );
        }

        final long stop = System.currentTimeMillis();
        builder.endTime( Instant.ofEpochMilli( stop ) );
        builder.duration( Duration.ofMillis( start - stop ) );

        return builder.build();
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
    public boolean isMaster()
    {

        return indexServiceInternal.isMaster();
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
        indexServiceInternal.getClusterHealth( CLUSTER_HEALTH_TIMEOUT_VALUE );

        final IndexSettings indexSettings = getSearchIndexSettings( repositoryId );
        indexServiceInternal.createIndex( CreateIndexRequest.create().
            indexName( searchIndexName ).
            indexSettings( indexSettings ).
            build() );

        indexServiceInternal.getClusterHealth( CLUSTER_HEALTH_TIMEOUT_VALUE );

        final IndexMapping indexMapping = getSearchIndexMapping( repositoryId );
        indexServiceInternal.applyMapping( ApplyMappingRequest.create().
            indexName( searchIndexName ).
            indexType( IndexType.SEARCH ).
            mapping( indexMapping ).
            build() );

        indexServiceInternal.getClusterHealth( CLUSTER_HEALTH_TIMEOUT_VALUE );
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
