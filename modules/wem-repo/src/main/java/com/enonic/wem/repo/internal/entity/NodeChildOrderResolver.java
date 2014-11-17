package com.enonic.wem.repo.internal.entity;

import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.repo.internal.entity.dao.NodeDao;
import com.enonic.wem.core.index.IndexContext;
import com.enonic.wem.core.index.query.QueryService;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeVersionId;

public class NodeChildOrderResolver
{
    private final NodeDao nodeDao;

    private final QueryService queryService;

    private final NodePath parentPath;

    private final ChildOrder childOrder;

    private NodeChildOrderResolver( final Builder builder )
    {
        nodeDao = builder.nodeDao;
        queryService = builder.queryService;
        parentPath = builder.nodePath;
        childOrder = builder.childOrder;
    }

    public ChildOrder resolve()
    {
        if ( this.childOrder != null && !this.childOrder.isEmpty() )
        {
            return this.childOrder;
        }

        if ( parentPath.isRoot() )
        {
            return ContextAccessor.current().getWorkspace().getChildOrder();
        }

        final NodeVersionId parentNodeVersion = this.queryService.get( this.parentPath, IndexContext.from( ContextAccessor.current() ) );

        if ( parentNodeVersion == null )
        {
            return ContextAccessor.current().getWorkspace().getChildOrder();
        }

        final Node parentNode = this.nodeDao.getByVersionId( parentNodeVersion );

        return parentNode.getChildOrder();
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private NodeDao nodeDao;

        private QueryService queryService;

        private NodePath nodePath;

        private ChildOrder childOrder;

        private Builder()
        {
        }

        public Builder nodeDao( final NodeDao nodeDao )
        {
            this.nodeDao = nodeDao;
            return this;
        }

        public Builder workspaceService( final QueryService queryService )
        {
            this.queryService = queryService;
            return this;
        }

        public Builder nodePath( final NodePath nodePath )
        {
            this.nodePath = nodePath;
            return this;
        }

        public Builder childOrder( final ChildOrder childOrder )
        {
            this.childOrder = childOrder;
            return this;
        }

        public NodeChildOrderResolver build()
        {
            return new NodeChildOrderResolver( this );
        }
    }
}
