package com.enonic.xp.repo.impl.search;

import com.enonic.xp.node.NodeCommitQuery;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeVersionQuery;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.SearchSource;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQuery;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repo.impl.version.search.NodeVersionDiffQuery;
import com.enonic.xp.repository.RepositoryId;

public interface NodeSearchService
{
    int GET_ALL_SIZE_FLAG = -1;

    SearchResult query( NodeQuery query, SearchSource source );

    SearchResult query( NodeQuery query, ReturnFields returnFields, SearchSource source );

    SearchResult query( NodeVersionQuery query, RepositoryId repositoryId );

    SearchResult query( NodeCommitQuery query, RepositoryId repositoryId );

    SearchResult query( NodeBranchQuery nodeBranchQuery, RepositoryId repositoryId );

    SearchResult query( NodeVersionDiffQuery query, RepositoryId repositoryId );
}
