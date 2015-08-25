package com.enonic.wem.repo.internal.index.query;

import com.enonic.wem.repo.internal.index.IndexContext;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionIds;
import com.enonic.xp.query.expr.OrderExpressions;

public interface QueryService
{
    static final int GET_ALL_SIZE_FLAG = -1;

    NodeQueryResult find( final NodeQuery query, final IndexContext context );

    NodeVersionId get( final NodeId nodeId, final IndexContext indexContext );

    NodeVersionId get( final NodePath nodePath, final IndexContext indexContext );

    NodeVersionIds find( final NodePaths nodePath, final OrderExpressions orderExprs, final IndexContext indexContext );

    NodeVersionIds find( final NodeIds nodeIds, final OrderExpressions orderExprs, final IndexContext indexContext );

    boolean hasChildren( final NodePath nodeId, final IndexContext indexContext );

}
