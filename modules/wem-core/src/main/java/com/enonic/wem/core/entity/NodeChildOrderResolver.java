package com.enonic.wem.core.entity;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.core.entity.dao.NodeDao;
import com.enonic.wem.core.workspace.WorkspaceContext;
import com.enonic.wem.core.workspace.WorkspaceService;

public class NodeChildOrderResolver
{
    private final NodeDao nodeDao;

    private final WorkspaceService workspaceService;

    private final NodePath parentPath;

    private final ChildOrder childOrder;

    private NodeChildOrderResolver( Builder builder )
    {
        nodeDao = builder.nodeDao;
        workspaceService = builder.workspaceService;
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
            return Context.current().getWorkspace().getChildOrder();
        }

        final NodeVersionId parentNodeVersion =
            this.workspaceService.getByPath( this.parentPath, WorkspaceContext.from( Context.current() ) );

        if ( parentNodeVersion == null )
        {
            return Context.current().getWorkspace().getChildOrder();
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

        private WorkspaceService workspaceService;

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

        public Builder workspaceService( final WorkspaceService workspaceService )
        {
            this.workspaceService = workspaceService;
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
