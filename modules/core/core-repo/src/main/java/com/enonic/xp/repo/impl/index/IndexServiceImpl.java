package com.enonic.xp.repo.impl.index;

import java.time.Duration;
import java.time.Instant;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentConstants;
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
import com.enonic.xp.repo.impl.branch.search.NodeBranchQuery;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQueryResult;
import com.enonic.xp.repo.impl.branch.storage.BranchIndexPath;
import com.enonic.xp.repo.impl.branch.storage.NodeFactory;
import com.enonic.xp.repo.impl.node.dao.NodeVersionService;
import com.enonic.xp.repo.impl.repository.DefaultIndexResourceProvider;
import com.enonic.xp.repo.impl.repository.IndexNameResolver;
import com.enonic.xp.repo.impl.repository.IndexResourceProvider;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.storage.IndexDataService;
import com.enonic.xp.repository.IndexMapping;
import com.enonic.xp.repository.IndexSettings;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.SystemConstants;

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

    private final static Logger LOG = LoggerFactory.getLogger( IndexServiceImpl.class );

    private final static String INDEX_RESOURCE_BASE_FOLDER = "/com/enonic/xp/repo/impl/repository/index";

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

            final NodeBranchQueryResult results = this.nodeSearchService.query( NodeBranchQuery.create().
                query( QueryExpr.from( compareExpr ) ).
                batchSize( BATCH_SIZE ).
                size( NodeSearchService.GET_ALL_SIZE_FLAG ).
                build(), InternalContext.from( reindexContext ) );

            long nodeIndex = 1;
            final long total = results.getSize();
            final long logStep = total < 10 ? 1 : total < 100 ? 10 : total < 1000 ? 100 : 1000;

            LOG.info( "Starting reindexing '" + branch + "' branch in '" + params.getRepositoryId() + "' repository: " + total +
                          " items to process" );

            for ( final NodeBranchEntry nodeBranchEntry : results )
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

        final String indexName = params.getIndexName();
        final UpdateIndexSettings updateIndexSettings = UpdateIndexSettings.from( params.getSettings() );

        if ( indexName != null )
        {
            updateIndexSettings( indexName, updateIndexSettings, result );
        }
        else
        {
            updateIndexSettings( ContentConstants.CONTENT_REPO.getId(), updateIndexSettings, result );
            updateIndexSettings( SystemConstants.SYSTEM_REPO.getId(), updateIndexSettings, result );
        }

        return result.build();
    }

    private void updateIndexSettings( final RepositoryId repositoryId, final UpdateIndexSettings updateIndexSettings,
                                      final UpdateIndexSettingsResult.Builder result )
    {
        final String searchIndexName = IndexNameResolver.resolveSearchIndexName( repositoryId );
        final String storageIndexName = IndexNameResolver.resolveStorageIndexName( repositoryId );
        updateIndexSettings( searchIndexName, updateIndexSettings, result );
        updateIndexSettings( storageIndexName, updateIndexSettings, result );
    }

    private void updateIndexSettings( final String indexName, final UpdateIndexSettings updateIndexSettings,
                                      final UpdateIndexSettingsResult.Builder result )
    {
        indexServiceInternal.updateIndex( indexName, updateIndexSettings );
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

        final IndexResourceProvider indexResourceProvider = new DefaultIndexResourceProvider( INDEX_RESOURCE_BASE_FOLDER );

        final IndexSettings indexSettings = indexResourceProvider.getSettings( repositoryId, IndexType.SEARCH );
        indexServiceInternal.createIndex( CreateIndexRequest.create().
            indexName( searchIndexName ).
            indexSettings( indexSettings ).
            build() );

        indexServiceInternal.getClusterHealth( CLUSTER_HEALTH_TIMEOUT_VALUE );

        final IndexMapping indexMapping = indexResourceProvider.getMapping( repositoryId, IndexType.SEARCH );
        indexServiceInternal.applyMapping( ApplyMappingRequest.create().
            indexName( searchIndexName ).
            indexType( IndexType.SEARCH ).
            mapping( indexMapping ).
            build() );

        indexServiceInternal.getClusterHealth( CLUSTER_HEALTH_TIMEOUT_VALUE );
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
}
