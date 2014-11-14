package com.enonic.wem.core.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.index.IndexPaths;
import com.enonic.wem.api.query.expr.FieldOrderExpr;
import com.enonic.wem.api.query.expr.OrderExpr;
import com.enonic.wem.api.query.expr.OrderExpressions;
import com.enonic.wem.core.entity.dao.NodeDao;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.index.query.QueryService;
import com.enonic.wem.core.version.VersionService;
import com.enonic.wem.core.workspace.WorkspaceService;
import com.enonic.wem.repo.CreateNodeParams;
import com.enonic.wem.repo.FindNodesByParentParams;
import com.enonic.wem.repo.FindNodesByParentResult;
import com.enonic.wem.repo.Node;
import com.enonic.wem.repo.NodeId;
import com.enonic.wem.repo.NodeIds;
import com.enonic.wem.repo.NodePath;
import com.enonic.wem.repo.Nodes;

abstract class AbstractNodeCommand
{
    static final OrderExpressions DEFAULT_ORDER_EXPRESSIONS =
        OrderExpressions.from( FieldOrderExpr.create( IndexPaths.MODIFIED_TIME_KEY, OrderExpr.Direction.DESC ) );

    final IndexService indexService;

    final NodeDao nodeDao;

    final WorkspaceService workspaceService;

    final VersionService versionService;

    final QueryService queryService;

    AbstractNodeCommand( final Builder builder )
    {
        this.indexService = builder.indexService;
        this.nodeDao = builder.nodeDao;
        this.workspaceService = builder.workspaceService;
        this.versionService = builder.versionService;
        this.queryService = builder.queryService;
    }

    void doStoreNode( final Node node )
    {
        StoreNodeCommand.create().
            node( node ).
            indexService( this.indexService ).
            workspaceService( this.workspaceService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            build().
            execute();
    }

    Node doGetByPath( final NodePath path, final boolean resolveHasChild )
    {
        return GetNodeByPathCommand.create().
            nodePath( path ).
            resolveHasChild( resolveHasChild ).
            indexService( this.indexService ).
            workspaceService( this.workspaceService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            build().
            execute();
    }

    Node doGetById( final NodeId id, final boolean resolveHasChild )
    {
        return GetNodeByIdCommand.create().
            id( id ).
            resolveHasChild( resolveHasChild ).
            indexService( this.indexService ).
            workspaceService( this.workspaceService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            build().
            execute();
    }

    Nodes doGetByIds( final NodeIds ids, final OrderExpressions orderExprs, final boolean resolveHasChild )
    {
        return GetNodesByIdsCommand.create().
            ids( ids ).
            orderExpressions( orderExprs ).
            resolveHasChild( resolveHasChild ).
            indexService( this.indexService ).
            workspaceService( this.workspaceService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            build().
            execute();
    }


    Node doCreateNode( final CreateNodeParams params )
    {
        return CreateNodeCommand.create().
            params( params ).
            indexService( this.indexService ).
            versionService( this.versionService ).
            workspaceService( this.workspaceService ).
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            build().
            execute();
    }

    FindNodesByParentResult doFindNodesByParent( final FindNodesByParentParams params )
    {
        return FindNodesByParentCommand.create().
            params( params ).
            queryService( this.queryService ).
            nodeDao( this.nodeDao ).
            workspaceService( this.workspaceService ).
            versionService( this.versionService ).
            indexService( this.indexService ).
            build().
            execute();
    }


    public static class Builder<B extends Builder>
    {
        IndexService indexService;

        NodeDao nodeDao;

        WorkspaceService workspaceService;

        VersionService versionService;

        QueryService queryService;

        Builder()
        {
        }

        @SuppressWarnings("unchecked")
        public B indexService( final IndexService indexService )
        {
            this.indexService = indexService;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B workspaceService( final WorkspaceService workspaceService )
        {
            this.workspaceService = workspaceService;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B versionService( final VersionService versionService )
        {
            this.versionService = versionService;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B queryService( final QueryService queryService )
        {
            this.queryService = queryService;
            return (B) this;
        }


        @SuppressWarnings("unchecked")
        public B nodeDao( final NodeDao nodeDao )
        {
            this.nodeDao = nodeDao;
            return (B) this;
        }

        void validate()
        {
            Preconditions.checkNotNull( indexService, "indexService not set" );
            Preconditions.checkNotNull( versionService, "workspaceService not set" );
            Preconditions.checkNotNull( nodeDao, "nodeDao not set" );
            Preconditions.checkNotNull( workspaceService, "workspaceService not set" );
        }


    }

}
