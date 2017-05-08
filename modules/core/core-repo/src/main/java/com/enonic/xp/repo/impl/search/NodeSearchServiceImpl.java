package com.enonic.xp.repo.impl.search;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeVersionDiffResult;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.SingleRepoStorageSource;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQuery;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQueryResult;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQueryResultFactory;
import com.enonic.xp.repo.impl.branch.storage.BranchIndexPath;
import com.enonic.xp.repo.impl.index.query.NodeQueryResult;
import com.enonic.xp.repo.impl.index.query.NodeQueryResultFactory;
import com.enonic.xp.repo.impl.search.result.SearchHit;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repo.impl.version.VersionIndexPath;
import com.enonic.xp.repo.impl.version.search.NodeVersionDiffQuery;
import com.enonic.xp.repo.impl.version.search.NodeVersionQuery;
import com.enonic.xp.repo.impl.version.search.NodeVersionQueryResultFactory;

@Component
public class NodeSearchServiceImpl
    implements NodeSearchService
{
    private static final ReturnFields VERSION_RETURN_FIELDS =
        ReturnFields.from( VersionIndexPath.VERSION_ID, VersionIndexPath.TIMESTAMP, VersionIndexPath.NODE_PATH, VersionIndexPath.NODE_ID );

    private static final ReturnFields BRANCH_RETURN_FIELDS =
        ReturnFields.from( BranchIndexPath.NODE_ID, BranchIndexPath.VERSION_ID, BranchIndexPath.STATE, BranchIndexPath.PATH,
                           BranchIndexPath.TIMESTAMP );

    private SearchDao searchDao;

    @Override
    public NodeQueryResult query( final NodeQuery query, final InternalContext context )
    {
        return doQuery( query, ReturnFields.empty(), context );
    }

    @Override
    public NodeQueryResult query( final NodeQuery query, ReturnFields returnFields, final InternalContext context )
    {
        return doQuery( query, returnFields, context );
    }

    private NodeQueryResult doQuery( final NodeQuery query, final ReturnFields returnFields, final InternalContext context )
    {
        final SearchRequest searchRequest = SearchRequest.create().
            searchSource( SingleRepoSearchSource.from( context ) ).
            query( query ).
            returnFields( returnFields ).
            build();

        final SearchResult result = searchDao.search( searchRequest );

        return NodeQueryResultFactory.create( result );
    }

    @Override
    public NodeBranchQueryResult query( final NodeBranchQuery nodeBranchQuery, final InternalContext context )
    {
        final SearchRequest searchRequest = SearchRequest.create().
            searchSource( new SingleRepoStorageSource( context.getRepositoryId(), SingleRepoStorageSource.Type.BRANCH ) ).
            returnFields( BRANCH_RETURN_FIELDS ).
            query( nodeBranchQuery ).
            build();

        final SearchResult result = searchDao.search( searchRequest );

        if ( result.isEmpty() )
        {
            return NodeBranchQueryResult.empty();
        }

        return NodeBranchQueryResultFactory.create( result );
    }

    @Override
    public NodeVersionQueryResult query( final NodeVersionQuery query, final InternalContext context )
    {
        final SearchRequest searchRequest = SearchRequest.create().
            searchSource( new SingleRepoStorageSource( context.getRepositoryId(), SingleRepoStorageSource.Type.VERSION ) ).
            returnFields( VERSION_RETURN_FIELDS ).
            query( query ).
            build();

        final SearchResult result = searchDao.search( searchRequest );

        if ( result.isEmpty() )
        {
            return NodeVersionQueryResult.empty();
        }

        return NodeVersionQueryResultFactory.create( query, result );
    }

    @Override
    public NodeVersionDiffResult query( final NodeVersionDiffQuery query, final InternalContext context )
    {
        final SearchRequest searchRequest = SearchRequest.create().
            searchSource( new SingleRepoStorageSource( context.getRepositoryId(), SingleRepoStorageSource.Type.VERSION ) ).
            returnFields( VERSION_RETURN_FIELDS ).
            query( query ).
            build();

        final SearchResult result = searchDao.search( searchRequest );

        if ( result.isEmpty() )
        {
            return NodeVersionDiffResult.create().
                totalHits( result.getResults().getTotalHits() ).
                build();
        }

        final NodeVersionDiffResult.Builder builder = NodeVersionDiffResult.create();

        builder.totalHits( result.getResults().getTotalHits() );

        for ( final SearchHit hit : result.getResults() )
        {
            builder.add( NodeId.from( hit.getField( VersionIndexPath.NODE_ID.toString() ).getSingleValue().toString() ) );
        }

        return builder.build();
    }

    @Reference
    public void setSearchDao( final SearchDao searchDao )
    {
        this.searchDao = searchDao;
    }
}
