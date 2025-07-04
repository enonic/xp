package com.enonic.xp.repo.impl.search;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.node.NodeCommitQuery;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeVersionQuery;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.SearchSource;
import com.enonic.xp.repo.impl.SingleRepoStorageSource;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQuery;
import com.enonic.xp.repo.impl.branch.storage.BranchIndexPath;
import com.enonic.xp.repo.impl.commit.storage.CommitIndexPath;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repo.impl.storage.StaticStorageType;
import com.enonic.xp.repo.impl.version.VersionIndexPath;
import com.enonic.xp.repo.impl.version.search.NodeVersionDiffQuery;
import com.enonic.xp.repository.RepositoryId;

@Component
public class NodeSearchServiceImpl
    implements NodeSearchService
{
    private static final ReturnFields VERSION_RETURN_FIELDS =
        ReturnFields.from( VersionIndexPath.VERSION_ID, VersionIndexPath.NODE_BLOB_KEY, VersionIndexPath.INDEX_CONFIG_BLOB_KEY,
                           VersionIndexPath.ACCESS_CONTROL_BLOB_KEY, VersionIndexPath.BINARY_BLOB_KEYS, VersionIndexPath.TIMESTAMP,
                           VersionIndexPath.NODE_PATH, VersionIndexPath.NODE_ID, VersionIndexPath.COMMIT_ID );

    private static final ReturnFields BRANCH_RETURN_FIELDS =
        ReturnFields.from( BranchIndexPath.NODE_ID, BranchIndexPath.VERSION_ID, BranchIndexPath.NODE_BLOB_KEY,
                           BranchIndexPath.INDEX_CONFIG_BLOB_KEY, BranchIndexPath.ACCESS_CONTROL_BLOB_KEY, BranchIndexPath.STATE,
                           BranchIndexPath.PATH, BranchIndexPath.TIMESTAMP );

    private static final ReturnFields COMMIT_RETURN_FIELDS =
        ReturnFields.from( CommitIndexPath.COMMIT_ID, CommitIndexPath.MESSAGE, CommitIndexPath.COMMITTER, CommitIndexPath.TIMESTAMP );

    private SearchDao searchDao;

    @Activate
    public NodeSearchServiceImpl( @Reference final SearchDao searchDao )
    {
        this.searchDao = searchDao;
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
        final SearchRequest searchRequest = SearchRequest.create().
            searchSource( source ).
            query( query ).returnFields( query.isWithPath() ? returnFields.add( NodeIndexPath.PATH ) : returnFields ).
            build();

        return searchDao.search( searchRequest );
    }

    @Override
    public SearchResult query( final NodeBranchQuery nodeBranchQuery, final RepositoryId repositoryId )
    {
        final SearchRequest searchRequest = SearchRequest.create()
            .searchSource( SingleRepoStorageSource.create( repositoryId, StaticStorageType.BRANCH ) )
            .returnFields( BRANCH_RETURN_FIELDS )
            .query( nodeBranchQuery )
            .build();

        return searchDao.search( searchRequest );
    }

    @Override
    public SearchResult query( final NodeVersionQuery query, final RepositoryId repositoryId )
    {
        final SearchRequest searchRequest = SearchRequest.create()
            .searchSource( SingleRepoStorageSource.create( repositoryId, StaticStorageType.VERSION ) )
            .returnFields( VERSION_RETURN_FIELDS )
            .query( query )
            .build();

        return searchDao.search( searchRequest );
    }

    @Override
    public SearchResult query( final NodeCommitQuery query, final RepositoryId repositoryId )
    {
        final SearchRequest searchRequest = SearchRequest.create()
            .searchSource( SingleRepoStorageSource.create( repositoryId, StaticStorageType.COMMIT ) )
            .returnFields( COMMIT_RETURN_FIELDS )
            .query( query )
            .build();

        return searchDao.search( searchRequest );
    }

    @Override
    public SearchResult query( final NodeVersionDiffQuery query, final RepositoryId repositoryId )
    {
        final SearchRequest searchRequest = SearchRequest.create()
            .searchSource( SingleRepoStorageSource.create( repositoryId, StaticStorageType.VERSION ) )
            .returnFields( VERSION_RETURN_FIELDS )
            .query( query )
            .build();

        return searchDao.search( searchRequest );
    }

    @Reference
    public void setSearchDao( final SearchDao searchDao )
    {
        this.searchDao = searchDao;
    }
}
