package com.enonic.wem.repo.internal.search;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.branch.search.NodeBranchQuery;
import com.enonic.wem.repo.internal.branch.search.NodeBranchQueryResult;
import com.enonic.wem.repo.internal.index.query.NodeQueryResult;
import com.enonic.wem.repo.internal.version.search.NodeVersionDiffQuery;
import com.enonic.wem.repo.internal.version.search.NodeVersionQuery;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeVersionDiffResult;
import com.enonic.xp.node.NodeVersionQueryResult;

public interface SearchService
{
    int GET_ALL_SIZE_FLAG = -1;

    NodeQueryResult search( final NodeQuery query, final InternalContext context );

    NodeVersionQueryResult search( final NodeVersionQuery query, final InternalContext context );

    NodeBranchQueryResult search( final NodeBranchQuery nodeBranchQuery, final InternalContext context );

    NodeVersionDiffResult search( final NodeVersionDiffQuery query, final InternalContext context );
}
