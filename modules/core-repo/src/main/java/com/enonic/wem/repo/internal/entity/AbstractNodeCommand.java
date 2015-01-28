package com.enonic.wem.repo.internal.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.FindNodesByParentParams;
import com.enonic.wem.api.node.FindNodesByParentResult;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeIds;
import com.enonic.wem.api.node.NodeIndexPath;
import com.enonic.wem.api.node.NodeName;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.Nodes;
import com.enonic.wem.api.node.UpdateNodeParams;
import com.enonic.wem.api.query.expr.FieldOrderExpr;
import com.enonic.wem.api.query.expr.OrderExpr;
import com.enonic.wem.api.query.expr.OrderExpressions;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.acl.AccessControlList;
import com.enonic.wem.api.security.auth.AuthenticationInfo;
import com.enonic.wem.repo.internal.blob.BlobStore;
import com.enonic.wem.repo.internal.entity.dao.NodeDao;
import com.enonic.wem.repo.internal.index.IndexService;
import com.enonic.wem.repo.internal.index.query.QueryService;
import com.enonic.wem.repo.internal.version.VersionService;
import com.enonic.wem.repo.internal.workspace.WorkspaceService;

abstract class AbstractNodeCommand
{
    static final OrderExpressions DEFAULT_ORDER_EXPRESSIONS =
        OrderExpressions.from( FieldOrderExpr.create( NodeIndexPath.MODIFIED_TIME, OrderExpr.Direction.DESC ) );

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

    void updateNodeMetadata( final Node node )
    {
        StoreNodeCommand.create().
            node( node ).
            updateMetadataOnly( true ).
            indexService( this.indexService ).
            workspaceService( this.workspaceService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            build().
            execute();
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

    Node doMoveNode( final NodePath newParentPath, final NodeName nodeName, final NodeId nodeId )
    {
        return MoveNodeCommand.create().
            id( nodeId ).
            newParent( newParentPath ).
            newNodeName( nodeName ).
            queryService( this.queryService ).
            nodeDao( this.nodeDao ).
            workspaceService( this.workspaceService ).
            indexService( this.indexService ).
            versionService( this.versionService ).
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


    Node doCreateNode( final CreateNodeParams params, final BlobStore binaryBlobStore )
    {
        return CreateNodeCommand.create().
            params( params ).
            indexService( this.indexService ).
            versionService( this.versionService ).
            workspaceService( this.workspaceService ).
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            binaryBlobStore( binaryBlobStore ).
            build().
            execute();
    }

    Node doUpdateNode( final UpdateNodeParams params, final BlobStore binaryBlobStore )
    {
        return UpdateNodeCommand.create().
            params( params ).
            indexService( this.indexService ).
            versionService( this.versionService ).
            workspaceService( this.workspaceService ).
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            binaryBlobStore( binaryBlobStore ).
            build().
            execute();
    }

    Node doDeleteNode( final NodeId nodeId )
    {
        return DeleteNodeByIdCommand.create().
            indexService( this.indexService ).
            versionService( this.versionService ).
            workspaceService( this.workspaceService ).
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            nodeId( nodeId ).
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

    protected PrincipalKey getCurrentPrincipalKey()
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();

        return authInfo != null && authInfo.isAuthenticated() ? authInfo.getUser().getKey() : PrincipalKey.ofAnonymous();
    }

    AccessControlList evaluatePermissions( final NodePath parentPath, final boolean inheritPermissions,
                                           final AccessControlList permissions )
    {
        return inheritPermissions ? getPermissions( parentPath ) : permissions;
    }

    private AccessControlList getPermissions( final NodePath nodePath )
    {
        if ( nodePath.isRoot() )
        {
            return AccessControlList.empty();
        }
        final Node node = doGetByPath( nodePath, false );
        return node != null ? node.getPermissions() : AccessControlList.empty();
    }

    public static abstract class Builder<B extends Builder>
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
            Preconditions.checkNotNull( queryService, "workspaceService not set" );
        }
    }
}
