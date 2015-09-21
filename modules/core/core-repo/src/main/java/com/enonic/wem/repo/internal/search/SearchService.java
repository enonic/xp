package com.enonic.wem.repo.internal.search;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.index.query.NodeQueryResult;
import com.enonic.wem.repo.internal.storage.branch.NodeBranchQuery;
import com.enonic.wem.repo.internal.storage.branch.NodeBranchQueryResult;
import com.enonic.wem.repo.internal.version.FindVersionsQuery;
import com.enonic.xp.node.FindNodeVersionsResult;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeVersionDiffQuery;
import com.enonic.xp.node.NodeVersionDiffResult;
import com.enonic.xp.node.NodeVersionIds;
import com.enonic.xp.query.expr.OrderExpressions;

public interface SearchService
{
    int GET_ALL_SIZE_FLAG = -1;

    NodeQueryResult search( final NodeQuery query, final InternalContext context );

    NodeVersionIds search( final NodeIds nodeIds, final OrderExpressions orderExprs, final InternalContext indexContext );

    NodeBranchQueryResult search( final NodeBranchQuery nodeBranchQuery, final InternalContext context );

    FindNodeVersionsResult findVersions( final FindVersionsQuery query, final InternalContext context );

    NodeVersionDiffResult diffNodeVersions( final NodeVersionDiffQuery query, final InternalContext context );


}
