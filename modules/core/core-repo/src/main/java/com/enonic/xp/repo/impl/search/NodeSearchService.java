package com.enonic.xp.repo.impl.search;

import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeVersionDiffResult;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQuery;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQueryResult;
import com.enonic.xp.repo.impl.index.query.NodeQueryResult;
import com.enonic.xp.repo.impl.version.search.NodeVersionDiffQuery;
import com.enonic.xp.repo.impl.version.search.NodeVersionQuery;

public interface NodeSearchService
{
    int GET_ALL_SIZE_FLAG = -1;

    NodeQueryResult query( final NodeQuery query, final InternalContext context );

    NodeQueryResult query( final NodeQuery query, final ReturnFields returnFields, final InternalContext context );

    NodeVersionQueryResult query( final NodeVersionQuery query, final InternalContext context );

    NodeBranchQueryResult query( final NodeBranchQuery nodeBranchQuery, final InternalContext context );

    NodeVersionDiffResult query( final NodeVersionDiffQuery query, final InternalContext context );


}
