package com.enonic.wem.repo.internal.index;

import java.time.Duration;
import java.time.Instant;

import org.elasticsearch.common.unit.TimeValue;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.branch.search.NodeBranchQuery;
import com.enonic.wem.repo.internal.branch.search.NodeBranchQueryResult;
import com.enonic.wem.repo.internal.branch.search.NodeBranchQueryResultEntry;
import com.enonic.wem.repo.internal.branch.storage.BranchIndexPath;
import com.enonic.wem.repo.internal.entity.dao.NodeDao;
import com.enonic.wem.repo.internal.repository.IndexNameResolver;
import com.enonic.wem.repo.internal.repository.RepositoryIndexMappingProvider;
import com.enonic.wem.repo.internal.repository.RepositorySearchIndexSettingsProvider;
import com.enonic.wem.repo.internal.search.SearchService;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.index.IndexType;
import com.enonic.xp.index.PurgeIndexParams;
import com.enonic.xp.index.ReindexParams;
import com.enonic.xp.index.ReindexResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repository.RepositoryId;

@Component
public class IndexServiceImpl
    implements IndexService
{
    private final static TimeValue CLUSTER_HEALTH_TIMEOUT_VALUE = TimeValue.timeValueSeconds( 10 );

    private IndexServiceInternal indexServiceInternal;

    private SearchService searchService;

    private NodeDao nodeDao;

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
                                    ValueExpr.string( branch.getName() ) );

            final NodeBranchQueryResult results = this.searchService.search( NodeBranchQuery.create().
                query( QueryExpr.from( compareExpr ) ).
                size( SearchService.GET_ALL_SIZE_FLAG ).
                build(), InternalContext.create( ContextAccessor.current() ).
                branch( branch ).
                build() );

            for ( final NodeBranchQueryResultEntry result : results )
            {
                final Node node = this.nodeDao.get( result.getNodeVersionId() );

                this.indexServiceInternal.store( node, result.getNodeVersionId(), InternalContext.create( ContextAccessor.current() ).
                    repositoryId( params.getRepositoryId() ).
                    branch( branch ).
                    build() );

                builder.add( node.id() );
            }
        }

        final long stop = System.currentTimeMillis();
        builder.endTime( Instant.ofEpochMilli( stop ) );
        builder.duration( Duration.ofMillis( start - stop ) );

        return builder.build();
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

        final IndexSettings searchIndexSettings = RepositorySearchIndexSettingsProvider.getSettings( repositoryId );

        indexServiceInternal.createIndex( searchIndexName, searchIndexSettings );

        indexServiceInternal.getClusterHealth( CLUSTER_HEALTH_TIMEOUT_VALUE );

        indexServiceInternal.applyMapping( searchIndexName, IndexType.SEARCH,
                                           RepositoryIndexMappingProvider.getSearchMappings( repositoryId ) );

        indexServiceInternal.getClusterHealth( CLUSTER_HEALTH_TIMEOUT_VALUE );
    }

    @Reference
    public void setIndexServiceInternal( final IndexServiceInternal indexServiceInternal )
    {
        this.indexServiceInternal = indexServiceInternal;
    }

    @Reference
    public void setSearchService( final SearchService searchService )
    {
        this.searchService = searchService;
    }

    @Reference
    public void setNodeDao( final NodeDao nodeDao )
    {
        this.nodeDao = nodeDao;
    }
}
