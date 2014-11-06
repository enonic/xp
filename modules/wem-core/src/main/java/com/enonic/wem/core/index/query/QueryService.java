package com.enonic.wem.core.index.query;

import com.enonic.wem.core.entity.NodeId;
import com.enonic.wem.core.entity.NodeIds;
import com.enonic.wem.core.entity.NodePath;
import com.enonic.wem.core.entity.NodePaths;
import com.enonic.wem.core.entity.NodeVersionId;
import com.enonic.wem.core.entity.NodeVersionIds;
import com.enonic.wem.core.entity.query.NodeQuery;
import com.enonic.wem.core.index.IndexContext;

public interface QueryService
{
    public static final int GET_ALL_SIZE_FLAG = -1;

    public NodeQueryResult find( final NodeQuery query, final IndexContext context );

    public NodeVersionId get( final NodeId nodeId, final IndexContext indexContext );

    public NodeVersionId get( final NodePath nodePath, final IndexContext indexContext );

    public NodeVersionIds get( final NodePaths nodePath, final IndexContext indexContext );

    public NodeVersionIds get( final NodeIds nodeIds, final IndexContext indexContext );

    public NodeVersionIds getByParent( final NodePath parentPath, final IndexContext indexContext );

}
