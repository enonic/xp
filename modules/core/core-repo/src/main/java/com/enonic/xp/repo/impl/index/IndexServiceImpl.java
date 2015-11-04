package com.enonic.xp.repo.impl.index;

import java.time.Duration;
import java.time.Instant;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.index.IndexType;
import com.enonic.xp.index.PurgeIndexParams;
import com.enonic.xp.index.ReindexParams;
import com.enonic.xp.index.ReindexResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQuery;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQueryResult;
import com.enonic.xp.repo.impl.branch.storage.BranchIndexPath;
import com.enonic.xp.repo.impl.branch.storage.NodeBranchMetadata;
import com.enonic.xp.repo.impl.branch.storage.NodeFactory;
import com.enonic.xp.repo.impl.node.dao.NodeVersionDao;
import com.enonic.xp.repo.impl.repository.IndexNameResolver;
import com.enonic.xp.repo.impl.repository.RepositoryIndexMappingProvider;
import com.enonic.xp.repo.impl.repository.RepositorySearchIndexSettingsProvider;
import com.enonic.xp.repo.impl.search.SearchService;
import com.enonic.xp.repository.RepositoryId;

@Component
public class IndexServiceImpl
    implements IndexService
{
    private final static String CLUSTER_HEALTH_TIMEOUT_VALUE = "10s";

    private IndexServiceInternal indexServiceInternal;

    private SearchService searchService;

    private NodeVersionDao nodeVersionDao;

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

            for ( final NodeBranchMetadata nodeBranchMetadata : results )
            {
                final NodeVersion nodeVersion = this.nodeVersionDao.get( nodeBranchMetadata.getVersionId() );

                final Node node = NodeFactory.create( nodeVersion, nodeBranchMetadata );

                this.indexServiceInternal.store( node, InternalContext.create( ContextAccessor.current() ).
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
    public void setNodeVersionDao( final NodeVersionDao nodeVersionDao )
    {
        this.nodeVersionDao = nodeVersionDao;
    }
}
