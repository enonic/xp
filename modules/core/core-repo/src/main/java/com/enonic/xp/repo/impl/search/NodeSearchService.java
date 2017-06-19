package com.enonic.xp.repo.impl.search;

import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.SearchSource;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQuery;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repo.impl.version.search.NodeVersionDiffQuery;
import com.enonic.xp.node.NodeVersionQuery;

public interface NodeSearchService
{
    int GET_ALL_SIZE_FLAG = -1;

    SearchResult query( final NodeQuery query, final SearchSource source );

    SearchResult query( final NodeQuery query, final ReturnFields returnFields, final SearchSource source );

    SearchResult query( final NodeVersionQuery query, final SearchSource source );

    SearchResult query( final NodeBranchQuery nodeBranchQuery, final SearchSource source );

    SearchResult query( final NodeVersionDiffQuery query, final SearchSource source );
}
