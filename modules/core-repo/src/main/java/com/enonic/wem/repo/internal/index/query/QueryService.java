package com.enonic.wem.repo.internal.index.query;

import com.enonic.xp.core.query.expr.OrderExpressions;
import com.enonic.xp.core.node.NodeId;
import com.enonic.xp.core.node.NodeIds;
import com.enonic.xp.core.node.NodePath;
import com.enonic.xp.core.node.NodePaths;
import com.enonic.xp.core.node.NodeVersionId;
import com.enonic.xp.core.node.NodeVersionIds;
import com.enonic.xp.core.node.NodeQuery;
import com.enonic.wem.repo.internal.index.IndexContext;

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
