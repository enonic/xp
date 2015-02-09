package com.enonic.wem.repo.internal.entity;

import java.util.concurrent.Callable;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.context.ContextBuilder;
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
import com.enonic.wem.repo.internal.branch.BranchService;
import com.enonic.wem.repo.internal.entity.dao.NodeDao;
import com.enonic.wem.repo.internal.index.IndexService;
import com.enonic.wem.repo.internal.index.query.QueryService;
import com.enonic.wem.repo.internal.version.VersionService;

abstract class AbstractNodeCommand
{
    static final OrderExpressions DEFAULT_ORDER_EXPRESSIONS =
        OrderExpressions.from( FieldOrderExpr.create( NodeIndexPath.TIMESTAMP, OrderExpr.Direction.DESC ) );

    final IndexService indexService;

    final NodeDao nodeDao;

    final BranchService branchService;

    final VersionService versionService;

    final QueryService queryService;

    AbstractNodeCommand( final Builder builder )
    {
        this.indexService = builder.indexService;
        this.nodeDao = builder.nodeDao;
        this.branchService = builder.branchService;
        this.versionService = builder.versionService;
        this.queryService = builder.queryService;
    }

    Node updateNodeMetadata( final Node node )
    {
        return StoreNodeCommand.create( this ).
            node( node ).
            updateMetadataOnly( true ).
            build().
            execute();
    }

    Node doStoreNode( final Node node )
    {
        return StoreNodeCommand.create( this ).
            node( node ).
            build().
            execute();
    }

    Node doMoveNode( final NodePath newParentPath, final NodeName nodeName, final NodeId nodeId )
    {
        return MoveNodeCommand.create( this ).
            id( nodeId ).
            newParent( newParentPath ).
            newNodeName( nodeName ).
            build().
            execute();
    }

    Node doGetByPath( final NodePath path, final boolean resolveHasChild )
    {
        return GetNodeByPathCommand.create( this ).
            nodePath( path ).
            resolveHasChild( resolveHasChild ).
            build().
            execute();
    }

    Node doGetById( final NodeId id, final boolean resolveHasChild )
    {
        return GetNodeByIdCommand.create( this ).
            id( id ).
            resolveHasChild( resolveHasChild ).
            build().
            execute();
    }

    Nodes doGetByIds( final NodeIds ids, final OrderExpressions orderExprs, final boolean resolveHasChild )
    {
        return GetNodesByIdsCommand.create( this ).
            ids( ids ).
            orderExpressions( orderExprs ).
            resolveHasChild( resolveHasChild ).
            build().
            execute();
    }


    Node doCreateNode( final CreateNodeParams params, final BlobStore binaryBlobStore )
    {
        return CreateNodeCommand.create( this ).
            params( params ).
            binaryBlobStore( binaryBlobStore ).
            build().
            execute();
    }

    Node doUpdateNode( final UpdateNodeParams params, final BlobStore binaryBlobStore )
    {
        return UpdateNodeCommand.create( this ).
            params( params ).
            binaryBlobStore( binaryBlobStore ).
            build().
            execute();
    }

    Node doDeleteNode( final NodeId nodeId )
    {
        return DeleteNodeByIdCommand.create( this ).
            nodeId( nodeId ).
            build().
            execute();
    }

    FindNodesByParentResult doFindNodesByParent( final FindNodesByParentParams params )
    {
        return FindNodesByParentCommand.create( this ).
            params( params ).
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
        final Node node = doGetByPath( nodePath, false );
        return node != null ? node.getPermissions() : AccessControlList.empty();
    }

    public static abstract class Builder<B extends Builder>
    {
        IndexService indexService;

        NodeDao nodeDao;

        BranchService branchService;

        VersionService versionService;

        QueryService queryService;

        Builder()
        {
        }

        Builder( final AbstractNodeCommand source )
        {
            this.indexService = source.indexService;
            this.nodeDao = source.nodeDao;
            this.branchService = source.branchService;
            this.queryService = source.queryService;
            this.versionService = source.versionService;
        }

        @SuppressWarnings("unchecked")
        public B indexService( final IndexService indexService )
        {
            this.indexService = indexService;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B branchService( final BranchService branchService )
        {
            this.branchService = branchService;
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
            Preconditions.checkNotNull( versionService, "versionService not set" );
            Preconditions.checkNotNull( nodeDao, "nodeDao not set" );
            Preconditions.checkNotNull( branchService, "branchService not set" );
            Preconditions.checkNotNull( queryService, "queryService not set" );
        }
    }
}
