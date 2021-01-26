package com.enonic.xp.repo.impl.search;

import com.enonic.xp.node.NodeCommitQuery;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeVersionQuery;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.SearchSource;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQuery;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repo.impl.version.search.NodeVersionDiffQuery;

public interface NodeSearchService
{
    int GET_ALL_SIZE_FLAG = -1;

    SearchResult query( NodeQuery query, SearchSource source );

    SearchResult query( NodeQuery query, ReturnFields returnFields, SearchSource source );

    SearchResult query( NodeVersionQuery query, SearchSource source );

    SearchResult query( NodeCommitQuery query, SearchSource source );

    SearchResult query( NodeBranchQuery nodeBranchQuery, SearchSource source );

    SearchResult query( NodeVersionDiffQuery query, SearchSource source );
}
