package com.enonic.xp.repo.impl.search;

import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeVersionDiffResult;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.SearchSource;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQuery;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQueryResult;
import com.enonic.xp.repo.impl.index.query.NodeQueryResult;
import com.enonic.xp.repo.impl.version.search.NodeVersionDiffQuery;
import com.enonic.xp.repo.impl.version.search.NodeVersionQuery;

public interface NodeSearchService
{
    int GET_ALL_SIZE_FLAG = -1;

    NodeQueryResult query( final NodeQuery query, final SearchSource source );

    NodeQueryResult query( final NodeQuery query, final ReturnFields returnFields, final SearchSource source );

    NodeVersionQueryResult query( final NodeVersionQuery query, final SearchSource source );

    NodeBranchQueryResult query( final NodeBranchQuery nodeBranchQuery, final SearchSource source );

    NodeVersionDiffResult query( final NodeVersionDiffQuery query, final SearchSource source );


}
