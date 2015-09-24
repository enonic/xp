package com.enonic.xp.repo.impl.index.query;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionIds;
import com.enonic.xp.query.expr.OrderExpressions;
import com.enonic.xp.repo.impl.index.IndexContext;

public interface QueryService
{
    public static final int GET_ALL_SIZE_FLAG = -1;

    public NodeQueryResult find( final NodeQuery query, final IndexContext context );

    public NodeVersionId get( final NodeId nodeId, final IndexContext indexContext );

    public NodeVersionId get( final NodePath nodePath, final IndexContext indexContext );

    public NodeVersionIds find( final NodePaths nodePath, final OrderExpressions orderExprs, final IndexContext indexContext );

    public NodeVersionIds find( final NodeIds nodeIds, final OrderExpressions orderExprs, final IndexContext indexContext );

    public boolean hasChildren( final NodePath nodeId, final IndexContext indexContext );

}
