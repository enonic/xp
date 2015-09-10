package com.enonic.wem.repo.internal.index.query;

import com.enonic.wem.repo.internal.index.IndexContext;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeVersionIds;
import com.enonic.xp.query.expr.OrderExpressions;

public interface QueryService
{
    int GET_ALL_SIZE_FLAG = -1;

    NodeQueryResult find( final NodeQuery query, final IndexContext context );

    NodeVersionIds find( final NodeIds nodeIds, final OrderExpressions orderExprs, final IndexContext indexContext );

}
