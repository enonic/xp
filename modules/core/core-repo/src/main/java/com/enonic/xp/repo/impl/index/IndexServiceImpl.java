package com.enonic.xp.repo.impl.index;

import java.time.Duration;
import java.time.Instant;

import org.elasticsearch.common.unit.TimeValue;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.index.IndexType;
import com.enonic.xp.index.PurgeIndexParams;
import com.enonic.xp.index.ReindexParams;
import com.enonic.xp.index.ReindexResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.repo.impl.branch.BranchContext;
import com.enonic.xp.repo.impl.branch.BranchService;
import com.enonic.xp.repo.impl.elasticsearch.branch.NodeBranchQuery;
import com.enonic.xp.repo.impl.elasticsearch.branch.NodeBranchQueryResult;
import com.enonic.xp.repo.impl.elasticsearch.branch.NodeBranchQueryResultEntry;
import com.enonic.xp.repo.impl.entity.dao.NodeDao;
import com.enonic.xp.repo.impl.index.query.QueryService;
import com.enonic.xp.repo.impl.repository.IndexNameResolver;
import com.enonic.xp.repo.impl.repository.RepositoryIndexMappingProvider;
import com.enonic.xp.repo.impl.repository.RepositorySearchIndexSettingsProvider;
import com.enonic.xp.repository.RepositoryId;

@Component
public class IndexServiceImpl
    implements IndexService
{
    private final static TimeValue CLUSTER_HEALTH_TIMEOUT_VALUE = TimeValue.timeValueSeconds( 10 );

    private IndexServiceInternal indexServiceInternal;

    private BranchService branchService;

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
            final NodeBranchQueryResult results = this.branchService.findAll( NodeBranchQuery.create().
                from( 0 ).
                size( QueryService.GET_ALL_SIZE_FLAG ).
                build(), BranchContext.from( branch, params.getRepositoryId() ) );

            for ( final NodeBranchQueryResultEntry result : results )
            {
                final Node node = this.nodeDao.getByVersionId( result.getNodeVersionId() );

                this.indexServiceInternal.store( node, result.getNodeVersionId(), IndexContext.create().
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
    public void setBranchService( final BranchService branchService )
    {
        this.branchService = branchService;
    }

    @Reference
    public void setNodeDao( final NodeDao nodeDao )
    {
        this.nodeDao = nodeDao;
    }
}
