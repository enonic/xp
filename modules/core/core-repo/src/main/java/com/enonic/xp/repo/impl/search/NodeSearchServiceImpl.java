package com.enonic.xp.repo.impl.search;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.node.NodeCommitQuery;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeVersionQuery;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQuery;
import com.enonic.xp.repo.impl.branch.storage.BranchIndexPath;
import com.enonic.xp.repo.impl.commit.storage.CommitIndexPath;
import com.enonic.xp.repo.impl.version.VersionIndexPath;
import com.enonic.xp.repo.impl.version.search.NodeVersionDiffQuery;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.storage.spi.NodeSearchIndex;
import com.enonic.xp.storage.spi.ReturnFields;
import com.enonic.xp.storage.spi.SearchRequest;
import com.enonic.xp.storage.spi.SearchResult;
import com.enonic.xp.storage.spi.SearchSource;
import com.enonic.xp.storage.spi.SingleRepoStorageSource;
import com.enonic.xp.storage.spi.StaticStoreType;

@Component
public class NodeSearchServiceImpl
    implements NodeSearchService
{
    private static final ReturnFields VERSION_RETURN_FIELDS = ReturnFields.from( VersionIndexPath.entryFields() );

    private static final ReturnFields BRANCH_RETURN_FIELDS = ReturnFields.from( BranchIndexPath.entryFields() );

    private static final ReturnFields COMMIT_RETURN_FIELDS = ReturnFields.from( CommitIndexPath.entryFields() );

    private NodeSearchIndex nodeSearchIndex;

    @Activate
    public NodeSearchServiceImpl( @Reference final NodeSearchIndex nodeSearchIndex )
    {
        this.nodeSearchIndex = nodeSearchIndex;
    }

    @Override
    public SearchResult query( final NodeQuery query, final SearchSource source )
    {
        return doQuery( query, ReturnFields.empty(), source );
    }

    @Override
    public SearchResult query( final NodeQuery query, ReturnFields returnFields, final SearchSource source )
    {
        return doQuery( query, returnFields, source );
    }

    private SearchResult doQuery( final NodeQuery query, final ReturnFields returnFields, final SearchSource source )
    {
        final SearchRequest searchRequest = SearchRequest.create()
            .searchSource( source )
            .query( query )
            .returnFields( query.isWithPath() ? returnFields.add( NodeIndexPath.PATH ) : returnFields )
            .build();

        return nodeSearchIndex.search( searchRequest );
    }

    @Override
    public SearchResult query( final NodeBranchQuery nodeBranchQuery, final RepositoryId repositoryId )
    {
        final SearchRequest searchRequest = SearchRequest.create()
            .searchSource( SingleRepoStorageSource.create( repositoryId, StaticStoreType.BRANCH ) )
            .returnFields( BRANCH_RETURN_FIELDS )
            .query( nodeBranchQuery )
            .build();

        return nodeSearchIndex.search( searchRequest );
    }

    @Override
    public SearchResult query( final NodeVersionQuery query, final RepositoryId repositoryId )
    {
        final SearchRequest searchRequest = SearchRequest.create()
            .searchSource( SingleRepoStorageSource.create( repositoryId, StaticStoreType.VERSION ) )
            .returnFields( VERSION_RETURN_FIELDS )
            .query( query )
            .build();

        return nodeSearchIndex.search( searchRequest );
    }

    @Override
    public SearchResult query( final NodeCommitQuery query, final RepositoryId repositoryId )
    {
        final SearchRequest searchRequest = SearchRequest.create()
            .searchSource( SingleRepoStorageSource.create( repositoryId, StaticStoreType.COMMIT ) )
            .returnFields( COMMIT_RETURN_FIELDS )
            .query( query )
            .build();

        return nodeSearchIndex.search( searchRequest );
    }

    @Override
    public SearchResult query( final NodeVersionDiffQuery query, final RepositoryId repositoryId )
    {
        final SearchRequest searchRequest = SearchRequest.create()
            .searchSource( SingleRepoStorageSource.create( repositoryId, StaticStoreType.VERSION ) )
            .returnFields( query.getReturnFields() )
            .query( query )
            .build();

        return nodeSearchIndex.search( searchRequest );
    }

    @Reference
    public void setNodeSearchIndex( final NodeSearchIndex nodeSearchIndex )
    {
        this.nodeSearchIndex = nodeSearchIndex;
    }
}
